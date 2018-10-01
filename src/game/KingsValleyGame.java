package game;

import enums.Direcao;
import enums.EstadoPartida;
import enums.OrdemJogada;

public class KingsValleyGame {

	public enum PieceTypes {
		B_SOLDIER, B_KING, W_SOLDIER, W_KING; 
	}
	
	/*
	 *  Representacao do tabuleiro
	 *		"." = casa nao ocupada
	 * 		"s" soldado claro (soldier)
	 *		"S" soldado escuro (Soldier)
	 *  	"k" rei claro (king)
	 * 		"K" rei escuro (King) 
	 */
	private Tabuleiro tabuleiro;			
	private Jogador jogador1, jogador2;
	
    private EstadoPartida estadoPartida;
    
    private int tempoAguardandoInicio; // tempo entre inicialização e entrada do segundo jogador 
    private int tempoDesdeUltimaJogada; // tempo desde a última jogada
    private int tempoDesdeEncerramento; // tempo desde o encerramento
        
    private boolean firstPlay;
    private int onMovePlayer; // Jogador da jogada atual
    
	public KingsValleyGame() {
		this.tabuleiro = new Tabuleiro();
		this.jogador1 = new Jogador();
		this.jogador2 = new Jogador();
		this.setUpGame();
	}
	
	public void limpaPartida() {
		this.setUpGame();
		this.jogador1.inicializaJogador();
		this.jogador2.inicializaJogador();
	}
	
	private void setUpGame() {
		this.tabuleiro.inicializaTabuleiro();
		this.tempoAguardandoInicio = 0; 
    	this.tempoDesdeUltimaJogada = 0; 
    	this.tempoDesdeEncerramento = 0; 
		this.firstPlay = true;
		this.estadoPartida = EstadoPartida.Vazia;
	}
	
	/*
	 * Retorna representação gráfica (caracteres) do tabuleiro.
	 */
	public String obtemTabuleiro() {
		return this.tabuleiro.toString();
	}
	
	/*
	 * Verifica se uma determinada peça é de um jogador.
	 * 
	 * @param idJogador		identificador do jogador que se deseja conferir a peça.
	 * @param peca			é a representação da peça em caracter.
	 * @return 				true se a peça é do jogador, false caso contrário
	 * 
	 */
	private boolean ehMinhaPeca(int idJogador, char peca) {
		if(idJogador == this.jogador1.getIdJogador()) {
			return this.jogador1.ehMinhaPeca(peca);
			
		}else if(idJogador == this.jogador2.getIdJogador()) {
			return this.jogador2.ehMinhaPeca(peca);
		}
		return false;
	}
	
	/*
	 * Verifica se uma peca tem pelo menos um movimento válido.
	 * 
	 * @param playerId	identificador do usuário.
	 * @return true se existe pelo menos um movimento, caso contrário retorna false
	 *  
	 */
	private boolean hasValidMove(int posicao) {
		for (int i = 0; i < 8; i++) {
			int result = this.tabuleiro.verificaProximaPos(posicao, i); 
			if(result >= 0)
				return true;
		}
		return false;
	}
	
	private int getReiPosicao(int idJogador) {
		if(idJogador == this.jogador1.getIdJogador())
			return this.tabuleiro.getRei1Pos();
		return this.tabuleiro.getRei2Pos();
	}
	
	private boolean win(int idJogador) { // vitória
		// meu  rei atingiu o meio
		if(this.tabuleiro.getPeca(9) == getJogadorPorId(idJogador).getReiChar())
			return true;
		// rei do adversário foi encurralado, ou seja, não tem um movimento válido
		int posicaoRei = getReiPosicao(idJogador);
		return !hasValidMove(posicaoRei);
	}

	private Jogador getJogadorPorId(int idJogador) {
		if(idJogador == this.jogador1.getIdJogador())
			return jogador1;
		else
			return jogador2;		
	}
	
	private int getOponentId(int idJogador) {
		if(idJogador == this.jogador1.getIdJogador())
			return this.jogador2.getIdJogador();
		else
			return jogador1.getIdJogador();
	}

	private boolean tie(int id) { // empate
		if(!hasValidMove(id) && !hasValidMove(getOponentId(id)))
			return true;		
		return false;
	}
	
	private boolean isOponentActive(int playerId) {
		if(playerId == this.jogador1.getIdJogador()) {
			if(this.jogador2.getIdJogador() == -3) {
				return false;
			}else {
				return true;
			}
		} else {
			if(this.jogador1.getIdJogador() == -3) {
				return false;
			}else {
				return true;
			}
		}
	}
	
	/**
	 * 
	 * Note que é possível o mesmo player jogar
	 * com os dois players, ou seja, jogar sozinho.
	 * TODO um mesmo player pode coexistir em uma
	 * partida?  hãm?
	 * 
	 * @param jogadorId
	 */
	public void setJogador1(int jogadorId) {
		this.jogador1.setIdJogador(jogadorId);
		this.jogador1.setOrdemJogador(OrdemJogada.Primeiro);
		this.onMovePlayer = jogadorId;
	}
    
	public void setJogador2(int jogadorId) {
		this.jogador2.setIdJogador(jogadorId);
		this.jogador2.setOrdemJogador(OrdemJogada.Segundo);
		this.estadoPartida = EstadoPartida.EmJogo;
	}
	
    public int getOnMovePlayer() {
    	return this.onMovePlayer;
    }
    
    public int getOpponentId(int playerId) {
    	// TODO e se o oponente ainda não foi inicializado?
    	if(playerId == jogador1.getIdJogador())
    		return jogador2.getIdJogador();
    	if(playerId == jogador2.getIdJogador())
    		return jogador1.getIdJogador();
    	else
    		return -1;
    }
	
	/*
	 * 
	 * 
	 * 
	 * @param id identificador do usuário
	 * @return função retorna:
	 * 			-2 se ainda não há dois jogadores na partida
	 * 			-1 TODO quando houve algum erro
	 * 			 0 se não é minha vez
	 * 			 1 se sim, é minha vez
	 * 			 
	 * 			 2 é vencedor
	 * 			 3 se é o perdedor
	 * 			 4 quando houver empate
	 * 			 5 vencedor por WO
	 * 			 6 perdedor por WO
	 * 
	 */
	public int ehMinhaVez(int playerId) {
	
		// TODO se o rei ficar encurralado em uma casa, sem poder se mover, isso também determina
		// a derrota do respectivo jogador.
		
		if(playerId != this.jogador1.getIdJogador() && playerId != this.jogador2.getIdJogador())
			return -1;
		
		// verifica se partida já começou
		if(this.jogador1.getIdJogador() < 0 || this.jogador2.getIdJogador() < 0) // inda não há dois jogadores na partida
			return -2;
		
		// verifica o estado do jogo, em relação a resultado (vitória, derrot e WO)
		if(win(playerId))
			return 2;
		if(win(getOponentId(playerId)))
			return 3;

		// empate
		if(tie(playerId))
			return 4;
		
		// vitória por WO - oponente desistiu (não está mais ativo)
		if(!isOponentActive(playerId))
			return 5;
		
		// TODO se eu perder por WO eu ainda posso consultar 
		
		// se chegou aqui sem resultado, retorna se é ou não minha vez 
		if(this.onMovePlayer != playerId) // é minha vez ou não
			return 0;
		else
			return 1;
		
	}
	
	/*
	 * 
	 * @param pos
	 * @param dir
	 * @return função retorna: 
	 * 			
	 * 			-0 para movimento inválido - por exemplo, movimento em uma direção e sentido
	 * 				que que resulta em uma posição ocupada ou fora do tabuleiro. Também 
	 * 				retornará 0 quando o jogador tent mover uma peça que não é sua.
	 * 			-1 se tudo estiver certo
	 * 			
	 * 			-1 para jogador não encontrado (TODO server pode validar isso também)
	 * 			-2 partida não iniciada: ainda não há dois jogadores registrados na partida
	 * 			-3 quando parâmetros de posição e orientação foram inválidos
	 * 			-4 não é a vez do jogador.
	 * 
	 *		Valores para dir (raciocinio em sentido horario)
	 *		0 - direita
	 *		1 - diagonal direita inferior
	 *		2 - para baixo
	 *		3 - diagonal esquerda inferior
	 *		4 - esquerda
	 *		5 - diagonal esquerda superior
	 *		6 - para cima
	 *		7 - diagonal direita superior 
	 *				
	 *				Tabuleiro
	 *								   min  max
	 *		's', '-', '-', '-', 'S' 	0  - 4 
	 *		's', '-', '-', '-', 'S' 	5  - 9
	 *		'k', '-', '-', '-', 'K' 	10 - 14
	 *		's', '-', '-', '-', 'S' 	15 - 19
	 *		's', '-', '-', '-', 'S'  	20 - 24
	  
	 * */
	public int movePeca(int idJogador, int lin, int col, Direcao direcao) {
		
		int dir = direcao.ordinal();
		int pos = (lin * 5) + col;
		char peca = this.tabuleiro.getPeca(pos);
		
		// Regra: primeira jogada deve ser com um soldado
		// TODO primeira jogada dos dois jogadores?
		// // quem inicia a partida deve mover um soldado (especificação) (rever)
		
		// Regra: Jogador deve estar no jogo
		if(this.jogador1.getIdJogador() != idJogador && this.jogador2.getIdJogador() != idJogador) {
			return -1; // Violação, erro código -1
		// Regra: sala ainda não possui dois jogadores
		}else if(this.jogador1.getIdJogador() == -1 || this.jogador2.getIdJogador() == -1) {
			// TODO o que acontece se um player abandonar?
			return -2; // Violação, erro código -2
		// Regra: Validade dos parâmetros de posição e orientação. 
		}else if(!ehMinhaPeca(idJogador, peca)) {
			return 0; // Violação, erro código 0
		}else if((dir < 0 || dir > 7) || (lin < 0 || lin > 4) || (col < 0 || col > 4)) {
			return -3; // Violação, erro código -3;
		// Regra: É a vez do jogador em questão? 
		}else if(getOnMovePlayer() != idJogador) {
			return -4;
		// Regra: um jogador só pode mover as suas peças.
		}

		if(this.firstPlay) {
			if(peca == 's' || peca == 'S')
				this.firstPlay = false;
			else
				return 0;
		}
			
		if(this.tabuleiro.movePeca(pos, dir)) {	
		
			// Regra: comando de resultar em um movimento válido
			//if(newPos == originalPos)
			//	return 0; // movimento foi inválido e posiçao foi mantida
			
			// jogadas são obrigatórias, um jogador não pode deixar de mover uma peca no seu turno
			// só passa a vez se a jogada foi válida
			if(idJogador == this.jogador1.getIdJogador())
				this.onMovePlayer = this.jogador2.getIdJogador();
			else
				this.onMovePlayer = this.jogador1.getIdJogador();
			
			// TODO jogadas inválidas valem para restart do contador?
			this.tempoDesdeUltimaJogada = 0;
			return 1;
		}
		return 0;
	}
	
	
	public boolean isEmptyGame() {
    	if(this.jogador1.getIdJogador() == - 1 && this.jogador2.getIdJogador() == -1)
    		return true;
    	else
    		return false;
    }
	
	/*
	 * 
	 * TODO verificar retornos
	 * 
	 */
	public int encerraPartida(int playerId) {
		this.estadoPartida = EstadoPartida.Encerrada;
		return 1;
	}
	
	public boolean emJogo() {
		return this.estadoPartida == EstadoPartida.EmJogo;
	}
	
	public boolean aguardandoJogador() {
		return this.estadoPartida == EstadoPartida.AguardandoJogador;
	}
	
	public boolean partidaEncerrada() {
		return this.estadoPartida == EstadoPartida.Encerrada;
	}
	
	
	/*
     * Atualiza temporizadores e a variável que denota o estado da partida. 
     * 
     * As restrições temporáis para modificação da variável de estado são as seguintes:
	 * 		2 minutos (120 segundos) pelo registro do segundo jogador; 
	 * 		60 segundos pelas jogadas de cada jogador; 
	 * 		60 segundos para “destruir” a partida depois de definido o vencedor.	 
     */
    public void atualizaRestricoesTemporais() {
    	if(this.estadoPartida == EstadoPartida.AguardandoJogador) {
    		this.tempoAguardandoInicio++; // tempo entre inicialização e entrada do segundo jogador
    		if(tempoAguardandoInicio == 120)
    			this.estadoPartida = EstadoPartida.Encerrada;
    	}else if(this.estadoPartida == EstadoPartida.EmJogo){
    		this.tempoDesdeUltimaJogada++; // tempo desde a última jogada
    		if(this.tempoDesdeUltimaJogada == 60)
    			this.estadoPartida = EstadoPartida.Encerrada;	
    	}else if(this.estadoPartida == EstadoPartida.Encerrada){
    		this.tempoDesdeEncerramento++; // tempo desde o encerramento
    		if(this.tempoDesdeEncerramento == 60)
        		this.estadoPartida = EstadoPartida.AguardandoDestruicao;
    	}else if(this.estadoPartida == EstadoPartida.Vazia){
    		// TODO Hello empty room!
    	}else {
    		System.out.println("Erro na atualização dos temporizadores!");
    	}
    }
    
    /*
     * Verifica se a partida é "destruível", ou seja, se a partida pode ser os dados
     * da partida podem ser zerados e a partida liberada para outros jogadores.
     * 
     * */
    public boolean ehDestruivel() {
    	if(EstadoPartida.AguardandoDestruicao == this.estadoPartida)
    		return true;
    	return false;
    }
    
	
}

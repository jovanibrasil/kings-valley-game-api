package game;

import enums.Direcao;
import enums.EstadosPartida;

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
	
    private int player1Id, player2Id;
    //private boolean player1Status, player2Status;
    
    private EstadosPartida estadoPartida;
    
    private int tempoAguardandoInicio; // tempo entre inicialização e entrada do segundo jogador 
    private int tempoDesdeUltimaJogada; // tempo desde a última jogada
    private int tempoDesdeEncerramento; // tempo desde o encerramento
        
    private boolean firstPlay;
    private int onMovePlayer; // The player that is on move
    private int king1Pos, king2Pos; // King position, use for test routines
    
	public KingsValleyGame() {
		this.tabuleiro = new Tabuleiro();
		this.setUpGame();
	}
	
	public void limpaPartida() {
		this.setUpGame();
	}
	
	private void setUpGame() {
		this.tabuleiro.inicializaTabuleiro();
		
		this.tempoAguardandoInicio = 0; 
    	this.tempoDesdeUltimaJogada = 0; 
    	this.tempoDesdeEncerramento = 0; 
		
		this.firstPlay = true;
		this.player1Id = -1;
		this.player2Id = -1;
		this.king1Pos = 10;
		this.king2Pos = 14;
		this.estadoPartida = EstadosPartida.Vazia;
	}
	
	
	
	
	public String obtemTabuleiro() {
		// should I send a string or a vector?
		String out = "";
		for (int i = 0, k = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++, k++)
				out += this.board[k] + " ";
			out += "\n";
		}
		out += "\n";
		return out;
	}
	
	private boolean ehMinhaPeca(int id, int pos) {
		// System.out.println("Player id="+id+" deseja mover " + board[pos]);
		// System.out.println(this.player1Id);
		if(id == this.player1Id) {
			if(board[pos] == 'k' || board[pos] == 's') {
		//		System.out.println("	player 1");
				return true;
			}
		}else if(id == this.player2Id) {
			if(board[pos] == 'K' || board[pos] == 'S') {
				//	System.out.println("	player 2");
				return true;
			}
		}
		//System.out.println("Peça não é do player");
		return false;
	}
	
	/*
	 * Verifica se um player tem um movimento válido. 
	 */
	private boolean hasValidMove(int playerId) {
		
		int kingPosition = getKingPosition(getOponentId(playerId));
		for (int i = 0; i < 8; i++) {
			int result = verificaPosicao(kingPosition, i); // return -1
			if(result >= 0)
				return true;
		}
		return false;
		
	}
	
	private boolean win(int playerId) { // vitória
		// meu  rei atingiu o meio
		if(this.board[9] == getKingChar(playerId))
			return true;
		// rei do adversário foi encurralado
		return !hasValidMove(playerId);
	}
	
	private int getKingPosition(int playerId) {
		if(playerId == player1Id)
			return king1Pos;
		else
			return king2Pos;
	}

	private int getOponentId(int playerId) {
		if(playerId == this.player1Id)
			return this.player2Id;
		else
			return player1Id;
	}

	private boolean tie(int id) { // empate
		if(!hasValidMove(id) && !hasValidMove(getOponentId(id)))
			return true;		
		return false;
	}
	
	private boolean isOponentActive(int playerId) {
		if(playerId == this.player1Id) {
			if(this.player2Id == -3) {
				return false;
			}else {
				return true;
			}
		} else {
			if(this.player1Id == -3) {
				return false;
			}else {
				return true;
			}
		}
	}

	private char getKingChar(int playerId) {
		if(playerId == this.player1Id) {
			return 'k';
		}else {
			return 'K';
		}
	}

	
	
	/**
	 * 
	 * Note que é possível o mesmo player jogar
	 * com os dois players, ou seja, jogar sozinho.
	 * TODO um mesmo player pode coexistir em uma
	 * partida?
	 * 
	 * @param player1Id
	 */
	public void setPlayer1(int player1Id) {
		this.player1Id = player1Id;
		this.onMovePlayer = player1Id;
	}
    
	public void setPlayer2(int player2Id) {
		this.player2Id = player2Id;
		this.estadoPartida = EstadosPartida.EmJogo;
	}
	
	public int encerraPartida(int playerId) {
		
		this.estadoPartida = EstadosPartida.Encerrada;
		
		if(playerId == player1Id)
    		this.player1Id = -3;
		else if(playerId == player2Id)
    		this.player2Id = -3;
    	else
    		return -1;
    	return 0; 
	}
    
    public int getOnMovePlayer() {
    	return this.onMovePlayer;
    }

    public boolean isEmptyGame() {
    	if(this.player1Id == - 1 && this.player2Id == -1)
    		return true;
    	else
    		return false;
    }
    
    public int getOpponentId(int playerId) {
    	
    	if(playerId == player1Id)
    		return player2Id;
    	if(playerId == player2Id)
    		return player1Id;
    	else
    		return -1;
 	
    }
	
	private boolean isBoardPositionEmpty(int pos) {
		if(this.board[pos] == '-')
			return true;
		else
			return false;
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
		
		if(playerId != this.player1Id && playerId != this.player2Id)
			return -1;
		
		// verifica se partida já começou
		if(this.player1Id < 0 || this.player2Id < 0) // inda não há dois jogadores na partida
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
	public int movePeca(int playerId, int lin, int col, Direcao direcao) {
		
		int dir = direcao.ordinal();
		
		int pos = (lin * 5) + col;
		// Regra: primeira jogada deve ser com um soldado
		// TODO primeira jogada dos dois jogadores?
		// // quem inicia a partida deve mover um soldado (especificação) (rever)
		
		// Regra: Jogador deve estar no jogo
		if(this.player1Id != playerId && this.player2Id != playerId) {
			return -1; // Violação, erro código -1
		// Regra: sala ainda não possui dois jogadores
		}else if(this.player1Id == -1 || this.player2Id == -1) {
			// TODO o que acontece se um player abandonar?
			return -2; // Violação, erro código -2
		// Regra: Validade dos parâmetros de posição e orientação. 
		}else if(!ehMinhaPeca(playerId, pos)) {
			return 0; // Violação, erro código 0
		}else if((dir < 0 || dir > 7) || (lin < 0 || lin > 4) || (col < 0 || col > 4)) {
			return -3; // Violação, erro código -3;
		// Regra: É a vez do jogador em questão? 
		}else if(getOnMovePlayer() != playerId) {
			return -4;
		// Regra: um jogador só pode mover as suas peças.
		}

		if(this.firstPlay) {
			if(board[pos] == 's' || board[pos] == 'S')
				this.firstPlay = false;
			else
				return 0;
		}
			
		int originalPos = pos;
		int newPos = 0;
		
		// peca se movimenta obrigatoriamente no sentido escolhido ate
		// 1) fim do tabuleiro
		// 2) encontrar uma outra peca (nao e possivel pular uma peca)
		
		// TODO unica peca que pode parar no centro é rei
		// TODO o rei também não poderá parar no centro se houver mais casas no sentido
		// soldados podem passar pelo centro mas não podem parar
		
		while(true) { // enquanto próximo movimento é válido
			
			newPos = verificaPosicao(pos, dir); // busca próximo movimento
			if(newPos >= 0) { // movimento foi válido
				// atualiza reis se suas posições forem modificadas
				king1Pos = pos == king1Pos ? newPos : pos;
				king2Pos = pos == king2Pos ? newPos : pos;
				pos = newPos;
				//System.out.println("Movimentou para outra posição");
			}else {
				// se nãoé mais possível se movimentar,mas houve algum movimento
				// realiza a atualização do tabuleiro
				if(pos != originalPos) {
					//System.out.println("Finalizou movimento para outra posição");
					this.board[pos] = this.board[originalPos]; 
					this.board[originalPos] = '-';
				}else {
					//System.out.println("Ficou parado");
					return 0;
				}
				break;
			}
		}
	
		// Regra: comando de resultar em um movimento válido
		//if(newPos == originalPos)
		//	return 0; // movimento foi inválido e posiçao foi mantida
		
		// jogadas são obrigatórias, um jogador não pode deixar de mover uma peca no seu turno
		// só passa a vez se a jogada foi válida
		if(playerId == this.player1Id)
			this.onMovePlayer = this.player2Id;
		else
			this.onMovePlayer = this.player1Id;
		
		// TODO jogadas inválidas valem para restart do contador?
		this.tempoDesdeUltimaJogada = 0;
		
		return 1;
	}
	
	public boolean emJogo() {
		return this.estadoPartida == EstadosPartida.EmJogo;
	}
	
	public boolean aguardandoJogador() {
		return this.estadoPartida == EstadosPartida.AguardandoJogador;
	}
	
	public boolean partidaEncerrada() {
		return this.estadoPartida == EstadosPartida.Encerrada;
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
    	if(this.estadoPartida == EstadosPartida.AguardandoJogador) {
    		this.tempoAguardandoInicio++; // tempo entre inicialização e entrada do segundo jogador
    		if(tempoAguardandoInicio == 120)
    			this.estadoPartida = EstadosPartida.Encerrada;
    	}else if(this.estadoPartida == EstadosPartida.EmJogo){
    		this.tempoDesdeUltimaJogada++; // tempo desde a última jogada
    		if(this.tempoDesdeUltimaJogada == 60)
    			this.estadoPartida = EstadosPartida.Encerrada;	
    	}else if(this.estadoPartida == EstadosPartida.Encerrada){
    		this.tempoDesdeEncerramento++; // tempo desde o encerramento
    		if(this.tempoDesdeEncerramento == 60)
        		this.estadoPartida = EstadosPartida.AguardandoDestruicao;
    	}else if(this.estadoPartida == EstadosPartida.Vazia){
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
    	if(EstadosPartida.AguardandoDestruicao == this.estadoPartida)
    		return true;
    	return false;
    }
	
}

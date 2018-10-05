package jogo.servidor;

import enums.Direcao;
import enums.EstadoJogador;
import enums.EstadoPartida;
import enums.TipoPecas;

/**
 *
 * Classe principal da implementação do jogo KingsValley.
 *
 * @author Jovani Brasil
 * @email jovanibrasil@gmail.com
 *   
 * TODO Jogadas inválidas valem para restart do contador de tempo das jogadas?
 * 
 * 
 */
public class KingsValleyGame {

	private Tabuleiro tabuleiro;			
	private Jogador jogador1, jogador2;
	private EstadoPartida estadoPartida; // Define o estado da partida em determina momento 
    
    /* A utilização do temporizador depende do estado da partida, podendo ser
    	1) tempo entre inicialização da partida e entrada do segundo jogador (estado AguardandoOponente)
    	2) tempo desde a última jogada (estado EmJogo)
    	3) tempo desde o encerramento (estado Encerrada)
    */
    private int temporizador;  
    
    private boolean firstPlay;
    private Jogador jogadorDaVez; // Jogador da jogada atual
    
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
		this.temporizador = 0; 
		this.firstPlay = true;
		this.estadoPartida = EstadoPartida.PartidaVazia;
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
		Jogador jogador = getJogadorPorId(idJogador);
		return jogador.ehMinhaPeca(peca);	
	}
	
	/*
	 * Verifica se uma peca tem pelo menos um movimento válido.
	 * 
	 * @param posicao	posição a qual se deseja realizar a verificação.
	 * @return 			true se existe pelo menos um movimento, caso contrário retorna false
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
	
	/*
	 * Busca a posição do rei de um determinado jogador no tabuleiro.
	 * 
	 * @param idJogador		identificaodr do jogador que se deseja verificar o rei
	 * @return				a posição do rei no tabuleiro
	 * 
	 */
	private int getReiPosicao(int idJogador) {
		if(idJogador == this.jogador1.getIdJogador())
			return this.tabuleiro.getRei1Pos();
		return this.tabuleiro.getRei2Pos();
	}
	
	/*
	 * Testa se um jogadore é vitorioso. Um jogador chega a vitória se o seu rei
	 * chegar ao centro ou se o rei do oponente for encurradalado.
	 * 
	 * @param idJogador		identificador do jogador que se deseja verificar
	 * @return				true se for vitorioso, caso contrário false
	 * 
	 */
	private boolean win(int idJogador) { 
		// testa se o rei do jogador atingiu o centro
		if(this.tabuleiro.getPeca(12) == getJogadorPorId(idJogador).getReiChar())
			return true;
		// rei do adversário foi encurralado, ou seja, não tem um movimento válido
		int posicaoRei = getReiPosicao(idJogador);
		return !hasValidMove(posicaoRei);
	}
	
	private boolean tie() { // empate
		if(!hasValidMove(jogador1.getIdJogador()) &&
				!hasValidMove(getIdOponente(jogador2.getIdJogador())))
			return true;		
		return false;
	}

	public Jogador getJogadorPorId(int idJogador) {
		if(idJogador == this.jogador1.getIdJogador())
			return jogador1;
		else if(idJogador == this.jogador2.getIdJogador())
			return jogador2;
		else
			return null;
	}
	
	public int getIdOponente(int idJogador) {
		if(idJogador == this.jogador1.getIdJogador())
			return this.jogador2.getIdJogador();
		else
			return jogador1.getIdJogador();
	}
	
	/*
	 * Retorna o oponente de um jogador qualquer.
	 * 
	 * @param idJogador		identificador do jogador ao qual se deseja conhecer o oponente
	 * 
	 * @return				se o idJogador for válidoretorna um player (objeto Jogador do tipo 
	 * 						jogador) , caso contrário é retornado null.
	 * 
	 */
	public Jogador getOponente(int idJogador) {
		if(idJogador == this.jogador1.getIdJogador())
			return this.jogador2;
		else if(idJogador == this.jogador2.getIdJogador())
			return jogador1;
		else
			return null;
	}

	/* 
	 * Configura jogador 1 da partida.
	 * 
	 * @param idJogador		identificador do jogador
	 * @param nome			nome do jogador
	 * 
	 */
	public void setJogador1(int idJogador, String nome) {
		this.jogador1.setIdJogador(idJogador);
		this.jogador1.setNome(nome);
		this.jogador1.setTipoPecas(TipoPecas.Claras);
		this.jogadorDaVez = this.jogador1;
		this.estadoPartida = EstadoPartida.PartidaAguardandoOponente;
	}
	
	/*
	 * Configura jogador 2 da partida.
	 * 
	 * @param idJogador		identificador do jogador
	 * @param nome			nome do jogador
	 * 
	 */
	public void setJogador2(int idJogador, String nome) {
		this.jogador2.setIdJogador(idJogador);
		this.jogador2.setNome(nome);
		this.jogador2.setTipoPecas(TipoPecas.Escuras);
		this.estadoPartida = EstadoPartida.PartidaEmJogo;
	}

	public int getIdJogador1() {
		return jogador1.getIdJogador();
	}
    
	public int getIdJogador2() {
		return jogador2.getIdJogador();
	}
	
	/*
	 * 
	 * Consulta se é a vez do jogador em questão. Note que o retorno também pode
	 * informar o estado da partida em relação a não inicialização, erros, vitória,
	 * derrota e empate.
	 * 
	 * @param id identificador do usuário
	 * @return função retorna:
	 * 			-2 se ainda não há dois jogadores na partida
	 * 			-1 quando houve algum erro
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
	public int ehMinhaVez(int idJogador) {
		// Verifica se é um id válido
		if(idJogador != this.jogador1.getIdJogador() && idJogador != this.jogador2.getIdJogador()) {
			return -1;
		}
		// verifica se partida já começou
		if(estadoPartida == EstadoPartida.PartidaAguardandoOponente) { // ainda não há dois jogadores na partida
			return -2;
		}
		
		if(win(idJogador)) // vitória
			return 2;
		if(win(getIdOponente(idJogador))) // derrota
			return 3;
		if(tie()) // empate
			return 4;
		
		// vitória por WO - oponente desistiu (não está mais ativo)
		if(this.getOponente(idJogador).getEstado() == EstadoJogador.Desistiu)
			return 5;
		
		// se chegou aqui sem resultado, retorna se é ou não minha vez 
		if(this.jogadorDaVez.getIdJogador() != idJogador) // é minha vez ou não
			return 0;
		else
			return 1;
		
	}
	
	/*
	 * 
	 * Realiza o movimento de uma peça no tabuleiro. Jogadas são obrigatórias, 
	 * um jogador não pode deixar de mover uma peca no seu turno.
	 * 
	 * @param idJogador		identificador do jogador que deseja realizar o movimento
	 * @param lin			posição da linha atual onde se localiza a peça
	 * @param col			posição da coluna atual onde se localiza a peça
	 * @param direcao		direção a qual se deseja movimentar a peça
	 * 
	 * @return função retorna: 
	 * 			
	 * 		0 para movimento inválido - por exemplo, movimento em uma direção e sentido
	 * 			que que resulta em uma posição ocupada ou fora do tabuleiro. Também 
	 * 			retornará 0 quando o jogador tent mover uma peça que não é sua.
	 * 		1 se tudo estiver certo
	 * 			
	 * 		-1 para jogador não encontrado
	 * 		-2 partida não iniciada: ainda não há dois jogadores registrados na partida
	 * 		-3 quando parâmetros de posição e orientação foram inválidos
	 * 		-4 não é a vez do jogador.
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
	 * */
	public int movePeca(int idJogador, int lin, int col, Direcao direcao) {
		
		int dir = direcao.ordinal();
		int pos = (lin * 5) + col;
		char peca = this.tabuleiro.getPeca(pos);
		
		// Regra: Jogador deve estar no jogo
		if(this.jogador1.getIdJogador() != idJogador && this.jogador2.getIdJogador() != idJogador) {
			return -1; // Violação, erro código -1
		// Regra: sala ainda não possui dois jogadores
		}else if(this.estadoPartida == EstadoPartida.PartidaAguardandoOponente) {
			return -2; // Violação, erro código -2
		// Regra: um jogador só pode mover as suas peças.
		}else if(!ehMinhaPeca(idJogador, peca)) {
			return 0; // Violação, erro código 0
		// Regra: Validade dos parâmetros de posição e orientação. 
		}else if((dir < 0 || dir > 7) || (lin < 0 || lin > 4) || (col < 0 || col > 4)) {
			return -3; // Violação, erro código -3;
		// Regra: É a vez do jogador em questão? 
		}else if(jogadorDaVez.getIdJogador() != idJogador) {
			return -4;
		}

		if(this.firstPlay) {
			if(peca == 's' || peca == 'S')
				this.firstPlay = false;
			else
				return 0;
		}
			
		if(this.tabuleiro.movePeca(pos, dir)) {	// se realiza movimento foi válido
			// passa a vez se a jogada foi válida
			if(idJogador == this.jogador1.getIdJogador())
				this.jogadorDaVez = this.jogador2;
			else
				this.jogadorDaVez = this.jogador1;
			this.temporizador = 0;
			return 1;
		}
		return 0;
	}
	
	/*
	 * Encerra a partida atual. A partida é dada como encerrada e o jogador que 
	 * desistiu é dado como desistente.  
	 * 
	 * @param idJogador		identificador do jogador que deseja encerrar a partida
	 * @return 				retorna 0 se partida for fechada com sucesso e -1 para erro.
	 * 
	 */
	public int encerraPartida(int idJogador) {
		this.estadoPartida = EstadoPartida.PartidaEncerrada;
		Jogador jogador = this.getJogadorPorId(idJogador);
		if(jogador != null) {
			jogador.setEstado(EstadoJogador.Desistiu);
			return 0;
		}
		return -1;
	}
	
	public void encerraPartida() {
		this.estadoPartida = EstadoPartida.PartidaEncerrada;
	}
	
	/*
     * Atualiza temporizadores e a variável que denota o estado da partida. 
     * 
     * As restrições temporáis para modificação da variável de estado são as seguintes:
	 * 		2 minutos (120 segundos) pelo registro do segundo jogador; 
	 * 		60 segundos pelas jogadas de cada jogador; 
	 * 		60 segundos para “destruir” a partida depois de definido o vencedor.	 
     */
    public void atualizaRestricoesTemporais(int time) {
    	if(this.estadoPartida == EstadoPartida.PartidaAguardandoOponente) {
    		this.temporizador++; // tempo entre inicialização e entrada do segundo jogador
    		//System.out.println("temporizador de partida: "+ this.temporizador);
    		if(temporizador == 120) { // mudei 120 para 6 segundos (para testes)
    			System.out.println("t="+time+" Estado partida Aguardando -> AguardandoDestruicao");
    			this.estadoPartida = EstadoPartida.AguardandoDestruicao;
    			this.temporizador = 0;
    		}
    	}else if(this.estadoPartida == EstadoPartida.PartidaEmJogo){
    		this.temporizador++; // tempo desde a última jogada
    		//System.out.println("temporizador de partida: "+ this.temporizador);
    		if(this.temporizador == 60) { // mudei de 60 para 3 (para testes)
    			System.out.println("t="+time+" Estado partida EmJogo -> Encerrada");
    			this.estadoPartida = EstadoPartida.PartidaEncerravel;
    			this.temporizador = 0;
    			// Muda estado do jogador atual
    			this.jogadorDaVez.setEstado(EstadoJogador.Desistiu);
    		}
    	}else if(this.estadoPartida == EstadoPartida.PartidaEncerrada){
    		this.temporizador++; // tempo desde o encerramento
    		if(this.temporizador == 60) { // mudei de 60 para 3 segundos (para testes)
    			System.out.println("t="+time+" Estado partida Encerrada -> AguardandoDestruicao	");
    			this.estadoPartida = EstadoPartida.AguardandoDestruicao;
    			this.temporizador = 0;
    		}
    	}else if(this.estadoPartida == EstadoPartida.PartidaVazia){
    		//System.out.println("Estado da partida é vazia!");
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

    /*
     * Verifica se a partida é encerrável.
     * 
     * @return 		true se a partida é encerrável, caso contrário false
     * 
     */
	public boolean ehEncerravel() {
		
		if(estadoPartida == EstadoPartida.PartidaEncerrada ||
				estadoPartida == EstadoPartida.AguardandoDestruicao)
			return false;
		
		if(EstadoPartida.PartidaEncerravel == estadoPartida 
				|| win(jogador1.getIdJogador()) || win(jogador2.getIdJogador()) ||
				jogador1.getEstado() == EstadoJogador.Desistiu || 
				jogador2.getEstado() == EstadoJogador.Desistiu) {
			return true;
		}	
		return false;
	}

	/*
	 * Verifica se a partida está vazia (sem nenhum jogador).
	 * 
	 * @return 		true se estiver vazia, false caso contrário.
	 */
	public boolean ehPartidaVazia() {
		return this.estadoPartida == EstadoPartida.PartidaVazia;
	}
	
	/*
	 * Verifica se a partida está em jogo (dois jogadores ativos na disputa).
	 * 
	 * @return 		true se estiver em jogo, false caso contrário.
	 */
	public boolean ehPartidaEmJogo() {
		return this.estadoPartida == EstadoPartida.PartidaEmJogo;
	}
	
	/*
	 * Verifica se a partida está aguardando jogador (existe apenas um jogador na partida, que
	 * está aguardando um oponente).
	 * 
	 * @return 		true se estiver em aguardo, false caso contrário.
	 */
	public boolean ehPartidaAguardandoJogador() {
		return this.estadoPartida == EstadoPartida.PartidaAguardandoOponente;
	}
	
	/*
	 * Verifica se a partida está encerrada. Uma partida pode ser encerrada por um jogador, restrições temporais, 
	 * ou por vitória de um jogador.
	 * 
	 * @return 		true se estiver encerrada, false caso contrário. 
	 */
	public boolean ehPartidaEncerrada() {
		return this.estadoPartida == EstadoPartida.PartidaEncerrada;
	}
    
	
}

package kingsvalley.jws;

import javax.jws.WebService;

/**
 *
 * @author Jovani Brasil 
 * @email jovanibrasil@gmail.com
 * 
 */
@WebService
public interface KingsValleyInterface {

	/*
     * 	Registra o jogador em uma partida.
     * 
     *  @param nome				String com o nome do usuário/jogador.
	 *	@return 				Identificação do usuário (que corresponde a um número de identificação 
	 * 							único para este usuário durante uma partida), -1 se este usuário já está 
	 * 							cadastradoou -2 se o número máximo de jogadores tiver sido atingido.
     */
    public int registraJogador(String nome);
    
    /*
     *	Realiza o encerramento de uma partida.
     * 
     *	@param idJogador 		Identificação do usuário.
     *	@return 				Retorna -1 se houve erro e 0 se tudo ok.
     */
    public int encerraPartida(int idJogador);
    
    /*
     *	Verifica se existe partida.
     * 
     *	@param idJogador	 	Identificação do usuário.
     *	@return 				Retorna o estado da partida, sendo que os possíveis são: -2 (tempo de espera esgotado)
     * 							-1 (erro), 0 (ainda não há partida), 1 (sim, há partida e o jogador inicia jogando) 
     *       					2 (sim, há partida e o jogador é o segundo a jogar).
     * */
    public int temPartida(int idJogador);
    
    /*
     *	Retorna o nome do oponente de um jogador.
     * 
     *	@param idJogador 		Identificação do usuário.
     *	@return 				String vazio para erro ou string com o nome do oponente.
     * 
     * */
    public String obtemOponente(int idJogador);
    
    /*
     *	Verifica se é a vez do jogador. Note que o retorno detalha situação do estado da partida.
     * 
     *	@param idJogador 		Identificação do usuário
     *	@return 				Retorna -2 (erro: ainda não há 2 jogadores registrados na partida), -1 (erro), 0 (não),
     *							1 (sim), 2 (é o vencedor), 3 (é o perdedor), 4 (houve empate), 5 (vencedor por WO), 
     *							6 (perdedor por WO).
     *  
     * */
    public int ehMinhaVez(int idJogador);
    
    /*
     *	Retorna tabuleiro da partida atual do usuário.
     * 
     *	@param idJogador 		Identificação do usuário.
     *	@return 				String vazio em caso de erro ou string representando o tabuleiro de jogo
     * 
     */
    public String obtemTabuleiro(int idJogador);
    
    /*
     * 	Realiza movimento de uma peça no tabuleiro.
     * 	
     *	@param 	idJogador		Identificação do usuário.
     *	@param 	lin				Número da linha do tabuleiro onde se encontra a peça que se deseja mover (de 0 até 4, inclusive).
     *	@param	col				Número da coluna do tabuleiro onde se encontra a peça que se deseja mover (de 0 até 4, inclusive)
     *	@param	dir				Sentido do deslocamento (0 a 7, inclusive):
     *								0 - direita
     *								1 - diagonal direita inferior
	 *								2 - para baixo
	 *								3 - diagonal esquerda inferior
	 *								4 - esquerda
	 *								5 - diagonal esquerda superior
	 *								6 - para cima
	 *								7 - diagonal direita superior 
     * 	@return					Retorna: 2 (partida encerrada, o que ocorrerá caso o jogador demore muito para enviar a sua
	 *							jogada e ocorra o time-out de 60 segundos para envio de jogadas), 1 (tudo certo), 0 (movimento
	 *							inválido, por exemplo, em um sentido e deslocamento que resulta em uma posição ocupada ou
	 *							fora do tabuleiro), -1 (jogador não encontrado), -2 (partida não iniciada: ainda não há dois
	 *							jogadores registrados na partida), -3 (parâmetros de posição e orientação inválidos), -4 (não é a
	 *							vez do jogador).
     */
    public int movePeca(int playerId, int lin, int col, int dir);
    
}

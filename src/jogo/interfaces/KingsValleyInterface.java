package jogo.interfaces;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Jovani Brasil 
 * @email jovanibrasil@gmail.com
 * 
 */
public interface KingsValleyInterface extends Remote {

	/*
     * 	Registra o jogador em uma partida.
     * 
     *  @param nome  string com o nome do usuário/jogador
	 *	@return (valor inteiro) do usuário (número de identificação único), -1
	 *	se o usuário já está cadastrado e -2 se o número máximo de jogadores
	 *	se tiver sido atingido. Retorna -3 se houver erro.
     * 
     */
    public int registraJogador(String name) throws RemoteException;
    
    /*
     * 
     * @param id identificação do usuário
     * @return -1 (erro) e 0 (ok)
     */
    public int encerraPartida(int playerId) throws RemoteException;
    
    /*
     * 
     * @param id identificação do usuário
     * @return os retornos possíveis são:
     * 		-2 (tempo de espera esgotado)
     * 		-1 (erro), 
     * 		 0 (ainda não há partida)
     * 		 1 (sim, há partida e o jogador inicia jogando) 
     *       2 (sim, há partida e o jogador é o segundo a jogar.)
     * */
    public int temPartida(int playerId) throws RemoteException;
    
    /*
     * 
     * 
     * @param id identificação do usuário
     * @return string vazio para erro ou string com o nome do oponente.
     * 
     * */
    public String obtemOponente(int playerId) throws RemoteException;
    
    /*
     * 
     * 
     * @param id identificação do usuário
     * @return -1 (erro)
     *  
     * */
    public int ehMinhaVez(int playerId) throws RemoteException;
    
    /*
     * Retorna tabuleiro da partida atual do usuário.
     * 
     */
    public String obtemTabuleiro(int playerId) throws RemoteException;
    
    /*
     * 
     * 
     */
    public int movePeca(int playerId, int lin, int col, int dir) throws RemoteException;
    
}

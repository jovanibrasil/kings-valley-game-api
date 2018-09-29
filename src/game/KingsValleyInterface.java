package game;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Jovani Brasil 
 * @email jovanibrasil@gmail.com
 * 
 */
public interface KingsValleyInterface extends Remote {

    public int registraJogador(String name) throws RemoteException;
    public int encerraPartida(int playerId) throws RemoteException;
    public int temPartida(int playerId) throws RemoteException; 
    public String obtemOponente(int playerId) throws RemoteException;
    public int ehMinhaVez(int playerId) throws RemoteException;
    public String obtemTabuleiro(int playerId) throws RemoteException;
    public int movePeca(int playerId, int lin, int col, int dir) throws RemoteException;

    // Chamada extra utilizada para realização de testes.
    public void restartServer() throws RemoteException;
    
}

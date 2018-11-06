package kingsvalley.rmi;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import kingsvalley.game.KingsValleyServerOperations;

/**
 *
 * @author Jovani Brasil
 * @email jovanibrasil@gmail.com
 *  
 */

public class KingsValleyRMIServerImpl extends UnicastRemoteObject implements KingsValleyInterface {

	private static final long serialVersionUID = 4193276219231176546L;
	private KingsValleyServerOperations gameServer;
		
    public KingsValleyRMIServerImpl(int totalPartidas) throws RemoteException {
    	this.gameServer = new KingsValleyServerOperations(totalPartidas);
    }
    
    @Override 
    public int registraJogador(String nome) throws RemoteException {
    	return this.gameServer.registraJogador(nome);
    }

    @Override
    public int encerraPartida(int idJogador) throws RemoteException {
    	return this.gameServer.encerraPartida(idJogador);
    }

    @Override 
    public int temPartida(int idJogador) throws RemoteException {
    	return this.gameServer.temPartida(idJogador);
    }
   
    
    @Override 
    public String obtemOponente(int idJogador) throws RemoteException {
    	return this.gameServer.obtemOponente(idJogador);
    }

    
    @Override 
    public int ehMinhaVez(int idJogador) throws RemoteException {
    	return this.gameServer.ehMinhaVez(idJogador);
    }

    
    @Override 
    public String obtemTabuleiro(int idJogador) throws RemoteException {
    	return this.gameServer.obtemTabuleiro(idJogador);
    }

    @Override 
    public int movePeca(int idJogador, int lin, int col, int dir) throws RemoteException {
    	return this.gameServer.movePeca(idJogador, lin, col, dir);
    }
    
}

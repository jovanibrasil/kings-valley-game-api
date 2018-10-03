package jogo.servidor;
import java.rmi.Naming;
import java.rmi.RemoteException;

/**
*
* @author Jovani Brasil
* @email jovanibrasil@gmail.com
*  
*/

class KingsValleyServer {
	// Programa servidor para o exercício de fatorial
	public static void main (String[] argv) {
		try {
			// Cria e inicia um objeto remoto de registros na porta 1099
			// Se a porta for emitida, por default será utilizada a porta 1099.
			//java.rmi.registry.LocateRegistry.createRegistry(1099);
			java.rmi.registry.LocateRegistry.createRegistry(1099);
			System.out.println("RMI registry ready.");			
		} catch (RemoteException e) {
			System.out.println("RMI registry already running.");			
		}
		try {
			// Realiza associação entre nome e objeto
			// bind = lança exceção caso já haja uma associação de um objeto com o nome especificado
			// rebind = caso já haja associação, o novo objeto substitui o já associado
			//Naming.rebind ("Fatorial", new Fatorial());
			System.out.println("KingsValley server is ready.");
			Naming.rebind("kv", new KingsValleyImpl(100));
		} catch (Exception e) {
			System.out.println ("KingsValleyServer failed:");
			e.printStackTrace();
		}
	}
}


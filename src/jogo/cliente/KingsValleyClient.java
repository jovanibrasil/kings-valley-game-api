package jogo.cliente;
import java.rmi.Naming;
import java.rmi.RemoteException;

import jogo.interfaces.KingsValleyInterface;

/*
*
* Classe que implementa um cliente RMI para o KingsValley distribuído proposto. 
*
* Note que foram implementados métodos para realização das chamadas remotes, de forma
* que o cliente pode ser instanciado e as chamadas então devidamente realizadas.
*
* @author Jovani Brasil
* @email jovanibrasil@gmail.com
*  
*/

public class KingsValleyClient {
	
	public KingsValleyInterface game;
	
	public KingsValleyClient() {
		try {
			this.game = (KingsValleyInterface) Naming.lookup("//localhost/kv");
		} catch (Exception e) {
			System.out.println ("KingsValleyClient failed:");
			e.printStackTrace();
		}
	}
	
    public int registraJogador(String name) {
        // @param nome  string com o nome do usuário/jogador
		// @return (valor inteiro) do usuário (número de identificação único), -1
		// se o usuário já está cadastrado e -1 se o número máximo de jogadores
		// se tiver sido atingido.
    	try {
    		System.out.println("Registrando jogador ...");
			return this.game.registraJogador(name);
		} catch (RemoteException e) {
			System.out.println("Register Player failed");
			e.printStackTrace();
		}
		return -3;
    }

    public int encerraPartida(int playerId) {
    	// @param id identificação do usuário
    	// @return -1 (erro) e 0 (ok)
    	// busca partida que o jogador está jogando
    	// se não existe ou está inválida, retorna erro
    	//System.out.println();
    	//return 0;
    	try {
			return this.game.encerraPartida(playerId);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
    }

    /*
     * 
     * @param id identificação do usuário
     * @return os retornos possíveis são:
     * 		TODO -2 (tempo de espera executado)
     * 		-1 (erro), 
     * 		 0 (ainda não há partida)
     * 		 1 (sim, há partida e o jogador inicia jogando) 
     *       2 (sim, há partida e o jogador é o segundo a jogar.)
     * */
    public int temPartida(int playerId) {
        try {
			return this.game.temPartida(playerId);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
    }
   
    /*
     * @param id identificação do usuário
     * @return string vazio para erro ou string com o nome do oponente.
     * 
     * */
    public String obtemOponente(int playerId) {
    	try {
    		return this.game.obtemOponente(playerId);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
    }

    
    public String obtemTabuleiro(int playerId) {
    	try {
    		return this.game.obtemTabuleiro(playerId);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
    }
    
    /*
     * @param id identificação do usuário
     * @return
     *  
     * */
    public int ehMinhaVez(int playerId) {
        try {
			return this.game.ehMinhaVez(playerId);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
    }

    public int movePeca(int playerId, int lin, int col, int dir) {
    	try {
    		return this.game.movePeca(playerId, lin, col, dir);
    	} catch (Exception e) {
			e.printStackTrace();
    		return 0;
		}
    }
	
	
}


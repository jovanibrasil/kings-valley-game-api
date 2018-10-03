package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.rmi.RemoteException;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import game.KingsValleyImpl;

public class KingsValleyServerTests {

	private static KingsValleyImpl server;
	
	@Test
	void testTimeoutIniciacaoPartida() {
		try {
			System.out.println("\n\n-->testTimeoutIniciacaoPartida()");
			server = new KingsValleyImpl(10);
			Thread.sleep(1000);
			int idJovani = server.registraJogador("jovani");
			Thread.sleep(1000);
			// não tem partida ainda (ainda não existe oponente)
			Assert.assertEquals(-2, server.ehMinhaVez(idJovani)); 
			Thread.sleep(6000); 
			// houve timeout para registro de oponente e partida foi destruída
			Assert.assertEquals(-1, server.ehMinhaVez(idJovani)); 
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testTimeoutJogada() {
		try {
			System.out.println("\n\n-->testTimeoutJogada()");
			server = new KingsValleyImpl(10);
			Thread.sleep(1000);
			int idJovani1 = server.registraJogador("jovani1");
			server.registraJogador("jovani2");		
			server.movePeca(idJovani1, 0, 0, 0);
			Thread.sleep(4000);
			// Ganhou por wo
			Assert.assertEquals(5, server.ehMinhaVez(idJovani1)); 
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testDuasPartidasSimultaneas() {
		try {
			System.out.println("\n\n-->testDuasPartidasSimultaneas()");
			server = new KingsValleyImpl(10);
			//Thread.sleep(1000);
			int idJovani1 = server.registraJogador("jovani1");
			int idJovani2 = server.registraJogador("jovani2");		
			//Thread.sleep(1000);
			
			server.movePeca(idJovani1, 0, 0, 0); //direita
			server.movePeca(idJovani2, 2, 4, 4); // esquerda
			int idJovani3 = server.registraJogador("jovani3");		
			server.movePeca(idJovani1, 3, 0, 0); //direita
			int idJovani4 = server.registraJogador("jovani4");
			server.movePeca(idJovani3, 0, 0, 0); //direita
			server.movePeca(idJovani4, 2, 4, 4); // esquerda
			server.movePeca(idJovani2, 1, 4, 3); // diagonal esquerda inferior
			server.movePeca(idJovani3, 3, 0, 0); //direita
			server.movePeca(idJovani4, 1, 4, 3); // diagonal esquerda inferior
			server.movePeca(idJovani1, 0, 3, 2); // baixo
			server.movePeca(idJovani2, 2, 1, 0); //direita
			server.movePeca(idJovani3, 0, 3, 2); // baixo
			server.movePeca(idJovani4, 2, 1, 0); //direita
			
			//System.out.println(this.game.obtemTabuleiro());
			assertEquals(3, server.ehMinhaVez(idJovani1));
			assertEquals(2, server.ehMinhaVez(idJovani2));
			assertEquals(3, server.ehMinhaVez(idJovani3));
			assertEquals(2, server.ehMinhaVez(idJovani4));
			
			Thread.sleep(9000);
			
			idJovani3 = server.registraJogador("jovani3");
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	
	
}

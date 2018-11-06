package kingsvalley.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.rmi.RemoteException;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import kingsvalley.rmi.KingsValleyRMIServerImpl;

/**
*
* Classe de testes para a classe KingsValleyServer.
*
* @author Jovani Brasil
* @email jovanibrasil@gmail.com
*  
*/

public class KingsValleyServerRMITests {

	private static KingsValleyRMIServerImpl server;
	
	/*
	 * Registra 1 player. O id do player deve ser 0.
	 */
	@Test
	void testRegistraJogador() {
		System.out.println("\n\ntestRegistraJogador()");
		try {
			Thread.sleep(1000);
			server = new KingsValleyRMIServerImpl(10);
			Thread.sleep(1000);
			int id = server.registraJogador("Jovani1");
			Assert.assertEquals(0, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Registra 2 players em sequência. O id do player 1 deve ser 0 e do
	 * player 2 é 1.
	 */
	@Test
	void testRegistraJogadorOponente() {
		System.out.println("\n\ntestRegistraJogadorOponente()");
		try {
			Thread.sleep(1000);
			server = new KingsValleyRMIServerImpl(10);
			Thread.sleep(1000);
			int id1 = server.registraJogador("Jovani1");
			int id2 = server.registraJogador("Jovani2");
			Assert.assertEquals(0, id1);
			Assert.assertEquals(1, id2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 2 players estão registrados. Consulta quem é o oponente
	 * do player 2 (Jovani2), que é o player 1 (Jovani1).
	 */
	@Test
	void testObtemOponente() {
		try {
			System.out.println("\n\ntestRegistraOponenteInvalido()");
			Thread.sleep(1000);
			server = new KingsValleyRMIServerImpl(10);
			Thread.sleep(1000);
			int idJogador = server.registraJogador("Jovani1");
			server.registraJogador("Jovani2");
			String name = server.obtemOponente(idJogador);
			Assert.assertEquals("Jovani2", name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Consulta oponente de um player com id inválido. Um id inválido
	 * é um id negativo ou maior que a capacidade de registro de um 
	 * servidor. O resultado esperado é uma string vazia. 
	 */
	@Test
	void testObtemOponenteComIdJogadorInvalido() {
		try {
			System.out.println("\n\ntestObtemOponenteComIdJogadorInvalido");
			Thread.sleep(1000);
			server = new KingsValleyRMIServerImpl(10);
			Thread.sleep(1000);
			server.registraJogador("Jovani1");
			server.registraJogador("Jovani2");
			String name = server.obtemOponente(2000);
			Assert.assertEquals("", name);
			name = server.obtemOponente(-10);
			Assert.assertEquals("", name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Realiza o registro de quatro jogadores
	 */
	@Test
	void testRegistraQuatroJogadores() {
		try {
			System.out.println("\n\ntestRegistraQuatroJogadores()");
			Thread.sleep(1000);
			server = new KingsValleyRMIServerImpl(10);
			Thread.sleep(1000);
			int id1 = server.registraJogador("Jovani1");
			int id2 = server.registraJogador("Jovani2");
			int id3 = server.registraJogador("Jovani3");
			
			// Oponente de Jovani1 é Jovani2
			String name = server.obtemOponente(id1);
			Assert.assertEquals("Jovani2", name);
			name = server.obtemOponente(id2);
			Assert.assertEquals("Jovani1", name);
			// Jovani3 ainda não possui oponente
			name = server.obtemOponente(id3);
			Assert.assertEquals("", name);
			
			int id4 = server.registraJogador("Jovani4");
			name = server.obtemOponente(id3);
			Assert.assertEquals("Jovani4", name);
			name = server.obtemOponente(id4);
			Assert.assertEquals("Jovani3", name);	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Abando partida não iniciada ainda. O abando é
	 * realizado com sucesso (retorno 0)
	 */
	@Test
	void testAbandonaPartidaNaoIniciada() {
		try {
			System.out.println("\n\ntestAbandonaPartidaNaoIniciada()");
			Thread.sleep(1000);
			server = new KingsValleyRMIServerImpl(10);
			Thread.sleep(1000);
			int id1 = server.registraJogador("Jovani1");
			int ret = server.encerraPartida(id1);
			Assert.assertEquals(0, ret);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Testa situação em que um jogador se cadastra em uma partida e a 
	 * mesma sofre destruição por timeout, uma vez que nenhum oponente
	 * se registra.
	 * 
	 * Atenção: Necessário reduzir os tempos dos temporizadores na lógica do jogo 
	 * para poder testar.
	 * 
	 */
	@Test
	void testTimeoutIniciacaoPartida() {
		try {
			System.out.println("\n\ntestTimeoutIniciacaoPartida()");
			Thread.sleep(5000);
			server = new KingsValleyRMIServerImpl(10);
			//Thread.sleep(1000);
			int idJovani = server.registraJogador("jovani");
			//Thread.sleep(1000);
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
	
	/*
	 * Testa situação de timeout de jogada. O jogador que restou na partida
	 * é declaro vencedor por wo (walkover que significa "vitória fácil").
	 * 
	 * Atenção: Necessário reduzir os tempos dos temporizadores na lógica do jogo 
	 * para poder testar.
	 * 
	 */
	@Test
	void testTimeoutJogada() {
		try {
			Thread.sleep(1000);
			System.out.println("\n\n-->testTimeoutJogada()");
			server = new KingsValleyRMIServerImpl(10);
			Thread.sleep(1000);
			int idJovani1 = server.registraJogador("jovani1");
			server.registraJogador("jovani2");		
			server.movePeca(idJovani1, 0, 0, 0);
			Thread.sleep(4000);
			Assert.assertEquals(5, server.ehMinhaVez(idJovani1)); // ganhou por wo
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Não são aceitos nomes repetidos enquanto existem partidas ativas
	 * com o nome requisitado.
	 */
	@Test
	void nomesRepetidos() {
		try {
			Thread.sleep(1000);
			System.out.println("\n\n-->testTimeoutJogada()");
			server = new KingsValleyRMIServerImpl(10);
			Thread.sleep(1000);
			server.registraJogador("jovani1");
			server.registraJogador("jovani2");		
			Thread.sleep(8000);
			server.registraJogador("jovani2");		
			server.registraJogador("jovani2");		
			server.registraJogador("jovani2");		
			server.registraJogador("jovani2");		
			server.registraJogador("jovani1");		
			
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Testa situação de timeout de jogada. O jogador que restou na partida
	 * é declaro vencedor por wo (walkover que significa "vitória fácil").
	 */
	@Test
	void testServidorVazio() {
		try {
			System.out.println("\n\ntestServidorVazio()");
			KingsValleyRMIServerImpl server2 = new KingsValleyRMIServerImpl(10);
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Testa duas partidas simultâneas ocorrendo. Note que dois player devem
	 * acabar o processo como vencedores.
	 */
	@Test
	void testDuasPartidasSimultaneas() {
		try {
			System.out.println("\n\n-->testDuasPartidasSimultaneas()");
			server = new KingsValleyRMIServerImpl(10);
			Thread.sleep(1000);
			int idJovani1 = server.registraJogador("jovani1");
			int idJovani2 = server.registraJogador("jovani2");		
			
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
			
		
			assertEquals(3, server.ehMinhaVez(idJovani1));
			assertEquals(2, server.ehMinhaVez(idJovani2));
			assertEquals(3, server.ehMinhaVez(idJovani3));
			assertEquals(2, server.ehMinhaVez(idJovani4));
			
			//System.out.println(this.game.obtemTabuleiro());
			
			Thread.sleep(9000);
			idJovani3 = server.registraJogador("jovani3");
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
			
}

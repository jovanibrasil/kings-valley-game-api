package kingsvalley.tests;

import org.junit.jupiter.api.Test;
import org.junit.Assert;
import kingsvalley.game.KingsValleyServerOperations;

/**
*
* Classe de testes para a classe KingsValleyServer.
*
* @author Jovani Brasil
* @email jovanibrasil@gmail.com
*  
*/

public class KingsValleyServerOperationsTests {

	private static KingsValleyServerOperations server;
	
	@Test
	void testPreRegistro() {
		try {
			
			System.out.println("\n\n-->testPreRegistro()");
			server = new KingsValleyServerOperations(10);
			
			server.preRegistro("Jovani", 3, "Camila", 6);
			Assert.assertEquals(3, server.registraJogador("Jovani"));
			Assert.assertEquals(0, server.temPartida(3));
			Assert.assertEquals(1, server.ehMinhaVez(3));
			Assert.assertEquals(6, server.registraJogador("Camila"));
			Assert.assertEquals("Jovani", server.obtemOponente(6));
			Assert.assertEquals("Camila", server.obtemOponente(3));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
			
}

package tests;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import game.KingsValleyClient;

class KingsValleyClientTests {

	private static KingsValleyClient game;
	
	@BeforeAll
	public static void beforeAll(){
		game = new KingsValleyClient();
	}
	
	@BeforeEach
	void init() {
		game.restartServer();
	}
	
	/*
	 * Registra 1 player. O id do player deve ser 0.
	 */
	@Test
	void playerRegister() {
		int id = game.registraJogador("Jovani1");
		Assert.assertEquals(0, id);
	}
	
	/*
	 * Registra 2 players em sequência. O id do player 1 deve ser 0 e do
	 * player 2 é 1.
	 */
	@Test
	void registerPlayerOpponent() {
		int id1 = game.registraJogador("Jovani1");
		int id2 = game.registraJogador("Jovani2");
		Assert.assertEquals(0, id1);
		Assert.assertEquals(1, id2);
	}
	
	/*
	 * 2 players estão registrados. Consulta quem é o oponente
	 * do player 2 (Jovani2), que é o player 1 (Jovani1).
	 */
	@Test
	void getValidPlayerOpponent() {
		int id = game.registraJogador("Jovani1");
		game.registraJogador("Jovani2");
		String name = game.obtemOponente(id);
		Assert.assertEquals("Jovani2", name);
	}
	
	/*
	 * Consulta oponente de um player com id inválido. Um id inválido
	 * é um id negativo ou maior que a capacidade de registro de um 
	 * servidor. O resultado esperado é uma string vazia. 
	 */
	@Test
	void getOpponentFromInvalidPlayerId() {
		game.registraJogador("Jovani1");
		game.registraJogador("Jovani2");
		String name = game.obtemOponente(2000);
		Assert.assertEquals("", name);
		name = game.obtemOponente(-10);
		Assert.assertEquals("", name);
	}
	
	/*
	 * 
	 */
	@Test
	void registraQuatroPlayers() {
		int id1 = game.registraJogador("Jovani1");
		int id2 = game.registraJogador("Jovani2");
		int id3 = game.registraJogador("Jovani3");
		
		// Oponente de Jovani1 é Jovani2
		String name = game.obtemOponente(id1);
		Assert.assertEquals("Jovani2", name);
		name = game.obtemOponente(id2);
		Assert.assertEquals("Jovani1", name);
		// Jovani3 ainda não possui oponente
		name = game.obtemOponente(id3);
		Assert.assertEquals("", name);
		
		int id4 = game.registraJogador("Jovani4");
		name = game.obtemOponente(id3);
		Assert.assertEquals("Jovani4", name);
		name = game.obtemOponente(id4);
		Assert.assertEquals("Jovani3", name);	
	}
	
	/*
	 * 
	 */
	@Test
	void abandonaPartidaNaoIniciada() {
		int id1 = game.registraJogador("Jovani1");
		int ret = game.encerraPartida(id1);
		Assert.assertEquals(0, ret);
		// Oponente de Jovani1 é Jovani2
		//String name = game.obtemOponente(id1);
	}

}

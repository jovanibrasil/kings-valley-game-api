package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import enums.Direcao;
import game.KingsValleyGame;

class KingsValleyGameTests {

	private KingsValleyGame game;
	
	/**
	 *  @Test define a test case
	 *  @Before run before every test case
	 *  @After run after every test case
	 *  @BeforeClass run once, before all tests
	 *  @AfterClass run once, after all tests
	 **/
	
	@BeforeEach
	void init() {
		this.game = new KingsValleyGame();
	}
		
	@Test
	void testNewGame() {
		String result = "s - - - S \n" + 
				"s - - - S \n" + 
				"k - - - K \n" + 
				"s - - - S \n" + 
				"s - - - S \n\n";
		Assert.assertEquals(result, this.game.obtemTabuleiro());
	}
	
	/*
	 * Considerando que existem dois player na 
	 * partida, testa o retorno do oponente do player
	 * com id=2.
	 */
	@Test
	void getPlayerValidOpponent() {
		this.game.setPlayer1(2);
		this.game.setPlayer2(3);
		Assert.assertEquals(3, this.game.getOpponentId(2));
	}
	
	/*
	 * Se existe apenas um player na partida, então
	 * deve retornar -1 (oponente não encontrado)
	 * 
	 */
	@Test
	void getPlayerInvalidOpponent() {
		this.game.setPlayer1(2);
		Assert.assertEquals(-1, this.game.getOpponentId(2));
	}
	
	/*
	 * Se o do player não está disponível, deve 
	 * retornar -1 (player consultado não encontrado).
	 * Este caso cobre o caso de não existir player 
	 * na partida.
	 * 
	 */
	@Test
	void getPlayerInvalidOpponentFromEmptyGame() {
		Assert.assertEquals(-1, this.game.getOpponentId(2));
	}
	
	/*
	 * Considerando uma partida com dois player, se um deles pedir encerramento 
	 * deve-se retornar valor 0.
	 */
	@Test
	void validCloseGame() {
		this.game.setPlayer1(2);
		this.game.setPlayer2(3);
		assertEquals(0, this.game.encerraPartida(2));
	}
	
	/*
	 * Considerando uma partida com dois player, se um deles pedir encerramento 
	 * deve-se retornar valor 0.
	 */
	@Test
	void invalidPlayerCloseGame() {
		this.game.setPlayer1(2);
		this.game.setPlayer2(3);
		assertEquals(-1, this.game.encerraPartida(4)); // retorna -1, pois não existe player com id 4
	}

	
	@Test
	void validRightMove() {
		this.game.setPlayer1(2);
		this.game.setPlayer2(3);
		int id = this.game.ehMinhaVez(2); // deve retornar 1, dado que é o turno player 1
		//System.out.println(this.game.getBoard());
		assertEquals(1, id);
		int ret = this.game.movePeca(2, 0, 0, Direcao.Direita); // retorna 1, pq o movimento é válido
		assertEquals(1, ret); 
		//System.out.println();
		//System.out.println(this.game.getBoard());
	}
	
	@Test
	void moveInvalidPlayer() {
		this.game.setPlayer1(2);
		this.game.setPlayer2(3);
		// retorna -1, player inválido
		assertEquals(-1, this.game.movePeca(4, 0, 0, Direcao.Direita)); 
	}
	
	@Test
	void moveWithNotStartedGame() {
		this.game.setPlayer1(2);
		// retorna -2, pq partida ainda não foi iniciada
		assertEquals(-2, this.game.movePeca(2, 0, 0, Direcao.Direita)); 
	}
	
	@Test
	void isNotMyTurn1() {
		this.game.setPlayer1(2);
		this.game.setPlayer2(3);
		// retorna -4, pq não é a vez do player id=3
		assertEquals(-4, this.game.movePeca(3, 0, 4, Direcao.Direita)); 
	}
	
	@Test
	void isNotMyTurn2() {
		this.game.setPlayer1(2);
		this.game.setPlayer2(3);
		this.game.movePeca(2, 0, 0, Direcao.Direita);
		//System.out.println(this.game.getBoard());
		
		assertEquals(-4, this.game.movePeca(2, 0, 3, Direcao.Esquerda)); // retorna -4, não  pode jogar duas seguidas
	}
	
	@Test
	void isNotMyTurn3() {
		this.game.setPlayer1(2);
		this.game.setPlayer2(3);
		int status = this.game.movePeca(2, 0, 0, Direcao.Direita);
		assertEquals(1, status);
		status = this.game.movePeca(3, 4, 4, Direcao.Esquerda);
		assertEquals(1, status);
		assertEquals(-4, this.game.movePeca(3, 1, 4, Direcao.Esquerda)); // retorna -4, não  pode jogar duas seguidas
	}
	
	@Test
	void isNotMyPiece1() {
		this.game.setPlayer1(2);
		this.game.setPlayer2(3);
		int status = this.game.movePeca(2, 1, 4, Direcao.Esquerda);
		assertEquals(0, status);
	}
	
	@Test
	void isNotMyPiece2() {
		this.game.setPlayer1(2);
		this.game.setPlayer2(3);
		int status = this.game.movePeca(3, 0, 0, Direcao.Direita);
		assertEquals(0, status);
	}
	
	@Test
	void invalidEmptyPieceMove() {
		this.game.setPlayer1(1);
		this.game.setPlayer2(2);
		assertEquals(0, this.game.movePeca(1, 0, 1, Direcao.Direita)); // mover um espaço vazio, retorna 0
	}
	
	@Test
	void testHorizontalMove(){
		this.game.setPlayer1(1);
		this.game.setPlayer2(2);
		// movimentos válidos
		assertEquals(1, this.game.movePeca(1, 0, 0, Direcao.Direita)); // p1 move s(0,0) para direita
		assertEquals(1, this.game.movePeca(2, 1, 4, Direcao.Esquerda)); // p2 move s(1,5) para esquerda
		// movimentos inválidos - movimenta para posição já ocupada
		// p1 tenta mover para s(0,5) que está ocupado, então retorna 0
		assertEquals(0, this.game.movePeca(1, 0, 4, Direcao.Direita)); // move para a direita
		assertEquals(1, this.game.movePeca(1, 3, 0, Direcao.Direita)); // então faz um movimento válido
		//System.out.println(game.getBoard());
		assertEquals(0, this.game.movePeca(2, 1, 1, Direcao.Esquerda)); // move para esquerda
		//System.out.println(game.getBoard());
		// movimentos inválidos - movimenta para fora do tabuleiro
		assertEquals(0, this.game.movePeca(1, 3, 0, Direcao.Esquerda));  
		assertEquals(0, this.game.movePeca(2, 3, 4, Direcao.Direita));
		//System.out.println("Test horizontal move");
		//System.out.println(this.game.getBoard());
	}
	
	@Test
	void testVerticalMove() { 
		this.game.setPlayer1(1);
		this.game.setPlayer2(2);
		// player 1 tenta um movimento inválido (cima)
		assertEquals(0, this.game.movePeca(1, 0, 0, Direcao.Cima)); 
		// não conseguindo, realiza um movimento válido (direta) 
		assertEquals(1, this.game.movePeca(1, 0, 0, Direcao.Direita));
		// player 2 também tenta movimento inválido e movimento válido
		assertEquals(0, this.game.movePeca(2, 4, 4, Direcao.Baixo)); 
		assertEquals(1, this.game.movePeca(2, 4, 4, Direcao.Esquerda));
		// movimentos válidos - movimenta para posições válidas
		assertEquals(1, this.game.movePeca(1, 0, 3, Direcao.Baixo)); // baixo  
		assertEquals(1, this.game.movePeca(2, 4, 1, Direcao.Cima)); // cima
		//System.out.println("Test vertical move");
		//System.out.println(this.game.getBoard());
	}
	
	@Test
	void testDiagonalMove() {
		this.game.setPlayer1(1);
		this.game.setPlayer2(2);
		
		assertEquals(1, this.game.movePeca(1, 0, 0, Direcao.DiagonalDireitaInferior)); 
		//System.out.println(this.game.getBoard());
		assertEquals(1, this.game.movePeca(2, 3, 4, Direcao.DiagonalEsquerdaSuperior));
		//System.out.println(this.game.getBoard());
		assertEquals(1, this.game.movePeca(1, 2, 0, Direcao.DiagonalDireitaSuperior)); 
		//System.out.println(this.game.getBoard());
		// falha, uma vez que o primeiro movimento colocou um soldado no caminho
		assertEquals(0, this.game.movePeca(2, 2, 4, Direcao.DiagonalEsquerdaInferior)); 
		//System.out.println(this.game.getBoard());
		assertEquals(0, this.game.movePeca(1, 0, 0, Direcao.DiagonalEsquerdaInferior));
		//System.out.println(this.game.getBoard());
		assertEquals(0, this.game.movePeca(2, 3, 3, Direcao.DiagonalDireitaInferior));
		//System.out.println(this.game.getBoard());
		assertEquals(0, this.game.movePeca(1, 0, 0, Direcao.DiagonalEsquerdaSuperior));
		//System.out.println(this.game.getBoard());
		assertEquals(0, this.game.movePeca(2, 3, 3, Direcao.DiagonalEsquerdaInferior));
		//System.out.println(this.game.getBoard());
		assertEquals(0, this.game.movePeca(1, 0, 0, Direcao.DiagonalDireitaSuperior));
		//System.out.println(this.game.getBoard());
		assertEquals(0, this.game.movePeca(2, 3, 3, Direcao.DiagonalDireitaSuperior)); 
		//System.out.println(this.game.getBoard());
	}
	
//	0 - direita
//	 *		1 - diagonal direita inferior
//	 *		3 - diagonal esquerda inferior
//	 *		5 - diagonal esquerda superior
//	 *		7 - diagonal direita superior
//	
	@Test
	void firstIsTheSoldier() {
		this.game.setPlayer1(1);
		this.game.setPlayer2(2);
		assertEquals(0, this.game.movePeca(1, 2, 0, Direcao.Direita)); // primeiro movimento não pode ser o rei  
	}
	
	@Test
	void isntMyTurn() {
		this.game.setPlayer1(2);
		this.game.setPlayer2(3);
		int res = this.game.ehMinhaVez(3);
		assertEquals(0, res); // retorna zero, pq não é a vez do player 2
	}
	
	@Test
	void isMyTurn() {
		this.game.setPlayer1(2);
		this.game.setPlayer2(3);
		int res = this.game.ehMinhaVez(2);
		assertEquals(1, res); // retorna um, pq é a vez do player 1
	}
	
	@Test
	void insufficientPlayer1() {
		this.game.setPlayer1(2);
		int res = this.game.ehMinhaVez(2);
		assertEquals(-2, res); // retorna -2, pq ainda não existem dois jogadores na partida
	}

	@Test
	void insufficientPlayer2() {
		this.game.setPlayer2(2);
		int res = this.game.ehMinhaVez(2);
		assertEquals(-2, res); // retorna -2, pq ainda não existem dois jogadores na partida
	}
	
	@Test
	void isMyTurnInvalidPlayerId1() {
		this.game.setPlayer2(2);
		int res = this.game.ehMinhaVez(1);
		assertEquals(-1, res); // retorna -1, pq não existe player com id 1 na sala
	}
	
	@Test
	void isMyTurnInvalidPlayerId2() {
		this.game.setPlayer1(3);
		this.game.setPlayer2(2);
		int res = this.game.ehMinhaVez(1);
		assertEquals(-1, res); // retorna -1, pq não existe player com id 1 na sala
	}
	
/*
* 	
* 			 2 é vencedor
* 			 3 se é o perdedor
* 			 4 quando houver empate
* 			 5 vencedor por WO
* 			 6 perdedor por WO
*/
	
	
	
	
	
}



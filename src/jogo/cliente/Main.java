package jogo.cliente;

/**
*
* Classe Main para chamadas de teste do cliente.
*
* @author Jovani Brasil
* @email jovanibrasil@gmail.com
*  
*/
public class Main {

	
	/*
	 * Registra um jogador 
	 */
	public static void testRegistraJogador(KingsValleyClient cli) {
		System.out.println("\n\nTeste de Registro de Jogador.");
		try {
			Thread.sleep(1000);
			String retorno = cli.registraJogador("Jovani1");
			System.out.println(retorno);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Registra 2 players em sequência.
	 */
	public static void testRegistraJogadorOponente(KingsValleyClient cli1, KingsValleyClient cli2) {
		System.out.println("Testando registro de jogador oponente.");
		try {
			Thread.sleep(1000);
			String retorno1 = cli1.registraJogador("Jovani1");
			String retorno2 = cli2.registraJogador("Jovani2");
			System.out.println(retorno1);
			System.out.println(retorno2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Realiza o registro de 2 jogadores. Jogador 1 (Jovani1) consulta o seu oponente,
	 * que é o jogador 2 (Jovani2).
	 */
	public static void testObtemOponente(KingsValleyClient cli1, KingsValleyClient cli2) {
		try {
			System.out.println("Testando obtenção de oponente");
			Thread.sleep(1000);
			String retorno = cli1.registraJogador("Jovani1");
			System.out.println(retorno);
			retorno = cli2.registraJogador("Jovani2");
			System.out.println(retorno);
			retorno = cli1.obtemOponente();
			System.out.println(retorno);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Abando partida não iniciada ainda. O abando é
	 * realizado com sucesso (retorno 0)
	 */
	public static void testAbandonaPartidaNaoIniciada(KingsValleyClient cliente1) {
		try {
			System.out.println("Testando encerramento de partida.");
			Thread.sleep(1000);
			String retorno = cliente1.registraJogador("Jovani1");
			System.out.println(retorno);
			retorno = cliente1.encerraPartida();
			System.out.println(retorno);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Testa situação de timeout de jogada. O jogador que restou na partida
	 * é declaro vencedor por wo (walkover que significa "vitória fácil").
	 */
	public static void testTimeoutJogada(KingsValleyClient cliente1, KingsValleyClient cliente2) {
		try {
			Thread.sleep(1000);
			System.out.println("Testando o timeout de jogada.");
			String retorno = cliente1.registraJogador("jovani1");
			System.out.println(retorno);
			retorno = cliente2.registraJogador("jovani2");		
			System.out.println(retorno);
			retorno = cliente1.movePeca("0", "0","0");
			System.out.println(retorno);
			Thread.sleep(4000);
			retorno = cliente1.ehMinhaVez(); // ganhou por wo
			System.out.println(retorno);
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Testa situação em que um jogador se cadastra em uma partida e a 
	 * mesma sofre destruição por timeout, uma vez que nenhum oponente
	 * se registra.
	 */
	public static void testTimeoutIniciacaoPartida(KingsValleyClient cliente) {
		//try {
//			System.out.println("\n\ntestTimeoutIniciacaoPartida()");
//			Thread.sleep(5000);
//			int idJovani = cliente.registraJogador("jovani");
//			Thread.sleep(500);
//			// não tem partida ainda (ainda não existe oponente)
//			int ret = cliente.ehMinhaVez(idJovani); 
//			// retorno deve ser -2
//			System.out.println("Aguardando oponente ainda (cod="+ret+")");
//			Thread.sleep(6000); 
//			// houve timeout para registro de oponente e partida foi destruída
//			ret = cliente.ehMinhaVez(idJovani); 
//			// retorno deve ser -1
//			System.out.println("A partida que eu entrei não existe mais (cod="+ret+")");
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} 
	}
	
	/*
	 * Teste de duas partidas occorendo em simultâneo.
	 */
	public static void testPartidaComDoisClients(KingsValleyClient cliente1, KingsValleyClient cliente2){
//		try {
//		
//			System.out.println("Testando duas partidas simultâneas");
//			Thread.sleep(1000);
//			int idJovani1 = cliente1.registraJogador("jovani1");
//			int idJovani2 = cliente2.registraJogador("jovani2");		
//			
//			cliente1.movePeca(idJovani1, 0, 0, 0); //direita
//			cliente2.movePeca(idJovani2, 2, 4, 4); // esquerda		
//			cliente1.movePeca(idJovani1, 3, 0, 0); //direita
//			cliente2.movePeca(idJovani2, 1, 4, 3); // diagonal esquerda inferior
//			cliente1.movePeca(idJovani1, 0, 3, 2); // baixo
//			cliente2.movePeca(idJovani2, 2, 1, 0); //direita
//			
//			/// Ordem dos resultados = 32
//			//System.out.println(this.game.obtemTabuleiro());
//			int ret = cliente1.ehMinhaVez(idJovani1);
//			System.out.println("Status da partida: "+ret+" (2 é vitória) e 3 é derrota)");
//			ret = cliente1.ehMinhaVez(idJovani2);
//			System.out.println("Status da partida: "+ret+" (2 é vitória) e 3 é derrota)");
//			
//			System.out.println(cliente1.obtemTabuleiro(idJovani1));
//			
//			Thread.sleep(5000);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	/*
	 * Teste de duas partidas occorendo em simultâneo com quatro clientes
	 */
	public static void testPartidasSimultaneas2(KingsValleyClient cliente1, KingsValleyClient cliente2,
			KingsValleyClient cliente3, KingsValleyClient cliente4){
		try {
//		
//			System.out.println("Testando duas partidas simultâneas");
//			Thread.sleep(1000);
//			int idJovani1 = cliente1.registraJogador("jovani1");
//			int idJovani2 = cliente2.registraJogador("jovani2");		
//			
//			cliente1.movePeca(idJovani1, 0, 0, 0); //direita
//			cliente2.movePeca(idJovani2, 2, 4, 4); // esquerda
//			int idJovani3 = cliente3.registraJogador("jovani3");		
//			cliente1.movePeca(idJovani1, 3, 0, 0); //direita
//			int idJovani4 = cliente4.registraJogador("jovani4");
//			cliente3.movePeca(idJovani3, 0, 0, 0); //direita
//			cliente4.movePeca(idJovani4, 2, 4, 4); // esquerda
//			cliente2.movePeca(idJovani2, 1, 4, 3); // diagonal esquerda inferior
//			cliente3.movePeca(idJovani3, 3, 0, 0); //direita
//			cliente4.movePeca(idJovani4, 1, 4, 3); // diagonal esquerda inferior
//			cliente1.movePeca(idJovani1, 0, 3, 2); // baixo
//			cliente2.movePeca(idJovani2, 2, 1, 0); //direita
//			cliente3.movePeca(idJovani3, 0, 3, 2); // baixo
//			cliente4.movePeca(idJovani4, 2, 1, 0); //direita
//			
//			/// Ordem dos resultados = 3232
//			//System.out.println(this.game.obtemTabuleiro());
//			int ret = cliente1.ehMinhaVez(idJovani1);
//			System.out.println("Status da partida: "+ret+" (2 é vitória) e 3 é derrota)");
//			ret = cliente2.ehMinhaVez(idJovani2);
//			System.out.println("Status da partida: "+ret+" (2 é vitória) e 3 é derrota)");
//			ret = cliente3.ehMinhaVez(idJovani3);
//			System.out.println("Status da partida: "+ret+" (2 é vitória) e 3 é derrota)");
//			ret = cliente4.ehMinhaVez(idJovani4);
//			System.out.println("Status da partida: "+ret+" (2 é vitória) e 3 é derrota)");
//			
//			System.out.println(cliente1.obtemTabuleiro(idJovani1));
//			
//			Thread.sleep(5000);
//			idJovani3 = cliente1.registraJogador("jovani3");
//			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		KingsValleyClient cliente1 = new KingsValleyClient();
		KingsValleyClient cliente2 = new KingsValleyClient();
		KingsValleyClient cliente3 = new KingsValleyClient();
		KingsValleyClient cliente4 = new KingsValleyClient();
		
		// Registra um jogador
		//testRegistraJogador(cliente1);
		
		// Registra dois jogadores
		//testRegistraJogadorOponente(cliente1, cliente2);
		
		// Obtem oponente
		//testObtemOponente(cliente1, cliente2);
		
		// Abandona uma partida
		//testAbandonaPartidaNaoIniciada(cliente1);
		
		
		
		// Testa uma partida com dois clients
		//testPartidaComDoisClients(cliente1, cliente2);
		
		// Duas partidas simultâneas1 (com dois clientes)
		//testPartidasSimultaneas1(cliente1, cliente2);
		
		// Testa timeout de jogada
		//testTimeoutJogada(cliente1, cliente2);
		
		// Testa timeout da inicialização da partida.
		//testTimeoutIniciacaoPartida(cliente1);
				
		// Duas partidas simultâneas1 (com quatro clientes)
		//testPartidasSimultaneas2(cliente1, cliente2, cliente3, cliente4);
			
	
		
		
	}

}

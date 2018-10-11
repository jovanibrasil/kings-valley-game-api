package jogo.cliente;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Scanner;

import jogo.interfaces.KingsValleyInterface;

/*
*
* Classe que implementa um cliente RMI para o KingsValley distribuído. 
*
* Note que foram implementados métodos para realização das chamadas remotas, de forma
* que o cliente pode ser instanciado e as chamadas então devidamente realizadas.
*
* TODO validar nome como parâmrtro
* TODO validar se id != -1 em todos os métodos
*
* @author Jovani Brasil
* @email jovanibrasil@gmail.com
*  
*/

public class KingsValleyClient {
	
	public KingsValleyInterface game;
	
	private int idJogador = -1;
	
	public KingsValleyClient() {
		try {
			this.game = (KingsValleyInterface) Naming.lookup("//localhost/kv");
		} catch (Exception e) {
			System.out.println ("KingsValleyClient failed:");
			e.printStackTrace();
		}
	}
	
	public void start() {
		
		while(true) {
			
			String ajuda = "Comandos:\n"
					+ "0 - Entrar em uma partida [Nome do Jogador]"
					+ "1 - Encerrar partida atual"
					+ "2 - Verificar status da partida"
					+ "3 - Obter oponente"
					+ "4 - Verifica se é minha vez"
					+ "5 - Obtem tabuleiro"
					+ "6 - Movimenta peça [Linha Coluna Direção]";
			
			System.out.println(ajuda);
			
			if(this.idJogador >=0)
				System.out.println(this.obtemTabuleiro());
			
			// pega comando
			Scanner scanner = new Scanner(System.in);
			System.out.println("Entre com o seu commando: ");
			String[] cmd = scanner.next().split(" ");
			String mensagemResultado = "";
			
			// realiza parsing do comando e executa rotina no servidor
			switch (Integer.parseInt(cmd[0])) {
			case 0:
				mensagemResultado = this.registraJogador(cmd[1]);
				break;
			case 1:
				mensagemResultado = this.encerraPartida();
				break;
			case 2:
				mensagemResultado = this.temPartida();
				break;
			case 3:
				mensagemResultado = this.obtemTabuleiro();
				break;
			case 4:
				mensagemResultado = this.ehMinhaVez();
				break;
			case 5:
				mensagemResultado = this.obtemTabuleiro();
				break;
			case 6:
				mensagemResultado = this.movePeca(cmd[0], cmd[1], cmd[2]);
				break;
			default:
				System.out.println("Comando desconhecido");
				break;
			}
			// faz print do resultado
			System.out.println(mensagemResultado);
		}
	}
	
	/*
     * 	Registra o jogador em uma partida.
     * 
     *  @param nome				String com o nome do usuário/jogador.
	 *	@return 				Mensagem com o status da requisição realizada pelo jogador.
     */
    public String registraJogador(String name) {
        try {
    		int retorno = this.game.registraJogador(name);
			if(retorno == -1) {
				return "Nome de jogador já cadastrado.";
			}else if(retorno == -2) {
				return "Número máximo de usuários no servidor foi atingido.";
			}else {
				return "Você foi cadastrado com sucesso.";
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			return "Houve um erro ao registrar você no servidor.";
		}
    }

    /*
     *	Realiza o encerramento de uma partida.
     * 
     *	@param idJogador 		Identificação do usuário.
     *	@return 				Mensagem com o status da requisição realizada pelo jogador.
     */
    public String encerraPartida() {
    	try {
			int retorno = this.game.encerraPartida(this.idJogador);
			if(retorno == 0) {
				return "Sua partida foi encerrada com sucesso.";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return "Houve um ao encerrar a sua partida.";
    }

    /*
     *	Verifica se existe partida.
     * 
     *	@param idJogador	 	Identificação do usuário.
     *	@return 				Mensagem com o status da requisição realizada pelo jogador.
     *	
     * */
    public String temPartida() {
        try {
			int retorno = this.game.temPartida(this.idJogador);
			switch (retorno) {
				case -2:
					return "Tempo de espera esgotado.";
				case -1:
					return "Houve um erro ao verificar sua partida";
				case 0:
					return "Ainda não há partida.";
				case 1:
					return "Há partida e você começa jogando.";
				case 2:
					return "Há partida e você é o segundo a jogar.";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return "Houve um erro ao verificar sua partida";
    }
   
    /*
     *	Retorna o nome do oponente de um jogador.
     * 
     *	@param idJogador 		Identificação do usuário.
     *	@return 				Mensagem com o status da requisição realizada pelo jogador.
     * 
     * */
    public String obtemOponente() {
    	try {
    		String retorno = this.game.obtemOponente(this.idJogador);
    		if(retorno != "") {
    			return "Seu oponente se chama " + retorno;
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return "Houve um erro ao obter seu oponente.";
    }

    /*
     *	Retorna tabuleiro da partida atual do usuário.
     * 
     *	@param idJogador 		Identificação do usuário.
     *	@return 				String vazio em caso de erro ou string representando o tabuleiro de jogo
     * 
     */
    public String obtemTabuleiro() {
    	try {
    		String retorno = this.game.obtemTabuleiro(this.idJogador);
    		if(retorno != "") {
    			return retorno;
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return "Houve um erro ao busca o tabuleiro do seu jogo";
    }
    
    /*
     *	Verifica se é a vez do jogador. Note que o retorno detalha situação do estado da partida.
     * 
     *	@param idJogador 		Identificação do usuário
     *	@return 				Mensagem com o status da requisição realizada pelo jogador.
     * 
     * */
    public String ehMinhaVez() {
        try {
			int retorno = this.game.ehMinhaVez(this.idJogador);
			switch (retorno) {
				case -2:
					return "Ainda não há dois jogadores na partida.";
				case 0:
					return "Não é sua vez.";
				case 1:
					return "É a sua vez.";
				case 2:
					return "Você é o vencedor.";
				case 3:
					return "Você é o perdedor.";
				case 4:
					return "Houve empate.";
				case 5:
					return "Você venceu por WO.";
				case 6:
					return "Você perdeu por WO";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return "Houve um erro ao verifica a sua vez.";
    }

    /*
     * 	Realiza movimento de uma peça no tabuleiro.
     * 	
     *	@param 	idJogador		Identificação do usuário.
     *	@param 	lin				Número da linha do tabuleiro onde se encontra a peça que se deseja mover (de 0 até 4, inclusive).
     *	@param	col				Número da coluna do tabuleiro onde se encontra a peça que se deseja mover (de 0 até 4, inclusive)
     *	@param	dir				Sentido do deslocamento (0 a 7, inclusive):
     *								0 - direita
     *								1 - diagonal direita inferior
	 *								2 - para baixo
	 *								3 - diagonal esquerda inferior
	 *								4 - esquerda
	 *								5 - diagonal esquerda superior
	 *								6 - para cima
	 *								7 - diagonal direita superior 
     * @return 				Mensagem com o status da requisição realizada pelo jogador.
     * 
     */
    public String movePeca(String lin, String col, String dir) {
    	try {
    		int l = Integer.parseInt(lin);
    		int c = Integer.parseInt(col);
    		int d = Integer.parseInt(dir);
    		
    		int retorno = this.game.movePeca(this.idJogador, l, c, d);
    		
    		switch (retorno) {
				case -4:					
					return "Não é a sua vez.";
				case -3:					
					return "Parâmetros inválidos.";
				case -2:					
					return "Partida ainda não iniciada (apenas você está na partida).";
				case -1:					
					return "Jogador não encontrado.";
				case 0:					
					return "Movimento desejado é inválido.";
				case 1:					
					return "Movimento realizado com sucesso.";
				case 2:					
					return "Partida encerrada.";
			}
    		
    	} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			return "Os parâmetros passados são inválidos.";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Houve um erro na execução do movimento da peça.";
    }
	
}


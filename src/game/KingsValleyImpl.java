package game;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import enums.Direcao;

/**
 *
 * @author Jovani Brasil
 * @email jovanibrasil@gmail.com
 *  
 *  
 * Reflexões:
 * 
 *  TODO estrtura de dados, qual a diferença?
 *  TODO qual a melhor maneira de testar um sistema distribuído? 
 *  
 *  
 */
public class KingsValleyImpl extends UnicastRemoteObject implements KingsValleyInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4193276219231176546L;

	// Fila de tokens de partidas que estão aguardando para serem jogadas
	Queue<Integer> idsPartidasAguardando; // contém tokens de partidas
	
	// Fila de tokens das partidas que estão em jogo
	List<Integer> idsPartidasAtivas; // contém tokens de partidas
	
	// Lista de tokens  de ids já utilizados e disponíveis no momento.
	// Cada vez que um jogador é removido, o id vem pra cá.
    // TODO id do usuário = metade sequência e outra metade aleatório
	Queue<Integer> pidsDisponiveis;
	
	// Mapeamento nome-id (eficiente para consultar strings)
	private Map<String, Integer> mapaNomeId;
	private String[] vetorIdNome;
	
	// Vetor para armazenamento das partidas
	private KingsValleyGame[] partidas;
	// Vetorpara localizar usuários
	private int[] localizacaoUsuarios;
	
	private int maximoUsuarios; // Número máximo de usuários
	private int contadorId; // Contador para o Id

	private int numPart;
	
    public KingsValleyImpl(int numPart) throws RemoteException{
    	this.numPart = numPart;
    	setupServer();
    	// thread responsável por atualizar contadores  e atualizar estados das partidas
    	new Thread(new Runnable() {
    	     @Override
    	     public void run() {
    	          // code goes here.
    	    	 for (Integer idPartida : idsPartidasAguardando) {
					
    	    		 partidas[idPartida].incrementaTempo();
    	    		 
    	    		 //2 minutos (120 segundos) pelo registro do segundo jogador; 
    	    		 
    	    		 //60 segundos pelas jogadas de cada jogador; 
    	    		 
    	    		 //e 60 segundos para “destruir” a partida depois de definido o vencedor.
    	    		 
    	    	 } 
    	    	 
    	     }
    	}).start();
    }
    
    @Override 
    public void restartServer() throws RemoteException {
    	setupServer();
    }
    
    private void setupServer() {
    	this.idsPartidasAguardando = new LinkedList<>(); 
    	this.idsPartidasAtivas = new LinkedList<>();
        this.pidsDisponiveis = new LinkedList<>(); 
        this.localizacaoUsuarios= new int[numPart*2];
        this.vetorIdNome = new String[numPart*2];
        this.mapaNomeId = new HashMap<>();
        this.contadorId = 0;
        this.partidas = new  KingsValleyGame[numPart];
        for (int i = 0; i < numPart; i++) {
        	this.partidas[i] = new KingsValleyGame();
			this.idsPartidasAguardando.add(i);
        }
        this.maximoUsuarios = numPart * 2;
    }
    
    @Override
    public int registraJogador(String name) throws RemoteException {
        // @param nome  string com o nome do usuário/jogador
		// @return (valor inteiro) do usuário (número de identificação único), -1
		// se o usuário já está cadastrado e -1 se o número máximo de jogadores
		// se tiver sido atingido.
		if(this.mapaNomeId.containsKey(name)){
			// Nome já existe.
			return -1;
		}else if(this.mapaNomeId.size() == this.maximoUsuarios){
			// Número de jogadores foi excedido.
			return -2;
		}else{
			/* Atribuição do identificador */	
			int id = this.contadorId++;
			// TODO se contadorId > maximo usuários, então busca Id da lista
			this.mapaNomeId.put(name, id); // registra nome e id
			System.out.println("Registrando player "+id+" como "+name);
			vetorIdNome[id] = name;		
			
			/* Atruição do jogador a uma partida */
			int gameId = 0;
			if(idsPartidasAguardando.size() > 0) { // verifica se existe partida disponível		
				gameId = idsPartidasAguardando.peek();
				if(partidas[gameId].isEmptyGame()) { // se existe  partida vazia, set player 1
					System.out.println("Player "+id+" entrou na partida" + gameId + " (nova partida foi criada)");
					partidas[gameId].setPlayer1(id);		
				}else {	// se existe partida com um player, set player 2
					System.out.println("Player "+id+" entrou na partida" + gameId + " (partida estava em aguardo)");
					idsPartidasAguardando.poll(); // tira da fila de tokens de partidas
					partidas[gameId].setPlayer2(id);
					
					// TODO questão temporal
					// 2 minutos (120 segundos) pelo registro do segundo jogador; 
					
					
				}
			}
			localizacaoUsuarios[id] = gameId;
			
			return id;
		}
    
    }

    @Override
    public int encerraPartida(int playerId) throws RemoteException {
    	// @param id identificação do usuário
    	// @return -1 (erro) e 0 (ok)
    	// busca partida que o jogador está jogando
    	// se não existe ou está inválida, retorna erro
    	if(playerId >= numPart*2 && playerId <= 0) // playerId inválido
    		return -1;
    	
    	int gameId = localizacaoUsuarios[playerId];
    	if(gameId >= 0) {
    		//int oponentId = partidas[gameId].getOponente(id);
    		partidas[gameId].encerraPartida(playerId);
    		
    		// TODO se a partida foi concluída, então marca como concluída
    			// TODO tratamento temporal
    			
    			
    		
    	}else {
    		System.out.println("Ocorreu um erro, nenhum jogo foi encontrado!");
    		return -1;
    	}
    	return 0;
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
    @Override
    public int temPartida(int playerId) throws RemoteException {
        try {
        	if(playerId >= numPart*2 && playerId <= 0) // playerId inválido
            	return -1;
        	
	    	if(idsPartidasAguardando.size() > 0) { // então existe partida
	    
	    		int gameId = localizacaoUsuarios[playerId];
	        	if(gameId >= 0) {
	        		// TODO Estes dois testes consideram que uma partida ainda
	        		// não começo. O que retornar quando uma partida já começou?
	        		
	        		// (sim, há partida e o jogador inicia jogando)
	        		if(partidas[gameId].ehMinhaVez(playerId) == 1) // eu que jogo?
	        			return 1;
	        		//(sim, há partida e o jogador é o segundo a jogar.)
	        		else
	        			return 2;
	        	}else {
	        		return -1;
	        	}
	    	}else {
	    		return 0; // ainda não há partida
	    	}
	    	
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
    @Override
    public String obtemOponente(int playerId) throws RemoteException {
    	if(playerId >= numPart*2 && playerId <= 0) // playerId inválido
        	return "";
    	
    	int gameId = localizacaoUsuarios[playerId];
    	System.out.println("Player "+ playerId + " está está buscando oponente no game " + gameId);
    	if(gameId >= 0) {
    		int opponentId = partidas[gameId].getOpponentId(playerId); // obtem id do oponente
    		if(opponentId >= 0)
    			System.out.println(vetorIdNome[opponentId]);
    			return vetorIdNome[opponentId];	// obtem nome do oponente
    	}
    	return "";
    	
    }

    /*
     * @param id identificação do usuário
     * @return
     *  
     * */
    @Override
    public int ehMinhaVez(int playerId) throws RemoteException {
    	if(playerId >= numPart*2 && playerId <= 0) // playerId inválido
        	return -1;
    	
    	int gameId = localizacaoUsuarios[playerId];
    	if(gameId >= 0) {
    		//int oponentId = partidas[gameId].getOponente(id);
    		// TODO análise temporal
    		// // 60 segundos para “destruir” a partida depois de definido o vencedor.
			// partida é liberada
			// filaDePartidas recebe o id da partida livre
    		
    		return partidas[gameId].ehMinhaVez(playerId);
    	}else {
    		System.out.println("Ocorreu um erro, nenhum jogo foi encontrado!");
    		return -1;
    	}
    	
    }

    @Override
    public String obtemTabuleiro(int playerId) throws RemoteException {
    	if(playerId >= numPart*2 && playerId <= 0) // playerId inválido
        	return "";
    	
    	int gameId = localizacaoUsuarios[playerId];
    	if(gameId >= 0) {
    		return partidas[gameId].obtemTabuleiro();
    	}else {
    		System.out.println("Ocorreu um erro, nenhum jogo foi encontrado!");
    		return "";
    	}
    	
    }

    @Override
    public int movePeca(int playerId, int lin, int col, int dir) throws RemoteException {
    	if(playerId >= numPart*2 && playerId <= 0) // playerId inválido
    		return 0; 
    	
    	// TODO questão temporal
    		// 60 segundos pelas jogadas de cada jogador; 
    		// na verdade qualquer comando deve estar condicionado ao intervalo de tempo
    	
    	int gameId = localizacaoUsuarios[playerId];
    	if(gameId >= 0) {
    		return partidas[gameId].movePeca(playerId, lin, col, Direcao.getDirecao(dir));
    	}else {
    		System.out.println("Ocorreu um erro, nenhum jogo foi encontrado!");
    		return 0;
    	}
    	
    }
    
    
}

package game;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import enums.Direcao;
import enums.EstadoPartida;

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

	private KingsValleyGame[] partidas; // Vetor para armazenamento das partidas
	
	// Fila de tokens de partidas que estão aguardando para estarem ativas
	Queue<Integer> idsPartidasAguardando; // contém tokens de partidas
	// Fila de tokens das partidas que estão em jogo
	Queue<Integer> idsPartidasAtivas; // contém tokens de partidas
	
	// Lista de tokens  de ids já utilizados e disponíveis no momento.
	// Cada vez que um jogador é removido, o id vem pra cá.
    // TODO id do usuário = metade sequência e outra metade aleatório
	//Queue<Integer> pidsDisponiveis; // TODO por enquanto não existe exclusão de jogadores
	
	// Mapeamento nome-id (eficiente para consultar strings)
	private Map<String, Integer> mapaNomeId;
	private String[] vetorIdNome;
	
	// Vetor para localizar usuários, sendo que o índice é o id do usuário e 
	// conteúdo é o id da partida, dado que o valor -1 é atribuído quando o usuário
	// já teve sua última partida encerrada
	private int[] indiceUsuarioPartida; 
	
	private int maximoUsuarios; // Número máximo de usuários
	private int contadorId; // Contador para o Id
	private int numPart;
	
	
    public KingsValleyImpl(int numPart) throws RemoteException{
    	this.numPart = numPart;
    	inicializaServidor();
    	inicializaThreadDeVerificacao();
    }
    
    /*
     * 
     */
    private void inicializaServidor() {
    	this.idsPartidasAguardando = new LinkedList<>(); 
    	this.idsPartidasAtivas = new LinkedList<>();
        //this.pidsDisponiveis = new LinkedList<>(); 
        this.indiceUsuarioPartida= new int[numPart*2];
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
    
    /*
     *	Inicializa uma thread responsável por atualizar contadores e atualizar o estado de todas 
     *	as partidas do jogo. 
     *
     *	É importante observar que foi utilizado uma fila de índices de partidas ativas para iterar 
     *  o vetor de partida. A ideia é iterar menos partidas (somente ativas) de forma eficiente.
     *
     */
    private void inicializaThreadDeVerificacao() {
    	new Thread(new Runnable() {
    	     @Override
    	     public void run() {
    	    	 while(true) {
	    	    	 int iter = 0;
	    	    	 // atualiza dados das partidas ativas, ou seja, com dois players jogando 
	    	    	 while(iter < idsPartidasAtivas.size()) {
	    	    		 
	    	    		 int idPartida = idsPartidasAtivas.poll();
	    	    		 partidas[idPartida].atualizaRestricoesTemporais();
	    	    		 
	    	    		 if(partidas[idPartida].ehDestruivel()) {
	    	    			 System.out.println("A partida id= "+idPartida+" foi destruída.");
	    	    			 partidas[idPartida].limpaPartida(); // destrói dados da partida
	    	    			 idsPartidasAguardando.add(idPartida); // adiciona as partidas em aguardo
	    	    		 }else {
	    	    			 idsPartidasAtivas.add(idPartida);
	    	    		 }
	    	    		 
	    	    		 iter++;
	    	    	 }
	    	    	 
	    	    	 // atualiza partida que está esperando o segundo jogador
	    	    	 if(idsPartidasAguardando.size() > 0) {
		    	    	 int idPartida = idsPartidasAguardando.peek();
	    	    		 partidas[idPartida].atualizaRestricoesTemporais();
		    	    	 
		    	    	 if(partidas[idPartida].ehDestruivel()) {
	    	    			 System.out.println("A partida id= "+idPartida+" foi destruída.");
	    	    			 idsPartidasAguardando.poll();
	    	    			 partidas[idPartida].limpaPartida(); // destrói dados da partida
	    	    			 idsPartidasAguardando.add(idPartida); // adiciona as partidas em aguardo
	    	    		 }
	    	    	 }
	    	    	 
	    	    	 try {
	    	    		 Thread.sleep(1000);
	    	    	 } catch (InterruptedException e) {
	    	    		 e.printStackTrace();
	    	    	 }
	    	     }
    	     }
    	}).start();
	}

    /*
     *  @param nome  string com o nome do usuário/jogador
	 *	@return (valor inteiro) do usuário (número de identificação único), -1
	 *	se o usuário já está cadastrado e -2 se o número máximo de jogadores
	 *	se tiver sido atingido.
     * 
     */
    @Override public int registraJogador(String name) throws RemoteException {
       
		if(this.mapaNomeId.containsKey(name)){
			return -1; // Nome já existe.
		}else if(this.mapaNomeId.size() == this.maximoUsuarios){
			return -2; // Número de jogadores foi excedido.
		}else{
			/* Atribuição do identificador */	
			// TODO se contadorId > maximo usuários, então busca Id da lista de ids
			
			int id = this.contadorId++;
			this.mapaNomeId.put(name, id); // registra nome e id
			System.out.println("Registrando player "+id+" como "+name);
			vetorIdNome[id] = name;		
			
			/* Atruição do jogador a uma partida */
			int gameId = 0;
			if(idsPartidasAguardando.size() > 0) { // verifica se existe partida disponível		
				gameId = idsPartidasAguardando.peek();
				if(partidas[gameId].isEmptyGame()) { // se existe  partida vazia, set player 1
					System.out.println("Player "+id+" entrou na partida " + gameId + " (nova partida foi criada)");
					partidas[gameId].setJogador1(id);		
				}else {	// se existe partida com um player, set player 2
					System.out.println("Player "+id+" entrou na partida " + gameId + " (partida estava em aguardo)");
					idsPartidasAguardando.poll(); // tira da fila de tokens de partidas
					partidas[gameId].setJogador2(id);
				}
			}
			indiceUsuarioPartida[id] = gameId;
			
			return id;
		}
    
    }

    /*
     * 
     * @param id identificação do usuário
     * @return -1 (erro) e 0 (ok)
     */
    @Override public int encerraPartida(int playerId) throws RemoteException {
    	
    	if(playerId >= numPart*2 && playerId <= 0) // verifica se playerId é inválido
    		return -1;

    	int gameId = indiceUsuarioPartida[playerId]; // busca partida que o jogador está jogando
    	if(gameId >= 0) {
    		if(partidas[gameId].partidaEncerrada()) {
    			System.out.println("Ocorreu um erro, tentativa de fechamento duplicada!");
    			return -1; // erro, pois estou tentando encerrar duas vezes a partida
    		}
    		partidas[gameId].encerraPartida(playerId);
    		System.out.println("O player "+playerId+" encerrou a partida "+gameId);
    	}else {
    		// se não existe ou está inválida, retorna erro
    		System.out.println("Ocorreu um erro, nenhum jogo foi encontrado!");
    		return -1;
    	}
    	return 0;
    }

    /*
     * 
     * @param id identificação do usuário
     * @return os retornos possíveis são:
     * 		-2 (tempo de espera esgotado)
     * 		-1 (erro), 
     * 		 0 (ainda não há partida)
     * 		 1 (sim, há partida e o jogador inicia jogando) 
     *       2 (sim, há partida e o jogador é o segundo a jogar.)
     * */
    @Override public int temPartida(int playerId) throws RemoteException {
        try {
        	if(playerId >= numPart*2 && playerId <= 0) // verifica se playerId é inválido
            	return -1;
        	
    		int gameId = indiceUsuarioPartida[playerId];
        	if(gameId >= 0) {
        		if(partidas[gameId].aguardandoJogador()){
	        		return 0; // esperando por player	
        		}else if(partidas[gameId].emJogo()) {
	        		if(partidas[gameId].ehMinhaVez(playerId) == 1) // eu que jogo?
	        			return 1; // sim, há partida e o jogador inicia jogando
	        		else
	        			return 2; // sim, há partida e o jogador é o segundo a jogar.
	        	}else if(partidas[gameId].partidaEncerrada()) {
	        		return -2; // limite de tempo acabou mas partida ainda não foi deletada 
	        	}else {
	        		System.out.println("Aconteceu um erro na verificação de existência de partida");
	        		return -1;
	        	}
        	}else {
        		System.out.println("Aconteceu um erro na verificação de existência de partida");
        		return -1;
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
    @Override public String obtemOponente(int playerId) throws RemoteException {
    	
    	if(playerId >= numPart*2 && playerId <= 0) // verifica se playerId é inválido
        	return "";
    	
    	int gameId = indiceUsuarioPartida[playerId];
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
     * @return -1 (erro)
     *  
     * */
    @Override public int ehMinhaVez(int playerId) throws RemoteException {
    	
    	if(playerId >= numPart*2 && playerId <= 0) // verifica se playerId é inválido
        	return -1;
    	
    	int gameId = indiceUsuarioPartida[playerId];
    	if(gameId >= 0) {
    		return partidas[gameId].ehMinhaVez(playerId);
    	}else {
    		System.out.println("Ocorreu um erro, nenhum jogo foi encontrado!");
    		return -1;
    	}
    	
    }

    /*
     * Retorna tabuleiro da partida atual do usuário.
     * 
     */
    @Override public String obtemTabuleiro(int playerId) throws RemoteException {
    	
    	if(playerId >= numPart*2 && playerId <= 0) // verifica se playerId é inválido
        	return "";
    	
    	int gameId = indiceUsuarioPartida[playerId];
    	if(gameId >= 0) { // é id válido de uma partida
    		System.out.println("Obtendo tabuleiro da partida "+gameId+ " para o player "+playerId);
        	return partidas[gameId].obtemTabuleiro();
    	}else {
    		return "";
    	}
    	
    }

    /*
     * TODO Documentation
     * 
     */
    @Override public int movePeca(int playerId, int lin, int col, int dir) throws RemoteException {
    	
    	if(playerId >= numPart*2 && playerId <= 0) // testa se playerId é inválido
    		return 0; 
    	
    	int gameId = indiceUsuarioPartida[playerId];
    	if(gameId >= 0) { // é id válido de uma partida
    		if(partidas[gameId].partidaEncerrada())
    			return -2; // teve sua partida encerrada
    		return partidas[gameId].movePeca(playerId, lin, col, Direcao.getDirecao(dir));
    	}else {
    		System.out.println("Ocorreu um erro, nenhum jogo foi encontrado!");
    		return 0;
    	}
    	
    }
    
    /*
     * Método para reinicializar o servidor. Utilizado para propósitos de teste.
     */
	@Override public void restartServer() throws RemoteException {
    	inicializaServidor();
    }
    
}

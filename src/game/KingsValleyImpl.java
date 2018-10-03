package game;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import enums.Direcao;

/**
 *
 * @author Jovani Brasil
 * @email jovanibrasil@gmail.com
 *  
 *  TODO synchronized garante controle de concorrência entre execuções da mesma chamada (função)
 *  TODO controle de concorrẽncia entre diferentes chamadas é feito com semáforos
 *  
 *  TODO quão custoso é um mutex (semáforo) para cada partida?
 *  
 */
public class KingsValleyImpl extends UnicastRemoteObject implements KingsValleyInterface {

	private static final long serialVersionUID = 4193276219231176546L;

	private KingsValleyGame[] partidas; // Vetor para armazenamento das partidas
	
	// Fila de tokens de partidas que estão aguardando para estarem ativas
	Queue<Integer> idsPartidasDisponiveis; // contém tokens de partidas
	// Fila de tokens das partidas que estão em jogo
	Queue<Integer> idsPartidasAtivas; // contém tokens de partidas
	
	// Lista de tokens  de ids já utilizados e disponíveis no momento.
	// Cada vez que um jogador é removido, o volta pra cá.
    Queue<Integer> idsDisponiveis; 
	// Mapeamento nome-id (eficiente para consultar strings)
	private Map<String, Integer> mapaNomeIdAtivos;
	private String[] mapaIdNomeAtivos;
	
	// Vetor para localizar usuários, sendo que o índice é o id do usuário e 
	// conteúdo é o id da partida
	private int[] indiceUsuarioPartida; 
	private int totalPartidas;
	
	
	
    public KingsValleyImpl(int totalPartidas) throws RemoteException {
    	this.totalPartidas = totalPartidas;
    	inicializaServidor();
    	inicializaThreadDeVerificacao();
    }
    
    /*
     * 
     */
    private void inicializaServidor() {
    	this.idsPartidasDisponiveis = new LinkedList<>(); 
    	this.idsPartidasAtivas = new LinkedList<>();
        this.idsDisponiveis = new LinkedList<>(); 
        this.indiceUsuarioPartida= new int[totalPartidas*2];
        this.mapaIdNomeAtivos = new String[totalPartidas*2];
        this.mapaNomeIdAtivos = new HashMap<>();
        this.partidas = new  KingsValleyGame[totalPartidas];
        for (int i = 0, j=0; i < totalPartidas; i++, j+=2) {
        	this.partidas[i] = new KingsValleyGame();
        	this.idsPartidasDisponiveis.add(i);
			idsDisponiveis.add(j); idsDisponiveis.add(j+1); 
        }
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
    	    	 System.out.println("Thread de verificação rodando ...");
    	    	 while(true) {
	    	    	 int iter = 0;
	    	    	 // atualiza dados das partidas ativas, ou seja, com dois players jogando 
	    	    	 //System.out.println("Atualizando partidas ativas");
	    	    	 while(iter < idsPartidasAtivas.size()) {
	    	    		 System.out.println("Verificando partida ativa");
	    	    		 int idPartida = idsPartidasAtivas.poll();
	    	    		 
	    	    		 synchronized (partidas[idPartida]) {
	    	    			 KingsValleyGame partida = partidas[idPartida];
		    	    		 partida.atualizaRestricoesTemporais();
		    	    		 
		    	    		 if(partida.ehEncerravel()) {
		    	    			 partida.encerraPartida();
		    	    			 limpaMapeamentoDeNomes(idPartida); // reset dos nomes nas estruturas
		    	    		 } else if(partida.ehDestruivel()) {
		    	    			 System.out.println("A partida id= "+idPartida+
		    	    					 " foi destruída (" + idsPartidasAtivas.size() + 
		    	    					 " partidas ativas).");
		    	    			 
		    	    			 devolveIds(idPartida); // devolve ids usados pelos jogadores
		    	    			 partida.limpaPartida(); // destrói dados da partida
		    	    			 idsPartidasDisponiveis.add(idPartida); // adiciona as partidas em aguardo
		    	    			 
		    	    		 }else {
		    	    			 idsPartidasAtivas.add(idPartida);
		    	    		 }
	    	    		 }
	    	    		 iter++;
	    	    	 }
	    	    	 
	    	    	 // atualiza partida que está esperando o segundo jogador
	    	    	 if(idsPartidasDisponiveis.size() > 0) {
	    	    		 //System.out.println("Atualizando partida aguardadndo");
		    	    	 int idPartida = idsPartidasDisponiveis.peek();
	    	    		 
		    	    	 synchronized (partidas[idPartida]) {
			    	    	 KingsValleyGame partida = partidas[idPartida];
		    	    		 if(!partida.partidaVazia()) {
			    	    		 partida.atualizaRestricoesTemporais();
				   		    	 if(partida.ehDestruivel()) {
			    	    			 System.out.println("A partida id= "+idPartida+" foi destruída.");
			    	    			 idsPartidasDisponiveis.poll();
			    	    			 partida.limpaPartida(); // destrói dados da partida
			    	    			 limpaMapeamentoDeNomes(idPartida);
			    	    			 idsPartidasDisponiveis.add(idPartida); // adiciona as partidas em aguardo
			    	    		 }
		    	    		 }
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
     * 	Registra o jogador em uma partida.
     * 
     *  @param nome  string com o nome do usuário/jogador
	 *	@return (valor inteiro) do usuário (número de identificação único), -1
	 *	se o usuário já está cadastrado e -2 se o número máximo de jogadores
	 *	se tiver sido atingido.
     * 
     */
    @Override 
    public synchronized int registraJogador(String nome) throws RemoteException {
       
		if(this.mapaNomeIdAtivos.containsKey(nome)){
			return -1; // Nome já existe.
		}else if(idsPartidasDisponiveis.size() == 0){
			return -2; // Número de partidas/jogadores foi excedido.
		}else{
			// Atribuição do identificador
			int id = idsDisponiveis.poll();
			this.mapaNomeIdAtivos.put(nome, id); // registra nome e id
			System.out.println("Registrando player "+id+" como "+nome);
			mapaIdNomeAtivos[id] = nome;		
			
			// Atribuição do jogador a uma partida
			int idPartida = 0;
		
			idPartida = idsPartidasDisponiveis.peek();
			if(partidas[idPartida].isEmptyGame()) { // se existe  partida vazia, set player 1
				System.out.println("Player "+id+" entrou na partida " + idPartida + " (nova partida foi criada)");
				partidas[idPartida].setJogador1(id, nome);		
			}else {	// se existe partida com um player, set player 2
				System.out.println("Player "+id+" entrou na partida " + idPartida + " (partida estava em aguardo)");
				idsPartidasDisponiveis.poll(); // tira da fila de tokens de partidas
				partidas[idPartida].setJogador2(id, nome);
				idsPartidasAtivas.add(idPartida);
			}
			indiceUsuarioPartida[id] = idPartida;
			return id;
		}
    
    }

    /*
     * 
     * @param id identificação do usuário
     * @return -1 (erro) e 0 (ok)
     */
    @Override
    public synchronized int encerraPartida(int idJogador) throws RemoteException {
    	
    	if(idJogador >= totalPartidas*2 && idJogador <= 0) // verifica se playerId é inválido
    		return -1;

    	int idPartida = indiceUsuarioPartida[idJogador]; // busca partida que o jogador está jogando
    	if(idPartida >= 0) {
    		synchronized (partidas[idPartida]) {
    			KingsValleyGame partida = partidas[idPartida]; 
	    		if(partida.partidaEncerrada()) {
	    			System.out.println("Ocorreu um erro, tentativa de fechamento duplicada!");
	    			return -1; // retorna erro, pois estou tentando encerrar duas vezes a partida
	    		}
	    		partida.encerraPartida(idJogador);
	    		limpaMapeamentoDeNomes(idPartida);
	    		indiceUsuarioPartida[idJogador] = -1;
	    		System.out.println("O player "+idJogador+" encerrou a partida "+idPartida);
    		}
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
    @Override 
    public synchronized int temPartida(int idJogador) throws RemoteException {
        try {
        	if(idJogador >= totalPartidas*2 && idJogador <= 0) // verifica se playerId é inválido
            	return -1;
        	
    		int idPartida = indiceUsuarioPartida[idJogador];
        	if(idPartida >= 0) {
        		KingsValleyGame partida = partidas[idPartida];
        		if(partida.aguardandoJogador()){
	        		return 0; // esperando por player	
        		}else if(partida.emJogo()) {
	        		if(partida.ehMinhaVez(idJogador) == 1) // eu que jogo?
	        			return 1; // sim, há partida e o jogador inicia jogando
	        		else
	        			return 2; // sim, há partida e o jogador é o segundo a jogar.
	        	}else if(partida.partidaEncerrada()) {
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
    @Override 
    public String obtemOponente(int idJogador) throws RemoteException {
    	
    	if(idJogador >= totalPartidas*2 && idJogador <= 0) // verifica se playerId é inválido
        	return "";
    	
    	int idPartida = indiceUsuarioPartida[idJogador];
    	System.out.println("Player "+ idJogador + " está está buscando oponente no game " + idPartida);
    	if(idPartida >= 0) { 
    		synchronized (partidas[idPartida]) {
	    		Jogador oponente = partidas[idPartida].getOponente(idJogador);
	    		if(oponente != null) {
		    		String nome = oponente.getNome();
		    		return nome;
	    		}
			}
    	}
    	return "";
    	
    }

    /*
     * @param id identificação do usuário
     * @return -1 (erro)
     *  
     * */
    @Override 
    public int ehMinhaVez(int idJogador) throws RemoteException {
    	
    	if(idJogador >= totalPartidas*2 && idJogador <= 0) // verifica se playerId é inválido
        	return -1;
    	
    	int idPartida = indiceUsuarioPartida[idJogador];
    	if(idPartida >= 0) {
    		synchronized (partidas[idPartida]) {
				return partidas[idPartida].ehMinhaVez(idJogador);
			}
    	}
    	System.out.println("Ocorreu um erro, nenhum jogo foi encontrado para o jogador " + idJogador);
		return -1;
    }

    /*
     * Retorna tabuleiro da partida atual do usuário.
     * 
     */
    @Override 
    public String obtemTabuleiro(int idJogador) throws RemoteException {
    	
    	if(idJogador >= totalPartidas*2 && idJogador <= 0) // verifica se playerId é inválido
        	return "";
    	
    	int idPartida = indiceUsuarioPartida[idJogador];
    	if(idPartida >= 0) { // é id válido de uma partida
    		System.out.println("Obtendo tabuleiro da partida "+idPartida+ " para o player "+idJogador);
        	synchronized (partidas[idPartida]) {
				return partidas[idPartida].obtemTabuleiro();
        	}
    	}
    	return "";
    }

    /*
     * TODO Documentation
     * 
     */
    @Override 
    public int movePeca(int idJogador, int lin, int col, int dir) throws RemoteException {
    	
    	System.out.println("Jogador "+idJogador+ " deseja movimentar sua peça");
    	
    	if(idJogador >= totalPartidas*2 && idJogador <= 0) // testa se playerId é inválido
    		return 0; 
    	
    	int idPartida = indiceUsuarioPartida[idJogador];
    	if(idPartida >= 0) { // é id válido de uma partida
			synchronized (partidas[idPartida]) {
				KingsValleyGame partida = partidas[idPartida]; 
				if(partida.partidaEncerrada())
	    			return -2; // teve sua partida encerrada
	    		return partida.movePeca(idJogador, lin, col, Direcao.getDirecao(dir));
			}			
    	}
    	System.out.println("Ocorreu um erro, nenhum jogo foi encontrado!");
    	return 0;
    }
    
    private void limpaMapeamentoDeNomes(int idPartida) {
    	KingsValleyGame partida = partidas[idPartida];
    	int idJogador1 = partida.getIdJogador1();
		int idJogador2 = partida.getIdJogador2();
		if(idJogador1 >= 0) {
			mapaNomeIdAtivos.remove(mapaIdNomeAtivos[idJogador1]);
			mapaIdNomeAtivos[idJogador1] = "";
		}
		if(idJogador2 >= 0) {
			mapaNomeIdAtivos.remove(mapaIdNomeAtivos[idJogador2]);
			mapaIdNomeAtivos[idJogador2] = "";	
		}
	}
    
    private void devolveIds(int idPartida) {
    	KingsValleyGame partida = partidas[idPartida];
    	int idJogador1 = partida.getIdJogador1();
		int idJogador2 = partida.getIdJogador2();
		if(idJogador1 >= 0)
			idsDisponiveis.add(idJogador1);
		if(idJogador2 >= 0)
			idsDisponiveis.add(idJogador2);	
    }

    
    /*
     * Método para reinicializar o servidor. Utilizado para propósitos de teste a partir do cliente.
     */
	@Override 
	public synchronized void restartServer() throws RemoteException {
    	inicializaServidor();
    }
    
}

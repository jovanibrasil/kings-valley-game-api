package jogo.servidor;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Semaphore;

import enums.Direcao;
import jogo.interfaces.KingsValleyInterface;

/**
 *
 * @author Jovani Brasil
 * @email jovanibrasil@gmail.com
 *  
 */

public class KingsValleyImpl extends UnicastRemoteObject implements KingsValleyInterface {

	private static final long serialVersionUID = 4193276219231176546L;

	private KingsValleyGame[] partidas; // Vetor para armazenamento das partidas
	
	// Lista de tokens  de ids já utilizados e disponíveis no momento. Quando um jogador é removido, o volta pra cá.
	LinkedList<Integer> idsDisponiveis; 
	// Mapeamento nome-id para garantir usuários únicos (eficiente para consultar strings)
	private Map<String, Integer> mapaNomeIdAtivos;
	private String[] mapaIdNomeAtivos;
	
	// Vetor para localizar usuários, sendo que o índice é o id do usuário e 
	// conteúdo é o id da partida
	private int[] indiceUsuarioPartida; 
	private int totalPartidas;
	
	private Semaphore idsDisponiveisMutex, nomeJogadorMutex;
	private Semaphore[] indiceUsuarioPartidaMutexes;
	
    public KingsValleyImpl(int totalPartidas) throws RemoteException {
    	this.totalPartidas = totalPartidas;
    
        this.idsDisponiveis = new LinkedList<>(); 
        this.indiceUsuarioPartida= new int[totalPartidas*2];
        this.mapaIdNomeAtivos = new String[totalPartidas*2];
        this.mapaNomeIdAtivos = new HashMap<>();
        this.indiceUsuarioPartidaMutexes = new Semaphore[totalPartidas];
        this.partidas = new  KingsValleyGame[totalPartidas];
        for (int i = 0, j=0; i < totalPartidas; i++, j+=2) {
        	this.partidas[i] = new KingsValleyGame();
        	idsDisponiveis.add(j); idsDisponiveis.add(j+1);
			indiceUsuarioPartidaMutexes[i] = new Semaphore(1);
        }
        this.idsDisponiveisMutex = new Semaphore(1);
        this.nomeJogadorMutex = new Semaphore(1);
    
    	inicializaThreadDeVerificacao();
    }
    
    /*
     *	Inicializa uma thread responsável por atualizar contadores e atualizar o estado de todas 
     *	as partidas do jogo. 
     *
     *	É importante observar que foi utilizado uma lista de índices de partidas ativas (partidas com
     *	jogadores ativos e jogando) e outras  para iterar 
     *  o vetor de partida. A ideia é iterar menos partidas (somente ativas) de forma eficiente.
     * 
     *
     */
    private void inicializaThreadDeVerificacao() {
    	new Thread(new Runnable() {	
    		 @Override
    	     public void run() {
    	    	 //System.out.println("Thread de verificação rodando ...");
    			 int time = 0;
    			 while(true) {
    	    		
    	    		for (int idPartida = 0; idPartida < totalPartidas; idPartida++) {
						synchronized (partidas[idPartida]) {
							KingsValleyGame partida = partidas[idPartida];
							if(!partida.ehPartidaVazia()) {
								System.out.println("t="+time+" Atualizando a partida " + idPartida);
			    	    		partida.atualizaRestricoesTemporais(time);
			    	    		if(partida.ehEncerravel()) {
			    	    			 System.out.println("t="+time+" a partida tornou-se encerrável");
			    	    			 partida.encerraPartida();
			    	    			 limpaMapeamentoDeNomes(idPartida);
			    	    		}else if(partida.ehDestruivel()) {
			    	    			 System.out.println("t=" + time + " a partida id= "+idPartida+
			    	    					 " será destruída");
			    	    			 limpaMapeamentoDeNomes(idPartida);
			    	    			 devolveIdsJogadores(idPartida); // devolve ids usados pelos jogadores
			    	    			 partida.limpaPartida(); // destrói dados da partida
				    	    	}
							}
    	    			}
					}
    	    		time++;
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
     * Atualiza estrutura utilizada para agilizar processo de busca
     * de jogadores no servidor.
     * 
     */
    public void atualizaIndiceUsuarioPartida(int idJogador, int idPartida) { 
    	try {
			indiceUsuarioPartidaMutexes[idJogador].acquire();
			indiceUsuarioPartida[idJogador] = idPartida;
			indiceUsuarioPartidaMutexes[idJogador].release();
    	} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
		
    @Override 
    public int registraJogador(String nome) throws RemoteException {
    	if(this.mapaNomeIdAtivos.containsKey(nome)){
    		System.out.println("Tentativa de cadastro falhou");
			return -1; // Nome já existe.
		}else{
			// Atribuição do identificador
			try {
				idsDisponiveisMutex.acquire();
				nomeJogadorMutex.acquire();
				
				int idJogador = idsDisponiveis.poll();
				this.mapaNomeIdAtivos.put(nome, idJogador); // registra nome e id
				mapaIdNomeAtivos[idJogador] = nome;
				System.out.println("Registrado o nome "+nome+" para o usuário"+idJogador);
				nomeJogadorMutex.release(); 
				idsDisponiveisMutex.release();
				
				for (int idPartida = 0; idPartida < totalPartidas; idPartida++) {
					synchronized (partidas[idPartida]) {
						KingsValleyGame partida = partidas[idPartida];
						if(partida.ehPartidaAguardandoJogador()) {
							System.out.println("Player "+idJogador+" ["+nome+"] entrou na partida " + 
									idPartida + " (partida estava em aguardo)");
							partida.setJogador2(idJogador, nome);
							atualizaIndiceUsuarioPartida(idJogador, idPartida);
							return idJogador;
						}
	    			}
				}
				for (int idPartida = 0; idPartida < totalPartidas; idPartida++) {
					synchronized (partidas[idPartida]) {
						KingsValleyGame partida = partidas[idPartida];
						if(partida.ehPartidaVazia()) {
							System.out.println("Player "+idJogador+" ["+nome+"] entrou na partida " +
									idPartida + " (nova partida foi criada)");
							partida.setJogador1(idJogador, nome);
							atualizaIndiceUsuarioPartida(idJogador, idPartida);
							return idJogador;
						}
	    			}
				}	
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
    	return -3;
    }

    @Override
    public int encerraPartida(int idJogador) throws RemoteException {
    	
    	if(idJogador >= totalPartidas*2 || idJogador < 0) // verifica se playerId é inválido
    		return -1;
    	
    	try {
			indiceUsuarioPartidaMutexes[idJogador].acquire();
			int idPartida = indiceUsuarioPartida[idJogador]; // busca partida que o jogador está jogando
	    	if(idPartida >= 0) {
	    		synchronized (partidas[idPartida]) {
	    			KingsValleyGame partida = partidas[idPartida]; 
		    		if(partida.ehPartidaEncerrada()) {
		    			indiceUsuarioPartidaMutexes[idJogador].release();
		    	    	System.out.println("Ocorreu um erro, tentativa de fechamento duplicada!");
		    			return -1; // retorna erro, pois estou tentando encerrar duas vezes a partida
		    		}
		    		partida.encerraPartida(idJogador);
		    		limpaMapeamentoDeNomes(idPartida);
		    		indiceUsuarioPartida[idJogador] = -1;
		    		System.out.println("O player "+idJogador+" encerrou a partida "+idPartida);
	    		}
	    	}else {
	    		indiceUsuarioPartidaMutexes[idJogador].release();
	    		System.out.println("Ocorreu um erro, nenhum jogo foi encontrado!");
	    		return -1; // se não existe é inválida, retorna erro
	    	}
	    	indiceUsuarioPartidaMutexes[idJogador].release();
	    	return 0;
    	} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	return -1;
    }

    @Override 
    public int temPartida(int idJogador) throws RemoteException {
        try {
        	if(idJogador >= totalPartidas*2 || idJogador < 0) // verifica se playerId é inválido
            	return -1;
        	indiceUsuarioPartidaMutexes[idJogador].acquire();
    		int idPartida = indiceUsuarioPartida[idJogador];
        	if(idPartida >= 0) {	
        		synchronized (partidas[idPartida]) {
	        		KingsValleyGame partida = partidas[idPartida];
	        		if(partida.ehPartidaAguardandoJogador()){
		        		return 0; // esperando por player	
	        		}else if(partida.ehPartidaEmJogo()) {
		        		if(partida.ehMinhaVez(idJogador) == 1) { // eu que jogo?
		        			indiceUsuarioPartidaMutexes[idJogador].release();
		        			return 1; // sim, há partida e o jogador inicia jogando
		        		}
		        		else {
		        			indiceUsuarioPartidaMutexes[idJogador].release();
		        			return 2; // sim, há partida e o jogador é o segundo a jogar.
		        		}
		        	}else if(partida.ehPartidaEncerrada()) {
		        		indiceUsuarioPartidaMutexes[idJogador].release();
		        		return -2; // limite de tempo acabou mas partida ainda não foi deletada 
		        	}else {
		        		indiceUsuarioPartidaMutexes[idJogador].release();
		        		System.out.println("Aconteceu um erro na verificação de existência de partida");
		        		return -1;
		        	}
				}
        	}else {
        		System.out.println("Aconteceu um erro na verificação de existência de partida");
        	}
            indiceUsuarioPartidaMutexes[idJogador].release();
        } catch (Exception e) {
        	e.printStackTrace();
		}
        return -1;
    }
   
    
    @Override 
    public String obtemOponente(int idJogador) throws RemoteException {
    	
    	if(idJogador >= totalPartidas*2 || idJogador < 0) // verifica se playerId é inválido
        	return "";
    	
    	try {
			indiceUsuarioPartidaMutexes[idJogador].acquire();
	    	int idPartida = indiceUsuarioPartida[idJogador];
	    	System.out.println("Player "+ idJogador + " está está buscando oponente no game " + idPartida);
	    	if(idPartida >= 0) { 
	    		synchronized (partidas[idPartida]) {
		    		Jogador oponente = partidas[idPartida].getOponente(idJogador);
		    		if(oponente != null) {
		    			String nome = oponente.getNome();
		    			indiceUsuarioPartidaMutexes[idJogador].release();
			    		return nome;
		    		}
		    		System.out.println("oponente nulo");
				}
	    	}
	    	indiceUsuarioPartidaMutexes[idJogador].release();
    	} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	return "";
    	
    }

    
    @Override 
    public int ehMinhaVez(int idJogador) throws RemoteException {
    	if(idJogador >= totalPartidas*2 || idJogador < 0) // verifica se playerId é inválido
        	return -1;
    		System.out.println("Jogador "+idJogador+ " está consultando se é sua vez");
    	try {
			indiceUsuarioPartidaMutexes[idJogador].acquire();		
	    	int idPartida = indiceUsuarioPartida[idJogador];
	    	if(idPartida >= 0) {
	    		synchronized (partidas[idPartida]) {
	    			int ret = partidas[idPartida].ehMinhaVez(idJogador);
	    			indiceUsuarioPartidaMutexes[idJogador].release();
					return ret;
				}
	    	}
	    	indiceUsuarioPartidaMutexes[idJogador].release();
    	} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	System.out.println("Ocorreu um erro, nenhum jogo foi encontrado para o jogador " + idJogador);
		return -1;
    }

    
    @Override 
    public String obtemTabuleiro(int idJogador) throws RemoteException {
    	
    	if(idJogador >= totalPartidas*2 || idJogador < 0) // verifica se playerId é inválido
        	return "";
    	try {
			indiceUsuarioPartidaMutexes[idJogador].acquire();
	    	int idPartida = indiceUsuarioPartida[idJogador];
	    	if(idPartida >= 0) { // é id válido de uma partida
	    		System.out.println("Obtendo tabuleiro da partida "+idPartida+ " para o player "+idJogador);
	        	synchronized (partidas[idPartida]) {
	        		String tabuleiro = partidas[idPartida].obtemTabuleiro();
	        		indiceUsuarioPartidaMutexes[idJogador].release();
					return tabuleiro;
	        	}
	    	}
	    	indiceUsuarioPartidaMutexes[idJogador].release();
    	} catch (InterruptedException e) {
			e.printStackTrace();
		}	
    	return "";
    }

    @Override 
    public int movePeca(int idJogador, int lin, int col, int dir) throws RemoteException {
    	
    	if(idJogador >= totalPartidas*2 || idJogador < 0) // testa se playerId é inválido
    		return 0; 
    	try {
			indiceUsuarioPartidaMutexes[idJogador].acquire();
	    	int idPartida = indiceUsuarioPartida[idJogador];
	    	if(idPartida >= 0) { // é id válido de uma partida
	    		System.out.println("Jogador "+idJogador+ " deseja movimentar sua peça "+
	    					"na partida "+idPartida);
	        	synchronized (partidas[idPartida]) {
					KingsValleyGame partida = partidas[idPartida]; 
					int ret = 0;
					if(partida.ehPartidaEncerrada())
		    			ret = -2; // teve sua partida encerrada
					else 
						ret = partida.movePeca(idJogador, lin, col, Direcao.getDirecao(dir));
					indiceUsuarioPartidaMutexes[idJogador].release();
					return ret;
				}			
	    	}
	    	indiceUsuarioPartidaMutexes[idJogador].release();
    	} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	System.out.println("Ocorreu um erro, nenhum jogo foi encontrado!");
    	return 0;
    }
    
    private void limpaMapeamentoDeNomes(int idPartida) {
    	KingsValleyGame partida = partidas[idPartida];
    	int idJogador1 = partida.getIdJogador1();
		int idJogador2 = partida.getIdJogador2();
		try {
			nomeJogadorMutex.acquire();
			if(idJogador1 >= 0) {
				mapaNomeIdAtivos.remove(mapaIdNomeAtivos[idJogador1]);
				mapaIdNomeAtivos[idJogador1] = "";
			}
			if(idJogador2 >= 0) {
				mapaNomeIdAtivos.remove(mapaIdNomeAtivos[idJogador2]);
				mapaIdNomeAtivos[idJogador2] = "";	
			}
			nomeJogadorMutex.release();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
    
    private void devolveIdsJogadores(int idPartida) {
    	try {
    		KingsValleyGame partida = partidas[idPartida];
        	int idJogador1 = partida.getIdJogador1();
    		int idJogador2 = partida.getIdJogador2();
    		idsDisponiveisMutex.acquire();
			if(idJogador1 >= 0)
				idsDisponiveis.add(idJogador1);
			if(idJogador2 >= 0)
				idsDisponiveis.add(idJogador2);
			idsDisponiveisMutex.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }

    
}

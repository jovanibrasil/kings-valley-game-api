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
	
	// Fila de tokens de partidas que estão aguardando para estarem ativas
	LinkedList<Integer> idsPartidasDisponiveis; // contém tokens de partidas
	// Fila de tokens das partidas que estão em jogo
	LinkedList<Integer> idsPartidasAtivas; // contém tokens de partidas
	
	// Lista de tokens  de ids já utilizados e disponíveis no momento. Quando um jogador é removido, o volta pra cá.
	LinkedList<Integer> idsDisponiveis; 
	// Mapeamento nome-id para garantir usuários únicos (eficiente para consultar strings)
	private Map<String, Integer> mapaNomeIdAtivos;
	private String[] mapaIdNomeAtivos;
	
	// Vetor para localizar usuários, sendo que o índice é o id do usuário e 
	// conteúdo é o id da partida
	private int[] indiceUsuarioPartida; 
	private int totalPartidas;
	
	private Semaphore idsPartidasDisponiveisMutex, idsPartidasAtivasMutex, 
		idsDisponiveisMutex, nomeJogadorMutex;
	private Semaphore[] indiceUsuarioPartidaMutexes;
	
    public KingsValleyImpl(int totalPartidas) throws RemoteException {
    	this.totalPartidas = totalPartidas;
    
    	this.idsPartidasDisponiveis = new LinkedList<>(); 
    	this.idsPartidasAtivas = new LinkedList<>();
        this.idsDisponiveis = new LinkedList<>(); 
        this.indiceUsuarioPartida= new int[totalPartidas*2];
        this.mapaIdNomeAtivos = new String[totalPartidas*2];
        this.mapaNomeIdAtivos = new HashMap<>();
        this.indiceUsuarioPartidaMutexes = new Semaphore[totalPartidas];
        this.partidas = new  KingsValleyGame[totalPartidas];
        for (int i = 0, j=0; i < totalPartidas; i++, j+=2) {
        	this.partidas[i] = new KingsValleyGame();
        	this.idsPartidasDisponiveis.add(i);
			idsDisponiveis.add(j); idsDisponiveis.add(j+1);
			indiceUsuarioPartidaMutexes[i] = new Semaphore(1);
        }
        this.idsPartidasDisponiveisMutex = new Semaphore(1);
        this.idsPartidasAtivasMutex = new Semaphore(1);
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
    	    	 System.out.println("Thread de verificação rodando ...");
    	    	 try {	 
    	    	 	while(true) {
		    	    	 // atualiza dados das partidas ativas, ou seja, com dois players jogando 
		    	    	 //System.out.println("Atualizando partidas ativas");
		    	    	 while(true) {
		    	    		 //System.out.println("Verificando partida ativa");
		    	    		 idsPartidasAtivasMutex.acquire();
		    	    		 if(idsPartidasAtivas.peek() == null) {
		    	    			 idsPartidasAtivasMutex.release();
		    	    			 break;
		    	    		 }
		    	    		 int idPartida = idsPartidasAtivas.poll();
		    	    		 idsPartidasAtivasMutex.release();
		    	    		 
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
			    	    			 
			    	    			 devolveIdsJogadores(idPartida); // devolve ids usados pelos jogadores
			    	    			 partida.limpaPartida(); // destrói dados da partida
			    	    			 
			    	    			 idsPartidasDisponiveisMutex.acquire();
									 idsPartidasDisponiveis.add(idPartida); // adiciona as partidas em aguardo
			    	    			 idsPartidasDisponiveisMutex.release();
			    	    			 
			    	    		 }else {
			    	    			 idsPartidasAtivasMutex.acquire();
			    	    			 idsPartidasAtivas.add(idPartida);
			    	    			 idsPartidasAtivasMutex.release();
			    	    		 }
		    	    		 }
		    	    	 }
		    	    	 
		    	    	 while(true) {
		    	    		 //System.out.println("Verificando partida ativa");
		    	    		 idsPartidasDisponiveisMutex.acquire();
		    	    		 if(idsPartidasDisponiveis.peek() == null) {
		    	    			 idsPartidasAtivasMutex.release();
		    	    			 break; // se não há partidas disponíveis, break
		    	    		 }else {
		    	    			 int idPartida = idsPartidasDisponiveis.poll();
		    	    			 idsPartidasDisponiveisMutex.release();
				    	    	 //System.out.println("Atualizando partida aguardadndo");
		    	    			 synchronized (partidas[idPartida]) {
					    	    	 KingsValleyGame partida = partidas[idPartida];
				    	    		 if(!partida.partidaVazia()) {
					    	    		 partida.atualizaRestricoesTemporais();
						   		    	 if(partida.ehDestruivel()) {
					    	    			 System.out.println("A partida id= "+idPartida+" foi destruída.");
					    	    			 partida.limpaPartida(); // destrói dados da partida
					    	    			 limpaMapeamentoDeNomes(idPartida);
					    	    			 idsPartidasDisponiveisMutex.acquire();
					    	    			 idsPartidasDisponiveis.add(idPartida); // adiciona as partidas em aguardo
					    	    			 idsPartidasDisponiveisMutex.release();
						   		    	 }else {
					    	    			 idsPartidasDisponiveisMutex.acquire();
							    	    	 idsPartidasDisponiveis.addFirst(idPartida);
							    	    	 idsPartidasDisponiveisMutex.release();
					    	    		 }
				    	    		 }else {
				    	    			 idsPartidasDisponiveisMutex.acquire();
						    	    	 idsPartidasDisponiveis.addFirst(idPartida);
						    	    	 idsPartidasDisponiveisMutex.release();
						    	    	 break;
				    	    		 }
				    	    	 }
		    	    			 
		    	    		 }
		    	    	 }
		    	    	Thread.sleep(1000);
		    	     }
	    	   }
    	       catch (InterruptedException e) {
					e.printStackTrace();
    	       }
    	    }
    	}).start();
	}

    @Override 
    public int registraJogador(String nome) throws RemoteException {
		if(this.mapaNomeIdAtivos.containsKey(nome)){
			return -1; // Nome já existe.
		}else if(idsPartidasDisponiveis.size() == 0){
			return -2; // Número de partidas/jogadores foi excedido.
		}else{
			// Atribuição do identificador
			try {
				idsPartidasDisponiveisMutex.acquire();
				idsDisponiveisMutex.acquire();
				nomeJogadorMutex.acquire();
				
				int idJogador = idsDisponiveis.poll();
				this.mapaNomeIdAtivos.put(nome, idJogador); // registra nome e id
				mapaIdNomeAtivos[idJogador] = nome;
				int idPartida = idsPartidasDisponiveis.poll(); // tira da fila de tokens de partidas
				
				idsPartidasDisponiveisMutex.release();
				idsDisponiveisMutex.release();
				nomeJogadorMutex.release(); 
				
				System.out.println("Registrando player "+idJogador+" como "+nome);
				
				// Processo de a tribuição do jogador a uma partida
				synchronized (partidas[idPartida]) {
					KingsValleyGame partida = partidas[idPartida];
					if(partida.isEmptyGame()) { // se existe  partida vazia, set player 1
						System.out.println("Player "+idJogador+" entrou na partida " +
								idPartida + " (nova partida foi criada)");
						partida.setJogador1(idJogador, nome);		
						idsPartidasDisponiveisMutex.acquire();
						idsPartidasDisponiveis.addFirst(idPartida);
						idsPartidasDisponiveisMutex.release();
					}else {	// se existe partida com um player, set player 2
						System.out.println("Player "+idJogador+" entrou na partida " + 
								idPartida + " (partida estava em aguardo)");
						partida.setJogador2(idJogador, nome);
						idsPartidasAtivasMutex.acquire();
						idsPartidasAtivas.add(idPartida);
						idsPartidasAtivasMutex.release();
					}
				}
				indiceUsuarioPartidaMutexes[idJogador].acquire();
				indiceUsuarioPartida[idJogador] = idPartida;
				indiceUsuarioPartidaMutexes[idJogador].release();
				return idJogador;
			} catch (InterruptedException e) {
				e.printStackTrace();
				return -3; // se houver algum erro
			}
		}
    
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
		    		if(partida.partidaEncerrada()) {
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
	        		if(partida.aguardandoJogador()){
		        		return 0; // esperando por player	
	        		}else if(partida.emJogo()) {
		        		if(partida.ehMinhaVez(idJogador) == 1) { // eu que jogo?
		        			indiceUsuarioPartidaMutexes[idJogador].release();
		        			return 1; // sim, há partida e o jogador inicia jogando
		        		}
		        		else {
		        			indiceUsuarioPartidaMutexes[idJogador].release();
		        			return 2; // sim, há partida e o jogador é o segundo a jogar.
		        		}
		        	}else if(partida.partidaEncerrada()) {
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
    	
    	System.out.println("Jogador "+idJogador+ " deseja movimentar sua peça");
    	
    	if(idJogador >= totalPartidas*2 || idJogador < 0) // testa se playerId é inválido
    		return 0; 
    	try {
			indiceUsuarioPartidaMutexes[idJogador].acquire();
	    	int idPartida = indiceUsuarioPartida[idJogador];
	    	if(idPartida >= 0) { // é id válido de uma partida
				synchronized (partidas[idPartida]) {
					KingsValleyGame partida = partidas[idPartida]; 
					int ret = 0;
					if(partida.partidaEncerrada())
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

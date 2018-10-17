package kingsvalley.game;

import kingsvalley.enums.EstadoJogador;
import kingsvalley.enums.TipoPecas;

/*
*
* Implementação de um jogador do KingsValley.
*
* @author Jovani Brasil
* @email jovanibrasil@gmail.com
*  
*/

public class Jogador {
	
	private int idJogador;
	private String nome;
	private char reiChar;
	private char soldadoChar;
	private EstadoJogador estado;
	
	public Jogador() {
		this.inicializaJogador();
	}
	
	public void inicializaJogador() {
		this.idJogador = -1;
		this.reiChar = ' ';
		this.soldadoChar = ' ';
		this.estado = EstadoJogador.NaoInicializado;
		this.nome = "";
	}
	
	/*
	 * Verifica se uma determina peça é do jogador em questão. 
	 * 
	 * @param peca 		é a peça que se deseja verificar (char) 
	 * @return 			true se a peça é do jogador e false caso contrário
	 * 
	 */
	public boolean ehMinhaPeca(char peca) {
		if(peca == this.reiChar || peca == this.soldadoChar)
			return true;
		return false;
	}
	
	/*
	 * Getters and setters
	 */
	public void setIdJogador(int idJogador) {
		this.idJogador = idJogador;
	}
	
	public int getIdJogador() {
		return this.idJogador;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getNome() {
		return this.nome;
	}
	
	public char getReiChar() {
		return this.reiChar;
	}
	
	public char getSoldadoChar(int playerId) {
		return this.soldadoChar;
	}
	
	public void setEstado(EstadoJogador estado) {
		this.estado = estado;
	}
	
	public EstadoJogador getEstado() {
		return this.estado;
	}
	
	/*
	 * Define a ordem de jogada, 
	 * 
	 */
	public void setTipoPecas(TipoPecas tipoPecas) {
		if(tipoPecas == TipoPecas.Claras) {
			this.reiChar = 'r';
			this.soldadoChar = 's';
		}else {
			this.reiChar = 'R';
			this.soldadoChar = 'S';
		}
	}
	
}

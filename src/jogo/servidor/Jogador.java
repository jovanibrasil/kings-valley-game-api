package jogo.servidor;

import enums.EstadoJogador;
import enums.OrdemJogada;

/*
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
		this.estado = EstadoJogador.EmJogo;
		this.nome = "";
	}
	
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

	public void setOrdemJogador(OrdemJogada ordemJogada) {
		if(ordemJogada == OrdemJogada.Primeiro) {
			this.reiChar = 'k';
			this.soldadoChar = 's';
		}else {
			this.reiChar = 'K';
			this.soldadoChar = 'S';
		}
	}
	
	public char getReiChar() {
		return this.reiChar;
	}
	
	public char getSoldadoChar(int playerId) {
		return this.soldadoChar;
	}
	
	/*
	 * Verifica se uma peça é do jogador. 
	 * 
	 * @param peca é a peça que se deseja verificar (char) 
	 * 
	 * @return true se a peça é do jogador e false caso contrário
	 * 
	 */
	public boolean ehMinhaPeca(char peca) {
		if(peca == this.reiChar || peca == this.soldadoChar)
			return true;
		return false;
	}

	public void setEstado(EstadoJogador estado) {
		this.estado = estado;
	}
	
	public EstadoJogador getEstado() {
		return this.estado;
	}
	
	
}

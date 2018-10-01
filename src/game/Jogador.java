package game;

import enums.OrdemJogada;

public class Jogador {
	
	private int idJogador;
	private int posRei;
	
	private char reiChar;
	private char soldadoChar;
	
	
	public Jogador() {
		this.inicializaJogador();
	}
	
	public void inicializaJogador() {
		this.idJogador = -1;
		this.posRei = -1;
		this.reiChar = ' ';
		this.soldadoChar = ' ';
	}
	
	public void setIdJogador(int idJogador) {
		this.idJogador = idJogador;
	}
	
	public int getIdJogador() {
		return this.idJogador;
	}

	public void setOrdemJogador(OrdemJogada ordemJogada) {
		if(ordemJogada == OrdemJogada.Primeiro) {
			this.posRei = 10;
			this.reiChar = 'k';
			this.soldadoChar = 's';
		}else {
			this.posRei = 14;
			this.reiChar = 'k';
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
	
	
	
}

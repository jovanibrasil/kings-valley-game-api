package game;

import javax.swing.Painter;

public class Tabuleiro {

	private char[] tabuleiro;
	int rei1Pos, rei2Pos;
	
	public Tabuleiro() {
		this.tabuleiro = new char[25];
		this.inicializaTabuleiro();
	}
	
	public void inicializaTabuleiro() {
		this.tabuleiro = new char[] { 's', '-', '-', '-', 'S', // 0 - 4 
				's', '-', '-', '-', 'S', // 5 - 9
				'k', '-', '-', '-', 'K', // 10 - 14
				's', '-', '-', '-', 'S', // 15 - 19
				's', '-', '-', '-', 'S'  }; // 20 - 24
	}

	/*
	 * Retorna a peça em uma determinada posição do tabuleiro.
	 * 
	 * @param pos		é a posição a qual se deseja verificar a peça
	 * @return 			retorna a peça (representação de tipo caractere)
	 * 
	 */
	public char getPeca(int pos) {
		return this.tabuleiro[pos];
	}
	
	/*
	 * Verifica se a posição do tabuleiro está vazia ou não. Lembre que uma posição
	 * vazia contém o caractere '-'.
	 * 
	 * @return true se vazio ou false se ocupado.
	 * 
	 */
	private boolean isTabuleiroVazio(int pos) {
		if(this.tabuleiro[pos] == '-')
			return true;
		else
			return false;
	}
	
	/*
	 * Verifica se a próxima posição está ocupada ou não. Se estiver ocupada, retorna -1,
	 * caso contrário retorna a posição destino.
	 * 
	 * @param posAtual 	posição atual do tabuleiro
	 * @param dir		direção a qual se deseja o movimento
	 * 
	 * @return 			retorna posição destino, caso o movimento seja possível, ou -1
	 * 					caso não seja possível movimentar a peça.
	 * 
	 */
	public int verificaProximaPos(int posAtual, int dir) {
		
		int boardLength = this.tabuleiro.length; 
		int l = posAtual / 5; 
		int min = l * 5; //menor x da linha
		int max = min + 4; //maior x da linha		
		
		switch (dir) {
			case 0: // direita
				return posAtual+1 <= max && isTabuleiroVazio(posAtual+1) ? posAtual+1 : -1;
			case 1: // diagonal direita inferior
				return posAtual < max && posAtual+6 < boardLength && isTabuleiroVazio(posAtual+6) ? posAtual+6 : -1;
			case 2: // para baixo 
				return posAtual+5 < boardLength && isTabuleiroVazio(posAtual+5) ? posAtual+5 : -1;
			case 3: // diagonal esquerda inferior
				return posAtual > min && posAtual+4 < boardLength && isTabuleiroVazio(posAtual+4) ? posAtual+4 : -1;
			case 4: // esquerda
				return posAtual-1 >= min && isTabuleiroVazio(posAtual-1) ? posAtual-1 : -1;
			case 5: // diagonal esquerda superior
				return posAtual > min && posAtual-6 > 0 && isTabuleiroVazio(posAtual-6) ? posAtual-6 : -1;
			case 6: // para cima
				return posAtual-5 >= 0 && isTabuleiroVazio(posAtual-5) ? posAtual-5 : -1;
			case 7: // diagonal direita superior 
				return posAtual < max && posAtual-4 > 0 && isTabuleiroVazio(posAtual-4) ? posAtual-4 : -1;
			default:
				System.out.println("Erro: direção desejada é inválida.");
				return -1;		
		}
	}
	
	public boolean movePeca(int pos, int dir) {
		
		int originalPos = pos;
		int newPos = 0;
		
		// peca se movimenta obrigatoriamente no sentido escolhido ate
		// 1) fim do tabuleiro
		// 2) encontrar uma outra peca (nao e possivel pular uma peca)
		
		// TODO unica peca que pode parar no centro é rei
		// TODO o rei também não poderá parar no centro se houver mais casas no sentido
		// soldados podem passar pelo centro mas não podem parar
		
		while(true) { // enquanto próximo movimento é válido
			
			newPos = this.verificaProximaPos(pos, dir); // busca próximo movimento
			if(newPos >= 0) { // movimento foi válido
				// atualiza reis se suas posições forem modificadas
				this.rei1Pos = pos == this.rei1Pos ? newPos : pos; 
				this.rei2Pos = pos == this.rei2Pos ? newPos : pos;
				// this.atualizaPosicaoRei(pos, newPos);
				pos = newPos;
				//System.out.println("Movimentou para outra posição");
			}else { //não houve um movimento válido
				// houve algum movimento anter, então realiza a atualização do tabuleiro
				if(pos != originalPos) {
					this.tabuleiro[pos] = this.tabuleiro[originalPos]; 
					this.tabuleiro[originalPos] = '-';
				}else {
					return false; // caso contrário, ficou parado
				}
				break;
			}
		}
		return true;
	}
	
	public int getRei1Pos() {
		return rei1Pos;
	}
	
	public int getRei2Pos() {
		return rei2Pos;
	}
	
	/*
	 * Sobrescrita do método toString.
	 */
	@Override public String toString() {
		String out = "";
		for (int i = 0, k = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++, k++)
				out += this.tabuleiro[k] + " ";
			out += "\n";
		}
		out += "\n";
		return out;
	}
	
}

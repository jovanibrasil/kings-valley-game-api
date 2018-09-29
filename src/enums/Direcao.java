package enums;

public enum Direcao {
	
	Direita, DiagonalDireitaInferior, Baixo, DiagonalEsquerdaInferior,
	Esquerda, DiagonalEsquerdaSuperior, Cima, DiagonalDireitaSuperior;  
	
	private static final Direcao[] direcoes = new Direcao[8];
	
	static {
		int idx = 0;
		for(Direcao d : Direcao.values())
			direcoes[idx++] = d;
	}
	
	public static Direcao getDirecao(int direcao) {
		return direcoes[direcao];
	}
	
}

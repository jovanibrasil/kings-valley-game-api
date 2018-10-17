package kingsvalley.enums;

/*
*
* Possíveis direções utilizadas para movimentação das peças.
*
* @author Jovani Brasil
* @email jovanibrasil@gmail.com
*  
*/
public enum Direcao {
	
	Direita, DiagonalDireitaInferior, Baixo, DiagonalEsquerdaInferior,
	Esquerda, DiagonalEsquerdaSuperior, Cima, DiagonalDireitaSuperior;  
	
	private static final Direcao[] direcoes = new Direcao[8];
	
	static {
		int idx = 0;
		for(Direcao d : Direcao.values())
			direcoes[idx++] = d;
	}
	
	/*
	 * Retorna um enum Direção de acordo com o inteiro de direção. 
	 * 
	 * @param direcao		Valor inteiro que se deseja obter o enum.
	 * @return 				Enum da direção buscada.
	 * 
	 */
	public static Direcao getDirecao(int direcao) {
		return direcoes[direcao];
	}
	
}

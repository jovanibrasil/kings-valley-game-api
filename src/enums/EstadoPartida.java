package enums;

/*
*
* Estados de uma partida.
*
* @author Jovani Brasil
* @email jovanibrasil@gmail.com
*  
*/
public enum EstadoPartida {
	/*
	 *  Definição dos possíveis estados de uma partida:	
	 *
	 *  Vazia 					- Não existe jogador na partida.
	 *  AguardandoOponente 		- Partida possui um oponente e está aguardando outro.
	 *  EmJogo 					- Existem dois jogadores ativamente jogando a partida. 
	 *  Encerrada 				- Partida foi encerrada por um jogador, restrições temporais, 
	 *  						ou por vitória de um jogador.
	 *  AguardandoDestruicao 	- Uma partida que foi encerrada, venceu seu tempo de vida pós encerramento
	 *  						e está esperando pela destruição (limpeza) das suas informações.
	 *  
	 */
	PartidaVazia, PartidaAguardandoOponente, PartidaEmJogo, PartidaEncerravel, PartidaEncerrada, 
		AguardandoDestruicao
}

package kingsvalley.jws;

import javax.jws.WebService;

import kingsvalley.game.KingsValleyServerOperations;

@WebService(endpointInterface = "KingsValleyInterface")
public class KingsValleyJwsImpl implements KingsValleyInterface {

	private static KingsValleyServerOperations gameServer;
	
	public KingsValleyJwsImpl() {
		if(gameServer == null) {
			this.gameServer = new KingsValleyServerOperations(100);
		}
	}
	
	@Override
	public int registraJogador(String nome) {
		return gameServer.registraJogador(nome);
	}

	@Override
	public int encerraPartida(int idJogador) {
		return gameServer.encerraPartida(idJogador);
	}

	@Override
	public int temPartida(int idJogador) {
		return gameServer.temPartida(idJogador);
	}

	@Override
	public String obtemOponente(int idJogador) {
		return gameServer.obtemOponente(idJogador);
	}

	@Override
	public int ehMinhaVez(int idJogador) {
		return gameServer.ehMinhaVez(idJogador);
	}

	@Override
	public String obtemTabuleiro(int idJogador) {
		return gameServer.obtemTabuleiro(idJogador);
	}

	@Override
	public int movePeca(int playerId, int lin, int col, int dir) {
		return gameServer.movePeca(playerId, lin, col, dir);
	}

}

package seabattle.client;

import games.client.BaseGame;
import games.client.GameController;
import games.client.GameView;
import games.shared.GameModel;
import seabattle.shared.SeaBattleModel;

public class SeaBattle extends BaseGame {

	@Override
	protected GameController createGameController(GameModel model, GameView view, boolean isStandalone) {
		return new SeaBattleController((SeaBattleModel)model, (SeaBattleView)view);
	}

	@Override
	protected GameView createGameView() {
		return new SeaBattleView();
	}

	@Override
	protected GameModel createGameModel() {
		return new SeaBattleModel();
	}

}

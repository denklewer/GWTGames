package games.client;

import games.shared.GameModelListener;

public interface GameController extends GameModelListener {

	void setGameCommunicator(GameCommunicator gameCommunicator);

}

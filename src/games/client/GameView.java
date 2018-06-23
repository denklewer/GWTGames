package games.client;

import com.google.gwt.user.client.ui.AbsolutePanel;

public interface GameView {

	void init(GameController controller);

	void setGamePanel(AbsolutePanel gamePanel);

	int getGameWidth();

}

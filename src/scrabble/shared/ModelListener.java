package scrabble.shared;

import games.shared.GameModelListener;

import java.util.Vector;

public interface ModelListener extends GameModelListener{

	void gamerAdded(byte side);

	void standFilled(byte side, Vector<ScrabbleDie> newDice);

	void moveMade();

	void gameEnded(String reason, byte winnerSide);

	void turnSwitched(byte side);

	void scoreChanged(int score, byte side);

	void addMoveMadeInfo(String info);

	void bagSizeChanged(int size);

}

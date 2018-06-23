package seabattle.shared;

import games.shared.GameModelListener;

public interface ModelListener extends GameModelListener{

	void gameOver(int[][] lastShooterField, int[][] anotherField, byte side);

	void gameOver(boolean b);

//	void gameStarted();

	void moveDone(SeaBattleMove move);

	void askMove(int i);

}

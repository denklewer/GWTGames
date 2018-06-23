package sudoku.shared;

import games.shared.GameModelListener;

public interface ModelListener extends GameModelListener{

	void newGame(Cell[][] cells);

	void win();

	void loose();

}

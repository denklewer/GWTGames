package sudoku.shared;

import games.shared.Point;


public class Move {
	private int _oldNumber = 0;
	private int _newNumber = 0;
	private Point _cellIndex;
	
	private boolean _deleteAll = false;
	
	public Move(final Point cellIndex, final int oldNumber, final int newNumber) {
		super();
		_cellIndex = cellIndex;
		_oldNumber = oldNumber;
		_newNumber = newNumber;
	}

	public Move(final boolean deleteAll) {
		_deleteAll = deleteAll;
	}

	public int getNewNumber() {
		return _newNumber;
	}

	public int getOldNumber() {
		return _oldNumber;
	}
	
	public Point getCellIndex() {
		return _cellIndex;
	}

	public boolean isDeleteAll() {
		return _deleteAll;
	}

	public void setDeleteAll(final boolean deleteAll) {
		_deleteAll = deleteAll;
	}
	
}

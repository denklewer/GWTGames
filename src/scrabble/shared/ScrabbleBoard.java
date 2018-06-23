package scrabble.shared;

import java.util.Vector;

public class ScrabbleBoard implements ScrabbleProtocol {
	
	private ScrabbleCell[][] _cells;
	
	public boolean equals(Object obj) {
		ScrabbleBoard board = (ScrabbleBoard) obj;
		for (int i = 0; i < CELLS_IN_LINE; i++) {
			for (int j = 0; j < CELLS_IN_LINE; j++) {
				if (!_cells[i][j].equals(board.getCell(i, j))) {
					return false;
				}
			}
		}
		return true;
	}
	
	public ScrabbleBoard copy(){
		ScrabbleCell[][] cells = null;
		if(_cells != null){
			cells = new ScrabbleCell[CELLS_IN_LINE][CELLS_IN_LINE];
			for (int i = 0; i < CELLS_IN_LINE; i++) {
				for (int j = 0; j < CELLS_IN_LINE; j++) {
					if(_cells[i][j] != null){
						cells[i][j] = _cells[i][j].copy();
					}
				}
			}
		}
		return new ScrabbleBoard(cells);
	}
	
	public ScrabbleBoard() {
	}
	
	
	public ScrabbleBoard(ScrabbleCell[][] cells) {
		_cells = cells;
	}

	public ScrabbleCell getCell(int x, int y){
		if(x < 0 || x > _cells.length - 1 || y < 0 || y > _cells[0].length -1)
			return null;
		return _cells[x][y];
	}
	
	public ScrabbleCell getSelectedCell(int x, int y) {
		if (_cells[x][y].isBusy()) {
			return null;
		}
		return _cells[x][y];
	}

	public int getWidth() {
		return _cells.length;
	}

	public int getHeight() {
		return _cells[0].length;
	}

	public void add(Vector cells) {
		for (int i = 0; i < cells.size(); i++) {
			ScrabbleCell cell = (ScrabbleCell) cells.elementAt(i);
			_cells[cell.getCoord().x][cell.getCoord().y].putDie(cell.getDie());
		}
	}

	public boolean firstMove() {
		for (int i = 0; i < _cells.length; i++) {
			for (int j = 0; j < _cells[0].length; j++) {
				if (_cells[i][j].getDie() != null) 
					return false;
			}
		}
		return true;
	}
	
}

package sudoku.shared;

public class Solve {

	private int _number;

	private int _col;

	private int _row;

	private int _style;

	public Solve(int style, int currnumber, int col, int row) {
		_style = style;
		_number = currnumber;
		_col = col;
		_row = row;
	}

	public int getComplication() {
		return _style;
	}

	public int getCol() {
		return _col;
	}

	public int getRow() {
		return _row;
	}

	public int getNumber() {
		return _number;
	}

}

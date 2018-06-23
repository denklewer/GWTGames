package sudoku.shared;

import games.shared.Point;

import java.util.List;
import java.util.Vector;

public class Table {
	
	protected Cell[][] _cells = new Cell[9][9];

	protected BigCell[] _rows = new BigCell[9];

	private BigCell[] _cols = new BigCell[9];

	private BigCell[] _bigcells = new BigCell[9];

	private BigCell[] _allRowsColsBigcells = new BigCell[27];

	private SudokuModel _sudoku;

	private boolean _makeRepaint = false;

	public Table(final SudokuModel sudoku) {
		// construktor dla vsego krome clienta
		_sudoku = sudoku;
		fillTable();
	}

	public Table() {
		// construktor dla clienta
		_sudoku = null;
		fillTable();
	}

	public boolean isFilled() {
		// polnostyu li zapolneno vse pole
		int tmpnumber = 0;
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				if (_cells[col][row].isStyleTask()
						|| _cells[col][row].isStyleSolved()
						|| _cells[col][row].isStyleLast()) {
					tmpnumber++;
				}
			}
		}
		if (tmpnumber == 9 * 9) {// 9*9==razmeru polya
			return true;
		} else {
			return false;
		}
	}

	public void fillTable() {
		// zapolnit kagduyu ya4eyku na4alnimi dannimi
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				_cells[col][row] = new Cell(col, row);
			}
		}
		// zapolnit vse _rows
		for (int tmprow = 0; tmprow < 9; tmprow++) {
			final Cell[] tmpcell = new Cell[9];
			for (int tmpcol = 0; tmpcol < 9; tmpcol++) {
				tmpcell[tmpcol] = _cells[tmpcol][tmprow];
			}
			_rows[tmprow] = new BigCell(new Point(0, tmprow), tmpcell,
					BigCell.ROW, _sudoku);
		}

		// zapolnit vse _cols
		for (int tmpcol = 0; tmpcol < 9; tmpcol++) {
			final Cell[] tmpcell = new Cell[9];
			for (int tmprow = 0; tmprow < 9; tmprow++) {
				tmpcell[tmprow] = _cells[tmpcol][tmprow];
			}
			_cols[tmpcol] = new BigCell(new Point(tmpcol, 0), tmpcell,
					BigCell.COL, _sudoku);
		}
		// zapolnit vse bigrows
		int tmpcounter = 0;
		for (int bigrow = 0; bigrow < 3; bigrow++) {
			for (int bigcol = 0; bigcol < 3; bigcol++) {
				final Cell[] tmpcell = new Cell[9];
				int tmpcounter2 = 0;
				for (int tmprow = bigrow * 3; tmprow < bigrow * 3 + 3; tmprow++) {
					for (int tmpcol = bigcol * 3; tmpcol < bigcol * 3 + 3; tmpcol++) {
						tmpcell[tmpcounter2] = _cells[tmpcol][tmprow];
						tmpcounter2++;
					}
				}
				_bigcells[tmpcounter] = new BigCell(new Point(bigcol, bigrow),
						tmpcell, BigCell.BIGCELL, _sudoku);
				tmpcounter++;
			}
		}
		// zapolnit vse allRowsColsBigcells
		for (int i = 0; i < 27; i++) {
			if (i < 9) {
				_allRowsColsBigcells[i] = _rows[i];
			}
			if (i >= 9 && i < 18) {
				_allRowsColsBigcells[i] = _cols[i - 9];
			}
			if (i >= 18) {
				_allRowsColsBigcells[i] = _bigcells[i - 18];
			}
		}
	}

	public void setSudoku(final SudokuModel sudoku) {
		_sudoku = sudoku;
		for (int i = 0; i < 9; i++) {
			_rows[i].setSudoku(sudoku);
			_cols[i].setSudoku(sudoku);
			_bigcells[i].setSudoku(sudoku);
		}
	}

	public void tableChanged() {
		// izmenit vse littleNumbers esli pole izmenilos
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				for (int currcannumber = 1; currcannumber <= 9; currcannumber++) {
					_cells[col][row].setLittleNumbers(currcannumber, true);
				}
			}
		}
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				tableChanged(col, row);
			}
		}
	}

	public void tableChanged(final int col, final int row) {
		// izmenit vse littleNumbers v stroke stolbce i bigcell pri izmenenii
		// ya4eyki c koordinatami col,row
		if (_cells[col][row].getNumber() != 0) {
			final Vector<Integer> tmpVector = new Vector<Integer>();
			tmpVector.add(_cells[col][row].getNumber());
			_rows[row].setLittleNumbersNot(tmpVector);
			_cols[col].setLittleNumbersNot(tmpVector);
			final int bigcell = (row) / 3 * 3 + (col) / 3;
			_bigcells[bigcell].setLittleNumbersNot(tmpVector);
		}
	}

	public void clearTable() {
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				if(!_cells[col][row].isStyleTask()){
					_cells[col][row].setStyle(Cell.NONE);
					_cells[col][row].setNumber(0);
					for (int i = 1; i <= 9; i++) {
						_cells[col][row].setLittleNumbers(i, true);
					}
				}
			}
		}
	}

	public void setBigcell(final BigCell newBigcell, final int number) {
		_bigcells[number] = newBigcell;
	}

	public Cell getCell(final int col, final int row) {
		return _cells[col][row];
	}

	public void setCell(final Cell newCell, final int col, final int row) {
		_cells[col][row] = newCell;
		_rows[row].setCell(col, newCell);
		_cols[col].setCell(row, newCell);
		_bigcells[row / 3 * 3 + col / 3]
				.setCell(row % 3 * 3 + col % 3, newCell);

	}

	public BigCell getCol(final int number) {
		return _cols[number];
	}

	public void setCol(final BigCell newCol, final int number) {
		_cols[number] = newCol;
	}

	public BigCell getRows(final int number) {
		return _rows[number];
	}

	public BigCell getBigCell(final int number) {
		return _bigcells[number];
	}

	public void setRow(final BigCell newRow, final int number) {
		_rows[number] = newRow;
	}

	public BigCell getAllRowsColsBigcells(final int number) {
		return _allRowsColsBigcells[number];
	}

	public void setAllRowsColsBigcells(final BigCell newAllRowsColsBigcells,
			final int number) {
		_allRowsColsBigcells[number] = newAllRowsColsBigcells;
	}

	public static Table fromIntMassive(final List<List<Integer>> numbers){
		final Table table = new Table();
		Cell cell;
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				cell = new Cell(j ,i);
				cell.setNumber(numbers.get(i).get(j));
				cell.setStyle(numbers.get(i).get(j) > 0 ? Cell.TASK : Cell.NONE);
				table.setCell(cell, j, i);
			}
		}
		table.tableChanged();
		return table;
	}

	public void setMakerepaint(final boolean makeRepaint) {
		this._makeRepaint = makeRepaint;
	}

	public void setPrintlog(final boolean printlog) {
		SudokuGameModel.printLog = printlog;
	}

	public boolean isMakerepaint() {
		return _makeRepaint;
	}

	public void changedCell(final int col, final int row) {
		_rows[row].generateLittleNumbersInCell(col);
		_cols[col].generateLittleNumbersInCell(row);
	}

	public String ToStringLittleNumbers(final int currnumber) {
		String tmpString = "";
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				if (_cells[col][row].getLittleNumber(currnumber)) {
					tmpString += "1";
				} else {
					tmpString += "0";
				}
			}
			tmpString += "\n";
		}
		return tmpString;
	}
}

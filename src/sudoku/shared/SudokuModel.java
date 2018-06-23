package sudoku.shared;

public class SudokuModel {
	protected Table _table = new Table(this);

	protected SolveLogic _logic = new SolveLogic(this);

	protected static boolean printLog = false;

	public SudokuModel() {
		_table.fillTable();
	}

	public Table getTable() {
		return _table;
	}

	public boolean Solve(int style) {
		return _logic.Solve(style);
	}

	public boolean SolveAll(boolean solveWithLoop) {
		return _logic.SolveAll(solveWithLoop);
	}

	public boolean isValid() {
		// provarke pravilno li rewen sudoku
		for (int col = 0; col < 9; col++) {
			for (int row = 0; row < 9; row++) {
				if (!_table.getCell(col, row).isStyleSolved()
						&& !_table.getCell(col, row).isStyleTask()
						&& !_table.getCell(col, row).isStyleLast()) {// esli
					// ne
					// vse
					// ya4eyki
					// zapolneni
					// to
					// isValid=false
					return false;
				}
			}
		}
		for (int i = 0; i < 27; i++) {
			if (!_table.getAllRowsColsBigcells(i).isValid()) {// esli
				// stroka/stolbets/bigcell
				// sodergat bolee
				// odnoy odinakovoy
				// tsfri to
				// isValid=false
				return false;
			}
		}
		return true;
	}

	public boolean isPrintlog() {
		return printLog;
	}

	public int getComplication() {
		return _logic.getComplication();
	}

	public SudokuModel clone() {
		// clonirovat sudok
		SudokuModel tmpsudoku = new SudokuModel();
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				tmpsudoku._table.getCell(col, row).setNumber(
						_table.getCell(col, row).getNumber());
				tmpsudoku._table.getCell(col, row).setStyle(
						_table.getCell(col, row).getStyle());
				tmpsudoku._table.getCell(col, row).setLittleNumbers(
						_table.getCell(col, row).getLettleNumbers());
			}
		}
		return tmpsudoku;
	}

	public void increaseComplication(int style, int currnumber, int col, int row) {
		_logic.increaseComplication(style, currnumber, col, row);
	}

	public Cell getCell(int col, int row) {
		return _table.getCell(col, row);
	}

	public Solve getSolve(int number) {
		return _logic.getVectorOfSolves().get(number);
	}

	public int getSolveCount() {
		return _logic.getVectorOfSolves().size();
	}

	public Solve getNextHint() {
		SudokuModel tmpSudoku = clone();
		tmpSudoku.SolveAll(true);// sna4ala rewaem copiyu sudoku
		for (int i = 0; i < tmpSudoku.getSolveCount(); i++) {// potom
			// dobavlayem
			// hinti v
			// vector
			if (tmpSudoku.getSolve(i).getComplication() == SolveLogic.COMPLICATION_1_NAKEDSINGLE
					|| tmpSudoku.getSolve(i).getComplication() == SolveLogic.COMPLICATION_2_HIDDENSINGLE) {
				return tmpSudoku.getSolve(i);
			}
		}
		return tmpSudoku.getSolve(0);
	}

	public void setTable(Table table) {
		_table = table;
	}

	@Override
	public String toString() {
		return _table.toString();
	}

}

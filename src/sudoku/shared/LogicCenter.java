package sudoku.shared;

import java.util.List;

public class LogicCenter {

	private SudokuModel _sudoku = new SudokuModel();// task
	private SudokuModel _sudokuSolve = new SudokuModel();// solved task
	private SudokuModel _playersTable = new SudokuModel();//players field
	

	public void setNewTable(List<List<Integer>> digits){
		Table table = Table.fromIntMassive(digits);
		_sudoku.setTable(table);
		_playersTable.setTable(table);
		table.tableChanged();
		solveSudoku();
	}

	private void solveSudoku() {
		// srazu rewit sudoku
		_sudokuSolve = _sudoku.clone();
		_sudokuSolve.SolveAll(true);
	}

	public Cell[][] getTask() {
		return _sudoku.getTable()._cells;
	}
	
	public boolean checkUpField(Cell[][] cells){
		Cell[][] solved = _sudokuSolve.getTable()._cells;
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				if(!cells[i][j].equals(solved[j][i]))
					return false;
			}
		}
		return true;
	}

	public Cell[][] getPlayersTable() {
		return _playersTable.getTable()._cells;
	}
	
	public void setPlayerMove(Move move){
		if(move != null && _playersTable != null){
			if(!move.isDeleteAll()){
				Cell cell = _playersTable.getTable()._cells[move.getCellIndex().y][move.getCellIndex().x];
				cell.setNumber(move.getNewNumber());
				cell.setStyle(Cell.SOLVED);
			} else {
				_playersTable.getTable().clearTable();
			}
		}
	}

}

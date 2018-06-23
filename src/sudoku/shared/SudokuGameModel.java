package sudoku.shared;

import games.shared.GameModel;

import java.util.ArrayList;
import java.util.List;

public class SudokuGameModel implements GameModel<ModelListener>{
	
	private final LogicCenter LOGIC_CENTER = new LogicCenter();
	private List<ModelListener> _listeners = new ArrayList<ModelListener>();

//	protected Table _table = new Table(this);
	protected Table _table;

//	protected SolveLogic _logic = new SolveLogic(this);
	
	private int _difficultyLevel = 0;

	protected static boolean printLog = false;
	private final FieldsByLevel _fieldsByLevel;

	public SudokuGameModel(FieldsByLevel fieldsByLevel) {
		_fieldsByLevel = fieldsByLevel;
//		_table.fillTable();
	}
	
	public void addListener(final ModelListener listener) {
		_listeners.add(listener);
	}
	
	public void removeListener(final ModelListener listener) {
		_listeners.remove(listener);
	}

	public void newGame(int level){
		_difficultyLevel = level;
		System.err.println("sudoku game model: new game");
		List<List<Integer>> digits = _fieldsByLevel.getField(level);
		LOGIC_CENTER.setNewTable(digits);
		fireNewGame();
	}

	private void fireNewGame() {
		for (ModelListener listener : _listeners) {
			Cell[][] task = LOGIC_CENTER.getTask();
			listener.newGame(task);
		}
	}

	public void checkField(Cell[][] cells) {
		if(LOGIC_CENTER.checkUpField(cells)){
			fireWin();
		} else {
			fireLoose();
		}
	}
	
	private void fireWin() {
		for (ModelListener listener : _listeners) {
			listener.win();
		}
	}
	
	private void fireLoose() {
		for (ModelListener listener : _listeners) {
			listener.loose();
		}
	}

	public void setMove(Move move) {
		LOGIC_CENTER.setPlayerMove(move);
	}

	public void newMove(Move move) {
		System.err.println("game model: new move");
		LOGIC_CENTER.setPlayerMove(move);
//		_game.setMove(cmd.getMove());
//		broadcast(cmd, _player.getAddress());
	}

	public void reloadGame() {
		// TODO Auto-generated method stub
		
	}


//	private void fireNewGame(int level) {
//		for (ModelListener listener : _listeners) {
//			listener.nextLevel(level);
//		}
//	}
}

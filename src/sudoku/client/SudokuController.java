package sudoku.client;

import games.client.BaseController;
import games.client.BaseView;
import sudoku.shared.Cell;
import sudoku.shared.ModelListener;
import sudoku.shared.Move;
import sudoku.shared.SudokuGameModel;

public class SudokuController extends BaseController<SudokuGameModel> implements ModelListener {

	private final SudokuView _view;

	public SudokuController(SudokuGameModel model, SudokuView view) {
		super(model);
		_model = model;
		_view = view;
	}

	@Override
	protected BaseView getView() {
		return _view;
	}

	@Override
	public void start() {
	}

	public void sendDifficulty(int difficulty) {
		_model.newGame(difficulty);
	}

	@Override
	public void newGame(Cell[][] cells) {
		_view.setTask(cells);
		
	}

	public void newMove(Move move) {
		_model.newMove(move);
	}

	@Override
	public void win() {
		_view.showVictoryMessage();
	}

	@Override
	public void loose() {
		_view.showLossMessage();
	}

	public void sendTask(Cell[][] cells) {
		_model.checkField(cells);
	}

}

package sudoku.client;

import games.client.BaseGame;
import games.client.ClientRandomGenerator;
import games.client.GameController;
import games.client.GameView;
import games.client.StopListener;
import games.shared.GameModel;
import sudoku.shared.FieldsByLevel;
import sudoku.shared.SudokuGameModel;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class Sudoku extends BaseGame {
	
	private FieldsByLevel _fieldsByLevel;

	@Override
	public void loadGame(final AbsolutePanel gamePanel, final boolean isStandalone) {
		final StopListener stopListener = new StopListener(new ScheduledCommand() {
			
			@Override
			public void execute() {
				Sudoku.super.loadGame(gamePanel, isStandalone);
			}
		});
		stopListener.setExpected(1);
		_fieldsByLevel = new FieldsByLevel(new ClientRandomGenerator());
		SudokuHandler.parseAllXMLFiles(stopListener, _fieldsByLevel);
	}

	@Override
	protected GameController createGameController(final GameModel model, final GameView view, final boolean isStandalone) {
		return new SudokuController((SudokuGameModel) model, (SudokuView) view);
	}

	@Override
	protected GameView createGameView() {
		return new SudokuView();
	}

	@Override
	protected GameModel createGameModel() {
		return new SudokuGameModel(_fieldsByLevel);
	}
}

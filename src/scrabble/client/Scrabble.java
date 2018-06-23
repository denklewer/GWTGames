package scrabble.client;

import games.client.BaseGame;
import games.client.ClientRandomGenerator;
import games.client.GameController;
import games.client.GameView;
import games.client.StopListener;
import games.client.util.DictionaryLoader;
import games.shared.GameModel;
import games.shared.RandomGenerator;
import scrabble.shared.ScrabbleDictionary;
import scrabble.shared.ScrabbleModel;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class Scrabble extends BaseGame {

	private RandomGenerator _random;
	private ScrabbleLoader _scrabbleLoader;
	private ScrabbleDictionary _scrabbleDictionary;

	@Override
	public void loadGame(final AbsolutePanel gamePanel, final boolean isStandalone) {
		final ScheduledCommand command = new ScheduledCommand() {

			@Override
			public void execute() {
				Scrabble.super.loadGame(gamePanel, isStandalone);
			}
		};
		final StopListener stopListener = new StopListener(command);
		stopListener.setExpected(3);
		_random = new ClientRandomGenerator();
		_scrabbleDictionary = new ScrabbleDictionary(_random);
		_scrabbleLoader = new ScrabbleLoader();
		_scrabbleLoader.loadBag(stopListener, _random);
		_scrabbleLoader.loadBoard(stopListener);
		DictionaryLoader.loadDictionary(stopListener, _scrabbleDictionary);
	}

	@Override
	protected GameController createGameController(final GameModel model,
			final GameView view, final boolean isStandalone) {
		return new ScrabbleController((ScrabbleModel) model,
				(ScrabbleView) view, _scrabbleDictionary, _scrabbleLoader,
				_random);
	}

	@Override
	protected GameView createGameView() {
		return new ScrabbleView();
	}

	@Override
	protected GameModel createGameModel() {
		return new ScrabbleModel(_random, _scrabbleLoader.getBoard(),
				_scrabbleLoader.getBag(), _scrabbleDictionary);
	}

}

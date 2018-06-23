package sevendoors.client;

import games.client.BaseGame;
import games.client.ClientRandomGenerator;
import games.client.GameController;
import games.client.GameView;
import games.client.StopListener;
import games.shared.GameModel;
import games.shared.RandomGenerator;
import sevendoors.shared.BonusProbabilityInfo;
import sevendoors.shared.SevenDoorsModel;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class SevenDoors extends BaseGame {

	private BonusProbabilityInfo _bonusProbabilityInfo = new BonusProbabilityInfo();
	private RandomGenerator _randomGenerator;

	@Override
	public void loadGame(final AbsolutePanel gamePanel, final boolean isStandalone) {
		final StopListener stopListener = new StopListener(
				new ScheduledCommand() {

					@Override
					public void execute() {
						SevenDoors.super.loadGame(gamePanel, isStandalone);
					}
				});
		stopListener.setExpected(1);
		SevenDoorsSAXHandler.parseBonuses(stopListener, _bonusProbabilityInfo);
		_randomGenerator = new ClientRandomGenerator();
	}

	@Override
	protected GameController createGameController(final GameModel model,
			final GameView view, final boolean isStandalone) {
		return new SevenDoorsController((SevenDoorsModel) model,
				(SevenDoorsView) view, _randomGenerator, _bonusProbabilityInfo);
	}

	@Override
	protected GameView createGameView() {
		return new SevenDoorsView();
	}

	@Override
	protected GameModel createGameModel() {
		return new SevenDoorsModel(_randomGenerator, _bonusProbabilityInfo, new ClientTimer());
	}

}

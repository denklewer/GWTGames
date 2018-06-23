package games.client;

import games.shared.Command;
import games.shared.GameModel;
import games.shared.Processor;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;

public abstract class BaseGame implements EntryPoint {

	private class ClientGameProcessor extends Processor {

		public ClientGameProcessor(final GameController controller) {
			super(controller);
		}
		
		@Override
		protected boolean unknownCommand(final Command command, final Object... params) {
			return false;
		}
		
	}

	private GameView _view;
	private GameController _controller;
	private Processor _processor;

	public void loadGame(final AbsolutePanel gamePanel, final boolean isStandalone){
		final GameModel model = createGameModel();
		_view = createGameView();
		_controller = createGameController(model, _view, isStandalone);
		model.addListener(getController());
		_view.setGamePanel(gamePanel);
		_view.init(getController());
		_processor = new ClientGameProcessor(_controller);
	}
	
	protected abstract GameController createGameController(GameModel model, GameView view, boolean isStandalone);

	protected abstract GameView createGameView();

	protected abstract GameModel createGameModel();

	@Override
	public void onModuleLoad() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				loadGame(RootPanel.get(), true);
			}
		});
	}

	public int getGameWidth() {
		return _view.getGameWidth();
	}

	public GameController getController() {
		return _controller;
	}

	public boolean processCommand(final Command command) {
		return _processor.process(command);
	}
}

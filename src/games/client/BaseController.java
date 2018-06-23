package games.client;

import games.shared.BaseModelListener;
import games.shared.GameModel;
import games.shared.GameModelListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public abstract class BaseController<Model extends GameModel<? extends GameModelListener>> extends GenericController<Model> implements BaseModelListener {

	public abstract class Action implements ScheduledCommand, Finishable {

		private final String _description;
		private boolean _isFinished;

		public Action(final String description) {
			_description = description;
		}

		@Override
		public void execute() {
			System.out.println("executing " + _description);
			run();
		}

		abstract public void run();

		@Override
		public void finished() {
			if (!_isFinished) {
				_isFinished = true;
				System.out.println("finished " + this);
				scheduleNextAction();
			}
		}

		@Override
		public String toString() {
			return _description;
		}

	}
	
	public BaseController(final Model model) {
		super(model);
	}

	protected final List<Action> _moveActions = new ArrayList<Action>();
	private Iterator<Action> _movesIterator;

	@Override
	public void startMove() {
		getView().allowUserInteractions(false);
		_moveActions.clear();
	}

	protected abstract BaseView getView();

	@Override
	public void endMove() {
		_movesIterator = _moveActions.iterator();
		scheduleNextAction();
	}

	public void scheduleNextAction() {
		if (hasMoreActions()) {
			final ScheduledCommand action = _movesIterator.next();
			action.execute();
		} else {
			getView().allowUserInteractions(true);
		}
	}

	public boolean hasMoreActions() {
		return _movesIterator.hasNext();
	}


}

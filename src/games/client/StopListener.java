package games.client;

import games.client.sprites.EngineFinishListener;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public class StopListener implements Stopper, EngineFinishListener {

	private int expected;
	private final ScheduledCommand command;

	public StopListener(final ScheduledCommand command) {
		this.command = command;
	}

	@Override
	public void arrived() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				expected--;
				if (expected <= 0) {
					command.execute();
				}
			}
		});
	}

	@Override
	public int getExpected() {
		return expected;
	}

	@Override
	public void setExpected(final int expected) {
		this.expected = expected;
	}

	@Override
	public void incrementExpectedBy(final int expected) {
		this.expected += expected;
	}
}
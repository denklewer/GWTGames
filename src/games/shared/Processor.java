package games.shared;

public abstract class Processor {

	private final Object _actualProcessor;

	public Processor() {
		_actualProcessor = this;
	}

	public Processor(final Object actualProcessor) {
		_actualProcessor = actualProcessor;
	}

	public final boolean process(final Command command, final Object... params) {
		try {
			if (processCommand(command, params)) {
				return true;
			}
			return unhandledCommand(command, params);
		} catch (final ClassCastException cce) {
			unknownCommand(command, params);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return false;

	}

	protected boolean processCommand(final Command command,
			final Object... params) {
		return command.execute(_actualProcessor, params);
	}

	protected boolean unhandledCommand(final Command command, final Object... params) {
		return unknownCommand(command, params);
	}

	protected boolean unknownCommand(final Command command,
			final Object... params) {
		System.err.println("unknown command : " + command + " in: " + this);
		return true;
	}

}

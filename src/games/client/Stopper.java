package games.client;

import games.client.sprites.EngineFinishListener;

public interface Stopper extends EngineFinishListener {

	public abstract int getExpected();

	public abstract void setExpected(final int expected);

	public abstract void incrementExpectedBy(final int expected);

}
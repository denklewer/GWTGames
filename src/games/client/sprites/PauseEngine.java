package games.client.sprites;

import games.client.util.DrawElement;

public abstract class PauseEngine<T> extends EngineTemplate<T> implements ImageEngine<DrawElement> {

	private final long _stopTime;

	public PauseEngine(final EngineFinishListener stopListener, final Sprite<T> sprite, final int delay) {
		super(stopListener, sprite);
		_stopTime = _lastTime + delay;
	}

	@Override
	protected boolean needsChanges() {
		final long elapse = _lastTime - _stopTime;
		return elapse <= 0;
	}

	@Override
	protected void changeSprite(final long coeff) {
	}

	@Override
	protected long calculateSpriteChangeCoeff(final long elapsed) {
		return 1;
	}

}

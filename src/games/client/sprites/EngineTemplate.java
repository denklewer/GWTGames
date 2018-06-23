package games.client.sprites;

public abstract class EngineTemplate<T> implements Engine {

	private EngineFinishListener _stopListener;
	private final Sprite<T> _sprite;
	protected long _lastTime = -1;

	public EngineTemplate(final EngineFinishListener stopListener,
			final Sprite<T> sprite) {
		_stopListener = stopListener;
		_sprite = sprite;
	}

	protected abstract boolean needsChanges();

	protected abstract void changeSprite(final long coeff);

	protected abstract void finalChanges();

	protected abstract long calculateSpriteChangeCoeff(final long elapsed);

	@Override
	public boolean updateTick(final long timestamp) {
		if (needsChanges()) {
			if (_lastTime == -1) {
				_lastTime = timestamp;
			}
			final long elapsed = timestamp - _lastTime;
			final long coeff = calculateSpriteChangeCoeff(elapsed);
			if (coeff == 0) {
				return false;
			}
			changeSprite(coeff);
			_lastTime = timestamp;
			return true;
		}
		finalChanges();
		_stopListener.arrived();
		return false;
	}

	public Sprite<T> getSprite() {
		return _sprite;
	}

	public EngineFinishListener getStopListener() {
		return _stopListener;
	}

	public void setStopListener(final EngineFinishListener stopListener) {
		_stopListener = stopListener;
	}
}
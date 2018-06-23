package games.client.sprites;

public abstract class MovementEngineTemplate<T> extends EngineTemplate<T> implements MovementEngine {

	protected int x;
	protected int y;
	final int _destX;
	final int _destY;
	private final int _speed;

	public MovementEngineTemplate(final EngineFinishListener stopListener,
			final Sprite<T> sprite, final int destX, final int destY, final int speed) {
		super(stopListener, sprite);
		_destX = destX;
		_destY = destY;
		_speed = speed;
		x = getSprite().getX();
		y = getSprite().getY();
	}

	@Override
	protected void finalChanges() {
		getSprite().moveTo(_destX, _destY);
		getSprite().setDefaultMovementEngine();
	}

	@Override
	protected long calculateSpriteChangeCoeff(final long elapsed) {
		return elapsed / getSpeed();
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getX() {
		return x;
	}

	public int getSpeed() {
		return _speed;
	}

	public int getDestY() {
		return _destY;
	}

	public int getDestX() {
		return _destX;
	}

}
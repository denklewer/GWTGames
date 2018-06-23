package games.client.util;

import games.client.sprites.EngineFinishListener;
import games.client.sprites.MovementEngineTemplate;
import games.client.sprites.Sprite;

public final class FixedSpeedMovementEngine<T> extends MovementEngineTemplate<T>  {
	private final int dx;
	private final int dy;

	public FixedSpeedMovementEngine(final Sprite<T> sprite,
			final EngineFinishListener stopListener, final int destX,
			final int destY, final int speed) {
		super(stopListener, sprite, destX, destY, speed);
		dx = getDestX() - x;
		dy = getDestY() - y;
	}

	@Override
	protected void changeSprite(final long distance) {
		y += Math.signum(dy) * distance;
		x += Math.signum(dx) * distance;
		if (Math.signum(getDestY() - y) != Math.signum(dy)) {
			y = getDestY();
		}
		if (Math.signum(getDestX() - x) != Math.signum(dx)) {
			x = getDestX();
		}
	}

	@Override
	protected boolean needsChanges() {
		final int leftX = Math.abs(x - getDestX());
		final int leftY = Math.abs(y - getDestY());
		return leftX > Math.abs(dx / getSpeed()) || leftY > Math.abs(dy / getSpeed());
	}

}
package games.client.util;

import games.client.sprites.EngineFinishListener;
import games.client.sprites.MovementEngineTemplate;
import games.client.sprites.Sprite;

public final class FallDownMovementEngine<T> extends MovementEngineTemplate<T> {
	int a = 0;

	public FallDownMovementEngine(final EngineFinishListener stopListener,
			final Sprite<T> sprite, final int destY, final int speed) {
		super(stopListener, sprite, sprite.getX(), destY, speed);
	}

	@Override
	protected boolean needsChanges() {
		return y < getDestY();
	}

	@Override
	protected void changeSprite(final long distance) {
		a += distance;
		y += a;
		if (y >= getDestY()) {
			y = getDestY() + 2;
		}
	}

}
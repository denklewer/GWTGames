package games.client.util;

import games.client.sprites.Rectangle;
import games.client.sprites.SpriteGraphics;

import com.google.gwt.canvas.dom.client.Context2d;

public class CanvasSpriteGraphics implements SpriteGraphics<DrawElement> {

	private final Context2d _context;

	public CanvasSpriteGraphics(final Context2d context) {
		_context = context;
	}

	@Override
	public void drawImage(final DrawElement combinedImage, final int offsetX,
			final int offsetY, final int width, final int height,
			final int destX, final int destY, final int destWidth,
			final int destHeight, final Rectangle cliparea) {
		drawImage(combinedImage, offsetX, offsetY, width, height, destX, destY, destWidth, destHeight, cliparea, 1.0);
	}
	
	@Override
	public void drawImage(final DrawElement combinedImage, final int offsetX,
			final int offsetY, final int width, final int height,
			final int destX, final int destY, final int destWidth,
			final int destHeight, final Rectangle cliparea, double alpha) {
		if (combinedImage == null) {
			return;
		}
		final Rectangle destRect = new Rectangle(destX, destY, destWidth,
				destHeight);
		if (!destRect.intersects(cliparea)) {
			return;
		}

		final Rectangle clipRect = destRect.intersection(cliparea);
		_context.save();
		_context.beginPath();
		_context.rect(clipRect.getX(), clipRect.getY(), clipRect.getWidth(),
				clipRect.getHeight());
		_context.clip();
		_context.setGlobalAlpha(alpha);
		try {
			combinedImage.draw(_context, offsetX, offsetY, width, height, destX, destY, destWidth, destHeight);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		_context.restore();
	}

}
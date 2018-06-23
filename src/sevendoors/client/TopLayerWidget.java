package sevendoors.client;

import games.client.sprites.ImageEngine;
import games.client.sprites.Rectangle;
import games.client.sprites.Sprite;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.client.util.UIUtils;
import games.shared.Point;
import games.shared.Resources;

public class TopLayerWidget extends Sprite<DrawElement> {
	
	private static Point[] _keysWidgetsCoords = new Point[] {
		new Point(537, 129),
		new Point(537, 250),
		new Point(537, 376),
		new Point(-9, 129),
		new Point(-9, 250),
		new Point(-9, 376),
		new Point(266, -9),
};

	public TopLayerWidget(final int x, final int y, final int w, final int h) {
		super(x, y, w, h);
		final SpriteImage id_zone = new SpriteImage<DrawElement>(Resources.get(ImageFactory.class).getDrawElement("id-zone"), 600, 568);
		final SpriteImage id_key_bg = new SpriteImage<DrawElement>(Resources.get(ImageFactory.class).getDrawElement("id-key-bg"), 72, 73);
		final SpriteImage id_key_bg_rotated = UIUtils.rotate(Resources.get(ImageFactory.class).getDrawElement("id-key-bg"), 72, 73, UIUtils.ROTATE_270);
		final SpriteImage score_field = new SpriteImage<DrawElement>(Resources.get(ImageFactory.class).getDrawElement("score-field"), 198, 50);
		
		setImageEngine(new ImageEngine<DrawElement>() {

			@Override
			public boolean updateTick(final long timestamp) {
				return false;
			}

			@Override
			public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image,
					final int x, final int y, final Rectangle dirtyRegion) {
				id_zone.draw(graphics, x, y, dirtyRegion);
				for (int i = 0; i < 6; i++) {
					id_key_bg.draw(graphics, x + _keysWidgetsCoords[i].x, y + _keysWidgetsCoords[i].y, dirtyRegion);
				}
				id_key_bg_rotated.draw(graphics, x + _keysWidgetsCoords[6].x, y + _keysWidgetsCoords[6].y, dirtyRegion);
				score_field.draw(graphics, x + 197, y + 520, dirtyRegion);
			}
		});
	}
	
	@Override
	public boolean onMouseDown(final int x, final int y, final int modifiers) {
		return false;
	}
	
	@Override
	public boolean onMouseUp(final int x, final int y, final int modifiers) {
		return false;
	}

}

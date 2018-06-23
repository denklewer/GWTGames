package sevendoors.client;

import games.client.sprites.ImageEngine;
import games.client.sprites.Rectangle;
import games.client.sprites.Sprite;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.client.util.UIUtils;
import games.shared.Resources;

import com.google.gwt.canvas.dom.client.CssColor;

public class NoMoreMovesDialog extends Sprite<DrawElement> {
	
	private static final CssColor WHITE = CssColor.make("rgba(" + 255 + ", " + 255 + "," + 255 + ", " + 0.0d +")");
	private static final CssColor BLACK = CssColor.make("rgba(" + 0 + ", " + 0 + "," + 0 + ", " + 0.3d +")");
	private final SevenDoorsView _parent;
	
	public NoMoreMovesDialog(final SevenDoorsView parent, final int x0, final int y0, final int width, final int height) {
		super(0, 0, parent.getGameWidth(), parent.getGameHeight());
		_parent = parent;

		final DrawElement bkRect = UIUtils.createRect(_parent.getGameWidth(), _parent.getGameHeight(), WHITE);
		final SpriteImage bkRectImage = new SpriteImage(bkRect, bkRect.getWidth(), bkRect.getHeight());
		
		final DrawElement rect = UIUtils.createRect(width, height, BLACK);
		final SpriteImage rectImage = new SpriteImage(rect, width, height);
		
		final DrawElement messageElement = Resources.get(ImageFactory.class).getDrawElement("message-01");
		final SpriteImage messageImage = new SpriteImage(messageElement, messageElement.getWidth(), messageElement.getHeight());
		
		final DrawElement textElement = Resources.get(ImageFactory.class).getDrawElement("message-nomoves-01");
		final SpriteImage textImage = new SpriteImage(textElement, textElement.getWidth(), textElement.getHeight());
		setImageEngine(new ImageEngine<DrawElement>() {
			
			@Override
			public boolean updateTick(final long timestamp) {
				return false;
			}
			
			@Override
			public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image,
					final int x, final int y, final Rectangle dirtyRegion) {
				bkRectImage.draw(graphics, x, y, dirtyRegion);
				rectImage.draw(graphics, x0 + x, y0 + y, dirtyRegion);
				final int messageImageOffsetX = (NoMoreMovesDialog.this.getWidth() - messageImage.width()) / 2;
				final int messageImageOffsetY = (NoMoreMovesDialog.this.getHeight() - messageImage.height()) / 2;
				messageImage.draw(graphics, x + messageImageOffsetX, y + messageImageOffsetY, dirtyRegion);
				
				textImage.draw(graphics, x + messageImageOffsetX, y + messageImageOffsetY, dirtyRegion);
			}
		});
	}
	
	@Override
	public boolean onMouseUp(final int x, final int y, final int modifiers) {
		setVisible(false);
		_parent.startNewGame();
		return true;
	}

}

package scrabble.client;

import games.client.sprites.ImageEngine;
import games.client.sprites.Rectangle;
import games.client.sprites.Sprite;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.client.util.UIUtils;
import games.shared.Resources;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

public class PlayerWidget extends Sprite<DrawElement> {

	private static final SpriteImage placeImage = new SpriteImage(Resources.get(ImageFactory.class).getDrawElement("place-pl-01"), 82, 58);
	private static CssColor GREEN = CssColor.make("rgba(" + 54 + ", " + 148 + "," + 37 + ", " + 1.0d +")");
	private static CssColor LIGHT_GREEN = CssColor.make("rgba(" + 228 + ", " + 251 + "," + 218 + ", " + 1.0d +")");
	private static CssColor WHITE = CssColor.make("rgba(" + 255 + ", " + 255 + "," + 255 + ", " + 0.0d +")");
	private static CssColor BLACK = CssColor.make("rgba(" + 0 + ", " + 0 + "," + 0 + ", " + 1.0d +")");
	private static final String BOLD_12PX_SANS_SERIF = "bold 12px sans-serif";
	
	private String _name;
	private int _score;
	private boolean _isLight;
	private SpriteImage _nameImage;
	private SpriteImage _scoreImage;
	
	public PlayerWidget(final int x, final int y, final String name) {
		super(x, y, placeImage.width(), placeImage.height());
		_name = name;
		final DrawElement nameElement = UIUtils.createCenteredTextElement(_name, placeImage.width(), placeImage.height(), GREEN, WHITE, 0d, BOLD_12PX_SANS_SERIF, 20);
		_nameImage = new SpriteImage(nameElement, nameElement.getWidth(), nameElement.getHeight());
		final DrawElement scoreDrawElement = new DrawElement() {
			
			@Override
			public void draw(final Context2d context, final int offsetX, final int offsetY,
					final int width, final int height, final int destX, final int destY, final int destWidth,
					final int destHeight) {
				context.setFillStyle(WHITE);
				context.fillRect(destX, destY, width, height);
				context.setFillStyle(LIGHT_GREEN);
				context.setFont(BOLD_12PX_SANS_SERIF);
				final int fontHeight = 12;
				context.fillText(_score+"", destX + width - (fontHeight/2) * (_score+"").length() - 7, destY + (height + fontHeight) / 2, width);				
			}
			
			@Override
			public int getWidth() {
				return 65;
			}
			
			@Override
			public int getHeight() {
				return 21;
			}
		};
		
		_scoreImage = new SpriteImage(scoreDrawElement, 65, 21);
		
		setImageEngine(new ImageEngine<DrawElement>() {
			
			@Override
			public boolean updateTick(final long timestamp) {
				return false;
			}
			
			@Override
			public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image,
					final int x, final int y, final Rectangle dirtyRegion) {
				double alpha;
				if(!_isLight){
					alpha = 0.7d;
				} else {
					alpha = 1.0d;
				}
				placeImage.draw(graphics, x, y, dirtyRegion, alpha);
				_nameImage.draw(graphics, x, y - 20, dirtyRegion, alpha);
				_scoreImage.draw(graphics, x + 8, y + 25, dirtyRegion);
			}
		});
		
	}
	
	public static int getPlayerWidgetHeight() {
		return placeImage.height();
	}
	
	public void addScore(final int score) {
		_score += score;
		markDirty();
	}

	public void setLight(final boolean light) {
		_isLight = light;
		markDirty();
	}

	public String getName() {
		return _name;
	}

}

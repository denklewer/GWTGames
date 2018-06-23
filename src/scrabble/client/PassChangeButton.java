package scrabble.client;

import games.client.sprites.Rectangle;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.client.util.UIUtils;
import games.shared.Resources;

import com.google.gwt.canvas.dom.client.CssColor;

public class PassChangeButton extends GameButton {

	public static final int CHANGE = 1;
	public static final int PASS = 2;
	public static final int RESET = 3;
	public static final int SIT = 4;
	public static final int PASS_2 = 5;
	public static final int COURCE_OF_GAME = 6;
	
	private static SpriteImage[] _buttonImages = UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("but-small-01"), 78, 124, 4);
	private static final CssColor COLOR_1 = CssColor.make(65, 105, 225);
	private static CssColor WHITE = CssColor.make("rgba(" + 255 + ", " + 255 + "," + 255 + ", " + 0.0d +")");
	private static final String BOLD_11PX_SANS_SERIF = "bold 11px sans-serif";
	private static final DrawElement COURCE_OF_GAME_ELEMENT = UIUtils.createCenteredTextElement("history", _buttonImages[0].width(), _buttonImages[0].height(), COLOR_1, WHITE, 1.0, BOLD_11PX_SANS_SERIF, 11);
	private static SpriteImage COURCE_OF_GAME_STRING_IMAGE = new SpriteImage(COURCE_OF_GAME_ELEMENT, COURCE_OF_GAME_ELEMENT.getWidth(), COURCE_OF_GAME_ELEMENT.getHeight());

	private static SpriteImage[] _labels = UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("but-name-01"), 78, 256, 8);
	private int _type;
	
	public PassChangeButton(final int type, final int x, final int y) {
		super(x, y, _buttonImages[0].width(), _buttonImages[0].height());
		_type = type;
	}

	@Override
	protected SpriteImage[] getStripe() {
		return _buttonImages;
	}
	
	@Override
	protected void drawFace(final SpriteGraphics graphics, final int x, final int y,
			final Rectangle dirtyRegion) {
		if (_type == CHANGE) {
			if (enabled()) {
				_labels[0].draw(graphics, x, y, dirtyRegion);
			} else {
				_labels[1].draw(graphics, x, y, dirtyRegion);
			}
		} else if (_type == PASS) {
			if (enabled()) {
				_labels[4].draw(graphics, x, y, dirtyRegion);
			} else {
				_labels[5].draw(graphics, x, y, dirtyRegion);
			}
		} else if (_type == PASS_2) {
			if (enabled()) {
				_labels[3].draw(graphics, x, y, dirtyRegion);
			} else {
				_labels[5].draw(graphics, x, y, dirtyRegion);
			}
		} else if (_type == RESET) {
			if (enabled()) {
				_labels[6].draw(graphics, x, y, dirtyRegion);
			} else {
				_labels[7].draw(graphics, x, y, dirtyRegion);
			}
		} else if(_type == SIT){
//			g.drawImage(SIT_STRING_IMAGE, (width-SIT_STRING_IMAGE.getWidth())/2, (height-SIT_STRING_IMAGE.getHeight())/2, null);
		} else if(_type == COURCE_OF_GAME){
			COURCE_OF_GAME_STRING_IMAGE.draw(graphics, x, y, dirtyRegion);
		}
	}
	
	public void changeType(final int type){
		_type = type;
	}
	
}

package scrabble.client;

import games.client.sprites.SpriteImage;
import games.client.util.ImageFactory;
import games.client.util.UIUtils;
import games.shared.Resources;

public class ReadyButton extends GameButton {
	
	static private SpriteImage[] _images = UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("but-ready-01"), 78, 180, 4);

	public ReadyButton(final int x, final int y) {
		super(x, y, _images[0].width(), _images[0].height());
	}

	@Override
	protected SpriteImage[] getStripe() {
		return _images;
	}

}

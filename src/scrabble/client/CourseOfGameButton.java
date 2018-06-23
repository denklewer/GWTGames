package scrabble.client;

import games.client.sprites.SpriteImage;
import games.client.util.ImageFactory;
import games.client.util.UIUtils;
import games.shared.Resources;

public class CourseOfGameButton extends PassChangeButton {
	
	private static SpriteImage[] _buttonSmall2Images = UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("but-small-02"), 85, 124, 4);

	public CourseOfGameButton(final int x, final int y) {
		super(COURCE_OF_GAME, x, y);
		resize(_buttonSmall2Images[0].width(), _buttonSmall2Images[0].height());
	}
	
	@Override
	protected SpriteImage[] getStripe() {
		return _buttonSmall2Images;
	}

}

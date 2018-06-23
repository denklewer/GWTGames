package sevendoors.client;

import games.client.sprites.SpriteGroup;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.client.util.ScoreWidget;
import games.client.util.UIUtils;
import games.shared.Resources;

public class SevenDoorsNegativeScoreWidget extends ScoreWidget {

private static SpriteImage[][] _scoreImages = new SpriteImage[5][];
	
	static {
		final DrawElement digitsStripe = Resources.get(ImageFactory.class).getDrawElement("score-02_frog");
		final int w = digitsStripe.getWidth();
		final int h = digitsStripe.getHeight();
		_scoreImages[0] = UIUtils.splitHorizontalStripeByVerticalTransparentLine(digitsStripe, w, h);
		_scoreImages[1] = UIUtils.splitHorizontalStripeByVerticalTransparentLine(UIUtils.addTransparency(digitsStripe, 0.80), w, h);
		_scoreImages[2] = UIUtils.splitHorizontalStripeByVerticalTransparentLine(UIUtils.addTransparency(digitsStripe, 0.60), w, h);
		_scoreImages[3] = UIUtils.splitHorizontalStripeByVerticalTransparentLine(UIUtils.addTransparency(digitsStripe, 0.40), w, h);
		_scoreImages[4] = UIUtils.splitHorizontalStripeByVerticalTransparentLine(UIUtils.addTransparency(digitsStripe, 0.20), w, h);
	}
	
	public SevenDoorsNegativeScoreWidget(final SpriteGroup parent, final int score) {
		super(parent, score);
	}

	@Override
	public SpriteImage[][] getScoreImages() {
		return _scoreImages;
	}
	
}

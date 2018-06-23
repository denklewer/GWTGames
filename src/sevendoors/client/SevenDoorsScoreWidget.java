package sevendoors.client;

import games.client.sprites.SpriteGroup;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.client.util.ScoreWidget;
import games.client.util.UIUtils;
import games.shared.Resources;
import sevendoors.shared.SevenDoorsConstants;

public class SevenDoorsScoreWidget extends ScoreWidget {
	
	private static SpriteImage[][] _scoreImages = new SpriteImage[5][];
	private static SpriteImage[][] _starScoreImages = new SpriteImage[5][];
	
	static {
		final DrawElement digitsStripe2 = Resources.get(ImageFactory.class).getDrawElement("score-star");
		int w = digitsStripe2.getWidth();
		int h = digitsStripe2.getHeight();
		_starScoreImages[0] = UIUtils.splitHorizontalStripeByVerticalTransparentLine(digitsStripe2, w, h);
		_starScoreImages[1] = UIUtils.splitHorizontalStripeByVerticalTransparentLine(UIUtils.addTransparency(digitsStripe2, 0.80), w, h);
		_starScoreImages[2] = UIUtils.splitHorizontalStripeByVerticalTransparentLine(UIUtils.addTransparency(digitsStripe2, 0.60), w, h);
		_starScoreImages[3] = UIUtils.splitHorizontalStripeByVerticalTransparentLine(UIUtils.addTransparency(digitsStripe2, 0.40), w, h);
		_starScoreImages[4] = UIUtils.splitHorizontalStripeByVerticalTransparentLine(UIUtils.addTransparency(digitsStripe2, 0.20), w, h);

		final DrawElement digitsStripe = Resources.get(ImageFactory.class).getDrawElement("score-02");
		w = digitsStripe.getWidth();
		h = digitsStripe.getHeight();
		_scoreImages[0] = UIUtils.splitHorizontalStripeByVerticalTransparentLine(digitsStripe, w, h);
		_scoreImages[1] = UIUtils.splitHorizontalStripeByVerticalTransparentLine(UIUtils.addTransparency(digitsStripe, 0.80), w, h);
		_scoreImages[2] = UIUtils.splitHorizontalStripeByVerticalTransparentLine(UIUtils.addTransparency(digitsStripe, 0.60), w, h);
		_scoreImages[3] = UIUtils.splitHorizontalStripeByVerticalTransparentLine(UIUtils.addTransparency(digitsStripe, 0.40), w, h);
		_scoreImages[4] = UIUtils.splitHorizontalStripeByVerticalTransparentLine(UIUtils.addTransparency(digitsStripe, 0.20), w, h);
	}

	private boolean _starScore;
	
	public SevenDoorsScoreWidget(final SpriteGroup parent, final int score, final boolean starScore) {
		super(parent, starScore ? score * SevenDoorsConstants.BONUS_MULTIPLIER : score);
		_starScore = starScore;
	}

	@Override
	public SpriteImage[][] getScoreImages() {
		if(_starScore){
			return _starScoreImages;
		}
		return _scoreImages;
	}

}

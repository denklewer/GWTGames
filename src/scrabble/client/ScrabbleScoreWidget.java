package scrabble.client;

import games.client.sprites.SpriteGroup;
import games.client.sprites.SpriteImage;
import games.client.util.ScoreWidget;

public class ScrabbleScoreWidget extends ScoreWidget {

private static SpriteImage[][] _scoreImages;
	
	static {
		_scoreImages = makeTransparencyStripe("score-01", 198, 22);
	}
	
	public ScrabbleScoreWidget(SpriteGroup parent, int score) {
		super(parent, score);
	}

	@Override
	public SpriteImage[][] getScoreImages() {
		return _scoreImages;
	}

}

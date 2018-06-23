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

import java.util.Vector;

import sevendoors.shared.SevenDoorsProtocol;

public class ScoreBoardWidget extends Sprite<DrawElement> {
	
	private static SpriteImage[] _numbers;
	private int _score = 0;
	
	static {
		final DrawElement scoreElement = Resources.get(ImageFactory.class).getDrawElement("score-01");
		_numbers = UIUtils.splitHorizontalStripeByVerticalTransparentLine(scoreElement, scoreElement.getWidth(), scoreElement.getHeight());
	}

	public ScoreBoardWidget(final int x, final int y, final int width, final int height) {
		super(x, y, width, height);
		setImageEngine(new ImageEngine<DrawElement>() {
			
			@Override
			public boolean updateTick(final long timestamp) {
				return false;
			}
			
			@Override
			public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image,
					final int x, final int y, final Rectangle dirtyRegion) {
				final Vector digits = new Vector();
				int score = _score * SevenDoorsProtocol.SCORE_COEFF;
				int fullWidth = 0;

				for (; score > 0 ;score = score /= 10) {
					final int digit = score % 10;
					digits.addElement(new Integer(digit));
					fullWidth += _numbers[digit].width();
				}

				int startx = (getWidth() - fullWidth) / 2;
				for(int i = digits.size() - 1; i >= 0; i--) {
					final int index = (((Integer)digits.elementAt(i)).intValue() + 9) % 10;
					_numbers[index].draw(graphics, x + startx, y + 6, dirtyRegion);
					startx += _numbers[index].width();
				}
				
				if(_score == 0){
					_numbers[9].draw(graphics, x + startx, y + 6, dirtyRegion);
				}
			}
		});
	}
	
	public void setScore(final long score) {
		_score = (int) score;
		markDirty();
	}

}

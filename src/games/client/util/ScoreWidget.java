package games.client.util;

import games.client.sprites.EngineTemplate;
import games.client.sprites.ImageEngine;
import games.client.sprites.Rectangle;
import games.client.sprites.Sprite;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteGroup;
import games.client.sprites.SpriteImage;
import games.shared.Resources;

import java.util.Vector;

import com.google.gwt.user.client.Random;

public abstract class ScoreWidget extends Sprite<DrawElement> {
	
	protected static SpriteImage[][] makeTransparencyStripe(final String digits, final int w, final int h) {
		final SpriteImage[][] scoreImages = new SpriteImage[5][];
		final DrawElement digitsStripe = Resources.get(ImageFactory.class).getDrawElement(digits);
		scoreImages[0] = UIUtils.splitLetters(digitsStripe, w, h);
		scoreImages[1] = UIUtils.splitLetters(UIUtils.addTransparency(digitsStripe, 0.80), w, h);
		scoreImages[2] = UIUtils.splitLetters(UIUtils.addTransparency(digitsStripe, 0.60), w, h);
		scoreImages[3] = UIUtils.splitLetters(UIUtils.addTransparency(digitsStripe, 0.40), w, h);
		scoreImages[4] = UIUtils.splitLetters(UIUtils.addTransparency(digitsStripe, 0.20), w, h);
		return scoreImages;
	}
	
	private static final int _a = 22;
	private static final double _w = 0.045;
	private static final double _v = 0.65;
	private int y0;
	private int x0;
	
	private int _currentTrans = 0;
	private double _phi;
	private Vector<Integer> _digits;
	private final SpriteGroup _parent;
	
	private boolean _isNegative = false;
	
	public abstract SpriteImage[][] getScoreImages();

	private class FlyingEngine extends EngineTemplate<DrawElement> implements ImageEngine<DrawElement> {

		long _ticks;
		
		public FlyingEngine(final Sprite<DrawElement> sprite) {
			super(null, sprite);
		}

		@Override
		public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image,
				final int x, final int y, final Rectangle dirtyRegion) {
			int xx = 0;
			if(_isNegative){
				final SpriteImage minus = getScoreImages()[_currentTrans][10];
				minus.draw(graphics, x, y, dirtyRegion);
				xx += minus.width();
			}
			
			for (int i = 0; i < _digits.size(); i++) {
				final int digit = _digits.elementAt(i).intValue();
				final SpriteImage img = getScoreImages()[_currentTrans][(digit + 9) % 10];
				img.draw(graphics, x + xx, y, dirtyRegion);
				xx += img.width();
			}
		}

		@Override
		protected boolean needsChanges() {
			return true;
		}

		@Override
		protected void changeSprite(final long coeff) {
			_ticks += coeff/7;
			final int yy = y0 - (int) (_v * _ticks);
			final int xx = x0 + (int) (_a * Math.sin(_w * _ticks + _phi));
			if (yy < y0 - 300) {
				_parent.removeSprite(getSprite());
			}
			_currentTrans = Math.abs((yy - y0)) >> 6;
			ScoreWidget.this.moveTo(xx, yy);
		}

		@Override
		protected void finalChanges() {
		}

		@Override
		protected long calculateSpriteChangeCoeff(final long elapsed) {
			return elapsed;
		}
		
	}
	
	public ScoreWidget(final SpriteGroup parent, final int score) {
		super(0, 0, 30, 30);
		_isNegative = score < 0;
		_phi = Random.nextDouble() * Math.PI;
		_parent = parent;
		_digits = Tool.getDigits(Math.abs(score));
		int width = 0;
		if(_isNegative){
			width += getScoreImages()[0][10].width();
		}
		for (int i = 0; i < _digits.size(); i++) {
			final int digit = _digits.elementAt(i).intValue();
			width += getScoreImages()[0][(digit + 9) % 10].width();
		}
		final int height = getScoreImages()[0][0].height();
		resize(width, height);
		
		setImageEngine(new FlyingEngine(this));
	}


	public void setY0(final int y0) {
		this.y0 = y0;
	}

	public void setX0(final int x0) {
		this.x0 = x0;
	}

}

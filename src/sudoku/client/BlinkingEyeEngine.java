package sudoku.client;

import games.client.sprites.EngineTemplate;
import games.client.sprites.ImageEngine;
import games.client.sprites.Rectangle;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.client.util.UIUtils;
import games.shared.Resources;
import sudoku.shared.CommonConstants;

import com.google.gwt.user.client.Random;

public class BlinkingEyeEngine extends EngineTemplate<DrawElement> implements ImageEngine<DrawElement> {
	
	final SpriteImage[] _eyesStripe;
	int _currentImage = 0;
	int _movement = CommonConstants.EYES_CLOSING;
	boolean _isBlinking = true;
	private int _ticks;
	private int _delayCoef;
	
	public BlinkingEyeEngine() {
		super(null, null);
		final DrawElement eyesElement = Resources.get(ImageFactory.class).getDrawElement("eyes");
		_eyesStripe = UIUtils.cutSpriteVertically(eyesElement, eyesElement.getWidth(), eyesElement.getHeight(), 4);
		_delayCoef = 2 + Math.abs(Random.nextInt(15000));
	}

	@Override
	public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image, final int x, final int y, final Rectangle dirtyRegion) {
		_eyesStripe[_currentImage].draw(graphics, x, y, dirtyRegion);
	}

	@Override
	protected boolean needsChanges() {
		return true;
	}

	@Override
	protected void changeSprite(final long coeff) {
	}

	@Override
	protected void finalChanges() {
	}

	@Override
	protected long calculateSpriteChangeCoeff(final long elapsed) {
		_ticks += elapsed;
		final int ticks = _ticks;
		if(_ticks / (CommonConstants.EYES_PAUSE*_delayCoef) > 0){
			_isBlinking = true;
			_ticks = 0;
		}
		if(_isBlinking && ticks / CommonConstants.EYES_FREQUENCY > 0){
			if(_movement == CommonConstants.EYES_CLOSING) {
				_currentImage++;
			} else {
				_currentImage--;
			}
			_ticks = 0;
			if(_currentImage <= 0 || _currentImage >= _eyesStripe.length - 1){
				_isBlinking = false;
				_movement = _movement == CommonConstants.EYES_CLOSING ? CommonConstants.EYES_OPENING : CommonConstants.EYES_CLOSING;
				_delayCoef = 3 + Math.abs(Random.nextInt(1500));
			}
			return 1;
		} 
		return 0;
	}
}
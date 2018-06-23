package sudoku.client;

import games.client.sprites.EngineTemplate;
import games.client.sprites.ImageEngine;
import games.client.sprites.Rectangle;
import games.client.sprites.Sprite;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.UIUtils;
import sudoku.shared.CommonConstants;

import com.google.gwt.user.client.Random;

public class SpiderSprite extends Sprite<DrawElement> {

	protected class SpiderEngine extends EngineTemplate<DrawElement> implements ImageEngine<DrawElement> {

		private SpriteImage[] _spiderImages;
		private SpriteImage _webLineImage;
		private int _webLineLength = 0;
		private int _curImage = 0;
		private int _status = CommonConstants.GETTING_DOWN;
		private int _wayLength;
		private int _currentWay;
		private int _ticks;
		private int _delayCoef;
		private boolean _isMoving = true;

		public SpiderEngine(SpriteImage[] spiderImages, SpriteImage webLineImage, int length) {
			super(null, null);
			_spiderImages = spiderImages;
			_webLineImage = webLineImage;
			_wayLength = length <= CommonConstants.DEFAULT_WAY * 12 ? CommonConstants.DEFAULT_WAY : length / 12;
			_currentWay = _wayLength;
			_delayCoef = 2 + Math.abs(5000);
		}

		@Override
		public <Z> void draw(SpriteGraphics<Z> graphics, SpriteImage<Z> image, int x, int y, Rectangle dirtyRegion) {
			if (_curImage > 0) {
				_webLineImage.draw(graphics, x, y, _webLineImage.width(), _webLineLength, dirtyRegion);
				_spiderImages[_curImage % 4].draw(graphics, x, y + CommonConstants.PIXELS_PER_FRAME * _curImage, dirtyRegion);
			} else {
				_spiderImages[0].draw(graphics, x, y, dirtyRegion);
			}
		}

		@Override
		protected boolean needsChanges() {
			return true;
		}

		@Override
		protected void changeSprite(long coeff) {
		}

		@Override
		protected void finalChanges() {
		}
		
		@Override
		protected long calculateSpriteChangeCoeff(long elapsed) {
			_ticks += elapsed;
			int ticks = _ticks;
			
			if(_ticks / (CommonConstants.SPIDER_PAUSE*_delayCoef) > 0){
				_isMoving = true;
				_ticks = 0;
			}
			if(_isMoving && ticks / CommonConstants.SPIDER_FREQUENCY > 0){
				if(_status == CommonConstants.GETTING_DOWN){
					_curImage++;
					_webLineLength += CommonConstants.PIXELS_PER_FRAME;
				} else{
					_curImage--;
					_webLineLength -= CommonConstants.PIXELS_PER_FRAME;
				}
				_ticks = 0;
				SpiderSprite.this.markDirty();
				if((_curImage <= 0 && _status == CommonConstants.GETTING_UP) || 
						(_curImage >= (_spiderImages.length*_currentWay) - 1 && _status == CommonConstants.GETTING_DOWN)){
					_isMoving = false;
					_status = _status == CommonConstants.GETTING_DOWN ? CommonConstants.GETTING_UP : CommonConstants.GETTING_DOWN;
					_delayCoef = 7 + Math.abs(Random.nextInt(5000));
					if(_wayLength != CommonConstants.DEFAULT_WAY && _status == CommonConstants.GETTING_DOWN){
						_currentWay = Math.abs(Random.nextInt()%_wayLength);
						_currentWay = _currentWay > CommonConstants.DEFAULT_WAY ? _currentWay : CommonConstants.DEFAULT_WAY;
					}
				}
				return 1;
			} 
			return 0;
		}
	}

	public SpiderSprite(final DrawElement spiderElement, int x, int y, final int webLength) {
		super(x, y, spiderElement.getWidth(), spiderElement.getHeight() + webLength);
		
		SpriteImage[] spiderImages = UIUtils.cutSpriteVertically(spiderElement, spiderElement.getWidth(), spiderElement.getHeight(), 4);
		DrawElement webLineElement = UIUtils.createVerticalLine(spiderElement.getWidth() / 2 - 1, 0, spiderElement.getWidth() / 2 - 1,
				CommonConstants.GAME_HEIGHT, spiderElement.getWidth(), CommonConstants.GAME_HEIGHT, SudokuClientConstants.NET_COLOR);
		SpriteImage webLineImage = new SpriteImage(webLineElement, webLineElement.getWidth(), webLineElement.getHeight());

		setImageEngine(new SpiderEngine(spiderImages, webLineImage, webLength));
	}

}

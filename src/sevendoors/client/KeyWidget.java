package sevendoors.client;

import games.client.sprites.EngineFinishListener;
import games.client.sprites.EngineTemplate;
import games.client.sprites.ImageEngine;
import games.client.sprites.Rectangle;
import games.client.sprites.Sprite;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.shared.Point;
import games.shared.Resources;

public class KeyWidget extends Sprite<DrawElement> {

	private static Point[][] _coords = {
		{
			new Point(17, 33),
			new Point(27, 56),
			new Point(49, 56),
			new Point(59, 37),
			new Point(38, 20),
			new Point(36, 36),
			new Point(36, 36)
		},

		{
			new Point(18, 109),
			new Point(55, 184),
			new Point(36, 270),
			new Point(36, 331),
			new Point(36, 415),
			new Point(43, 464),
			new Point(32, 532)
		},

		{
			new Point(29, 99),
			new Point(45, 172),
			new Point(37, 258),
			new Point(56, 330),
			new Point(19, 400),
			new Point(36, 492),
			new Point(36, 542)
		},

		{
			new Point(36, 125),
			new Point(58, 187),
			new Point(47, 260),
			new Point(17, 327),
			new Point(29, 392),
			new Point(40, 455),
			new Point(38, 545)
		},

		{
			new Point(51, 114),
			new Point(41, 199),
			new Point(23, 242),
			new Point(23, 336),
			new Point(42, 378),
			new Point(36, 466),
			new Point(36, 551)
		},

		{
			new Point(36, 127),
			new Point(26, 186),
			new Point(48, 262),
			new Point(53, 317),
			new Point(36, 390),
			new Point(20, 462),
			new Point(36, 547)
		},

		{
			new Point(55, 108),
			new Point(18, 181),
			new Point(50, 241),
			new Point(21, 339),
			new Point(52, 411),
			new Point(24, 458),
			new Point(36, 546)
		}

	};

	static {
		for (int i = 1; i < 7; i++) {
			for (int j = 0; j < 7; j++) {
				_coords[i][j] = new Point(_coords[i][j].x, _coords[i][j].y - (j+1) * 72);
			}
		}
	}

	private static final int T = 250;
	private static final EngineFinishListener FAKE_STOP_LISTENER = new EngineFinishListener() {
		@Override
		public void arrived() {
		}
	};

	private CircleTrajectory _circleTrajectory = new CircleTrajectory();
	private KeysWidget _keysWidget;
	private boolean _onItsPlace = true;
	private int _keyNum;
	private int _keysElementNum;

	private static SpriteImage _keyLight;
	
	class KeyImageEngine extends EngineTemplate<DrawElement> implements ImageEngine<DrawElement> {

		private int _ticks;
		
		public KeyImageEngine() {
			super(FAKE_STOP_LISTENER, KeyWidget.this);
		}

		@Override
		public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image,
				final int x, final int y, final Rectangle dirtyRegion) {
			if (_onItsPlace){
				_keyLight.draw(graphics, x, y, dirtyRegion);
			}
			final int shiftX = _keyLight.width() / 2 - _coords[_keysElementNum][_keyNum].x;
			final int shiftY = _keyLight.height() / 2 -  _coords[_keysElementNum][_keyNum].y;
			image.draw(graphics, x + shiftX, y + shiftY, dirtyRegion);			
		}

		@Override
		protected boolean needsChanges() {
			if (!_onItsPlace) {
				return false;
			}
			return _ticks <= T;
		}

		@Override
		protected void changeSprite(final long coeff) {
			final int xx = _circleTrajectory.getX(_ticks); 
			final int yy = _circleTrajectory.getY(_ticks);
			moveTo(xx, yy);			
		}

		@Override
		protected void finalChanges() {
			_onItsPlace = false;
			moveTo(_keysWidget.getX() +  _coords[_keysElementNum][_keyNum].x - _keyLight.width()/2, _keysWidget.getY() +  _coords[_keysElementNum][_keyNum].y - _keyLight.height()/2);			
		}

		@Override
		protected long calculateSpriteChangeCoeff(final long elapsed) {
			_ticks += elapsed / 7;
			return elapsed;
		}
		
	}
	
	static {
		_keyLight = new SpriteImage<DrawElement>(Resources.get(ImageFactory.class).getDrawElement("key-light"), 62, 62);
	}

	public KeyWidget(final KeysWidget keysWidget, final int x, final int y, final int keyNum) {
		super(keysWidget.getImage(keyNum), x, y, 62, 62);
		_keysWidget = keysWidget;
		_keyNum = keyNum;
		_keysElementNum = keysWidget.getNum();
		setImageEngine(new KeyImageEngine());
	}

	public void calculateTrajectory() {
		_circleTrajectory.calculate(getX(), getY(), _keysWidget.getX() +  _coords[_keysElementNum][_keyNum].x - _keyLight.width()/2, _keysWidget.getY() +  _coords[_keysElementNum][_keyNum].y - _keyLight.height()/2, T);
	}

}

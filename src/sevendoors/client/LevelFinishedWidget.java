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

public class LevelFinishedWidget extends Sprite<DrawElement> {

	private static final int G = 2;
	private static final int V0 = -40;
	private static final int WAIT_TIME = 500;
	

	private static SpriteImage _message = new SpriteImage(Resources.get(ImageFactory.class).getDrawElement("message-01"), 239, 244);
	
	private static SpriteImage[] _messageKey = UIUtils.cutSpriteVertically(UIUtils.addTransparency(Resources.get(ImageFactory.class).getDrawElement("message-key-01"), 0.2), 239, 1708, 7);
	
	private static SpriteImage _messageFindKey = new SpriteImage(Resources.get(ImageFactory.class).getDrawElement("message-findkey-01"), 239, 244);
	
	private int _key;

	private int _v;
	private boolean _stopped;
	private int _ticksWhenStopped;
	private final SevenDoorsView _parent;

	public LevelFinishedWidget(final SevenDoorsView parent, final int key) {
		super(0, 0, _message.width(), _message.height());
		_parent = parent;
		_key = key;
		_v = V0;
		
		setImageEngine(new ImageEngine<DrawElement>() {
			
			protected long _lastTime = -1;
			private int _ticks;
			
			@Override
			public boolean updateTick(final long timestamp) {
				if (_lastTime == -1) {
					_lastTime = timestamp;
				}
				final long elapsed = timestamp - _lastTime;
				_ticks += elapsed;
				_lastTime = timestamp;
				if (_stopped) {
					if (_ticks - _ticksWhenStopped > WAIT_TIME) {
						_stopped = false;
					}
					return false;
				}
				_v += G;
				if (_v > 0 && _v - G <= 0) {
					_stopped = true;
					_ticksWhenStopped = _ticks;
				}
				moveTo(getX(), getY() + _v);
				if (getY() > SevenDoorsView.PANE_SIZE + 200) {
					_parent.removeTopLayerSprite(LevelFinishedWidget.this);
				}
				markDirty();
				return true;
			}
			
			@Override
			public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image,
					final int x, final int y, final Rectangle dirtyRegion) {
				_message.draw(graphics, x, y, dirtyRegion);
				_messageKey[_key - 1].draw(graphics, x, y, dirtyRegion);
				_messageFindKey.draw(graphics, x, y, dirtyRegion);
			}
		});
	}
	
}

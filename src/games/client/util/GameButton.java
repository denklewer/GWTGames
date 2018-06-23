package games.client.util;

import games.client.sprites.ImageEngine;
import games.client.sprites.Rectangle;
import games.client.sprites.Sprite;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteImage;

import com.google.gwt.user.client.Timer;


public class GameButton extends Sprite<DrawElement> {

	protected SpriteImage[] _unpressed;
	protected SpriteImage[] _pressed;
	
	protected boolean _showPressedImage = false;
	protected Runnable _action;
	private int _currentFrame;

	public GameButton(int x, int y, SpriteImage<DrawElement> unpressed, SpriteImage<DrawElement> pressed, Runnable action) {
		super(x, y, unpressed.width(), unpressed.height());
		_unpressed = new SpriteImage[] {unpressed};
		_pressed = new SpriteImage[] {pressed};
		_action = action;
		setImageEngine(new ImageEngine<DrawElement>() {
			
			@Override
			public boolean updateTick(long timestamp) {
				return false;
			}
			
			@Override
			public <Z> void draw(SpriteGraphics<Z> graphics, SpriteImage<Z> image,
					int x, int y, Rectangle dirtyRegion) {
				preDraw(graphics, x, y, dirtyRegion);
				if(_currentFrame >= _pressed.length || _currentFrame < 0){
					_currentFrame = 0;
				}
				if(_showPressedImage){
					_pressed[_currentFrame].draw(graphics, x, y, dirtyRegion);
				} else {
					_unpressed[_currentFrame].draw(graphics, x, y, dirtyRegion);
				}
				extraDraw(graphics, x, y, dirtyRegion);
			}
		});
		new Timer() {

			private int _sgn = 1;
			
			@Override
			public void run() {
				if(_pressed.length == 1){
					return;
				}
				_currentFrame = _currentFrame + 1 * _sgn;
				if(_currentFrame < 1 || _currentFrame > _pressed.length - 2){
					_sgn = -_sgn;
				}
				markDirty();
			}
		}.scheduleRepeating(200);
	}
	
	protected void extraDraw(SpriteGraphics graphics, int x, int y, Rectangle dirtyRegion){
		// to override
	}
	
	protected void preDraw(SpriteGraphics graphics, int x, int y, Rectangle dirtyRegion){
		// to override
	}
	
	@Override
	public boolean onMouseDown(int x, int y, int modifiers) {
		_showPressedImage = true;
		markDirty();
		return true;
	}
	
	@Override
	public boolean onMouseUp(int x, int y, int modifiers) {
		_showPressedImage = false;
		_action.run();
		markDirty();
		return true;
	}

}

package scrabble.client;

import games.client.sprites.ImageEngine;
import games.client.sprites.Rectangle;
import games.client.sprites.Sprite;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;

public abstract class GameButton extends Sprite<DrawElement> {
	
	private boolean _enabled;
	private boolean _isPressed;
	private Runnable _action;
	
	public GameButton(int x, int y, int width, int height) {
		super(x, y, width, height);
		
		setImageEngine(new ImageEngine<DrawElement>() {
			
			@Override
			public boolean updateTick(long timestamp) {
				return false;
			}
			
			@Override
			public <Z> void draw(SpriteGraphics<Z> graphics, SpriteImage<Z> image,
					int x, int y, Rectangle dirtyRegion) {
				if (_enabled) {
					if (isOver()) {
						if (_isPressed) {
							getStripe()[2].draw(graphics, x, y, dirtyRegion);
						} else {
							getStripe()[1].draw(graphics, x, y, dirtyRegion);
						}
					} else {
						getStripe()[0].draw(graphics, x, y, dirtyRegion);
					}
				} else {
					getStripe()[3].draw(graphics, x, y, dirtyRegion);
				}
				drawFace(graphics, x, y, dirtyRegion);
			}
		});
	}
	
	protected void drawFace(SpriteGraphics graphics, int x, int y, Rectangle dirtyRegion){
		
	}
	
	public void setEnabled(boolean enabled) {
		_enabled = enabled;
		markDirty();
	}
	
	protected boolean enabled() {
		return _enabled;
	}
	
	public void setAction(Runnable action) {
		_action = action;
	}
	
	@Override
	public boolean onMouseDown(int x, int y, int modifiers) {
		if (enabled()) {
			_isPressed = true;
			markDirty();
		}
		return true;
	}

	@Override
	public boolean onMouseUp(int x, int y, int modifiers) {
		if (enabled()) {
			_isPressed = false;
			_action.run();
			markDirty();
		}
		return true;
	}
	
	@Override
	public boolean onMouseEntered(int x, int y) {
		markDirty();
		return true;
	}
	
	@Override
	public boolean onMouseExit() {
		markDirty();
		return true;
	}
	
	protected abstract SpriteImage[] getStripe(); 
}

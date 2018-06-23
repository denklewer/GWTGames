package games.client.util;

import games.client.sprites.Sprite;
import games.client.sprites.SpriteImage;

public class SimpleButton extends Sprite<DrawElement>{
	
	public final static int STATE_NORMAL = 0;
	public final static int STATE_MOUSE_OVER = 1;
	public final static int STATE_MOUSE_PRESSED = 2;
	public final static int STATE_DISABLED = 3;

	private Runnable _action;
	private int _stripeHeight;
	protected SpriteImage[] _buttonFaces;
	
	protected int _state = STATE_NORMAL;

	public SimpleButton(int x, int y, int width, int height, DrawElement stripe, Runnable action, int parts) {
		super(x, y, width, height/parts);
		init(height, action);
		createButtonFaces(stripe, parts);
		updateSpriteImage();
	}
	
	public SimpleButton(int x, int y, int width, int height, SpriteImage[] buttonFaces, Runnable action) {
		super(x, y, width, height);
		init(height, action);
		_buttonFaces = buttonFaces;
		updateSpriteImage();
	}

	private void init(int height, Runnable action) {
		_stripeHeight = height;
		_action = action;
	}

	private void createButtonFaces(DrawElement stripe, int parts) {
		_buttonFaces = UIUtils.cutSpriteVertically(stripe, getWidth(), _stripeHeight, parts);
	}
	
	@Override
	public boolean onMouseDown(int x, int y, int modifiers) {
		if (_state == STATE_DISABLED)
			return false;
		_state = STATE_MOUSE_PRESSED;
		updateSpriteImage();
		return true;
	}
	
	@Override
	public boolean onMouseEntered(int x, int y) {
		if (_state == STATE_DISABLED)
			return false;
		_state = STATE_MOUSE_OVER;
		updateSpriteImage();
		return true;
	}
	
	@Override
	public boolean onMouseExit() {
		if (_state == STATE_DISABLED)
			return false;
		_state = _state == STATE_MOUSE_PRESSED ? STATE_MOUSE_PRESSED : STATE_NORMAL;
		updateSpriteImage();
		return true;
	}
	
	@Override
	public boolean onMouseUp(int x, int y, int modifiers) {
		if (_state == STATE_DISABLED)
			return false;
		_state = contains(x, y) ? STATE_MOUSE_OVER : STATE_NORMAL;
		updateSpriteImage();
		if (_action != null && contains(x, y)) {
			_action.run();
		}
		return true;
	}
	
	public void setDisabled() {
		if (_state != STATE_DISABLED) {
			_state = STATE_DISABLED;
			updateSpriteImage();
		}
	}
	
	public void setEnabled() {
		if (_state != STATE_NORMAL) {
			_state = STATE_NORMAL;
			updateSpriteImage();
		}
	}
	
	public void setEnabled(boolean value) {
		if (value) {
			_state = STATE_NORMAL;
		} else {
			_state = STATE_DISABLED;
		}
		updateSpriteImage();
	}

	public int getState() {
		return _state;
	}
	
	public boolean isDisabled() {
		return _state == STATE_DISABLED;
	}
	
	protected void updateSpriteImage(){
		setSpriteImage(_buttonFaces[_state]);
	}
}


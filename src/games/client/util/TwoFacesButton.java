package games.client.util;

import games.client.sprites.SpriteImage;

public class TwoFacesButton extends GameButton {

	public TwoFacesButton(int x, int y, SpriteImage<DrawElement> unpressed,
			SpriteImage<DrawElement> pressed, Runnable action) {
		super(x, y, unpressed, pressed, action);
	}
	
	@Override
	public boolean onMouseDown(int x, int y, int modifiers) {
		return true;
	}
	
	@Override
	public boolean onMouseUp(int x, int y, int modifiers) {
		_showPressedImage = !_showPressedImage;
		_action.run();
		markDirty();
		return true;
	}

}

package sudoku.client;

import games.client.sprites.ImageEngine;
import games.client.sprites.Rectangle;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.SimpleButton;

public class SudokuSimpleButton extends SimpleButton {

	private SpriteImage _buttonImage;

	public SudokuSimpleButton(int x, int y, int width, int height, DrawElement buttonImage, SpriteImage<DrawElement>[] buttonBackgroundImages,
			Runnable action) {
		super(x, y, width, height, buttonBackgroundImages, action);

		_buttonImage = new SpriteImage(buttonImage, buttonImage.getWidth(), buttonImage.getHeight());
		
		setImageEngine(new ImageEngine<DrawElement>() {
			@Override
			public boolean updateTick(long timestamp) {
				return false;
			}

			@Override
			public <Z> void draw(SpriteGraphics<Z> graphics, SpriteImage<Z> image, int x, int y, Rectangle dirtyRegion) {
				_buttonFaces[_state].draw(graphics, x, y, dirtyRegion);
				_buttonImage.draw(graphics, x, y, dirtyRegion);
			}
		});
	}

}

package games.client.sprites;

public class SpriteImage<T> {

	private final T _image;
	private final int _offsetX;
	private final int _offsetY;
	private final int _width;
	private final int _height;

	public SpriteImage(final T image, final int width, final int height) {
		this(image, 0, 0, width, height);
	}

	public SpriteImage(final T combinedImage, final int offsetX,
			final int offsetY, final int width, final int height) {
		_image = combinedImage;
		_offsetX = offsetX;
		_offsetY = offsetY;
		_width = width;
		_height = height;
	}

	public void draw(final SpriteGraphics<T> graphics, final int x,
			final int y, final Rectangle clipArea, double alpha) {
		graphics.drawImage(_image, _offsetX, _offsetY, _width, _height, x, y,
				_width, _height, clipArea, alpha);
	}
	
	public void draw(final SpriteGraphics<T> graphics, final int x,
			final int y, final Rectangle clipArea) {
		graphics.drawImage(_image, _offsetX, _offsetY, _width, _height, x, y,
				_width, _height, clipArea);
	}

	public void draw(final SpriteGraphics<T> graphics, final int x,
			final int y, final int width, final int height,
			final Rectangle clipArea) {
		graphics.drawImage(_image, _offsetX, _offsetY, _width, _height, x, y,
				width, height, clipArea);
	}

	public int width() {
		return _width;
	}

	public int height() {
		return _height;
	}

	public T getImage() {
		return _image;
	}

}

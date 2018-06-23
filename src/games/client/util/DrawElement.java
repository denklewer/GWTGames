package games.client.util;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;

public abstract class DrawElement<T> {
	
	private static class ImageDrawElement extends DrawElement<ImageElement>{

		public ImageDrawElement(ImageElement imageElement) {
			_image = imageElement;
		}
		
		@Override
		public void draw(Context2d context, int offsetX, int offsetY,
				int width, int height, int destX, int destY, int destWidth,
				int destHeight) {
			context.drawImage(_image, offsetX, offsetY, width, height,
					destX, destY, destWidth, destHeight);
		}

		@Override
		public int getWidth() {
			return _image.getWidth();
		}

		@Override
		public int getHeight() {
			return _image.getHeight();
		}

	}
	
	private static class CanvasDrawElement extends DrawElement<CanvasElement>{

		public CanvasDrawElement(CanvasElement canvasElement) {
			_image = canvasElement;
		}
		
		@Override
		public void draw(Context2d context, int offsetX, int offsetY,
				int width, int height, int destX, int destY, int destWidth,
				int destHeight) {
			context.drawImage(_image, offsetX, offsetY, width, height,
					destX, destY, destWidth, destHeight);
		}
		
		@Override
		public int getWidth() {
			return _image.getWidth();
		}

		@Override
		public int getHeight() {
			return _image.getHeight();
		}
		
	}
	
	public static DrawElement createDrawElement(ImageElement imageElement) {
		return new ImageDrawElement(imageElement);
	}

	public static DrawElement createDrawElement(CanvasElement canvasElement) {
		return new CanvasDrawElement(canvasElement);
	}

	protected T _image;

	public abstract void draw(final Context2d context, final int offsetX,
			final int offsetY, final int width, final int height,
			final int destX, final int destY, final int destWidth,
			final int destHeight);

	public void draw(Context2d context){
		draw(context, 0, 0, getWidth(), getHeight(), 0, 0, getWidth(), getHeight());
	}

	public abstract int getWidth();
	
	public abstract int getHeight();
	
}

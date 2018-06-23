package games.client.util;

import games.client.sprites.Rectangle;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteImage;

import java.util.Vector;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.ImageData;

public class UIUtils {
	
	private static final String BOLD_14PX_SANS_SERIF = "bold 14px sans-serif";

	public static final double ROTATE_270 = 4.71;
	public static final double ROTATE_180 = 3.14;
	public static final double ROTATE_90 = 1.57;
	
	private UIUtils() {
	}
	
	public static DrawElement addTransparency(DrawElement element, double coeff){
		final int width = element.getWidth();
		final int height = element.getHeight();
		final Canvas canvas = createCanvas(width, height);
		final Context2d context = canvas.getContext2d();
		context.setGlobalAlpha(coeff);
		element.draw(context);
		return DrawElement.createDrawElement(canvas.getCanvasElement());
	}
	
	public static DrawElement bright(DrawElement element, double brightCoeff){
		final int width = element.getWidth();
		final int height = element.getHeight();
		final Canvas canvas = createCanvas(width, height);
		final Context2d context = canvas.getContext2d();
		element.draw(context);
		ImageData imageData = context.getImageData(0, 0, width, height);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				imageData.setRedAt((int) (imageData.getRedAt(i, j) * brightCoeff), i, j);
				imageData.setGreenAt((int) (imageData.getGreenAt(i, j) * brightCoeff), i, j);
				imageData.setBlueAt((int) (imageData.getBlueAt(i, j) * brightCoeff), i, j);
			}
		}
		context.putImageData(imageData, 0, 0);
		return DrawElement.createDrawElement(canvas.getCanvasElement());
	}
	
	public static SpriteImage<DrawElement>[] cutSpriteVertically(DrawElement element, int width, int height, int parts){
		SpriteImage<DrawElement>[] result = new SpriteImage[parts];
		int newHeight = height/parts;
		for (int i = 0; i < parts; i++) {
			result[i] = new SpriteImage<DrawElement>(element, 0, i * newHeight, width, newHeight);
		}
		return result;
	}

	public static SpriteImage<DrawElement>[] cutStripeHorizontally(DrawElement drawElement, int width, int height, int alphabetSize) {
		SpriteImage<DrawElement>[] result = new SpriteImage[alphabetSize];
		int newWidth = width/alphabetSize;
		for (int i = 0; i < alphabetSize; i++) {
			result[i] = new SpriteImage<DrawElement>(drawElement, i * newWidth, 0, newWidth, height);
		}
		return result;
	}

	public static SpriteImage[] splitLetters(DrawElement drawElement, int w, int h) {
		Vector<SpriteImage> letters = new Vector<SpriteImage>();
		int lastx = 0;
		
		final Canvas canvas = createCanvas(w, h);
		final Context2d context = canvas.getContext2d();
		drawElement.draw(context);
		final ImageData data = context.getImageData(0, 0, w, h);
		
		for(int i = 0; i < w; i++){
			int count = 0;
			for(int j = 0; j < h; j++){
				if(data.getAlphaAt(i, j) == 0){
					count++;
				}
				else
					break;
			}
			if (count == h) {
				if((i - lastx) > 1) {
					SpriteImage image = new SpriteImage(drawElement, lastx, 0, i - lastx, h);
					letters.addElement(image);
				}
				lastx = i + 1; 
			}
		}
		
		if (lastx < w) {
			SpriteImage image = new SpriteImage(drawElement, lastx, 0, w - lastx, h);
			letters.addElement(image);
		}
		
		SpriteImage[] lettersImages = new SpriteImage[letters.size()]; 
		
		for(int i = 0; i < letters.size(); i++)
			lettersImages[i] = letters.elementAt(i);
		
		return lettersImages;
	}

	public static Canvas createCanvas(int w, int h) {
		final Canvas canvas = Canvas.createIfSupported();
		canvas.setWidth(w + "px");
		canvas.setHeight(h + "px");
		canvas.setCoordinateSpaceWidth(w);
		canvas.setCoordinateSpaceHeight(h);
		return canvas;
	}
	
	public static SpriteImage<DrawElement> rotate(DrawElement drawElement, int width, int height, double angle) {
		DrawElement image = rotateDrawElement(drawElement, width, height, angle);
		return new SpriteImage<DrawElement>(image, image.getWidth(), image.getHeight());
	}
	
	public static DrawElement rotateDrawElement(DrawElement drawElement, int width, int height, double angle) {
		if(angle == ROTATE_90 || angle == ROTATE_270){
			int prevWidth = width;
			width = height;
			height = prevWidth;
		}
		final Canvas canvas = createCanvas(width, height);
		final Context2d context = canvas.getContext2d();
		if(angle == ROTATE_90){
			context.translate(width, 0);
		} else if(angle == ROTATE_270){
			context.translate(0, height);
		} else if(angle == ROTATE_180){
			context.translate(width, height);
		}
		context.rotate(angle);
		drawElement.draw(context);
		context.rotate(-angle);
		return DrawElement.createDrawElement(canvas.getCanvasElement());
	}
	
	public static SpriteImage<DrawElement> rotateRelativeToMiddle(DrawElement drawElement, int width, int height, double angle) {
		final Canvas canvas = createCanvas(width, height);
		final Context2d context = canvas.getContext2d();
		context.translate(width/2, 0);
		context.rotate(angle);
		context.translate(-width/2, 0);
		drawElement.draw(context);
		context.rotate(-angle);
		return new SpriteImage<DrawElement>(DrawElement.createDrawElement(canvas.getCanvasElement()), width, height);
	}
	
	public static DrawElement createCenteredTextElement(final String text, final int width, final int height, final CssColor color, final CssColor bgColor, final double shadow){
		return createCenteredTextElement(text, width, height, color, bgColor, shadow, BOLD_14PX_SANS_SERIF, 14);
	}
	
	public static DrawElement createCenteredTextElement(final String text, final int width, final int height, final CssColor color, final CssColor bgColor, final double shadow, String font, int fontHeight){
		int textX = (width - (fontHeight/2) * text.length())/2;
		int textY = (height + fontHeight) / 2;
		return createTextElement(text, width, height, textX, textY, color, bgColor, shadow, font, fontHeight);
	}
	
	public static DrawElement createLeftTextElement(final String text, final int width, final int height, final CssColor color, final CssColor bgColor, final double shadow, String font, int fontHeight){
		int textX = 0;
		int textY = (height + fontHeight) / 2;
		return createTextElement(text, width, height, textX, textY, color, bgColor, shadow, font, fontHeight);
	}
	
	public static DrawElement createTextElement(final String text, final int width, final int height, int textX, int textY, final CssColor color, final CssColor bgColor, final double shadow, String font, int fontHeight){
		final Canvas canvas = createCanvas(width, height);
		final Context2d context = canvas.getContext2d();
		if(bgColor != null){
			context.setFillStyle(bgColor);
			context.fillRect(0, 0, width, height);
		}
		context.setFillStyle(color);
		context.setFont(font);
		if(shadow > 0){
			context.setShadowBlur(shadow);
		}
		context.fillText(text, textX, textY, width);
		return DrawElement.createDrawElement(canvas.getCanvasElement());
	}
	
	
	public static SpriteImage[] splitImageVertically(DrawElement image, int leftWidth, int rightWidth) {

    	int w = image.getWidth();
    	int h = image.getHeight();
    	
        int tileWidth = w - leftWidth - rightWidth;
        
        SpriteImage left, center, right;
        
        if (leftWidth > 0){
        	left = new SpriteImage(image, 0, 0, leftWidth, h);
        }else{
        	// TODO:
        	left = new SpriteImage(image, 1, h);
//        	left = new BufferedImage(1, h, BufferedImage.TYPE_INT_ARGB);
        }
        if (tileWidth > 0){
        	center = new SpriteImage(image, leftWidth, 0, tileWidth, h);
        } else {
        	center = new SpriteImage(image, 1, h);
//        	center = new BufferedImage(1, h, BufferedImage.TYPE_INT_ARGB);
        }	
        if (rightWidth > 0){
        	right = new SpriteImage(image, leftWidth + tileWidth, 0, rightWidth, h);
        } else {
        	right = new SpriteImage(image, 1, h);
//        	right = new BufferedImage(1, h, BufferedImage.TYPE_INT_ARGB);
        }
        SpriteImage[] result = {left, center, right};
        
        return result;
    }
	
	public static SpriteImage[] splitImageHorizontally(DrawElement image, int upHeight, int downHeight) {
		int w = image.getWidth();
		int h = image.getHeight();

		int tileHeight = h - upHeight - downHeight;

		SpriteImage up = new SpriteImage(image, 0, 0, w, Math.max(1, upHeight));
		SpriteImage center = new SpriteImage(image, 0, upHeight, w, Math.max(1, tileHeight));
		SpriteImage down = new SpriteImage(image, 0, upHeight + tileHeight, w, Math.max(1, downHeight));

		SpriteImage[] result = {up, center, down};

		return result;
	}
	
	public static <Z> void drawNumber(int number, SpriteImage[] numberImages, SpriteGraphics<Z> graphics, int x, int y,
			Rectangle dirtyRegion) {
		char[] figures = Integer.toString(number).toCharArray();
		int lastx = 0;
		for (int i = 0; i < figures.length; i++) {
			if (figures[i] - '1' >= 0) {
				numberImages[figures[i] - '1'].draw(graphics, x + lastx, y, dirtyRegion);
				lastx += numberImages[figures[i] - '1'].width();
			} else if (figures[i] == '-') {
				numberImages[11].draw(graphics, x + lastx, y, dirtyRegion);
				lastx += numberImages[11].width();
			} else {
				numberImages[9].draw(graphics, x + lastx, y, dirtyRegion);
				lastx += numberImages[9].width();
			}
		}
	}
	
	public static int calculateFiguresWidth(char[] figures,
			SpriteImage[] numbers) {
		int lastx = 0;

		for (int i = 0; i < figures.length; i++) {
			if (figures[i] - '1' >= 0)
				lastx += numbers[figures[i] - '1'].width();
			else
				lastx += numbers[9].width();
		}
		return lastx;
	}
	
	public static DrawElement changeColorAndTransparency(DrawElement tileElement, int oldR, int oldG, int oldB, int newR, int newG, int newB, double transp) {
		final int width = tileElement.getWidth();
		final int height = tileElement.getHeight();
		final Canvas canvas = createCanvas(width, height);
		final Context2d context = canvas.getContext2d();
		tileElement.draw(context);
		final ImageData data = context.getImageData(0, 0, width, height);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				final int redAt = data.getRedAt(i, j);
				final int greenAt = data.getGreenAt(i, j);
				final int blueAt = data.getBlueAt(i, j);
				if(redAt == oldR && greenAt == oldG && blueAt == oldB){
					data.setRedAt(newR, i, j);
					data.setGreenAt(newG, i, j);
					data.setBlueAt(newB, i, j);
					data.setAlphaAt((int) (data.getAlphaAt(i, j) * transp), i, j);
				}
			}
		}
		context.putImageData(data, 0, 0);

		return DrawElement.createDrawElement(canvas.getCanvasElement());
	}
	
	public static DrawElement createRect(int width, int height, CssColor color){
		return createRect(width, height, color, 1.0d);
	}
	
	public static DrawElement createRect(int width, int height, CssColor color, double alpha){
		final Canvas canvas = createCanvas(width, height);
		final Context2d context = canvas.getContext2d();
		context.setGlobalAlpha(alpha);
		context.setFillStyle(color);
		context.fillRect(0, 0, width, height);
		return DrawElement.createDrawElement(canvas.getCanvasElement());
	}
	
	public static SpriteImage<DrawElement>[] splitHorizontalStripeByVerticalTransparentLine(DrawElement horizontalStripe, int w, int h){
		Vector<SpriteImage<DrawElement>> letters = new Vector<SpriteImage<DrawElement>>();
		int lastx = 0;
		final Canvas canvas = createCanvas(w, h);
		final Context2d context = canvas.getContext2d();
		horizontalStripe.draw(context);
		final ImageData imageData = context.getImageData(0, 0, w, h);

		for(int i = 0; i < w; i++){
			int count = 0;
			for(int j = 0; j < h; j++){
				if(imageData.getAlphaAt(i, j) == 0){
					count++;
				}
				else
					break;
			}
			if (count == h) {
				if((i - lastx) > 1) {
					SpriteImage<DrawElement> image = new SpriteImage<DrawElement>(horizontalStripe, lastx, 0, i - lastx, h);
					letters.addElement(image);
				}
				lastx = i + 1; 
			}
		}
		if (lastx < w) {
			SpriteImage<DrawElement> image = new SpriteImage<DrawElement>(horizontalStripe, lastx, 0, w - lastx, h);
			letters.addElement(image);
		}
		SpriteImage<DrawElement>[] lettersImages = new SpriteImage[letters.size()]; 
		for(int i = 0; i < letters.size(); i++){
			lettersImages[i] = letters.elementAt(i);
		}
		return lettersImages;
	}
	
	public static DrawElement createVerticalLine(int xStart, int yStart, int xEnd, int yEnd, int width, int height, CssColor color){
		final Canvas canvas = createCanvas(width, height);
		final Context2d context = canvas.getContext2d();
		context.beginPath();
		context.setFillStyle(color);
		context.fillRect(xStart, yStart, 1, Math.abs(yStart - yEnd));
		context.stroke();
		return DrawElement.createDrawElement(canvas.getCanvasElement());
	}
}

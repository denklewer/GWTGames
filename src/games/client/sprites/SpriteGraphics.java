package games.client.sprites;

public interface SpriteGraphics<T> {

	void drawImage(T combinedImage, int offsetX, int offsetY, int width,
			int height, int x, int y, int width2, int height2,
			Rectangle clipArea);
	
	void drawImage(T combinedImage, int offsetX, int offsetY, int width,
			int height, int x, int y, int width2, int height2,
			Rectangle clipArea, double alpha);

}

package games.client.sprites;

public interface ImageEngine<T> extends Engine{

	public <Z> void draw(SpriteGraphics<Z> graphics, SpriteImage<Z> image, int x,
			int y, Rectangle dirtyRegion);
}

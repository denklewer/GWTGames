package games.client.sprites;

import java.util.List;


public interface TimerListener<T> {

	public void update(long timestamp);

	public void paint(SpriteGraphics<T> graphics);

	public void setDirtyRegions(List<Rectangle> dirtyRegions);

	public boolean onMouseDown(int x, int y, int modifiers);

	public boolean onMouseUp(int x, int y, int modifiers);

	public boolean onMouseMove(int x, int y);

	public boolean onDoubleClick(int x, int y);

}

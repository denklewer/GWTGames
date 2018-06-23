package games.client.sprites;

public interface MouseHandler {

	boolean onMouseDown(int x, int y, int modifiers);

	boolean onMouseUp(int x, int y, int modifiers);

	boolean onMouseMove(int x, int y);

	boolean onMouseEntered(int x, int y);

	boolean onMouseExit();

	boolean onDoubleClick(int x, int y);

}

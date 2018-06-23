package games.client.sprites;

public interface MovementEngine extends Engine {

	@Override
	public boolean updateTick(long timestamp);

	public int getX();

	public int getY();
}

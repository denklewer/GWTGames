package games.shared;

public interface RepeatingTimer {
	
	public void scheduleRepeating(Runnable action, int periodMillis);
	public void stop();

}

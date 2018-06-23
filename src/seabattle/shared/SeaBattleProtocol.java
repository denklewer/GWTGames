package seabattle.shared;


public interface SeaBattleProtocol {

	public static final int SIZE = 10;
	
	public static final int EMPTY = 0, 
							KILLED = 1, 
							SHOT = 2,
							ALIVE = 3,
							WIDE = 4,
							GAME_OVER = 5;
	
	public final static byte WHITE = 1;
	public final static byte BLACK = 2;
	
	public final static int GAME_NOT_STARTED = 105;
	public final static int GAME_NOT_FINISHED = 104;
	
}

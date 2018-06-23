package sevendoors.shared;

public class SevenDoorsMove {
	
	public static final SevenDoorsMove SECRET_MOVE = new SevenDoorsMove(-1, -1, -1, -1);
	
	private int _x1;
	private int _x2;
	private int _y1;
	private int _y2;
	
	public int getX2() {
		return _x2;
	}
	
	public int getY2() {
		return _y2;
	}

	public int getX1() {
		return _x1;
	}

	public int getY1() {
		return _y1;
	}

	public boolean isValid() {
		return isValid(_x1) && isValid(_y1) && isValid(_x2) && isValid(_y2) && 
		Math.abs(_x2 - _x1) <= 1 && Math.abs(_y2 - _y1) <= 1;
	}

	private boolean isValid(int x) {
		return x >= 0 && x < 8;
	}

	public SevenDoorsMove(int x1, int y1, int x2, int y2) {
		super();
		_x1 = x1;
		_x2 = x2;
		_y1 = y1;
		_y2 = y2;
	}
	
	public SevenDoorsMove() {}

}

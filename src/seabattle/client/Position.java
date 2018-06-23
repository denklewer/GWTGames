package seabattle.client;

public class Position {
	private int _x;
	private int _y;
	
	public Position() {
	}

	public Position(int x, int y){
		_x = x;
		_y = y;
	}
	
	public Position copy(){
		return new Position(_x, _y);
	}

	public int getX() {
		return _x;
	}

	public void setX(int x) {
		_x = x;
	}

	public int getY() {
		return _y;
	}

	public void setY(int y) {
		_y = y;
	}

	public boolean equals(Object obj) {
		if(obj instanceof Position){
			return ((((Position) obj).getX() == _x) && (((Position) obj).getY() == _y));
		}
		return false;
	}

}
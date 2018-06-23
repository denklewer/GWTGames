package games.shared;

public class Point {

	public int x;
	public int y;

	public Point() {
	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Point))
            return false;
        Point pn = (Point)o;
        return pn.x == x &&
               pn.y  == y;
    }

	@Override
	public int hashCode() {
		int result = 17;
		result = 37*result + x;
		result = 37*result + y;
		return result;
	}
	
	@Override
	public String toString() {
		return "x = " + x + ", y = " + y;
	}

}

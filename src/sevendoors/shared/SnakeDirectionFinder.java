package sevendoors.shared;

import games.shared.Point;


public class SnakeDirectionFinder {

	public static Point getDxDy(final int direction) {
		int dx = 0, dy = 0;
		switch (direction) {
		case 0:
			dx = 1;
			dy = 0;
			break;
		case 1:
			dx = 1;
			dy = -1;
			break;
		case 2:
			dx = 0;
			dy = -1;
			break;
		case 3:
			dx = -1;
			dy = -1;
			break;
		case 4:
			dx = -1;
			dy = 0;
			break;
		case 5:
			dx = -1;
			dy = 1;
			break;
		case 6:
			dx = 0;
			dy = 1;
			break;
		case 7:
			dx = 1;
			dy = 1;
			break;
	
		}
		return new Point(dx, dy);
	}

}

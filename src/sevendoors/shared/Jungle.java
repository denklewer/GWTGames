package sevendoors.shared;

import games.shared.Point;
import games.shared.RandomGenerator;

public class Jungle {
	
	private static final int N = 0;
	private static final int E = 1;
	private static final int S = 2;
	private static final int W = 3;
	private static final int LIMIT = 100;
	
	private boolean[][] _jungle = new boolean[SevenDoorsProtocol.BOARD_SIZE][SevenDoorsProtocol.BOARD_SIZE];
	private final RandomGenerator _randomGenerator;
	
	public Jungle(final RandomGenerator randomGenerator) {
		_randomGenerator = randomGenerator;
	}
	
	public Point grow() {
		final int i = _randomGenerator .nextInt(4);
		final int n = _randomGenerator.nextInt(8);
		Point p = null;
		switch (i) {
		case N:
			p = new Point(n, 0);
			break;
		case E:
			p = new Point(7, n);
			break;
		case S:
			p = new Point(n, 7);
			break;
		case W:
			p = new Point(0, n);
			break;
		}
		
		for (int j = 0; j < LIMIT && _jungle[p.x][p.y]; j++) {
			final int direction = _randomGenerator.nextInt(4);
			if (isOutOfBounds(getNewCoords(p, direction))) {
				continue;
			}
			p = getNewCoords(p, direction);
		};

		_jungle[p.x][p.y] = true;
		return p;
	}
	
	private Point getNewCoords(Point p, final int direction) {
		switch (direction) {
		case N:
			p = new Point(p.x, p.y - 1);
			break;
		case E:
			p = new Point(p.x + 1, p.y);
			break;
		case S:
			p = new Point(p.x, p.y + 1);
			break;
		case W:
			p = new Point(p.x - 1, p.y);
			break;
		}
		return p;
	}
	
	private boolean isOutOfBounds(final Point p) {
		return !(p.x < 8 && p.x >= 0 && p.y < 8 && p.y >= 0);
	}

	public boolean useMachete(final Point point) {
		boolean result = false;
		for (int k = -1; k <= 1; k++) {
			for (int l = -1; l <= 1; l++) {
				if (!isOutOfBounds(new Point(point.x + k, point.y + l))) {
					if (_jungle[point.x + k][point.y + l]) {
						result = true;
					}
					_jungle[point.x + k][point.y + l] = false;
				}
			}
		}
		return result;
	}

	public boolean[][] toBooleanArray() {
		return _jungle;
	}
	
	public void setJungle(final boolean value, final int i, final int j){
		_jungle[i][j] = value;
	}
	
	public Jungle copy() {
		final boolean[][] copy = new boolean[SevenDoorsProtocol.BOARD_SIZE][SevenDoorsProtocol.BOARD_SIZE];
		for (int i = 0; i < copy.length; i++) {
			copy[i] = new boolean[SevenDoorsProtocol.BOARD_SIZE];
			System.arraycopy(_jungle[i], 0, copy[i], 0, SevenDoorsProtocol.BOARD_SIZE);
		}
		final Jungle result = new Jungle(_randomGenerator);
		result._jungle = copy;
		return result;
	}
	
}

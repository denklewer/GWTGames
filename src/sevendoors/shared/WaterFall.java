package sevendoors.shared;

import games.shared.Point;
import games.shared.RandomGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class WaterFall implements SevenDoorsProtocol {

	private SevenDoorsBoard _board;
	private final RandomGenerator _randomGenerator;

	public WaterFall(final RandomGenerator randomGenerator) {
		_randomGenerator = randomGenerator;
	}

	public WaterFall(final SevenDoorsBoard board, final RandomGenerator randomGenerator) {
		this(randomGenerator);
		_board = board;
	}

	public void spreadWaterfall() {
		final Vector waterfallQueue = new Vector();
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				if (_board.getTile(i, j).getId() == WATERFALL && !_board.getTile(i, j).isDeleted()) {
					waterfallQueue.addElement(new Point(i, j));
				}
			}
		}
		while (!waterfallQueue.isEmpty()) {
			final Point p = (Point) waterfallQueue.elementAt(0);
			if (p.y + 1 < BOARD_SIZE && _board.getTile(p.x, p.y + 1).isDeleted()) {
				_board.setTile(new Tile(WATERFALL), p.x, p.y + 1);
				waterfallQueue.addElement(new Point(p.x, p.y + 1));
			}
			if (p.x + 1 < BOARD_SIZE && _board.getTile(p.x + 1, p.y).isDeleted()) {
				_board.setTile(new Tile(WATERFALL), p.x + 1, p.y);
				waterfallQueue.addElement(new Point(p.x + 1, p.y));
			}
			if (p.x - 1 >= 0 && _board.getTile(p.x - 1, p.y).isDeleted()) {
				_board.setTile(new Tile(WATERFALL), p.x - 1, p.y);
				waterfallQueue.addElement(new Point(p.x - 1, p.y));
			}
			waterfallQueue.removeElementAt(0);
		}
	}

	public boolean markTilesToDelete() {
		boolean result = false;
		for (int i = 0; i < BOARD_SIZE; i++) {
			Tile tile;
			if ((tile = _board.getTile(i, BOARD_SIZE - 1)).getId() == WATERFALL && !tile.isDeleted()) {
				deleteWaterfall(i, BOARD_SIZE - 1);
				result = true;
			}
		}
		return result;
	}

	private void deleteWaterfall(final int i, final int j) {
		final Vector queue = new Vector();
		queue.addElement(new Point(i, j));
		while (!queue.isEmpty()) {
			final Point p = (Point) queue.elementAt(0);
			queue.removeElementAt(0);
			_board.getTile(p.x, p.y).setDeleted(true);
			Tile tile;
			if (p.y - 1 >= 0 && (tile = _board.getTile(p.x, p.y - 1)).getId() == WATERFALL && !tile.isDeleted()) {
				queue.addElement(new Point(p.x, p.y - 1));
			}

			if (p.y + 1 < BOARD_SIZE && (tile = _board.getTile(p.x, p.y + 1)).getId() == WATERFALL && !tile.isDeleted()) {
				queue.addElement(new Point(p.x, p.y + 1));
			}

			if (p.x + 1 < BOARD_SIZE && (tile = _board.getTile(p.x + 1, p.y)).getId() == WATERFALL && !tile.isDeleted()) {
				queue.addElement(new Point(p.x + 1, p.y));
			}

			if (p.x - 1 >= 0 && (tile = _board.getTile(p.x - 1, p.y)).getId() == WATERFALL && !tile.isDeleted()) {
				queue.addElement(new Point(p.x - 1, p.y));
			}
		}
	}

	// public int getWaterfallCount(int x, int y) {
	// int j = 0;
	// for (int i = y + 1; i < BOARD_SIZE && _board.getTile(x, i).getId() == WATERFALL; i++, j++);
	// return j;
	// }

	public int addWater() {
		final List<Integer> deletedTiles = new ArrayList<Integer>();
		for (int i = 0; i < SevenDoorsProtocol.BOARD_SIZE; i++) {
			if (_board.getTile(i, 0).isDeleted()) {
				deletedTiles.add(i);
			}
		}
		final int size = deletedTiles.size();

		final int index = _randomGenerator.nextInt(size);
		_board.setTile(new Tile(SevenDoorsProtocol.WATERFALL), deletedTiles.get(index), 0);
		return deletedTiles.get(index);
	}

	public BoardMask getWaterMask() {
		final BoardMask result = new BoardMask(SevenDoorsProtocol.BOARD_SIZE);
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				final Tile tile = _board.getTile(i, j);
				result.setTrue(i, j, tile.getId() == WATERFALL && !tile.isDeleted());
			}
		}
		return result;
	}

}

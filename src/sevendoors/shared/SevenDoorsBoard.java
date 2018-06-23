package sevendoors.shared;

import games.shared.Point;
import games.shared.RandomGenerator;

import java.io.Serializable;
import java.util.Vector;

public class SevenDoorsBoard implements SevenDoorsProtocol, Serializable {
	private Tile[][] _tiles;
	private int _size;
	private final RandomGenerator _randomGenerator;

	public SevenDoorsBoard(final int size, final RandomGenerator randomGenerator){
		_randomGenerator = randomGenerator;
		_tiles = new Tile[size][size];
		_size = size;
	}
	
	public void resetDeletedTiles() {
		for (int i = 0; i < _size; i++) {
			for (int j = 0; j < _size; j++) {
				_tiles[i][j].setDeleted(false);
			}
		}
	}

	public void deleteAllTiles() {
		for (int i = 0; i < _size; i++) {
			for (int j = 0; j < _size; j++) {
				_tiles[i][j].setDeleted(true);
			}
		}
	}

	public TilesFallMask moveTilesDown() {
		final int[][] fallMask = new int[_size][_size];
		final Tile[][] tmp = new Tile[_size][_size];
		
		for (int i = 0; i < _size; i++) {
			for (int j = 0; j < _size; j++) {
				final Tile tile = _tiles[i][j];
				int newy;
				if (!tile.isDeleted()) {
					newy = findBottom(i, j);
				} else {
					newy = findTop(i, j);
				}
				tmp[i][newy] = _tiles[i][j];
				fallMask[i][j] = newy;
			}
		}
		_tiles = tmp;
		return new TilesFallMask(fallMask);
	}

	private int findTop(final int x, final int y) {
		int j = 0;
		for (int i = y; i >= 0 && !(_tiles[x][i].getId() == WATERFALL && !_tiles[x][i].isDeleted()); i--) {
			if (!_tiles[x][i].isDeleted()) {
				j++;
			}
		}
		return y - j;
	}

	private int findBottom(final int x, final int y) {
		int j = 0;
		for (int i = y; i < _size && !(_tiles[x][i].getId() == WATERFALL && !_tiles[x][i].isDeleted()); i++) {
			if (_tiles[x][i].isDeleted()) {
				j++;
			}
		}
		return y + j;
	}

	public void addNewTiles(final Vector tiles) {
		int tmp = 0;
		for (int i = 0; i < _size; i++) {
			for (int j = 0; j < _size; j++) {
				if (_tiles[i][j].isDeleted()) {
					_tiles[i][j] = (Tile) tiles.elementAt(tmp++);
				}
			}
		}
	}
	
	public boolean markTilesToDelete() {
		boolean result = markSimpleTilesToDelete();
		takeLightBonusIntoAccount();
		result = deleteBoxAndForestOnBottom(result) || result;
		return result;
	}

	private boolean deleteBoxAndForestOnBottom(boolean result) {
		for (int i = 0; i < _size; i++) {
			if (getTile(i, _size - 1).getId() == BOX || getTile(i, _size - 1).getId() == FOREST) {
				getTile(i, _size - 1).setDeleted(true);
				result = true;
			}
		}
		return result;
	}

	private void takeLightBonusIntoAccount() {
		for (int i = 0; i < _size; i++) {
			for (int j = 0; j < _size; j++) {
				if (getTile(i, j).isLightBonus() && getTile(i, j).isDeleted()) {
					deleteSameTiles(getTile(i, j).getId());
				}
			}		
		}
	}
	
	private void deleteSameTiles(final int id) {
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				if (getTile(i, j).getId() == id) {
					getTile(i, j).setDeleted(true);
					getTile(i, j).setGivesScore(false);
				}
			}
		}
	}

	public boolean markSimpleTilesToDelete() {
		boolean result = false;
		int counter = 1;
		for (int i = 0; i < _size; i++) {
			Tile prevTile = _tiles[i][0];
			int j;
			for (j = 1; j < _size; j++) {
				if (!_tiles[i][j].canBeDeleted()) {
					if (counter >= 3) {
						break;
					}
					counter = 0;
				} else if (_tiles[i][j].getId() == prevTile.getId()) { 
					counter++;
				} else {
					if (counter >= 3) {
						break;
					}
					prevTile = _tiles[i][j];
					counter = 1;
				}
					
			}
			if (counter >= 3) {
				result = true;
				for (int k = j - 1; k >= 0 && _tiles[i][k].getId() == prevTile.getId(); k--) { 
					_tiles[i][k].setDeleted(true);
				}
			}
			counter = 1;
		}
		for (int i = 0; i < _size; i++) {
			Tile prevTile = _tiles[0][i];
			int j;
			for (j = 1; j < _size; j++) {
				if (!_tiles[j][i].canBeDeleted()) {
					if (counter >= 3) {
						break;
					}
					counter = 0;
				} else if (_tiles[j][i].getId() == prevTile.getId()) { 
					counter++;
				} else {
					if (counter >= 3) {
						break;
					}
					prevTile = _tiles[j][i];
					counter = 1;
				}
					
			}
			if (counter >= 3) {
				result = true;
				for (int k = j - 1; k >= 0 && _tiles[k][i].getId() == prevTile.getId(); k--) { 
					_tiles[k][i].setDeleted(true);
				}
			}
			counter = 1;
		}
		return result;
	}

	public void swapTiles(final SevenDoorsMove move) {
		Tile tmp;
		tmp = _tiles[move.getX2()][move.getY2()];
		_tiles[move.getX2()][move.getY2()] = _tiles[move.getX1()][move.getY1()];
		_tiles[move.getX1()][move.getY1()] = tmp;
	}
	
	public Tile getTile(final int i, final int j) {
		return _tiles[i][j];
	}

	public SevenDoorsBoard copy() {
		final SevenDoorsBoard result = new SevenDoorsBoard(_size, _randomGenerator) ;
		for (int i = 0; i < _size; i++) {
			for (int j = 0; j < _size; j++) {
				result.setTile(getTile(i, j).copy(), i, j);
			}
		}
		return result;
	}

	public synchronized void activateSnakes(final Point p) {
		_tiles[p.x][p.y].setDeleted(true);
		_tiles[p.x][p.y].setGivesScore(false);
		
		for (int l = 0; l < _size; l++) {
			final Point incPoint = SnakeDirectionFinder.getDxDy(l);
		
			for (int k = 1; ; k++) {
				final int i = p.x + k * incPoint.x;
				final int j = p.y + k * incPoint.y;
				if (!isOnBoard(i, j) || getTile(i, j).getId() == WATERFALL) {
					break;
				}
				_tiles[i][j].setDeleted(true);
				_tiles[i][j].setGivesScore(false);
			}
		}
		
		
	}

	public boolean isOnBoard(final int i, final int j) {
		return i >= 0 && i < _size && j >=0 && j < _size;
	}

	public int countScore() {
		int result = 0;
		boolean hasBonus = false;
		for (int i = 0; i < _size; i++) {
			for (int j = 0; j < _size; j++) {
				final Tile tile = getTile(i, j);
				if (tile.isDeleted()) {
					if (tile.getId() == FROG) {
						result += SevenDoorsConstants.SCORE_FOR_FROG;
					} else 	if (tile.getId() == BONUS) {
						result++;
						hasBonus = true;
					} else if (tile.getId() == WATERFALL) {
						continue;
					} else {
						result++;
					}
				}
			}
		}
		return hasBonus ? SevenDoorsConstants.BONUS_MULTIPLIER * result : result;
	}
	
	public int countKeys() {
		int result = 0;
		Tile tile;
		for (int i = 0; i < _size; i++) {
			if ((tile = getTile(i, _size - 1)).getId() == BOX && tile.isDeleted() && tile.hasKey()) {
				result++;
			}
		}
		return result;
	}

	public void setTile(final Tile tile, final int i, final int j) {
		_tiles[i][j] = tile;
	}

	public int countTilesToAdd() {
		int result = 0;
		for (int i = 0; i < _size; i++) {
			for (int j = 0; j < _size; j++) {
				if (_tiles[i][j].isDeleted()) {
					result++;
				}
			}
		}
		return result;
	}

	public void init(final Vector vector) {
		int tmp = 0;
		for (int i = 0; i < _size; i++) {
			for (int j = 0; j < _size; j++) {
				_tiles[i][j] = (Tile) vector.elementAt(tmp++);
			}
		}
	}

	public Vector cutNumOfLianasTiles() {
		final Vector deletedLianasPoints = new Vector();
		final Vector lianasPoints = new Vector();
		for (int i = 0; i < _size; i++) {
			for (int j = 0; j < _size; j++) {
				if (getTile(i, j).hasLiana()) {
					lianasPoints.addElement(new Point(i, j));
				}
			}
		}
		
		while (lianasPoints.size() > SevenDoorsConstants.LIANAS_LIMIT) {
			final int num = _randomGenerator.nextInt(lianasPoints.size());
			final Point p = (Point) lianasPoints.elementAt(num);
			lianasPoints.removeElementAt(num);
			deletedLianasPoints.addElement(p);
			setTile(new Tile(FOREST), p.x, p.y);
		}
		
		return deletedLianasPoints;
	}

	public BoardMask deletedMask() {
		final BoardMask result = new BoardMask(_size);
		for (int i = 0; i < _size; i++) {
			for (int j = 0; j < _size; j++) {
				result.setTrue(i, j, _tiles[i][j].isDeleted());
			}
		}
		return result;
	}
	
	public Point containsAtLeastOneSnake(){
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				if(_tiles[i][j].isSnake()){
					return new Point(i, j);
				}
			}
		}
		return null;
	}

}

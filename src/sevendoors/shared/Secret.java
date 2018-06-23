package sevendoors.shared;

import java.util.HashMap;

public class Secret implements SevenDoorsProtocol {
	
	private static final int NUM_OF_SECRETS = 7;
	
	private static int[][][] _secret = new int[NUM_OF_SECRETS][BOARD_SIZE][BOARD_SIZE];
	
	static {
		_secret[0][4][3] = 1;
        _secret[0][3][4] = 1;
        _secret[0][4][4] = 2;
        
        _secret[1][3][3] = 3;
        _secret[1][3][4] = 1;
        _secret[1][4][3] = 2;
        _secret[1][4][4] = 1;
        
        _secret[2][3][3] = 2;
        _secret[2][3][4] = 1;
        _secret[2][4][3] = 1;
        _secret[2][4][4] = 2;
        
        _secret[3][3][3] = 2;
        _secret[3][3][4] = 2;
        _secret[3][4][3] = 1;
        _secret[3][4][4] = 2;
        
        _secret[4][3][3] = 1;
        _secret[4][3][4] = 1;
        _secret[4][4][3] = 1;
        _secret[4][4][4] = 1;
        
        _secret[5][2][3] = 3;
        _secret[5][3][3] = 3;
        _secret[5][3][4] = 3;
        _secret[5][4][3] = 2;
        _secret[5][4][4] = 2;
        
        _secret[6][2][3] = 3;
        _secret[6][3][3] = 3;
        _secret[6][3][4] = 1;
        _secret[6][4][3] = 1;
        _secret[6][4][4] = 2;        
        
	}

	private int _currentSecret;

	private SevenDoorsBoard _board;

	public Secret(SevenDoorsBoard board) {
		_board = board;
	}
	
	public Secret copy() {
		Secret copy = new Secret(_board.copy());
		copy._currentSecret = _currentSecret;
		return copy;
	}

	public boolean isSolved() {
		HashMap tmp1 = new HashMap();
		HashMap tmp2 = new HashMap();
		
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				int num = _secret[_currentSecret][i][j];
				if (num != 0) {
					if (tmp1.get(new Integer(num)) == null) {
						int id = _board.getTile(i, j).getId() + 1;
						if (tmp2.get(new Integer(id)) == null) {
							tmp1.put(new Integer(num), new Integer(id));
							tmp2.put(new Integer(id), new Integer(num));
						} else {
							return false;
						}
					} else if (((Integer) tmp1.get(new Integer(num))).intValue() != _board.getTile(i, j).getId() + 1) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public void next() {
		_currentSecret = (_currentSecret + 1) % NUM_OF_SECRETS;
	}
	
	public void setToZero() {
		_currentSecret = 0;
	}

	public void deleleSecretTiles() {
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				if (_secret[_currentSecret][i][j] != 0) {
					_board.getTile(i, j).setDeleted(true);
				}
			}
		}
	}

	public int getCell(int i, int j) {
		return _secret[_currentSecret][i][j];
	}

	public int getSecretNum() {
		return _currentSecret;
	}
	
	public int[][] toArray() {
		return _secret[_currentSecret];
	}

	public void setCurrentSecret(int currentSecret) {
		_currentSecret = currentSecret;
	}

}

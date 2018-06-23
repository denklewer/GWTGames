package sevendoors.shared;


public class BoardMask {
	
	private boolean[][] _mask;
	
	public BoardMask(int size) {
		_mask = new boolean[size][size];
	}
	
	public BoardMask copy(){
		BoardMask copy = new BoardMask(_mask.length);
		for (int i = 0; i < _mask.length; i++) {
			for (int j = 0; j < _mask[i].length; j++) {
				copy._mask[i][j] = _mask[i][j];
			}
		}
		return copy;
	}

	public void setTrue(int i, int j, boolean b) {
		_mask[i][j] = b;
	}

	public boolean isTrue(int i, int j) {
		return _mask[i][j];
	}

}

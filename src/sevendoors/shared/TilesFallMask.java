package sevendoors.shared;

public class TilesFallMask {

	private int[][] _fallMask;

	public TilesFallMask(int[][] fallMask) {
		_fallMask = fallMask;
	}

	public TilesFallMask() {
	}
	
	public TilesFallMask copy(){
		TilesFallMask copy = new TilesFallMask();
		if(_fallMask.length > 0){
			int[][] fallMask = new int[_fallMask.length][_fallMask[0].length];
			for (int i = 0; i < _fallMask.length; i++) {
				fallMask[i] = new int[_fallMask[i].length];
				System.arraycopy(_fallMask[i], 0, fallMask[i], 0, _fallMask[i].length);
			}
			copy._fallMask = fallMask;
		}
		return copy;
	}

	public int getNewPlace(int x, int y) {
		return _fallMask[x][y];
	}
}

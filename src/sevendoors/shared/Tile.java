package sevendoors.shared;

public class Tile implements SevenDoorsProtocol {
	
	public static final int N_TILES = 6;
	protected int _id;
	private boolean _isDeleted;
	private boolean _isLightBonus;
	private boolean _hasLiana;
	private boolean _givesScore = true;
	
	public boolean hasLiana() {
		return _hasLiana;
	}

	public void setLiana(boolean hasLiana) {
		_hasLiana = hasLiana;
	}

	public boolean isLightBonus() {
		return _isLightBonus;
	}

	public void setLightBonus(boolean isLightBonus) {
		_isLightBonus = isLightBonus;
	}

	public Tile() {}
	
	public Tile(int id) {
		_id = id;
	}
	
	@Override
	public String toString() {
		switch (_id) {
		case 0:
			return "b";
		case 1:
			return "c";
		case 2:
			return "f";
		case 3:
			return "e";
		case 4:
			return "t";
		case 5:
			return "m";
		default:
			break;
		}
		return super.toString();
	}

	public int getId() {
		return _id;
	}
	
	public Tile copy(){
		Tile copy = new Tile(_id);
		copy._isLightBonus = _isLightBonus;
		copy._hasLiana = _hasLiana;
		return copy;
	}

	public void setDeleted(boolean b) {
		_isDeleted = b;
	}

	public boolean isDeleted() {
		return _isDeleted;
	}

	public boolean canBeMoved() {
		return _id != FOREST && _id != WATERFALL && !hasLiana();
	}

	public boolean canBeDeleted() {
		return _id != FOREST && _id != WATERFALL;
	}

	public boolean givesScore() {
		return _givesScore;
	}

	public void setGivesScore(boolean givesScore) {
		_givesScore = givesScore;
	}

	public boolean isBonus() {
		return _id > N_TILES || _id < 0;
	}
	
	public boolean isSnake() {
		return _id == SNAKE;
	}
	
	public boolean hasKey() {
		if (_id != SevenDoorsProtocol.BOX)
			return false;
//			throw new RuntimeException("tile is not a box");
		// TODO
		return true;
	}
	
}

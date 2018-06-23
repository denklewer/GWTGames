package sevendoors.shared;

import games.shared.Point;
import games.shared.RandomGenerator;

import java.util.Vector;

public class SevenDoorsState {

	private SevenDoorsBoard _board;
	private Jungle _jungle;
	private Secret _secret;
	private WaterFall _waterFall;
	
	private long _score = 0;
	private int _keys = 0;
	private int _lastScore = 0;
	private int _level = 1;
	private int _numOfMachete = SevenDoorsConstants.NUM_OF_MACHETE;
	private int _numOfMadeMoves;
	private final RandomGenerator _randomGenerator;
	
	public SevenDoorsState(final RandomGenerator randomGenerator) {
		_randomGenerator = randomGenerator;
		_board = new SevenDoorsBoard(SevenDoorsProtocol.BOARD_SIZE, _randomGenerator);
		_jungle = new Jungle(_randomGenerator);
		_secret = new Secret(_board);
		_waterFall = new WaterFall(_board, _randomGenerator);
	}
	
	public SevenDoorsState getCopy() {
		final SevenDoorsState copy = new SevenDoorsState(_randomGenerator);
		copy._board = _board.copy();
		copy._jungle = _jungle.copy();
		copy._secret = _secret.copy();
		copy._waterFall = new WaterFall(_board, _randomGenerator);
		
		copy._score = _score;
		copy._keys = _keys;
		copy._lastScore = _lastScore;
		copy._level = _level;
		copy._numOfMachete = _numOfMachete;
		copy._numOfMadeMoves = _numOfMadeMoves;
		return copy;
	}

	
	public void initBoard(final Vector tiles) {
		_board.init(tiles);		
	}

	public void clearScore() {
		_score = 0;
	}

	public long getScore() {
		return _score;
	}

	public void setScore(final long score) {
		_score = score;
	}

	public int getLevel() {
		return _level;
	}

	public SevenDoorsBoard getBoard() {
		return _board;
	}

	public void increaseNumOfMadeMoves() {
		_numOfMadeMoves++;
	}

	public void swapTiles(final SevenDoorsMove move) {
		_board.swapTiles(move);
	}

	public Point growJungle() {
		return _jungle.grow();
	}

	public int getLastScore() {
		return _lastScore;
	}

	public boolean isSecretSolved() {
		return _secret.isSolved();
	}

	public Point boardContainsAtLeastOneSnake() {
		return _board.containsAtLeastOneSnake();
	}

	public void deleleSecretTiles() {
		_secret.deleleSecretTiles();
	}

	public BoardMask deletedMask() {
		return _board.deletedMask();
	}

	public void addScoreForMove() {
		_score += _lastScore = _board.countScore();		
	}

	public void addOneKey() {
		_keys++;
	}

	public void nextSecret() {
		_secret.next();
	}

	public int getSecretNum() {
		return _secret.getSecretNum();
	}

	public int[][] secretToArray() {
		return _secret.toArray();
	}

	public void setBoard(final SevenDoorsBoard board) {
		_board = board;		
	}

	public void createNewWaterFall() {
		_waterFall = new WaterFall(_board, _randomGenerator);		
	}

	public int getNumOfMachete() {
		return _numOfMachete;
	}

	public boolean[][] getJungle() {
		return _jungle.toBooleanArray();
	}

	public int getKeys() {
		return _keys;
	}

	public void decreaseNumOfMachete() {
		_numOfMachete--;
	}

	public boolean useMachete(final Point point) {
		return _jungle.useMachete(point);
	}

	public void increaseScoreOnBoardSize() {
		_score += _board.countScore();		
	}

	public void clearLastScore() {
		_lastScore = 0;
	}

	public void activateSnakes(final Point p) {
		_board.activateSnakes(p);
	}

	public Tile getTile(final int x, final int y) {
		return _board.getTile(x, y);
	}

	public void increaseLevel() {
		_level++;
	}

	public void increaseNumOfMacheteOnChangeLevel() {
		_numOfMachete += SevenDoorsConstants.NUM_OF_MACHETE;
	}

	public void deleteAllTiles() {
		_board.deleteAllTiles();
	}

	public void doubleScore() {
		_score *= 2;
	}

	public int countTilesToAdd() {
		return _board.countTilesToAdd();
	}

	public void addNewTiles(final Vector tiles) {
		_board.addNewTiles(tiles);
	}

	public boolean markSimpleTilesToDelete() {
		return _board.markSimpleTilesToDelete();
	}

	public void resetDeletedTiles() {
		_board.resetDeletedTiles();
	}

	public boolean markTilesToDelete() {
		return _board.markTilesToDelete();
	}

	public boolean markWaterFallTilesToDelete() {
		return _waterFall.markTilesToDelete();
	}

	public void increaseNeededKeys() {
		_keys += _board.countKeys();		
	}

	public void spreadWaterfall() {
		_waterFall.spreadWaterfall();
	}

	public BoardMask getWaterMask() {
		return _waterFall.getWaterMask();
	}

	public TilesFallMask moveTilesDown() {
		return _board.moveTilesDown();
	}

	public int addWater() {
		return _waterFall.addWater();
	}

	public Vector cutNumOfLianasTiles() {
		return _board.cutNumOfLianasTiles();
	}

	public int getNumOfMadeMoves() {
		return _numOfMadeMoves;
	}

	public void setKeys(final int keys) {
		_keys = keys;
	}

	public void setLastScore(final int lastScore) {
		_lastScore = lastScore;
	}

	public void setLevel(final int level) {
		_level = level;
	}

	public void setNumOfMachete(final int numOfMachete) {
		_numOfMachete = numOfMachete;
	}

	public void setNumOfMadeMoves(final int numOfMadeMoves) {
		_numOfMadeMoves = numOfMadeMoves;
	}

	public void setJungle(final Jungle jungle) {
		_jungle = jungle;
	}

	public void setSecret(final Secret secret) {
		_secret = secret;
	}

	
}

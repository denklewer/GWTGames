package sevendoors.shared;

import games.shared.GameModel;
import games.shared.Point;
import games.shared.RandomGenerator;
import games.shared.RepeatingTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SevenDoorsModel implements SevenDoorsProtocol, GameModel<ModelListener> {
	
	private static final int SHOW_HINT_DELAY = 15 * 1000;

	private final EventManager _startGameEventManager = new EventManager(){

		@Override
		public void fireRemoveOldField() {
		}

		@Override
		public boolean hideMenu() {
			return false;
		}
		
		@Override
		public void resetStoredGame(){
			fireResetStoredGame();
		}
		
	};
	
	private final EventManager _resumeGameEventManager = new EventManager(){
		
		@Override
		public void fireRemoveOldField() {
		}
		
		@Override
		public boolean hideMenu() {
			return true;
		}
		
		@Override
		public void resetStoredGame(){
		}
	};
	
	private final EventManager _reloadGameEventManager = new EventManager(){
		
		@Override
		public void fireRemoveOldField() {
			fireNextLevel(0);
		}

		@Override
		public boolean hideMenu() {
			return true;
		}
		
		@Override
		public void resetStoredGame(){
			
		}
	};
	
	private List<ModelListener> _listeners = new ArrayList<ModelListener>();
	
	private TileFactory _factory;
	private long _playerMaxScore;
	
	private boolean _isPlaying = false;


	private final BonusProbabilityInfo _bonusProbabilityInfo;

	private final RandomGenerator _randomGenerator;
	
	private SevenDoorsState _state;

	private final RepeatingTimer _timer;
	
	public SevenDoorsModel(final RandomGenerator randomGenerator, final BonusProbabilityInfo bonusProbabilityInfo, final RepeatingTimer timer) {
		_randomGenerator = randomGenerator;
		_bonusProbabilityInfo = bonusProbabilityInfo;
		_timer = timer;
		_factory = new FirstZoneTileFactory(_bonusProbabilityInfo, _randomGenerator);
		_state = new SevenDoorsState(_randomGenerator);
		_state.initBoard(_factory.generateSimpleTilesPackage(64));
		clear();
		_state.clearScore();
	}
	
	@Override
	public void addListener(final ModelListener listener) {
		_listeners.add(listener);
	}
	
	public void removeListener(final ModelListener listener) {
		_listeners.remove(listener);
	}
	
	private void fireResetStoredGame() {
		for (final ModelListener listener : _listeners) {
			listener.resetStoredGame();
		}
	}
	
	public void fireStartMove() {
		for (final ModelListener listener : _listeners) {
			listener.startMove();
		}
	}
	
	public void fireEndMove() {
		for (final ModelListener listener : _listeners) {
			listener.endMove();
		}
	}
	
	private void fireSwapTiles(final int x1, final int y1, final int x2, final int y2) {
		for (final ModelListener listener : _listeners) {
			listener.tilesSwapped(x1, y1, x2, y2);
		}
	}
	
	private void fireHint() {
		for (final ModelListener listener : _listeners) {
			listener.showHint(getMove());
		}
	}
	
	private void fireStopAnimateHint() {
		for (final ModelListener listener : _listeners) {
			listener.stopShowHint();
		}
	}
	
	private void fireScore() {
		for (final ModelListener listener : _listeners) {
			listener.setScore(_state.getScore());
		}
	}
	
	private void fireNoMoreMoves() {
		for (final ModelListener listener : _listeners) {
			listener.noMoreMoves();
		}
	}
	
	private void fireGameFinished() {
		for (final ModelListener listener : _listeners) {
			listener.gameFinished();
		}
	}
	
	private void fireWaterfallSpread(final BoardMask waterMask) {
		for (final ModelListener listener : _listeners) {
			listener.waterfallSpread(waterMask);
		}
	}
	
	private void fireLianasChangedToForest(final Vector deletedLianas) {
		for (final ModelListener listener : _listeners) {
			listener.lianasChangedToForest(deletedLianas);
		}
	}
	
	private void fireSnakesActivated(final int x, final int y, final BoardMask deletedMask) {
		for (final ModelListener listener : _listeners) {
			listener.snakesActivated(x, y, deletedMask);
		}
	}
	
	private void fireNextLevel() {
		fireNextLevel(_state.getLevel());
	}
	
	private void fireNextLevel(final int level) {
		for (final ModelListener listener : _listeners) {
			listener.nextLevel(level);
		}
	}
	
	private void fireJungleGrow(final Point grow) {
		for (final ModelListener listener : _listeners) {
			listener.jungleGrow(grow);
		}
	}
	
	private void fireAddKey() {
		for (final ModelListener listener : _listeners) {
			listener.addKey();
		}
	}
	
	private void fireSecretStatusChanged(final boolean solved) {
		for (final ModelListener listener : _listeners) {
			listener.secretStatusChanged(solved);
		}
	}
	
	private void fireSetSecret(final int secretNum, final int[][] secret) {
		for (final ModelListener listener : _listeners) {
			listener.setSecret(secretNum, secret);
		}
	}
	
	private void fireTilesMarkedToDelete(final BoardMask deletedMask) {
		for (final ModelListener listener : _listeners) {
			listener.markTilesToDelete(deletedMask.copy());
		}
	}
	
	private void fireMoveAndAdd(final TilesFallMask fallMask, final BoardMask waterMask,
			final int waterX, final Vector<Tile> tiles, final BoardMask deletedMask) {
		final Vector<Tile> copyTiles = new Vector<Tile>();
		for (int i = 0; i < tiles.size(); i++) {
			copyTiles.add(tiles.elementAt(i).copy());
		}
		for (final ModelListener listener : _listeners) {
			listener.moveAndAdd(fallMask.copy(), waterMask.copy(), waterX, copyTiles, deletedMask.copy());
		}
	}
	
	private void fireTilesToAdd(final Vector<Tile> tiles, final BoardMask deletedMask) {
		final Vector<Tile> copyTiles = new Vector<Tile>();
		for (int i = 0; i < tiles.size(); i++) {
			copyTiles.add(tiles.elementAt(i).copy());
		}
		for (final ModelListener listener : _listeners) {
			listener.addTiles(copyTiles, deletedMask.copy());
		}
	}
	
	private void fireNumOfMachete(final int numOfMachete) {
		for (final ModelListener listener : _listeners) {
			listener.numOfMachete(numOfMachete);
		}
	}

	private void fireUseMachete(final Point point) {
		for (final ModelListener listener : _listeners) {
			listener.showUseMachete(point);
		}
	}
	
    public SevenDoorsBoard getBoard() {
		return _state.getBoard();
    }

	public int getMoney() {
		return (int) (_state.getScore() * 10 * SevenDoorsConstants.MONEY_PER_SCORE);
	}
	
	public void makeMove(final SevenDoorsMove move) {
		fireStartMove();
		if (!canMove(move)){
			fireEndMove();
			return;
		}
		_state.increaseNumOfMadeMoves();
		_state.swapTiles(move);
		fireSwapTiles(move.getX1(), move.getY1(), move.getX2(), move.getY2());
		clear();
		if (_randomGenerator.nextInt(_100_PERSENTS) < _factory.getBonusProbability("jungle", _state.getLastScore(), _state.getLevel())) {
			fireJungleGrow(_state.growJungle());
		}
		checkNextLevel();
		fireSecretStatusChanged(_state.isSecretSolved());
		if(!hasMove()){
			fireNoMoreMoves();
		}
		fireEndMove();
		fireStoreGame();
		refreshTimer();
	}

	private void refreshTimer() {
		_timer.stop();
		startHintTimer();
		fireStopAnimateHint();
	}
	
	private void fireStoreGame() {
		try {
			for (final ModelListener listener : _listeners) {
				listener.storeGame(_state.getCopy());
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean hasMove(){
		return getMove() != null;
	}
	
	public SevenDoorsMove getMove(){
		final SevenDoorsMove move = canDoAtLeastOneMove();
		if(move != null){
			return move;
		}
		if(_state.isSecretSolved()) {
			return SevenDoorsMove.SECRET_MOVE;
		}
		final Point snakeCoord = _state.boardContainsAtLeastOneSnake();
		if(snakeCoord != null){
			return new SevenDoorsMove(snakeCoord.x, snakeCoord.y, snakeCoord.x, snakeCoord.y);
		}
		return null;
	}
	
	SevenDoorsMove canDoAtLeastOneMove(){
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				SevenDoorsMove move = new SevenDoorsMove(i, j, i + 1, j);
				if(canMove(move)){
					return move;
				}
				move = new SevenDoorsMove(i, j, i + 1, j + 1);
				if(canMove(move)){
					return move;
				}
				move = new SevenDoorsMove(i, j, i, j + 1);
				if(canMove(move)){
					return move;
				}
				move = new SevenDoorsMove(i, j, i - 1, j + 1);
				if(canMove(move)){
					return move;
				}
			}
		}
		return null;
	}
	
	public void solveSecret() {
		if (!_state.isSecretSolved()) {
			return;
		}
		fireStartMove();
		_state.deleleSecretTiles();
		fireTilesMarkedToDelete(_state.deletedMask());
		fireAddKey();
		_state.addScoreForMove();
		fireScore();
		_state.addOneKey();
		
		fillEmptySpaces();
		clear();
		_state.nextSecret();
		fireSetSecret(_state.getSecretNum(), _state.secretToArray());
		checkNextLevel();
		fireSecretStatusChanged(_state.isSecretSolved());
		fireEndMove();
	}
	
	private boolean canMove(final SevenDoorsMove move) {
		if (!move.isValid()) {
			return false;
		}
		if (!_state.getTile(move.getX1(), move.getY1()).canBeMoved() || !_state.getTile(move.getX2(), move.getY2()).canBeMoved()) {
			return false;
		}
		_state.swapTiles(move);
		final boolean result = _state.markSimpleTilesToDelete();
		_state.resetDeletedTiles();
		_state.swapTiles(move);
		return result;
	}
	
	public void clear() {
		while (_state.markTilesToDelete() | _state.markWaterFallTilesToDelete()) {
			fireTilesMarkedToDelete(_state.deletedMask());
			_state.addScoreForMove();
			fireScore();
			_state.increaseNeededKeys();
			fillEmptySpaces();
		}
	}
	
	public void fillEmptySpaces() {
		_state.spreadWaterfall();
		fireWaterfallSpread(_state.getWaterMask());
		final TilesFallMask fallMask = _state.moveTilesDown();
		_state.spreadWaterfall();
		final BoardMask waterMask = _state.getWaterMask();
		int waterX = -1;
		if (_state.countTilesToAdd() > 0 && _randomGenerator.nextInt(_100_PERSENTS) < _factory.getBonusProbability("waterfall", _state.getLastScore(), _state.getLevel())) {
			waterX = _state.addWater();
		}
		final Vector<Tile> tiles = _factory.generateTilesPackage(_state.countTilesToAdd(), _state.getLastScore(), _state.getLevel());
		final BoardMask deletedMask = _state.deletedMask();
		fireMoveAndAdd(fallMask, waterMask, waterX, tiles, deletedMask);
		_state.addNewTiles(tiles);
		final Vector deletedLianas = _state.cutNumOfLianasTiles();
		if (deletedLianas.size() > 0){
			fireLianasChangedToForest(deletedLianas);
		}
	}

	private void checkNextLevel() {
		if(_state.getKeys() / 7 + 1 > _state.getLevel()) {
			_state.increaseLevel();
			_state.increaseNumOfMacheteOnChangeLevel();
			fireNumOfMachete(_state.getNumOfMachete());
			_state.deleteAllTiles();
			_state.increaseScoreOnBoardSize();
			_state.clearLastScore();
			fireScore();
			fireNextLevel();
			if(gameEnded()) {
				_state.doubleScore();
				fireScore();
				fireGameFinished();
			} else {
				final Vector tiles = _factory.generateSimpleTilesPackage(_state.countTilesToAdd());
				fireTilesToAdd(tiles, _state.deletedMask());
				_state.addNewTiles(tiles);
				clear();
			}
		}
	}

	public void activateSnakes(final int x, final int y) {
		if (_state.getTile(x, y).getId() != SNAKE) {
			return;
		}
		fireStartMove();
		final Point p = new Point(x, y);
		_state.activateSnakes(p);
		fireSnakesActivated(x, y, _state.deletedMask());
		_state.increaseScoreOnBoardSize();
		_state.clearLastScore();
		fireScore();
		fillEmptySpaces();
		clear();
		checkNextLevel();
		fireSecretStatusChanged(_state.isSecretSolved());
		fireEndMove();
	}

	public void useMachete(final Point point) {
		if (_state.getNumOfMachete() > 0 && _state.useMachete(point)) {
			_state.decreaseNumOfMachete();
			fireUseMachete(point);
			fireNumOfMachete(_state.getNumOfMachete());
		}
	}

	public boolean[][] getJungle() {
		return _state.getJungle();
	}

	public int[][] getSecret() {
		return _state.secretToArray();
	}

	public int getSecretNum() {
		return _state.getSecretNum();
	}

	public long getScore() {
		return _state.getScore();
	}

	public int getKeys() {
		return _state.getKeys();
	}

	public int getMachete() {
		return _state.getNumOfMachete();
	}

	public int getLevel() {
		return _state.getLevel();
	}
	
	public void prepareGame(final EventManager eventManager) {
		_isPlaying = true;
		fireStartMove();
		eventManager.fireRemoveOldField();
		_factory = new FirstZoneTileFactory(_bonusProbabilityInfo, _randomGenerator);
		_state.createNewWaterFall();
		for (final ModelListener listener : _listeners) {
			listener.gameFilled(eventManager.hideMenu());
		}
		fireEndMove();
		fireStoreGame();
		startHintTimer();
	}

	private void startHintTimer() {
		_timer.scheduleRepeating(new Runnable() {
			
			@Override
			public void run() {
				fireHint();
			}
		}, SHOW_HINT_DELAY);
	}

	public void setPlayerMaxScore(final long playerMaxScore) {
		_playerMaxScore = playerMaxScore;
		
	}

	public long getPlayerMaxScore() {
		return _playerMaxScore;
	}

	// only for test
	void setBoard(final SevenDoorsBoard board) {
		_state.setBoard(board);
	}

	public void startNewGame() {
		prepareGame(_startGameEventManager);
	}

	public void reloadGame() {
		prepareGame(_reloadGameEventManager);
	}
	
	public void resumeGame() {
		prepareGame(_resumeGameEventManager);
	}

	public void resumeGame(final SevenDoorsState state) throws Exception {
		_state = state;
		resumeGame();
	}

	public boolean isPlaying() {
		return _isPlaying;
	}

	public boolean gameEnded() {
		return _state.getLevel() > 7;
	}

}

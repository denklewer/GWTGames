package sevendoors.client;


import games.client.BaseController;
import games.client.BaseView;
import games.client.BaseView.ActionFinishedScheduledCommand;
import games.client.StopListener;
import games.client.Stopper;
import games.shared.Point;
import games.shared.RandomGenerator;

import java.util.Vector;

import sevendoors.shared.BoardMask;
import sevendoors.shared.BonusProbabilityInfo;
import sevendoors.shared.ModelListener;
import sevendoors.shared.SevenDoorsModel;
import sevendoors.shared.SevenDoorsMove;
import sevendoors.shared.SevenDoorsProtocol;
import sevendoors.shared.SevenDoorsState;
import sevendoors.shared.Tile;
import sevendoors.shared.TilesFallMask;

public class SevenDoorsController extends BaseController<SevenDoorsModel> implements ModelListener{

	private final SevenDoorsView _view;
	private final RandomGenerator _randomGenerator;
	private final BonusProbabilityInfo _bonusProbabilityInfo;

	public SevenDoorsController(final SevenDoorsModel model, final SevenDoorsView view, final RandomGenerator randomGenerator, final BonusProbabilityInfo bonusProbabilityInfo) {
		super(model);
		_view = view;
		_randomGenerator = randomGenerator;
		_bonusProbabilityInfo = bonusProbabilityInfo;
	}
	
	@Override
	protected void start() {
		_view.start();
	}

	@Override
	protected BaseView getView() {
		return _view;
	}

	@Override
	public void gameFilled(final boolean hideMenu) {
		_moveActions.add(new Action("set board"){
			@Override
			public void run() {
				_view.setBoard(this, _model.getBoard().copy());
			}
		});
		
		_moveActions.add(new Action("gameFilled"){
			@Override
			public void run() {
				_view.setJungle(_model.getJungle());
				_view.setSecret(_model.getSecretNum(), _model.getSecret());
				_view.setScore(_model.getScore());
				_view.setKeys(_model.getKeys());
				_view.setNumOfMachete(_model.getMachete());
				if(hideMenu){
					_view.hideMenu();
				}
				finished();
			}
		});
	}

	public void sendMove(final SevenDoorsMove move) {
		_model.makeMove(move);
	}

	@Override
	public void tilesSwapped(final int x1, final int y1, final int x2, final int y2) {
		_moveActions.add(new Action("swap tiles") {
			@Override
			public void run() {
				_view.drawSwapTiles(this, x1, y1, x2, y2);
			}
		});		
	}

	@Override
	public void markTilesToDelete(final BoardMask mask) {
		_moveActions.add(new Action("delete tiles action"){
			@Override
			public void run() {
				_view.deleteTileWidgets(this, mask);
			}
		});
		
	}

	@Override
	public void addTiles(final Vector tiles, final BoardMask mask) {
		_moveActions.add(new Action("add tiles action"){
			@Override
			public void run() {
				final Stopper stopListener = new StopListener(
				new ActionFinishedScheduledCommand(this));
				_view.addTiles(stopListener, tiles, mask);
				if(stopListener.getExpected() == 0){
					stopListener.arrived();
				}
			}
		});
	}

	@Override
	public void setScore(final long score) {
		_view.setScore(score);
	}

	public void solveSecret() {
		_model.solveSecret();
	}

	@Override
	public void secretStatusChanged(final boolean solved) {
		_view.secretStatusChanged(solved);
	}

	@Override
	public void setSecret(final int secretNum, final int[][] secret) {
		_view.setSecret(_model.getSecretNum(), _model.getSecret());		
	}

	@Override
	public void addKey() {
		_view.addNewKey();
	}

	@Override
	public void jungleGrow(final Point grow) {
		_view.jungleGrow(grow);
	}

	public void useMachete(final Point point) {
		_model.useMachete(point);
	}

	@Override
	public void numOfMachete(final int numOfMachete) {
		_view.setNumOfMachete(_model.getMachete());
	}

	@Override
	public void showUseMachete(final Point point) {
		_view.showUseMachete(point);
	}

	@Override
	public void nextLevel(final int level) {
		_moveActions.add(new Action("delete all tiles"){
			@Override
			public void run() {
				_view.removeAllTiles(this, level);
			}
		});
	}

	public void activateSnakes(final int i, final int j) {
		_model.activateSnakes(i, j);		
	}

	@Override
	public void snakesActivated(final int x, final int y, final BoardMask deletedMask) {
		_moveActions.add(new Action("snakesActivated"){
			@Override
			public void run() {
				_view.snakesActivated(this, x, y, deletedMask);
			}
		});
		
		_moveActions.add(new Action("delete tiles after snakesActivated"){
			@Override
			public void run() {
				_view.deleteTileWidgets(this, deletedMask);
			}
		});
	}

	@Override
	public void lianasChangedToForest(final Vector deletedLianas) {
		_moveActions.add(new Action("lianasChangedToForest"){
			@Override
			public void run() {
				_view.lianasChangedToForest(this, deletedLianas);
			}
		});
	}

	@Override
	public void waterfallSpread(final BoardMask waterMask) {
		_moveActions.add(new Action("waterfall spread"){
			@Override
			public void run() {
				_view.waterfallSpread(this, waterMask);
			}
		});
	}

	@Override
	public void gameFinished() {
		_moveActions.add(new Action("game finished"){
			@Override
			public void run() {
				_view.gameFinished();
				finished();
			}
		});
	}

	public void startNewGame() {
		storeScoreAndCreateModel();
		_model.reloadGame();
	}

	public void storeScoreAndCreateModel() {
		storeScore(_model.getScore());
		_model.removeListener(this);
		_model = new SevenDoorsModel(_randomGenerator, _bonusProbabilityInfo, new ClientTimer());
		_model.addListener(this);
	}

	@Override
	public void storeScore(final long score) {
		long scoreToSave = score;
		if(score == 0){
			try {
				final SevenDoorsState restore = _view.restore(_randomGenerator);
				if(restore != null){
					scoreToSave = restore.getScore();
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		_view.storeScore(SevenDoorsProtocol.SCORE_COEFF * scoreToSave);
	}

	@Override
	public void noMoreMoves() {
		_view.noMoreMoves();
	}

	@Override
	public void moveAndAdd(final TilesFallMask fallMask, final BoardMask waterMask,
			final int waterX, final Vector<Tile> tiles, final BoardMask deletedMask) {
		_moveActions.add(new Action("move and add"){
			@Override
			public void run() {
				final StopListener stopListener = new StopListener(
						new ActionFinishedScheduledCommand(this)){
					@Override
					public void arrived() {
						super.arrived();
						if(getExpected() <= 0){
							_view.extraActions();
						}
					};
				};
				_view.moveTilesDown(stopListener, fallMask);
				_view.waterfallSpread(stopListener, waterMask);
				if(waterX != -1){
					_view.addWater(stopListener, waterX);
				}
				_view.addTiles(stopListener, tiles, deletedMask);
				if(stopListener.getExpected() == 0){
					stopListener.arrived();
				}
			}
		});
	}

	@Override
	public void storeGame(final SevenDoorsState sevenDoorsState) {
		_view.storeGame(sevenDoorsState);
	}

	public void resumeGame() {
		try {
			final SevenDoorsState state = _view.restore(_randomGenerator);
			_model.resumeGame(state);
			if(!_model.hasMove() || _model.gameEnded()){
				startNewGame();
			}
		} catch (final Exception e) {
			resumeBadGame();
			System.err.println("Cant parse game");
		}
	}

	public void resumeBadGame() {
		storeScoreAndCreateModel();
		_model.reloadGame();
	}
	
	public void restartEndedGame() {
		storeScoreAndCreateModel();
		_model.startNewGame();
	}

	public boolean isPlaying() {
		return _model.isPlaying();
	}

	public boolean isGameEnded() {
		return _model.gameEnded();
	}

	@Override
	public void resetStoredGame() {
		_view.resetStoredGame();
	}

	@Override
	public void showHint(final SevenDoorsMove move) {
		if(_view.isShowingHint()){
			return;
		}
		if(move == SevenDoorsMove.SECRET_MOVE){
			_view.animateSecret();
		} else {
			_view.showSwapHint(move);
		}
	}

	@Override
	public void stopShowHint() {
		_view.stopShowHint();
	}
	
}

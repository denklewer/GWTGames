package sevendoors.client;

import games.client.BaseController.Action;
import games.client.BaseView;
import games.client.GameController;
import games.client.StopListener;
import games.client.Stopper;
import games.client.sprites.EngineFinishListener;
import games.client.sprites.ImageEngine;
import games.client.sprites.Rectangle;
import games.client.sprites.Sprite;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteGroup;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.FallDownMovementEngine;
import games.client.util.ImageFactory;
import games.client.util.ScoreWidget;
import games.client.util.SimpleButton;
import games.client.util.UIUtils;
import games.shared.Point;
import games.shared.RandomGenerator;
import games.shared.Resources;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import sevendoors.client.GameStorage.TopScore;
import sevendoors.shared.BoardMask;
import sevendoors.shared.SevenDoorsBoard;
import sevendoors.shared.SevenDoorsMove;
import sevendoors.shared.SevenDoorsProtocol;
import sevendoors.shared.SevenDoorsState;
import sevendoors.shared.Tile;
import sevendoors.shared.TilesFallMask;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Timer;

public class SevenDoorsView extends BaseView {
	
	private static final String TOPSCORE = "SevenGates.topscore";
	static final String GAME = "SevenGates.game";

	class MovingMachete extends Sprite<DrawElement> {
		
		public MovingMachete(final int x, final int y) {
			super(x, y, 80, 90);
			
			setImageEngine(new ImageEngine<DrawElement>() {
				
				@Override
				public boolean updateTick(final long timestamp) {
					return false;
				}
				
				@Override
				public <Z> void draw(final SpriteGraphics<Z> graphics,
						final SpriteImage<Z> image, final int x, final int y,
						final Rectangle dirtyRegion) {
					_macheteImage[0].draw(graphics, x, y, dirtyRegion);
					if(_canUseMachete){
						_lightMachete.draw(graphics, x, y, dirtyRegion);
					}
				}
				
			});
		}
		
		@Override
		public boolean onMouseDown(final int x, final int y, final int modifiers) {
			return false;
		}
		
		@Override
		public boolean onMouseMove(final int x, final int y) {
			return false;
		}
		
	}
	
	public class Machete extends Sprite<DrawElement> {
		
		private Vector<Integer> _digits = new Vector<Integer>();

		public Machete(final SevenDoorsView parent) {
			super(520, 469, 80, 90);
			final DrawElement quan = Resources.get(ImageFactory.class).getDrawElement("bonus-machete-quan");
			final SpriteImage[] numbers = UIUtils.splitHorizontalStripeByVerticalTransparentLine(quan, quan.getWidth(), quan.getHeight());
			setImageEngine(new ImageEngine<DrawElement>() {
				@Override
				public boolean updateTick(final long timestamp) {
					return false;
				}
				@Override
				public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image,
						final int x, final int y, final Rectangle dirtyRegion) {
					if (_numOfMachete > 0) {
						if (isOver()){
							_lightMachete.draw(graphics, x, y, dirtyRegion);
						} else { 
							_macheteImage[1].draw(graphics, x, y, dirtyRegion);
						}
					} else {
						_macheteImage[0].draw(graphics, x, y, dirtyRegion);
					}
					int macheteX = 40;
					for(int i = _digits.size() - 1; i >= 0; i--) {
						final int index = (_digits.elementAt(i).intValue()+ 9) % 10;
						numbers[index].draw(graphics, x + macheteX, y + 58, dirtyRegion);
						macheteX += numbers[index].width();
					}
				}
			});
			recalculateNumOfMachete();
		}
		
		public void recalculateNumOfMachete() {
			int num = _numOfMachete;
			_digits.removeAllElements();
			for (; num > 0 ;num = num /= 10) {
				final int digit = num % 10;
				_digits.addElement(new Integer(digit));
			}
			markDirty();
		}
		
		@Override
		public boolean onMouseEntered(final int x, final int y) {
			markDirty();
			return true;
		}
		
		@Override
		public boolean onMouseExit() {
			markDirty();
			return true;
		}
		
		@Override
		public boolean onMouseUp(final int x, final int y, final int modifiers) {
			if (!_movingMachete.isVisible()) {
				if (_numOfMachete > 0) {
					activateMachete();
					--_numOfMachete;
					recalculateNumOfMachete();
				}
			} else {
				setMachete(false);
				++_numOfMachete;
				recalculateNumOfMachete();
			}
			return true;
		}
		
	}

	
	public final static int LEFT_INDENT = 50;
	public final static int TOP_INDENT = 40;
	public static final int PANE_SIZE = 496;

	private static final int FALL_DOWN_SPEED = 10;
	
	private static Sprite<DrawElement> GAME_FINISHED;
	protected static Sprite<DrawElement> GAME_FINISHED_TEXT;
	
	private static Point[] _keysWidgetsCoords = new Point[] {
		new Point(537, 129),
		new Point(537, 250),
		new Point(537, 376),
		new Point(-9, 129),
		new Point(-9, 250),
		new Point(-9, 376),
		new Point(266, -9),
	};
	
	private SevenDoorsController _controller;
	private List<TileWidget> _tiles = new ArrayList<TileWidget>();
	private TileWidget _lastTile;
	private TopLayerWidget _topLayerWidget;

	private static SpriteImage[] _macheteImage;
	private static SpriteImage _lightMachete;
	
	private SpriteGroup<DrawElement> _topLayerSprites;
	private SevenDoorsMenu _menu;
	private SevenDoorsRulesGroup _rules;
	private ScoreBoardWidget _scoreWidget;
	
	private SecretWidget _secretWidget;
	private SymbolsWidget _symbolsWidget;
	
	private Machete _machete;
	private int _numOfMachete;
	private boolean _canUseMachete;
	
	private MovingMachete _movingMachete;
	
	private JungleWidget _jungle;
	private WaterFallView _waterFallView;
	
	private int _numOfKeys;
	
	private KeysWidget[] _keysWidgets = new KeysWidget[7];
	private Vector<KeyWidget> _keyWidgets = new Vector<KeyWidget>();
	
	private SimpleButton _menuButton;
	private NoMoreMovesDialog _noMoreMovesDialog;
	
	private GameStorage _storage = new GameStorage();
	private Store _stateStoring = new SevenDoorsStateStoring();
	private AnimationTimer _timer;
	private Sprite _cave;
	private Sprite _scroll;
	
	@Override
	protected Map<String, String> getImageNames() {
		final Map<String, String> imageNames = new HashMap<String, String>();
		imageNames.put("bg-01", "images/sevendoors/bg-01.gif");

		imageNames.put("bonus-light-01", "images/sevendoors/bonus-light-01.png");
		imageNames.put("bonus-machete", "images/sevendoors/bonus-machete-01.png");
		imageNames.put("bonus-machete-light", "images/sevendoors/bonus-machete-light-01.png");
		imageNames.put("bonus-machete-quan", "images/sevendoors/bonus-machete-quan-01.png");
		
		imageNames.put("bonus-secret", "images/sevendoors/bonus-secret-01.png");
		imageNames.put("bonus-secret-light", "images/sevendoors/bonus-secret-light-01.png");
		imageNames.put("bonus-secret-symbol-01", "images/sevendoors/bonus-secret-symbol-01.png");
		imageNames.put("bonus-secret-symbol-02", "images/sevendoors/bonus-secret-symbol-02.gif");
		
		imageNames.put("box", "images/sevendoors/box01.png");

		imageNames.put("disappear-01", "images/sevendoors/disappear-01.png");
		
		imageNames.put("end_screen_txt", "images/sevendoors/end_screen_txt.png");
		
		imageNames.put("final-01", "images/sevendoors/end_screen_back.png");
		
		imageNames.put("home", "images/sevendoors/home.png");
		
		imageNames.put("id-key-01", "images/sevendoors/id-key-01.png");
		imageNames.put("id-key-02", "images/sevendoors/id-key-02.png");
		imageNames.put("id-key-03", "images/sevendoors/id-key-03.png");
		imageNames.put("id-key-04", "images/sevendoors/id-key-04.png");
		imageNames.put("id-key-05", "images/sevendoors/id-key-05.png");
		imageNames.put("id-key-06", "images/sevendoors/id-key-06.png");
		imageNames.put("id-key-07", "images/sevendoors/id-key-07.png");
		
		imageNames.put("id-zone", "images/sevendoors/id-zone-01.png");
		imageNames.put("id-key-bg", "images/sevendoors/id-key-bg-01.png");
		
		imageNames.put("key-light", "images/sevendoors/key-light.png");
		imageNames.put("liana-01", "images/sevendoors/liana-01.png");
		imageNames.put("lightbox", "images/sevendoors/box_bonus.png");
		
		imageNames.put("message-01", "images/sevendoors/message-01.png");
		imageNames.put("message-findkey-01", "images/sevendoors/message-findkey-01.png");
		imageNames.put("message-key-01", "images/sevendoors/message-key-01.png");
		imageNames.put("message-nomoves-01", "images/sevendoors/message-nomoves-01.png");
		
		imageNames.put("move-marker-01", "images/sevendoors/move-marker-01.png");
		imageNames.put("score-01", "images/sevendoors/score-01.png");
		imageNames.put("score-02", "images/sevendoors/score-02.png");
		imageNames.put("score-field", "images/sevendoors/score-field-01.png");
		imageNames.put("score-star", "images/sevendoors/score-star.png");
		imageNames.put("score-02_frog", "images/sevendoors/score-02_frog.png");
		
		imageNames.put("snake-01", "images/sevendoors/snake_h.png");
		imageNames.put("snake-02", "images/sevendoors/snake_d.png");
		
		imageNames.put("thicket", "images/sevendoors/thicket-01.png");
		
		imageNames.put("tile-basket-01", "images/sevendoors/basket.png");
		imageNames.put("tile-butterfly", "images/sevendoors/butterfly.png");
		imageNames.put("tile-canabis", "images/sevendoors/plant.png");
		imageNames.put("tile-chest-01", "images/sevendoors/chest.png");
		imageNames.put("tile-depthicket-01", "images/sevendoors/depth.png");
		imageNames.put("tile-elephant", "images/sevendoors/elephant.png");
		imageNames.put("tile-flower", "images/sevendoors/flower.png");
		imageNames.put("tile-frog-01", "images/sevendoors/frog.png");
		imageNames.put("tile-mask", "images/sevendoors/mask.png");
		imageNames.put("tile-star-01", "images/sevendoors/star.png");
		imageNames.put("tile-tukan", "images/sevendoors/tucan.png");
		imageNames.put("tile-water-01", "images/sevendoors/tile-water-01.png");
		
		imageNames.put("water-01", "images/sevendoors/water.png");
		
		imageNames.put("menu_zone", "images/sevendoors/menu/menu_zone.png");
		imageNames.put("newgame", "images/sevendoors/menu/newgame.png");
		imageNames.put("resume_button", "images/sevendoors/menu/resume_button.png");
		imageNames.put("saved_game", "images/sevendoors/menu/saved_game.png");
		imageNames.put("settings", "images/sevendoors/menu/settings.png");
		imageNames.put("topscore", "images/sevendoors/menu/topscore.png");
		imageNames.put("top-score", "images/sevendoors/menu/top-score.png");
		imageNames.put("back_button", "images/sevendoors/menu/back_button.png");
		imageNames.put("help_screen", "images/sevendoors/menu/help_screen.png");
		imageNames.put("rules_button", "images/sevendoors/menu/rules_button.png");
		imageNames.put("cave", "images/sevendoors/menu/cave.png");
		
		return imageNames;
	}

	@Override
	public void init(final GameController controller) {
		_controller = (SevenDoorsController) controller;
		super.init();
		
	}

	@Override
	protected ScheduledCommand getStartGameCommand() {
		return new ScheduledCommand() {
			@Override
			public void execute() {
				_jungle = new JungleWidget(SevenDoorsView.this);
				_spriteManager.addListener(_jungle);
				
				_waterFallView = new WaterFallView();
				_spriteManager.addListener(_waterFallView);
				
				_topLayerSprites = new SpriteGroup<DrawElement>();
				_spriteManager.addListener(_topLayerSprites);
				
				
				initTileImages();
				initView();
				
				final SpriteImage<DrawElement> gameFinished = new SpriteImage<DrawElement>(Resources.get(ImageFactory.class).getDrawElement("final-01"), getGameWidth(), getGameHeight());
				GAME_FINISHED = new Sprite<DrawElement>(gameFinished, 0, 0, gameFinished.width(), gameFinished.height());
				final SpriteImage<DrawElement> gameFinishedText = new SpriteImage<DrawElement>(Resources.get(ImageFactory.class).getDrawElement("end_screen_txt"), getGameWidth(), getGameHeight());
				GAME_FINISHED_TEXT = new Sprite<DrawElement>(gameFinishedText, 0, 0, gameFinishedText.width(), gameFinishedText.height());
				GAME_FINISHED_TEXT.setVisible(false);
				_gameSprites.addSprite(GAME_FINISHED);
				_topLayerSprites.addSprite(GAME_FINISHED_TEXT);
				setVisibleGameFinishedWidget(false);
				_controller.uiReady();
			}
		};
	}

	private void initTileImages() {
		_macheteImage = UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("bonus-machete"), 80, 180, 2);
		_lightMachete = new SpriteImage<DrawElement>(Resources.get(ImageFactory.class).getDrawElement("bonus-machete-light"), 80, 90);
	}

	private void initView() {
		addBackground();
		
		addNoMoreMovesDialog();
		
		_scoreWidget = new ScoreBoardWidget(216, 537, 165, 27);
		_topLayerSprites.addSprite(_scoreWidget);
		
		_secretWidget = new SecretWidget(this, 0, 491);
		_topLayerSprites.addSprite(_secretWidget);
		
		_symbolsWidget = new SymbolsWidget();
		_topLayerSprites.addSprite(_symbolsWidget);
		
		_machete = new Machete(this);
		_topLayerSprites.addSprite(_machete);
		
		_movingMachete = new MovingMachete(_machete.getX(), _machete.getY());
		setMachete(false);
		_topLayerSprites.addSprite(_movingMachete);
		
		addKeysWidgets();
		
		addMenuButton();
		
		createScreen();
		
	}

	public void addMenuButton() {
		final DrawElement newGameStripe = Resources.get(ImageFactory.class).getDrawElement("home");
		final int x = getGameWidth() - newGameStripe.getWidth();
		_menuButton = new SimpleButton(x, 3, newGameStripe.getWidth(), newGameStripe.getHeight(), newGameStripe, new Runnable() {

			@Override
			public void run() {
				if(_noMoreMovesDialog.isVisible()){
					return;
				}
				if(_controller.isGameEnded()){
					_controller.restartEndedGame();
				}
				_menu.showMenu();
				hideGame();
			}
		}, 3){
			@Override
			public boolean onMouseEntered(final int x, final int y) {
				if(_noMoreMovesDialog.isVisible()){
					return true;
				}
				return super.onMouseEntered(x, y);
			}
			
			@Override
			public boolean onMouseExit() {
				if(_noMoreMovesDialog.isVisible()){
					return true;
				}
				return super.onMouseExit();
			}
		};
		_topLayerSprites.addSprite(_menuButton);
	}
	
	public void startNewGame() {
		_controller.startNewGame();
	}

	private void addNoMoreMovesDialog() {
		_noMoreMovesDialog = new NoMoreMovesDialog(this, LEFT_INDENT + TileWidget.TILE_SIZE, TOP_INDENT + TileWidget.TILE_SIZE, 6 * TileWidget.TILE_SIZE, 6 * TileWidget.TILE_SIZE);
		_topLayerSprites.addSprite(_noMoreMovesDialog);
		_noMoreMovesDialog.setVisible(false);
	}

	private void addKeysWidgets() {
		for (int i = 0; i < 7; i++) {
			_keysWidgets[i] = new KeysWidget(i, _keysWidgetsCoords[i].x, _keysWidgetsCoords[i].y);
			_topLayerSprites.addSprite(_keysWidgets[i]);
		}
	}
	
	public void setKeys(final int keys) {
		_numOfKeys = 0;
		removeKeyWidgets();
		for (int i = 0; i < keys; i++) {
			addKey(_keysWidgets[_numOfKeys / 7].getX(), _keysWidgets[_numOfKeys / 7].getY());
		}
	}
	
	private void removeKeyWidgets() {
		final Enumeration<KeyWidget> en = _keyWidgets.elements();
		while (en.hasMoreElements()) {
			_topLayerSprites.removeSprite(en.nextElement());
		}
		_keyWidgets.removeAllElements();
	}

	private void addBackground() {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 9; j++) {
				final SpriteImage<DrawElement> bg01 = new SpriteImage<DrawElement>(Resources.get(ImageFactory.class).getDrawElement("bg-01"), TileWidget.TILE_SIZE, TileWidget.TILE_SIZE);
				_gameSprites.addSprite(new Sprite<DrawElement>(bg01, i * TileWidget.TILE_SIZE, j * TileWidget.TILE_SIZE, TileWidget.TILE_SIZE, TileWidget.TILE_SIZE));
			}
		}
		_topLayerWidget = new TopLayerWidget(0, 0, getGameWidth(), getGameHeight());
		_topLayerSprites.addSprite(_topLayerWidget);
	}
	
	@Override
	protected String getPanelName() {
		return "game";
	}

	@Override
	protected int getGameHeight() {
		return 568;
	}

	@Override
	public int getGameWidth() {
		return 600;
	}

	public TileWidget lastTile() {
		return _lastTile;
	}

	public void setLastTile(final TileWidget tile) {
		_lastTile = tile;
	}

	public void setBoard(final Action action, final SevenDoorsBoard board) {
		setVisibleGameFinishedWidget(false);
		for (int i = 0; i < SevenDoorsProtocol.BOARD_SIZE; i++) {
			for (int j = 0; j < SevenDoorsProtocol.BOARD_SIZE; j++) {
				final Tile tile = board.getTile(i, j);
				if (tile != null) {
					if (tile.getId() != SevenDoorsProtocol.WATERFALL) {
						final TileWidget widget = new TileWidget(this, tile, i, j);
						_tiles.add(widget);
						addWidget(widget);
					} 
				}
			}
		}
		_waterFallView.setBoard(action, board);
	}

	private void setVisibleGameFinishedWidget(final boolean value) {
		GAME_FINISHED.setVisible(value);
		GAME_FINISHED_TEXT.setVisible(value);
	}

	private void addWidget(final Sprite<DrawElement> widget) {
		_gameSprites.addSprite(widget);
	}

	public void sendMove(final SevenDoorsMove move) {
		_controller.sendMove(move);
	}

	public void drawSwapTiles(final Action action, final int x1, final int y1, final int x2, final int y2) {
		final Stopper stopListener = new StopListener(
				new ActionFinishedScheduledCommand(action));
		stopListener.setExpected(2);
		final TileWidget sprite1 = findTileWidget(x1, y1);
		final TileWidget sprite2 = findTileWidget(x2, y2);
		
		final int iFor1 = sprite2.getI();
		final int jFor1 = sprite2.getJ();
		
		final int iFor2 = sprite1.getI();
		final int jFor2 = sprite1.getJ();
		
		sprite1.setI(iFor1);
		sprite1.setJ(jFor1);
		
		sprite2.setI(iFor2);
		sprite2.setJ(jFor2);
		
		moveIconFromTo(stopListener, sprite1, sprite2.getX(), sprite2.getY());
		moveIconFromTo(stopListener, sprite2, sprite1.getX(), sprite1.getY());		
	}
	
	private TileWidget findTileWidget(final int nx, final int ny) {
		for (int i = 0; i < _tiles.size(); i++) {
			final TileWidget tile = _tiles.get(i);
			if (tile.getI() == nx && tile.getJ() == ny) {
				return tile;
			}
		}
		return null;
	}
	
	String getTileName(final int i, final int j){
		return _tiles.get(SevenDoorsProtocol.BOARD_SIZE * i + j).getTileName();
	}

	private void moveIconFromTo(final Stopper stopListener,
			final TileWidget sprite, final int destX, final int destY) {

		_gameSprites.toTop(sprite);
		sprite.swap(stopListener, destX, destY);

	}

	public void deleteTileWidgets(final Action action, final BoardMask deletedMask) {
		final Stopper stopListener = new StopListener(
				new ActionFinishedScheduledCommand(action));
		boolean hasBonus = false;
		for (final TileWidget tile : _tiles) {
			if (deletedMask.isTrue(tile.getI(), tile.getJ()) && tile.isBonus()) {
				hasBonus = true;
				break;
			}
		}
		for (int i = 0; i < _tiles.size(); i++) {
			final TileWidget tile = _tiles.get(i);
			if (deletedMask.isTrue(tile.getI(), tile.getJ())) {
				stopListener.incrementExpectedBy(1);
				tile.disappear(stopListener, hasBonus);
				_tiles.remove(tile);
				i--;
			} 
		}
		_waterFallView.tilesMarkedToDelete(stopListener, deletedMask);
		if(stopListener.getExpected() == 0){
			stopListener.arrived();
		}

	}

	public void removeSprite(final Sprite<DrawElement> sprite) {
		_gameSprites.removeSprite(sprite);
	}

	public void moveTilesDown(final Stopper stopListener, final TilesFallMask mask) {
		for (int i = 0; i < _tiles.size(); i++) {
			final TileWidget tile = _tiles.get(i);
			final int newY = mask.getNewPlace(tile.getI(), tile.getJ());
			if (newY != tile.getJ()) {
				stopListener.incrementExpectedBy(1);
				slideDownSprite(stopListener, tile, newY * TileWidget.TILE_SIZE + TOP_INDENT);
				tile.setJ(newY);
			}
		}
	}

	private void slideDownSprite(final EngineFinishListener stopListener,
			final Sprite<DrawElement> sprite, final int destY) {
		sprite.setMovementEngine(new FallDownMovementEngine<DrawElement>(
				stopListener, sprite, destY, FALL_DOWN_SPEED));
	}

	public void addTiles(final Stopper stopListener, final Vector<Tile> tiles, final BoardMask deletedMask) {
		int number = 0;
		for (int i = 0; i < SevenDoorsProtocol.BOARD_SIZE; i++) {
			for (int j = 0; j < SevenDoorsProtocol.BOARD_SIZE; j++) {
				if (deletedMask.isTrue(i, j)) {
					int k;
					for (k = j + 1; k < SevenDoorsProtocol.BOARD_SIZE && deletedMask.isTrue(i, k); k++) {
						;
					}
					final Tile tile = tiles.elementAt(number++);
					final TileWidget widget = new TileWidget(this, tile, i, j);
					widget.moveTo(LEFT_INDENT + i * TileWidget.TILE_SIZE,  (j - k) * TileWidget.TILE_SIZE);
					_tiles.add(widget);
					addWidget(widget);
					stopListener.incrementExpectedBy(1);
					slideDownSprite(stopListener, widget, TOP_INDENT + TileWidget.TILE_SIZE * j);
				}
			}
		}
	}

	public void setScore(final long score) {
		_scoreWidget.setScore(score);		
	}

	public void showSymbolsEvent(final boolean value) {
		if(GAME_FINISHED.isVisible() || _noMoreMovesDialog.isVisible()){
			return ;
		}
		_symbolsWidget.setVisible(value);
	}

	public void setSecret(final int secretNum, final int[][] secret) {
		_secretWidget.setSecret(secretNum);
		_symbolsWidget.setSecret(secret);
	}

	public void setNumOfMachete (final int num) {
		_numOfMachete = num;
		_machete.recalculateNumOfMachete();
	}

	@Override
	protected void onMouseMove(final int x, final int y) {
		super.onMouseMove(x, y);
		if (hasMachete()) {
			_canUseMachete = _jungle.canUseMachete(x - LEFT_INDENT, y - TOP_INDENT);
			_movingMachete.moveTo(x - _movingMachete.getWidth() / 2, y - _movingMachete.getHeight() / 2);
		}
	}
	
	public boolean hasMachete() {
		return _movingMachete != null && _movingMachete.isVisible();
	}
	
	private void activateMachete() {
		setMachete(true);
		_movingMachete.moveTo(_machete.getX(), _machete.getY());
	}

	public void secretSolved() {
		if(GAME_FINISHED.isVisible()){
			return;
		}
		_controller.solveSecret();
	}

	public void secretStatusChanged(final boolean solved) {
		_secretWidget.secretStatusChanged(solved);
	}

	public void addNewKey() {
		addKey(LEFT_INDENT + (PANE_SIZE - _keysWidgets[0].getWidth()) / 2, TOP_INDENT + (PANE_SIZE - _keysWidgets[0].getHeight()) / 2);		
	}
	
	public void addKey(final int xx, final int yy) {
		if (_numOfKeys == 49) {
			return;
		}
		final KeyWidget keyWidget = new KeyWidget(_keysWidgets[_numOfKeys / 7], xx, yy, _numOfKeys % 7);
		keyWidget.calculateTrajectory();
		_topLayerSprites.addSprite(keyWidget);
		_keyWidgets.addElement(keyWidget);
		_numOfKeys++;
	}

	public void jungleGrow(final Point grow) {
		_jungle.grow(grow);		
	}

	public TileWidget findTile(final int x, final int y) {
		for (final TileWidget tile : _tiles) {
			if(tile.contains(x, y)){
				return tile;
			}
		}
		return null;
	}

	public void useMachete(final Point point) {
		_controller.useMachete(point);
	}

	public void showUseMachete(final Point point) {
		_jungle.showUseMachete(point);
	}

	public void setMachete(final boolean value) {
		_movingMachete.setVisible(value);		
	}

	public void addScore(final int xx, final int yy, final int score, final boolean starScore) {
		ScoreWidget scoreWidget;
		if(score < 0){
			scoreWidget = new SevenDoorsNegativeScoreWidget(_topLayerSprites, score);
		} else {
			scoreWidget = new SevenDoorsScoreWidget(_topLayerSprites, score, starScore);
		}
		final int x = xx - scoreWidget.getWidth()/2;
		final int y = yy - scoreWidget.getHeight()/2;
		scoreWidget.moveTo(x, y);
		scoreWidget.setX0(x);
		scoreWidget.setY0(y);
		_topLayerSprites.addSprite(scoreWidget);
	}

	public void removeTopLayerSprite(final Sprite<DrawElement> sprite) {
		_topLayerSprites.removeSprite(sprite);
	}

	public void removeAllTiles(final Action action, final int level) {
		final Stopper stopListener = new StopListener(
				new ActionFinishedScheduledCommand(action));
		for (int i = 0; i < SevenDoorsProtocol.BOARD_SIZE; i++) {
			final Vector<TileWidget> line = getLine(i);
			for (int j = 0; j < line.size(); j++) {
				stopListener.incrementExpectedBy(1);
				final TileWidget tile = line.elementAt(j);
				tile.disappear(stopListener, (i + 1) * 100, false);
				_tiles.remove(tile);
			}
		}
		
		_waterFallView.nextLevel(stopListener);
		if(stopListener.getExpected() == 0){
			stopListener.arrived();
		}
		if(level == 0){
			return;
		}
		final LevelFinishedWidget levelFinishedWidget = new LevelFinishedWidget(this, level - 1);
		levelFinishedWidget.moveTo(LEFT_INDENT + PANE_SIZE / 2 - levelFinishedWidget.getWidth() / 2, PANE_SIZE);
		_topLayerSprites.addSprite(levelFinishedWidget);
		
	}
	
	private Vector<TileWidget> getLine(final int n) {
		final Vector<TileWidget> result = new Vector<TileWidget>();
		for (int i = 0; i < _tiles.size(); i++) {
			final TileWidget tw = _tiles.get(i);
			if (tw.getJ() == n) {
				result.addElement(tw);
			}
		}
		return result;
	}

	public void activateSnakes(final int i, final int j) {
		_controller.activateSnakes(i, j);
	}

	public void snakesActivated(final Action action, final int x, final int y, final BoardMask deletedMask) {
		final Stopper stopListener = new StopListener(
				new ActionFinishedScheduledCommand(action));
		for (int i = 0; i < SevenDoorsProtocol.BOARD_SIZE; i++) {
			stopListener.incrementExpectedBy(1);
			final int snakeX = LEFT_INDENT + x * TileWidget.TILE_SIZE;
			final int snakeY = TOP_INDENT + y * TileWidget.TILE_SIZE;
			final SnakeWidget snakeWidget = new SnakeWidget(this, snakeX, snakeY, stopListener, i, deletedMask);
			addWidget(snakeWidget);
		}
	}
	
	public void eatWidget(final Stopper action, final int i, final int j) {
		final TileWidget w = findTileWidget(i, j);
		
		if (w != null && !w.isDisappearing()) {
			action.incrementExpectedBy(1);
			w.disappear(action);
			_tiles.remove(w);
		}
	}

	public void lianasChangedToForest(final Action action, final Vector deletedLianas) {
		final Stopper stopListener = new StopListener(
				new ActionFinishedScheduledCommand(action));
		final Vector forestPoints = deletedLianas;
		for (int i = 0; i < forestPoints.size(); i++) {
			final Point p = (Point) forestPoints.elementAt(i);
			final TileWidget tw = findTileWidget(p.x, p.y);
			stopListener.incrementExpectedBy(1);
			tw.changeToForest(stopListener);
		}
	}

	public void addWater(final Stopper stopListener, final int x) {
		stopListener.incrementExpectedBy(1);
		_waterFallView.addWater(stopListener, x);		
	}

	public void waterfallSpread(final Action action, final BoardMask waterMask) {
		_waterFallView.waterfallSpread(action, waterMask);	
	}
	
	public void waterfallSpread(final Stopper action, final BoardMask waterMask) {
		_waterFallView.waterfallSpread(action, waterMask);	
	}

	public void gameFinished() {
		setVisibleGameFinishedWidget(true);
		_jungle.setVisible(false);
	}

	public void noMoreMoves() {
		_topLayerSprites.toTop(_noMoreMovesDialog);
		_noMoreMovesDialog.setVisible(true);
	}

	public void setJungle(final boolean[][] jungle) {
		_jungle.setJungle(jungle);
	}

	public void extraActions() {
		_waterFallView.clearWaterFallMask();
	}

	public void storeGame(final SevenDoorsState sevenDoorsState) {
		try {
			_storage.saveGame(GAME, _stateStoring.store(sevenDoorsState));
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	
	public void hideMenu(){
		_menu.hide();
	}

	public void resumeGame() {
		if(!_controller.isPlaying()){
			_controller.resumeGame();
		}
	}

	public void storeScore(final long score) {
		_storage.saveScore(TOPSCORE, score);
	}

	public List<TopScore> getTopScore() {
		return _storage.getTopScore(TOPSCORE, 10);
	}

	public boolean hasSavedGame() {
		return _storage.haveSavedGame(GAME);
	}

	public int getGameX(){
		return canvas.getAbsoluteLeft();
	}
	
	public int getGameY(){
		return canvas.getAbsoluteTop();
	}

	public SevenDoorsState restore(final RandomGenerator randomGenerator) throws Exception {
		final String loadGame = _storage.loadGame(GAME);
		if(loadGame == null || "".equals(loadGame)){
			return null;
		}
		return _stateStoring.restore(loadGame, randomGenerator);
	}

	public void start() {
		createMenu();
		createRules();
	}

	private void createMenu() {
		hideGame();
		final boolean haveSavedGame = _storage.haveSavedGame(GAME);
		_menu = new SevenDoorsMenu(this);
		_menu.initMenu(haveSavedGame);
		showMenu();
	}
	
	private void createRules() {
		_rules = new SevenDoorsRulesGroup(this);
	}

	public void resetStoredGame() {
		_storage.resetGame(GAME);
	}

	public void showSwapHint(final SevenDoorsMove move) {
		final TileWidget tile1 = findTileWidget(move.getX1(), move.getY1());
		final TileWidget tile2 = findTileWidget(move.getX2(), move.getY2());
		showSwapHint(tile1, tile2);
	}
	
	private static class AnimationTimer extends Timer{
		
		private Runnable _startAction;
		private Runnable _stopAction;

		public AnimationTimer(final Runnable startAction, final Runnable stopAction) {
			_startAction = startAction;
			_stopAction = stopAction;
		}
		
		@Override
		public void run() {
			if(_startAction == null){
				return;
			}
			_startAction.run();
		}
		
		public void stop(){
			if(_stopAction == null){
				return;
			}
			_stopAction.run();
		}
		
	}
	
	private void showSwapHint(final TileWidget tile1, final TileWidget tile2){
		createTimer(new Runnable() {
			
			@Override
			public void run() {
				startAction(tile1);
				if(tile1 != tile2){
					startAction(tile2);
				}
			}
		}, new Runnable() {
			
			@Override
			public void run() {
				stopAction(tile1);
				if(tile1 != tile2){
					stopAction(tile2);
				}
			}
		});
	}
	
	private void startAction(final TileWidget tile){
		tile.setHintBlinking(true);
		tile.animateTile();
	}
	
	private void stopAction(final TileWidget tile){
		tile.setHintBlinking(false);
		tile.stopAnimateTile();
	}

	public void createTimer(final Runnable startAction, final Runnable stopAction) {
		if(_timer != null){
			_timer.cancel();
		}
		_timer = new AnimationTimer(startAction, stopAction);
		_timer.run();
	}

	public void animateSecret() {
		createTimer(new Runnable() {
			
			@Override
			public void run() {
				_secretWidget.setHintBlinking(true);
				_secretWidget.animateSecret();
			}
		}, new Runnable() {
			
			@Override
			public void run() {
				_secretWidget.setHintBlinking(false);
				_secretWidget.stopAnimateSecret();
			}
		});
	}

	public void stopShowHint() {
		if(_timer == null){
			return;
		}
		_timer.stop();
		_timer.cancel();
		_timer = null;
	}

	public boolean isShowingHint() {
		return _timer != null;
	}

	public void showRules() {
		_rules.showRules();		
	}

	public void showMenu() {
		_menu.showMenu();
	}
	
	private void createScreen(){
		final DrawElement caveElement = Resources.get(ImageFactory.class).getDrawElement("cave");
		final SpriteImage<DrawElement> cave = new SpriteImage<DrawElement>(caveElement, caveElement.getWidth(), caveElement.getHeight());
		_cave = new Sprite(cave, 0, 0, cave.width(), cave.height());
		
		final DrawElement secretElement = Resources.get(ImageFactory.class).getDrawElement("bonus-secret");
		final SpriteImage<DrawElement> secretImage = new SpriteImage<DrawElement>(secretElement, secretElement.getWidth(), secretElement.getHeight() / 2);
		_scroll = new Sprite<DrawElement>(secretImage, 0, 491, secretElement.getWidth(), secretElement.getHeight());
	}
	
	public void showGame() {
		if(_cave != null){
			_topLayerSprites.removeSprite(_cave);
			_topLayerSprites.removeSprite(_scroll);
		}
	}
	
	public void hideGame() {
		_topLayerSprites.addSprite(_cave);
		_topLayerSprites.addSprite(_scroll);
	}

	
}

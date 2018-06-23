package seabattle.client;

import games.client.BaseView;
import games.client.GameController;
import games.client.sprites.ImageEngine;
import games.client.sprites.Rectangle;
import games.client.sprites.Sprite;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.shared.Resources;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import seabattle.shared.SeaBattleMove;
import seabattle.shared.SeaBattleProtocol;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public class SeaBattleView extends BaseView {

	private static final int GAME_WIDTH = 600;
	private static final int GAME_HEIGHT = 566;
	
	private SeaBattleController _controller;
	private Clock _playerClock;
	private Clock _opponentClock;
	private NameContainer _playerContainer;
	private NameContainer _opponentContainer;
	private ShipChoicePanel _shipFrame;
	private int _shipsReady;
	private CellsContainerGroup _fieldMine;
	private CellsContainerGroup _fieldOpp;
	public boolean _gameStarted;
	public boolean _isPlayerMove;
	public int _moveNumber;
	public byte _playerSide;
	private Vector<ShipSprite> _shipWidgets;
	private boolean _movingShip;
	private ShipSprite _shipToMove;

	@Override
	public void init(final GameController controller) {
			_controller = (SeaBattleController) controller;
			super.init();
	}

	@Override
	protected Map<String, String> getImageNames() {
		final Map<String, String> imageNames = new HashMap<String, String>();
		imageNames.put("bg1", "images/seabattle/bg-01.gif");
		imageNames.put("bg2", "images/seabattle/bg-02.gif");
		imageNames.put("grid", "images/seabattle/grid-01.gif");
		
		imageNames.put("nameField", "images/seabattle/field-01.gif");
		imageNames.put("marker", "images/seabattle/pimpa-01.gif");
		imageNames.put("shadow", "images/seabattle/shadow-01.gif");
		
		imageNames.put("clock", "images/seabattle/time-01.gif");
		imageNames.put("clockShadow", "images/seabattle/shadow-02.gif");
		imageNames.put("sec0", "images/seabattle/time-arrow-01.gif");
		imageNames.put("sec5", "images/seabattle/time-arrow-02.gif");
		imageNames.put("sec10", "images/seabattle/time-arrow-03.gif");
		
		imageNames.put("closeButton", "images/seabattle/but-close-01.gif");
		imageNames.put("grid2", "images/seabattle/grid-02.gif");
		imageNames.put("mouse", "images/seabattle/mouse-01.gif");
		imageNames.put("readyButton", "images/seabattle/but-ready-01.gif");
		imageNames.put("autoButton", "images/seabattle/but-avto-01.gif");
		
		imageNames.put("ship1", "images/seabattle/ship-04.gif");
		imageNames.put("ship2", "images/seabattle/ship-03.gif");
		imageNames.put("ship3", "images/seabattle/ship-02.gif");
		imageNames.put("ship4", "images/seabattle/ship-01.gif");
		imageNames.put("wide", "images/seabattle/wide-01.gif");
		imageNames.put("fieldMarker", "images/seabattle/marker-01.gif");

		imageNames.put("crash", "images/seabattle/crash-02.gif");
		imageNames.put("explose", "images/seabattle/crash-01.gif");
		
		return imageNames;
	}
	
	@Override
	protected ScheduledCommand getStartGameCommand() {
		return new ScheduledCommand() {

			@Override
			public void execute() {
				initView();
				_controller.init();
				loadNewGame("name1", "name2");
				_controller.startGame();
			}
		};
	}

	protected void initView() {
		addBackground();
		
		addPlayersFields();
		addClocks();
		
		_shipWidgets = new Vector<ShipSprite>();

		_fieldMine = new CellsContainerGroup(this, 11, 59);
		_spriteManager.addListener(_fieldMine);
		_fieldMine.init();
		_fieldMine.setActive(false);
		_fieldMine.setHidden(false);
		
		_fieldOpp = new CellsContainerGroup(this, 307, 59);
		_spriteManager.addListener(_fieldOpp);
//		_fieldOpp.init();
		_fieldOpp.setActive(false);
		_fieldOpp.setHidden(true);
		
		showChooseFrame();
//		_fieldMine.resize(11, 59, 285, 285);
//		_fieldOpp.resize(307, 59, 285, 285);
		
	}

	private void addClocks() {
		_playerClock = new Clock(11, 350);
		_opponentClock = new Clock(596 - Resources.get(ImageFactory.class).getDrawElement("clock").getWidth(), 350);
		_gameSprites.addSprite(_playerClock);
		_gameSprites.addSprite(_opponentClock);
	}

	private void addPlayersFields() {
		_playerContainer = new NameContainer(this, 105, 355, 183, 58);
		_spriteManager.addListener(_playerContainer);
		_playerContainer.init();
		
		_opponentContainer = new NameContainer(this, 320, 355, 183, 58);
		_spriteManager.addListener(_opponentContainer);
		_opponentContainer.init();
		
	}

	

	private void addBackground() {
		final DrawElement bg1Element = Resources.get(ImageFactory.class).getDrawElement("bg1");
		final SpriteImage bg1Image = new SpriteImage(bg1Element, bg1Element.getWidth(), bg1Element.getHeight());
		final Sprite<DrawElement> bg1 = new Sprite<DrawElement>(0, 0, GAME_WIDTH, GAME_HEIGHT);
		
		final int height = bg1Element.getHeight();
		final int width = bg1Element.getWidth();
		bg1.setImageEngine(new ImageEngine<DrawElement>() {
			@Override
			public boolean updateTick(final long timestamp) {
				return false;
			}

			@Override
			public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image, final int x, final int y, final Rectangle dirtyRegion) {
				for (int i = 0; i < GAME_HEIGHT; i += height) {
					for (int j = 0; j < GAME_WIDTH; j += width) {
						bg1Image.draw(graphics, x + j, y + i, dirtyRegion);
					}
				}
			}
		});
		_gameSprites.addSprite(bg1);
		
		final DrawElement bg2Element = Resources.get(ImageFactory.class).getDrawElement("bg2");
		final SpriteImage<DrawElement> bg2Image = new SpriteImage<DrawElement>(bg2Element, bg2Element.getWidth(), bg2Element.getHeight());
		_gameSprites.addSprite(new Sprite<DrawElement>(bg2Image, 0, 0, bg2Element.getWidth(), bg2Element.getHeight()));
		
	}

	@Override
	protected int getGameHeight() {
		return GAME_HEIGHT;
	}

	@Override
	public int getGameWidth() {
		return GAME_WIDTH;
	}

	private void showChooseFrame() {
//        if(_shipFrame != null)
//        	removeWidget(_shipFrame);
        
		_shipFrame = new ShipChoicePanel(this);
		_spriteManager.addListener(_shipFrame);
		_shipFrame.init();
	
	}

	public void increaseShipsReady() {
		_shipsReady++;
	}

	public CellsContainerGroup getContainer(){
		return _fieldMine;
	}

	public void sendAnswerMove(final SeaBattleMove seaBattleMove, final int moveNumber) {
		_controller.sendAnswerMove(seaBattleMove, moveNumber);
	}
	
	private void deleteShipWidgets() {
//		TODO
//		for(int i = 0; i < _shipWidgets.size(); i++){
//			removeWidget((Widget) _shipWidgets.elementAt(i));
//		}
		_shipWidgets.removeAllElements();
	}

	private void addShipWidgets() {
		final ShipSprite ship4 = new ShipSprite(31 + _shipFrame.getX(), 30 + _shipFrame.getY(), 4, null, true, CellsContainerGroup.getShipImage(4, false, true), this);
		_gameSprites.addSprite(ship4);
		_shipWidgets.addElement(ship4);
		
		for(int i = 0; i < 2; i++){
			final ShipSprite ship3 = new ShipSprite(30 + i * 85 + _shipFrame.getX(), 87 + _shipFrame.getY(), 3, null, true, CellsContainerGroup.getShipImage(3, false, true), this);
			_gameSprites.addSprite(ship3);
			_shipWidgets.add(ship3);
		}
		
		for(int i = 0; i < 3; i++){
			int x;
			int y;
			if(i < 2) {
				x = 144 + i * 57 + _shipFrame.getX();
				y = 30 + _shipFrame.getY();
			} else {
				x = 200 + _shipFrame.getX();
				y = 88 + _shipFrame.getY();
			}
			final ShipSprite ship2 = new ShipSprite(x, y, 2, null, true, CellsContainerGroup.getShipImage(2, false, true), this);
			_gameSprites.addSprite(ship2);
			_shipWidgets.add(ship2);
		}
		
		for(int i = 0; i < 4; i++){
			final ShipSprite ship1 = new ShipSprite(30 + i * 56 + _shipFrame.getX(), 142 + _shipFrame.getY(), 1, null, true, CellsContainerGroup.getShipImage(1, false, true), this);
			_gameSprites.addSprite(ship1);
			_shipWidgets.add(ship1);
		}
	}
	
//	@Override
//	public boolean onMouseDown(int mousex, int mousey, int modifiers) {
//		return super.onMouseDown(mousex, mousey, modifiers);
//	}
	
//	public void showWinnerFrame(int side){
//		int frameWidth = 178;
//        int frameHeight = 142;
//        
//		_winnerFrame = new DraggedWidgetContainer();
//	    _winnerFrame.resize(width - frameWidth - 5, frameHeight >> 1, frameWidth, frameHeight);
//		addWidget(_winnerFrame);
//	    
//		Widget widget = NameContainer.createField(NameContainer._field, frameWidth, frameHeight);
//		_winnerFrame.addWidget(widget);
//		
//		NeoLabel label = new NeoLabel();
//		label.setOpaque(false);
//		label.setText(Messages.getString("SeaBattleView.Winner")); //$NON-NLS-1$
//		label.setFont(_plainFont);
//		label.pack();
//		label.setXY((_winnerFrame.width - label.width - 6) >> 1 , 34);
//		_winnerFrame.addWidget(label);
//		
//		label = new NeoLabel();
//		label.setOpaque(false);
//		label.setText(_playerSide == side ? _playerContainer.getText() : _opponentContainer.getText());
//		label.setFont(_font);
//		label.pack();
//		label.setXY((_winnerFrame.width - label.width - 6) >> 1, 55);
//		_winnerFrame.addWidget(label);
//		
//		
//		SimpleButton button = new SimpleButton(UIUtils.cutStripeVertically(_buttonImage, 3));
//		button.setXY((_winnerFrame.width - button.width - 6) >> 1, 89);
//		button.setAction(new Runnable(){
//			public void run() {	
//				removeWidget(_winnerFrame);
//				_winnerFrame = null;
//			}
//		});
//		_winnerFrame.addWidget(button);
//	}
	
	protected void setCloseAction() {
//		closeGame();
	}

	public void loadNewGame(final String string, final String string2) {
//	TODO	"name1", "name2"
		final String name1 = "name1";
		final String name2 = "bot";
		
		
//		_gameStarted = true;
		
	//TODO	_shipFrame.setButtonsEnabled();
//		_fieldMine.setDragged(false);
//		_fieldOpp.setDragged(false);
		
		
//		if(_isWatcher){
//			if(_winnerFrame != null){
//				removeWidget(_winnerFrame);
//				_winnerFrame = null;
//			}
//			_fieldMine.setFieldMatrix(new int[SeaBattleProtocol.SIZE][SeaBattleProtocol.SIZE]);
//			_fieldOpp.setFieldMatrix(new int[SeaBattleProtocol.SIZE][SeaBattleProtocol.SIZE]);
//		}
		
		if(_playerContainer.getText().compareTo(name1) == 0) {
			_opponentContainer.setText(name2);
		} else {
			_opponentContainer.setText(name1);
		}
		
		final NameContainer container = _playerSide == SeaBattleProtocol.WHITE ? _opponentContainer : _playerContainer;
    	container.makeShadow();
    	final Clock clock = _playerSide == SeaBattleProtocol.WHITE ? _opponentClock : _playerClock;
    	clock.setShadowActive(false);
    	final Clock clock2 = _playerSide == SeaBattleProtocol.WHITE ? _playerClock : _opponentClock;
    	clock2.start();
		
    	
	}

	public void sendFieldMatrix() {
		_controller.sendFieldMatrix(_fieldMine.getFieldMatrix(), _playerSide);
		
	}

	public void setFields(final int[][] field1, final int[][] field2) {
		_fieldMine.setFieldMatrix(field1);
		_fieldOpp.setFieldMatrix(field2);
	}

	public void setShipsReady(final int ships) {
		_shipsReady = ships;
	}
	
	public int getShipsReady(){
		return _shipsReady;
	}
	
//	@Override
//	protected void onMouseMove(int x, int y) {
////		super.onMouseMove(x, y);
//		if (hasShip()) {
////			_canUseMachete = _jungle.canUseMachete(x - LEFT_INDENT, y - TOP_INDENT);
//			_shipToMove.moveTo(x - _shipToMove.getWidth() / 2, y - _shipToMove.getHeight() / 2);
//		}
//	}
	
	public boolean hasShip() {
		return _movingShip;
	}

	public void setMovingShip(final ShipSprite ship) {
//		_movingShip = true;
//		_shipToMove = ship;
	}

	public ShipSprite getShipToMove() {
		return _shipToMove;
	}
	
//	private void activateMachete() {
//		setMachete(true);
//		_movingMachete.moveTo(_machete.getX(), _machete.getY());
//	}
	
}

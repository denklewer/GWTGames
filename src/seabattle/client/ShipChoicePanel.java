package seabattle.client;

import games.client.sprites.ImageEngine;
import games.client.sprites.Rectangle;
import games.client.sprites.Sprite;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteGroup;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.client.util.SimpleButton;
import games.client.util.UIUtils;
import games.shared.Point;
import games.shared.Resources;

import java.util.Vector;

import seabattle.shared.FactoryField;
import seabattle.shared.SeaBattleProtocol;

import com.google.gwt.canvas.dom.client.CssColor;

public class ShipChoicePanel extends SpriteGroup<DrawElement> {

	private static final int GAP = 10;
	final String PLAIN_12PX_SANS_SERIF = "bold 12px sans-serif";//TODO bold->plain
	final CssColor BLACK_COLOR = CssColor.make(0, 0, 0);
	final CssColor WHITE_COLOR = CssColor.make("rgba(" + 255 + ", " + 255 + "," + 255 + ", " + 0.0d + ")");
	final CssColor DARK_GRAY_COLOR = CssColor.make(64, 64, 64);
	
	private static SpriteImage GRID;
	private static SpriteImage MOUSE;
	private static SpriteImage<DrawElement>[] READY_BUTTON;
	private static SpriteImage<DrawElement>[] AUTO_BUTTON;
	private SeaBattleView _parent;
	private int _height;
	private int _width;
	private int _y;
	private int _x;
	private SimpleButton _readyButton;
	private SimpleButton _autoButton;
	private Vector<ShipSprite> _shipSprites = new Vector<ShipSprite>();
	
	private Point _mouseDown;
	private Point _draggedPosition;
	private Point _startPosition;
	private boolean _isDragged = true;
	private ShipSprite _currentMovingShip;
	private Point _currentMovingInitPoint;

	static {
		final DrawElement grid = Resources.get(ImageFactory.class).getDrawElement("grid2");
		GRID = new SpriteImage(grid, grid.getWidth(), grid.getHeight());
		final DrawElement mouse = Resources.get(ImageFactory.class).getDrawElement("mouse");
		MOUSE = new SpriteImage(mouse, mouse.getWidth(), mouse.getHeight());
		final DrawElement readyButton = Resources.get(ImageFactory.class).getDrawElement("readyButton");
		READY_BUTTON = UIUtils.cutSpriteVertically(readyButton, readyButton.getWidth(), readyButton.getHeight(), 4); 
		final DrawElement autoButton = Resources.get(ImageFactory.class).getDrawElement("autoButton");
		AUTO_BUTTON = UIUtils.cutSpriteVertically(autoButton, autoButton.getWidth(), autoButton.getHeight(), 4); 
		
	}
	
	public ShipChoicePanel(final SeaBattleView parent) {
		final int frameWidth = 294;
        final int frameHeight = 294;
		_parent = parent;
		_x = 600 - frameWidth;
		_y = 58;
		_width = frameWidth;
		_height = frameHeight;
	}
	
	public void init(){
		final Sprite field = NameContainer.createFieldImage(NameContainer.FIELD_DRAWELEMENT, _x, _y, _width, _height);
		addSprite(field);
		
		final Sprite background = new Sprite(_x, _y, _width, _height);
		background.setImageEngine(new ImageEngine<DrawElement>() {

			@Override
			public boolean updateTick(final long timestamp) {
				return false;
			}

			@Override
			public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image, final int x, final int y, final Rectangle dirtyRegion) {
				GRID.draw(graphics, x+30, y+30, dirtyRegion);
				MOUSE.draw(graphics, x+38, y+_height - 41 - MOUSE.height(), dirtyRegion);
				
//				DrawElement lineElement = UIUtils.createVerticalLine(63, _height - 59, 72, _height - 59, 9, 1, DARK_GRAY_COLOR);
//				SpriteImage lineImage = new SpriteImage(lineElement, lineElement.getWidth(), lineElement.getHeight());
//				lineImage.draw(graphics, x+63, y+_height - 59, dirtyRegion);
				
				
				//g.setColor(Color.DARK_GRAY);
				//g.drawLine(63, frameHeight - 59, 72, frameHeight - 59);
				final String rotateString = "You can rotate ship";
				final String rightClickString = "for right click";
				
				final DrawElement rotateStringElement = UIUtils.createLeftTextElement(rotateString, _width - MOUSE.width(), 15, DARK_GRAY_COLOR, WHITE_COLOR, 0d, PLAIN_12PX_SANS_SERIF, 14);
				final SpriteImage rotateStringImage = new SpriteImage<DrawElement>(rotateStringElement, rotateStringElement.getWidth(), rotateStringElement.getHeight());
				rotateStringImage.draw(graphics, x+65, y+_height - 70, dirtyRegion);

				final DrawElement rightClickStringElement = UIUtils.createLeftTextElement(rightClickString, _width - MOUSE.width(), 15, DARK_GRAY_COLOR, WHITE_COLOR, 0d, PLAIN_12PX_SANS_SERIF, 14);
				final SpriteImage rightClickStringImage = new SpriteImage<DrawElement>(rightClickStringElement, rightClickStringElement.getWidth(), rightClickStringElement.getHeight());
				rightClickStringImage.draw(graphics, x+65, y+_height - 56, dirtyRegion);
			}
			
		});
		addSprite(background);
		
		addShipWidgets();
		
		_readyButton = new SimpleButton(_x+33, _y+183, READY_BUTTON[0].width(), READY_BUTTON[0].height(), READY_BUTTON, new Runnable() {
			
			@Override
			public void run() {
				if(_parent.getShipsReady() == 10){
					hideSprites();
					_parent.sendFieldMatrix();
					_parent.setShipsReady(0);
				}
			}
		});
		addSprite(_readyButton);
		_readyButton.setEnabled();
		
		_autoButton = new SimpleButton(_x+110, _y+183, AUTO_BUTTON[0].width(), AUTO_BUTTON[0].height(), AUTO_BUTTON, new Runnable() {
			
			@Override
			public void run() {
				deleteShipWidgets();
				_parent.setFields(FactoryField.createRandomField(), new int[SeaBattleProtocol.SIZE][SeaBattleProtocol.SIZE]);
//				_fieldMine.setFieldMatrix(FactoryField.createRandomField());
//				_fieldOpp.setFieldMatrix(new int[SeaBattleProtocol.SIZE][SeaBattleProtocol.SIZE]);
				_parent.setShipsReady(10);
				
//				NeoApplication.instance.getAudio().play("seabattleAutoset");
			}
		});
		addSprite(_autoButton);
		_autoButton.setEnabled();
	}
	
	private void deleteShipWidgets() {
		
//		for(int i = 0; i < _shipWidgets.size(); i++){
//			removeWidget((Widget) _shipWidgets.elementAt(i));
//		}
		_shipSprites.removeAllElements();
	}

	private void addShipWidgets() {
		final ShipSprite ship4 = new ShipSprite(31 + _x, 30 + _y, 4, null, true, CellsContainerGroup.getShipImage(4, false, true), _parent);
		addSprite(ship4);
		ship4.setDragged(true);
		_shipSprites.addElement(ship4);
		
		for(int i = 0; i < 2; i++){
			final ShipSprite ship3 = new ShipSprite(30 + i * 85 + _x, 87 + _y, 3, null, true, CellsContainerGroup.getShipImage(3, false, true), _parent);
			addSprite(ship3);
			ship3.setDragged(true);
			_shipSprites.add(ship3);
		}
		
		for(int i = 0; i < 3; i++){
			int x;
			int y;
			if(i < 2){
				x = 144 + i * 57 + _x;
				y = 30 + _y;
			} else {
				x = 200 + _x;
				y = 88 + _y;
			}
			final ShipSprite ship2 = new ShipSprite(x, y, 2, null, true, CellsContainerGroup.getShipImage(2, false, true), _parent);
			addSprite(ship2);
			ship2.setDragged(true);
			_shipSprites.add(ship2);
		}
		
		for(int i = 0; i < 4; i++){
			final ShipSprite ship1 = new ShipSprite(30 + i * 56 + _x, 142 + _y, 1, null, true, CellsContainerGroup.getShipImage(1, false, true), _parent);
			addSprite(ship1);
			ship1.setDragged(true);
			_shipSprites.add(ship1);
		}
	}
	
	public int getX(){
		return _x;
	}

	public int getY(){
		return _y;
	}
	
	public void setButtonsEnabled(){
		_autoButton.setEnabled();
		_readyButton.setEnabled();
	}
	
	@Override
	public boolean onMouseDown(final int mousex, final int mousey, final int modifiers) {

		for (final ShipSprite ship : _shipSprites) {
			if (ship.contains(mousex, mousey)){
				_currentMovingShip = ship;
				_currentMovingInitPoint = new Point(ship.getX(), ship.getY());
				break;
			}
		}
		if(_currentMovingShip != null) {
			_startPosition = new Point(_currentMovingShip.getX(), _currentMovingShip.getY());
		}
		_mouseDown = new Point(mousex, mousey);
		return true;
	}

	@Override
	public boolean onMouseMove(final int mousex, final int mousey) {
		if(_currentMovingShip == null) {
			return true;
		}
		_currentMovingShip.moveTo(_currentMovingInitPoint.x + mousex - _mouseDown.x, _currentMovingInitPoint.y + mousey - _mouseDown.y);
		toTop(_currentMovingShip);
		return true;
	}
	
	@Override
	public boolean onMouseUp(final int x, final int y, final int modifiers) {
		if (_currentMovingShip == null){
			return true;
		}
		final CellsContainerGroup container = _parent.getContainer();
		final int dx = _currentMovingShip.getX() - container.getCellsContainerX() + GAP;
		final int dy = _currentMovingShip.getY() - container.getCellsContainerY() + GAP;
		
		final Position position = new Position(dy / CellsContainerGroup.CELL_SIZE, dx / CellsContainerGroup.CELL_SIZE);
		System.out.println("ShipChoicePanel mouseUp position IJ: "+position.getX()+", "+position.getY());
		_currentMovingShip.setPosition(position);
		
		if (container.contains(x, y) && !container.haveShipsAround(_currentMovingShip) && container.hasPlace(_currentMovingShip)){
			_parent.increaseShipsReady();
			removeSprite(_currentMovingShip);
			container.addSprite(_currentMovingShip);
			final int xShip = container.getCellsContainerX() + _currentMovingShip.getPosition().getY() * CellsContainerGroup.CELL_SIZE + CellsContainerGroup.BORDER_WIDTH;
			final int yShip = container.getCellsContainerY() + _currentMovingShip.getPosition().getX() * CellsContainerGroup.CELL_SIZE + CellsContainerGroup.BORDER_WIDTH;
			_currentMovingShip.moveTo(xShip, yShip);
			System.out.println("ShipChoicePanel mouseUp position XY: "+xShip+", " + yShip);
			container.placeShip(_currentMovingShip);
		} else {
			_currentMovingShip.moveTo(_startPosition.x, _startPosition.y);
			_currentMovingShip.setPosition(null);
		}
		_currentMovingShip = null;
		return true;
	}
	
	
}

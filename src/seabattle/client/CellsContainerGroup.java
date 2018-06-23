package seabattle.client;

import games.client.sprites.ImageEngine;
import games.client.sprites.Rectangle;
import games.client.sprites.Sprite;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteGroup;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.client.util.UIUtils;
import games.shared.Resources;

import java.util.List;
import java.util.Vector;

import seabattle.shared.SeaBattleCondition;
import seabattle.shared.SeaBattleMove;
import seabattle.shared.SeaBattleProtocol;

import com.google.gwt.canvas.dom.client.CssColor;

public class CellsContainerGroup extends SpriteGroup implements SeaBattleProtocol{

	public static final int CELL_SIZE = 28;
	public static final int BORDER_WIDTH = 2;
	final CssColor GRID_COLOR = CssColor.make("rgba(" + 6 + ", " + 42 + "," + 84 + ", " + 0.4d + ")");
	
//	private static Color _markerColor = new Color(0, 0xDF, 0xFF, 0x88);
	final CssColor MARKER_COLOR = CssColor.make(223, 255, 136);
	private int[][] _fieldMatrix;
	private boolean _isActive = false; 
	private boolean _isHidden;
	
	public static SpriteImage[] _ship1;
	public static SpriteImage[] _ship2;
	public static SpriteImage[] _ship3;
	public static SpriteImage[] _ship4;
	public static SpriteImage[] _ship1t;
	public static SpriteImage[] _ship2t;
	public static SpriteImage[] _ship3t;
	public static SpriteImage[] _ship4t;
	private static SpriteImage _wide;
	private static SpriteImage _marker;
	
	private List<ShipSprite> _ships;

	private Vector<BurningSprite> _burning;
    private Sprite _markerWidget;
	
	private SeaBattleView _view;

	private int _cellsContainerX;
	private int _cellsContainerY;

	static{
		final DrawElement ship1 = UIUtils.changeColorAndTransparency(Resources.get(ImageFactory.class).getDrawElement("ship1"), 255, 0, 255, 0, 0, 0, 0.66d);
		_ship1 = UIUtils.cutSpriteVertically(ship1, ship1.getWidth(), ship1.getHeight(), 2);
		final DrawElement ship2 = UIUtils.changeColorAndTransparency(Resources.get(ImageFactory.class).getDrawElement("ship2"), 255, 0, 255, 0, 0, 0, 0.66d);
		_ship2 = UIUtils.cutSpriteVertically(ship2, ship2.getWidth(), ship2.getHeight(), 2);
		final DrawElement ship3 = UIUtils.changeColorAndTransparency(Resources.get(ImageFactory.class).getDrawElement("ship3"), 255, 0, 255, 0, 0, 0, 0.66d);
		_ship3 = UIUtils.cutSpriteVertically(ship3, ship3.getWidth(), ship3.getHeight(), 2);
		final DrawElement ship4 = UIUtils.changeColorAndTransparency(Resources.get(ImageFactory.class).getDrawElement("ship4"), 255, 0, 255, 0, 0, 0, 0.66d);
		_ship4 = UIUtils.cutSpriteVertically(ship4, ship4.getWidth(), ship4.getHeight(), 2);

		final DrawElement ship1t = UIUtils.rotateDrawElement(ship1, ship1.getWidth(), ship1.getHeight(), UIUtils.ROTATE_90);
		_ship1t = UIUtils.cutStripeHorizontally(ship1t, ship1t.getWidth(), ship1t.getHeight(), 2);
		final DrawElement ship2t = UIUtils.rotateDrawElement(ship2, ship2.getWidth(), ship2.getHeight(), UIUtils.ROTATE_90);
		_ship2t = UIUtils.cutStripeHorizontally(ship2t, ship2t.getWidth(), ship2t.getHeight(), 2);
		final DrawElement ship3t = UIUtils.rotateDrawElement(ship3, ship3.getWidth(), ship3.getHeight(), UIUtils.ROTATE_90);
		_ship3t = UIUtils.cutStripeHorizontally(ship3t, ship3t.getWidth(), ship3t.getHeight(), 2);
		final DrawElement ship4t = UIUtils.rotateDrawElement(ship4, ship4.getWidth(), ship4.getHeight(), UIUtils.ROTATE_90);
		_ship4t = UIUtils.cutStripeHorizontally(ship4t, ship4t.getWidth(), ship4t.getHeight(), 2);

		final DrawElement wide = Resources.get(ImageFactory.class).getDrawElement("wide");
		_wide = new SpriteImage(wide, wide.getWidth(), wide.getHeight());
		
		final DrawElement marker = UIUtils.changeColorAndTransparency(Resources.get(ImageFactory.class).getDrawElement("fieldMarker"), 255, 0, 255, 0, 223, 255, 0.66d);
		_marker = new SpriteImage(marker, marker.getWidth(), marker.getHeight());
	}

	public CellsContainerGroup(final SeaBattleView view, final int x, final int y){
		super();
		_view = view;
		_cellsContainerX = x;
		_cellsContainerY = y;
	}

	public void init() {
		_burning = new Vector<BurningSprite>();
		_fieldMatrix = new int [SIZE][SIZE];
		
		final DrawElement gridElement = Resources.get(ImageFactory.class).getDrawElement("grid");
		final DrawElement squareElement = UIUtils.createRect(gridElement.getWidth(), gridElement.getHeight(), GRID_COLOR);
		addGrid(_cellsContainerX, _cellsContainerY, gridElement, squareElement);
	}
	
	private void addGrid(final int x, final int y, final DrawElement gridElement, final DrawElement squareElement) {
		final SpriteImage gridImage = new SpriteImage(gridElement, gridElement.getWidth(), gridElement.getHeight());
		final SpriteImage squareImage = new SpriteImage(squareElement, squareElement.getWidth(), squareElement.getHeight());
		
		final Sprite<DrawElement> gridSprite = new Sprite<DrawElement>(x, y, gridImage.width(), gridImage.height());
		gridSprite.setImageEngine(new ImageEngine<DrawElement>() {
			@Override
			public boolean updateTick(final long timestamp) {
				return false;
			}

			@Override
			public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image, final int x, final int y, final Rectangle dirtyRegion) {
				squareImage.draw(graphics, x, y, dirtyRegion);
				gridImage.draw(graphics, x, y, dirtyRegion);
			}
		});
		addSprite(gridSprite);
	}
	
	public int[][] getFieldMatrix() {
		return _fieldMatrix;
	}
	
	public void setFieldMatrix(final int[][] fieldMatrix) {
		clearAll();
		_fieldMatrix = fieldMatrix;
		resetShips();
	}

	public void resetShips() {
		final int[][] matrix = new int[SIZE][SIZE];
		
		for(int i = 0; i < SIZE; i++){
			for(int j = 0; j < SIZE; j++){
				Sprite sprite = null;
				
				final int x = j * CELL_SIZE + 2 + _cellsContainerX;
				final int y = i * CELL_SIZE + 2 + _cellsContainerY;
				
				if(!notShip(i, j) && matrix[i][j] == 0 && (!_isHidden || _fieldMatrix[i][j] == KILLED)) {
					sprite = createShip(i, j, matrix, x, y);
				}
				
				if(_fieldMatrix[i][j] == WIDE){
					sprite = new Sprite(_wide, x, y, _wide.width(), _wide.height());
					addSprite(sprite);
				}
				
				if(_fieldMatrix[i][j] == SHOT){
					final BurningSprite bSprite = new BurningSprite(x, y, this);
					addSprite(bSprite);
					_burning.add(bSprite);
				}
			}
		}
	}
	
	private ShipSprite createShip(final int i, final int j, final int[][] matrix, final int x, final int y) {
		matrix[i][j] = 1;
		final boolean killed = _fieldMatrix[i][j] == KILLED;
		boolean horizontal = true;
		
		int count = findShip(matrix, i, j, 0, 1);
		if (count == 1){
			count = findShip(matrix, i, j, 1, 0);
			if(count > 1) {
				horizontal = false;
			}
		}

		final SpriteImage image = getShipImage(count, killed, horizontal);

		final ShipSprite sprite = new ShipSprite(x + _cellsContainerX, y + _cellsContainerY, count, new Position(i, j), horizontal, image, _view);
		return sprite;
	}

	private int findShip(final int[][] matrix, int i, int j, final int k, final int l) {
		int count = 1;
		
		while(!notShip(i + k, j + l)){
			count++;
			matrix[i + k][j + l] = 1;
			i += k;
			j += l;
		}
		return count;
	}

	public void makeMove(final SeaBattleMove move, final int result) {
		final ShipSprite ship = getShip(move._x, move._y);
		_fieldMatrix[move._x][move._y] = result;
		
		final int x = move._y * CELL_SIZE + 2 + _cellsContainerX;
		final int y = move._x * CELL_SIZE + 2 + _cellsContainerY;
		switch(result){
		case WIDE:
			Sprite sprite = new Sprite(_wide, x, y, _wide.width(), _wide.height());
			addSprite(sprite);
			break;
		case SHOT:
		case KILLED:
		case GAME_OVER:
			sprite = new BurningSprite(x, y, this);
			addSprite(sprite);
			_burning.add((BurningSprite) sprite);
			
			if(result != SHOT){
				((BurningSprite) sprite).setLast(true);

				if(ship == null){
					final ShipSprite shipSprite = findKilledShip(move);
//					int index = _widgets.indexOf(_burning.elementAt(0));
					addSprite(shipSprite);
//					TODO insertWidget(shipSprite, index);
					surroundWide(shipSprite);
				}
				else{
					SeaBattleCondition.makeKilledNeighbours(move._x, move._y, _fieldMatrix);
					final SpriteImage killedImage = getShipImage(ship.getLength(), true, !ship.isVertical());
//					if(ship.isVertical())
//						killedImage = UIUtils.rotate(killedImage, UIUtils.ROTATE_270);
					ship.setSpriteImage(killedImage);
					surroundWide(ship);
				}
			}
			break;
		}
	}

	private void surroundWide(final ShipSprite shipSprite) {
		int i = shipSprite.getPosition().getX();
		int j = shipSprite.getPosition().getY();
		while(isKilled(i, j)){
			for(int k = -1; k <= 1; k++){
				for(int l = -1; l <= 1; l++) {
					if(isEmpty(i + k, j + l)){
						final Sprite sprite = new Sprite(_wide, (j + l) * CELL_SIZE + 2 + _cellsContainerX, (i + k) * CELL_SIZE + 2 + _cellsContainerY, _wide.width(), _wide.height());
						addSprite(sprite);
						_fieldMatrix[i + k][j + l] = WIDE;
					}
				}
			}
			if(shipSprite.isHorizontal()) {
				j++;
			} else {
				i++;
			}
		}
	}


	private ShipSprite findKilledShip(final SeaBattleMove move) {
		ShipSprite sprite = null;
		int count = 0;
		int i = move._x;
		boolean horizontal = true;
		Position pos = null;
		
		while(isKilled(i, move._y)){
			count++;
			_fieldMatrix[i][move._y] = KILLED;
			i--;
		}
		int j = move._x + 1;
		while(isKilled(j, move._y)){
			count++;
			_fieldMatrix[j][move._y] = KILLED;
			j++;
		}
		if(count > 1){
			horizontal = false;
			pos = new Position(i + 1, move._y);
		}
		else{
			i = move._y - 1;
			while(isKilled(move._x, i)){
				count++;
				_fieldMatrix[move._x][i] = KILLED;
				i--;
			}
			j = move._y + 1;
			while(isKilled(move._x, j)){
				count++;
				_fieldMatrix[move._x][j] = KILLED;
				j++;
			}
			if(count > 1) {
				pos = new Position(move._x, i + 1);
			} else {
				pos = new Position(move._x, move._y);
			}
		}
		
		final SpriteImage killedImage = getShipImage(count, true, horizontal);
		sprite = new ShipSprite(pos.getY() * CELL_SIZE + 2 + _cellsContainerX, pos.getX() * CELL_SIZE + 2 + _cellsContainerY, count, pos, horizontal, killedImage, _view);
		sprite.setDragged(false);
		
		return sprite;
	}

	private boolean isKilled(final int i, final int j) {
		if(i >= 0 && j >= 0 && i < SIZE && j < SIZE && (_fieldMatrix[i][j] == KILLED || _fieldMatrix[i][j] == SHOT)) {
			return true;
		}
		return false; 
	}

	private boolean isEmpty(final int i, final int j) {
		if(j < SIZE && i < SIZE && i >= 0 && j >= 0 && _fieldMatrix[i][j] == EMPTY) {
			return true;
		}
		return false;
	}
	
	private boolean isAlive(final int i, final int j) {
		if(j < SIZE && i < SIZE && i >= 0 && j >= 0 && _fieldMatrix[i][j] == ALIVE) {
			return true;
		}
		return false;
	}
	
	public boolean notShip(final int i, final int j){
		if(j >= SIZE || i >= SIZE || i < 0 || j < 0 || _fieldMatrix[i][j] == EMPTY || _fieldMatrix[i][j] == WIDE) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onMouseDown(final int mousex, final int mousey, final int modifiers) {
//		if (_view.hasShip()){
//			ShipSprite ship = _view.getShipToMove();
//			int nx = mousex - this._x + ship.getX() - _mouseDown.x;
//			int ny = mousey + ship.getY() - this._y - _mouseDown.y;
//			
//			_position = new Position(ny / CellsContainer.CELL_SIZE, nx / CellsContainer.CELL_SIZE);
//			if(this.contains(nx, ny) && !container.haveShipsAround(this) && container.hasPlace(this)){
//				seabattleView.increaseShipsReady();
//				_parent.removeWidget(this);
//				container.addSprite(this);
//				
//				moveTo(_position.getY() * CellsContainer.CELL_SIZE + 2, _position.getX() * CellsContainer.CELL_SIZE + 2);
//				container.placeShip(this);
////				NeoApplication.instance.getAudio().play("seabattleSet");
//			}
//			else if(_startPosition != null){
//				moveTo(_startPosition.x, _startPosition.y);
//				_position = null;
//			}
//		}
//		
//		
//		if(!_view._gameStarted)
//			return super.onMouseDown(mousex, mousey, modifiers);
//		
//		if(!_view._isPlayerMove)
//			return false;
//		
//		int yy = mousex / CELL_SIZE;
//		int xx = mousey / CELL_SIZE;
//		
////		TODO
////		if(_fieldMatrix[xx][yy] == EMPTY || _fieldMatrix[xx][yy] == ALIVE)
////			_view.sendAnswerMove(new SeaBattleMove(xx, yy, _view._playerSide), _view._moveNumber, );
		return super.onMouseDown(mousex, mousey, modifiers);
	}
	
	@Override
	public boolean onMouseMove(final int mousex, final int mousey) {
		
		if(!_isActive){
			return false;
		}
		
		final int ny = mousex / CELL_SIZE;
		final int nx = mousey / CELL_SIZE;
	
		if(_markerWidget == null){
			_markerWidget = new Sprite(_marker, ny * CELL_SIZE + 3, nx * CELL_SIZE + 3, _marker.width(), _marker.height());
			addSprite(_markerWidget);
		} else {
			final int yy = _markerWidget.getY() / CELL_SIZE;
			final int xx = _markerWidget.getX() / CELL_SIZE;
			if(yy != ny || xx != nx) {
				_markerWidget.moveTo(ny * CELL_SIZE + 3, nx * CELL_SIZE + 3);
			}
		}

		toTop(_markerWidget);
		
		return super.onMouseMove(mousex, mousey);
	}
	
//	public void resize(int newx, int newy, int neww, int newh) {
//		super.resize(newx, newy, neww, newh);
//	}

	public void doLayout() {
	}

	private ShipSprite getShip(final int nx, final int ny) {
//		for(int i = 0; i < _widgets.size(); i++){
//			Sprite sprite = (Sprite) _widgets.elementAt(i);
//			if(sprite instanceof ShipSprite){
//				ShipSprite shipSprite = (ShipSprite)sprite;
//				if((shipSprite).isHorizontal()){
//					int dx = nx - shipSprite.getPosition().getX();
//					int dy = ny - shipSprite.getPosition().getY();
//					if(dy >= 0 && dy < shipSprite.getLength() && dx == 0)
//						return shipSprite;
//				}
//				else{
//					int dy = ny - shipSprite.getPosition().getY();
//					int dx = nx - shipSprite.getPosition().getX();
//					if(dx >= 0 && dx < shipSprite.getLength() && dy == 0)
//						return shipSprite;
//				}
//			}
//		}
		return null;
	}
	
	public static SpriteImage getShipImage(final int count, final boolean killed, final boolean isHorizontal) {
		SpriteImage image = null;
		final int index = killed ? 1 : 0;
		final int indexTurned = killed ? 0 : 1;

		switch(count){
		case 1:
			image = isHorizontal ? _ship1[index] : _ship1t[indexTurned];
			break;
		case 2:
			image = isHorizontal ? _ship2[index] : _ship2t[indexTurned];
			break;
		case 3:
			image = isHorizontal ? _ship3[index] : _ship3t[indexTurned];
			break;
		case 4:
			image = isHorizontal ? _ship4[index] : _ship4t[indexTurned];
			break;
		}
		return image;
	}

	public void setActive(final boolean isActive) {
		_isActive = isActive;
	}

	public void setHidden(final boolean isHidden) {
		_isHidden = isHidden;
	}
	
	public void removeBurningWidgets() {
		for(int i = 0; i < _burning.size(); i++){
			final Sprite sprite = _burning.elementAt(i);
			
			if(_fieldMatrix[sprite.getY() / CELL_SIZE][sprite.getX() / CELL_SIZE] == KILLED){
				removeSprite(sprite);
				_burning.removeElementAt(i--);
			}
		}
	}

	public void clearAll() {
//TODO		removeAllWidgets();
		_fieldMatrix = new int[SIZE][SIZE];
		_markerWidget = null;
		_burning.removeAllElements();
	}

	public void placeShip(final ShipSprite widget) {
		final int xx = widget.getPosition().getX();
		final int yy = widget.getPosition().getY();
		
		for(int i = 0; i < widget.getLength(); i++){
			if(widget.isHorizontal() && yy + i < SIZE) {
				_fieldMatrix[xx][yy + i] = ALIVE;
			} else if (xx + i < SIZE) {
				_fieldMatrix[xx + i][yy] = ALIVE;
			}
		}
	}

	public boolean haveShipsAround(final ShipSprite ship) {
		int xx = ship.getPosition().getX();
		int yy = ship.getPosition().getY();
		for(int i = 0; i < ship.getLength(); i++){
			if(ship.isVertical()) {
				xx = ship.getPosition().getX() + i;
			} else {
				yy = ship.getPosition().getY() + i;
			}
			for(int k = -1; k <= 1; k++){
				for(int l = -1; l <= 1; l++){
					if(isAlive(xx + k, yy + l)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void removeShip(final ShipSprite ship) {
		final int xx = ship.getPosition().getX();
		final int yy = ship.getPosition().getY();
		
		for(int i = 0; i < ship.getLength(); i++){
			if(ship.isHorizontal() && yy + i < SIZE) {
				_fieldMatrix[xx][yy + i] = EMPTY;
			} else if(xx + i < SIZE) {
				_fieldMatrix[xx + i][yy] = EMPTY;
			}
		}
	}

	public void setDragged(final boolean dragged) {
//		for(int i = 0; i < _widgets.size(); i++){
//			Sprite widget = (Sprite) _widgets.elementAt(i);
//			if(widget instanceof ShipSprite)
//				((ShipSprite) widget).setDragged(dragged);
//		}
	}

	public boolean hasPlace(final ShipSprite ship) {
		System.out.println("hasPlace y "+ship.getPosition().getY());
		System.out.println("hasPlace x "+ship.getPosition().getX());
		System.out.println("hasPlace length "+ship.getLength());
		System.out.println("hasPlace horiz "+ship.isHorizontal());
		if(ship.isHorizontal() && ship.getPosition().getY() + ship.getLength() <= SIZE) {
			return true;
		}
		
		if(ship.isVertical() && ship.getPosition().getX() + ship.getLength() <= SIZE) {
			return true;
		}
		return false;
	}

	public boolean contains(final int x, final int y) {
		final Rectangle rect = new Rectangle(_cellsContainerX, _cellsContainerY, SIZE * CELL_SIZE + 2 * BORDER_WIDTH, SIZE * CELL_SIZE + 2 * BORDER_WIDTH);
		return rect.contains(x, y);
	}

	public int getCellsContainerX() {
		return _cellsContainerX;
	}

	public int getCellsContainerY() {
		return _cellsContainerY;
	}

}

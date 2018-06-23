package seabattle.client;

import games.client.sprites.Sprite;
import games.client.sprites.SpriteImage;
import games.shared.Point;
import seabattle.shared.SeaBattleProtocol;

public class ShipSprite extends Sprite {
	
		private int _length;
		private Position _position;
		private boolean _horizontal;
		
		private Point _mouseDown;
		private Point _draggedPosition;
		private Point _startPosition;
		private boolean _isDragged = true;
		private SpriteImage _image;
		private SeaBattleView _parent;
		
		
		
		public ShipSprite(final int x, final int y, final int length, final Position position, final boolean horizontal, final SpriteImage image, final SeaBattleView seaBattleView) {
			super(image, x, y, image.width(), image.height());
			_length = length;
			_position = position;
			_horizontal = horizontal;
			_image = image;
			_parent = seaBattleView;
		}

		public boolean isHorizontal() {
			return _horizontal;
		}
		
		public void setHorizontal(final boolean horizontal) {
			_horizontal = horizontal;
		}
		
		public int getLength() {
			return _length;
		}
		
		public void setLength(final int length) {
			_length = length;
		}
		
		public Position getPosition() {
			return _position;
		}
		
		public void setPosition(final Position position) {
			_position = position;
		}
		
		public boolean isVertical() {
			return !_horizontal;
		}

		public void setDragged(final boolean isDragged) {
			_isDragged = isDragged;
		}
		
//		@Override
//		public boolean onMouseDown(int mousex, int mousey, int modifiers) {
//			System.out.println("shipSprite mouseDown _isdragged "+_isDragged);
//			if(!_isDragged)
//				return false;
//			
//			if(_mouseDown == null)
//				_startPosition = new Point(getX(), getY());
//			_mouseDown = new Point(mousex, mousey);
//			
//			return super.onMouseDown(mousex, mousey, modifiers);
//		}
//
//		@Override
//		public boolean onMouseMove(int mousex, int mousey) {
//			if(!_isDragged)
//				return false;
//			
//			Point pos = new Point(mousex + getX() - _mouseDown.x, mousey + getY() - _mouseDown.y);
//			
//			if (!pos.equals(_draggedPosition)) {
//	            _draggedPosition = pos;
//	            this.moveTo(pos.x, pos.y);
//	        }
////			return true;
//			return super.onMouseMove(mousex, mousey);
//		}
//		
//		@Override
//		public boolean onMouseEntered(int x, int y) {
//			markDirty();
//			return true;
//		}
//		
//		@Override
//		public boolean onMouseExit() {
//			markDirty();
//			return true;
//		}
//		
//		@Override
//		public boolean onMouseUp(int x, int y, int modifiers) {
//			if(!_isDragged)
//				return false;
////			
////			
////			if (!_movingMachete.isVisible()) {
////				if (_numOfMachete > 0) {
////					activateMachete();
////					--_numOfMachete;
////					recalculateNumOfMachete();
////				}
////			} else {
////				setMachete(false);
////				++_numOfMachete;
////				recalculateNumOfMachete();
////			}
//			activateShip();
//			return true;
//		}
//		
//		
//		private void activateShip() {
//			_parent.setMovingShip(this);
//		}

//
//		@Override
//		public boolean onMouseUp(int mousex, int mousey, int modifiers) {
//			if(!_isDragged)
//				return false;
//			
//				SeabattleView seabattleView = (SeabattleView)_parent;
//				CellsContainer container = seabattleView.getContainer();
//				
//				int nx = mousex - container.x + getX() - _mouseDown.x;
//				int ny = mousey + getY() - container.y - _mouseDown.y;
//				
//				if ((modifiers & InputEvent.BUTTON3_MASK) != 0) {
//					if(!_horizontal)
//						setImage(UIUtils.rotate(_image, UIUtils.ROTATE_270));
//					else
//						setImage(UIUtils.rotate(_image, UIUtils.ROTATE_90));
//					resize(getX(), getY(), _image.getWidth(), _image.getHeight());
//					_horizontal = !_horizontal;
//				}
//				
//				_position = new Position(ny / CellsContainer.CELL_SIZE, nx / CellsContainer.CELL_SIZE);
//				if(container.contains(nx, ny) && !container.haveShipsAround(this) && container.hasPlace(this)){
//					seabattleView.increaseShipsReady();
//					_parent.removeWidget(this);
//					container.addSprite(this);
//					
//					moveTo(_position.getY() * CellsContainer.CELL_SIZE + 2, _position.getX() * CellsContainer.CELL_SIZE + 2);
//					container.placeShip(this);
////					NeoApplication.instance.getAudio().play("seabattleSet");
//				}
//				else if(_startPosition != null){
//					moveTo(_startPosition.x, _startPosition.y);
//					_position = null;
//				}
//			}
//			else{
//				CellsContainer parent = (CellsContainer) _parent;
//				Position oldPos = _position;
//				boolean oldOrient = _horizontal;
//				
//				int nx = mousex + getX() - _mouseDown.x;
//				int ny = mousey + getY() - _mouseDown.y;
//				Position newPos = new Position(ny / CellsContainer.CELL_SIZE, nx / CellsContainer.CELL_SIZE);
//				
//				if(isValid(newPos)){
//					parent.removeShip(this);
//					_position = newPos;	
//					
//					
//					
//					if ((modifiers & InputEvent.BUTTON3_MASK) != 0) {
//						_horizontal = !_horizontal;
//					}
//					
//					if(!parent.haveShipsAround(this) && parent.hasPlace(this)){
//						if ((modifiers & InputEvent.BUTTON3_MASK) != 0) {
//							if(_horizontal)
//								setImage(UIUtils.rotate(_image, UIUtils.ROTATE_270));
//							else
//								setImage(UIUtils.rotate(_image, UIUtils.ROTATE_90));
//							resize(_x, _y, _image.getWidth(), _image.getHeight());
//							parent.placeShip(this);
//						}
//						else
//							parent.placeShip(this);
//					}
//					else{
//						_horizontal = oldOrient;
//						_position = oldPos;
//						parent.placeShip(this);
//					}
//				}
//				this.moveTo(_position.getY() * CellsContainer.CELL_SIZE + 2, _position.getX() * CellsContainer.CELL_SIZE + 2);
//			}
//				
//			return super.onMouseUp(mousex, mousey, modifiers);
//		}

		private boolean isValid(final Position pos) {
			return pos.getX() >= 0 && pos.getX() < SeaBattleProtocol.SIZE && pos.getY() >= 0 && pos.getY() < SeaBattleProtocol.SIZE;
		}
	}

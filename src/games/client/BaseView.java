package games.client;

import games.client.sprites.SpriteGroup;
import games.client.sprites.SpriteManager;
import games.client.util.CanvasSpriteGraphics;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.client.util.Images;
import games.shared.Point;
import games.shared.Resources;

import java.util.Map;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;

public abstract class BaseView implements GameView{

	private static final int FRAME_TIME = 10;
	private static final String upgradeMessage = "Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	public static class ActionFinishedScheduledCommand implements
	ScheduledCommand {
		private final Finishable _action;
		public ActionFinishedScheduledCommand(final Finishable action) {
			_action = action;
		}
		@Override
		public void execute() {
			if (_action != null) {
				_action.finished();
			}
		}
	}
	
	private CanvasSpriteGraphics _spriteGraphics;
	protected SpriteManager<DrawElement> _spriteManager;
	protected Canvas canvas;
	protected Context2d context;
	protected SpriteGroup<DrawElement> _gameSprites;
	private boolean _isUserInteractionsAllowed = true;
	private AbsolutePanel _gamePanel;

	public BaseView() {
		Resources.set(ImageFactory.class, new Images(getImageNames()));
	}
	
	protected abstract Map<String, String> getImageNames();
	protected abstract ScheduledCommand getStartGameCommand();
	protected abstract int getGameHeight();

	@Override
	public void setGamePanel(final AbsolutePanel gamePanel) {
		_gamePanel = gamePanel;
	}
	
	public AbsolutePanel getGamePanel() {
		return _gamePanel;
	}
	
	protected void init() {
		canvas = Canvas.createIfSupported();
		if (canvas == null) {
			RootPanel.get().add(new Label(upgradeMessage));
			return;
		}
		setCanvasSize();
		if("".equals(getPanelName())){
			getGamePanel().add(canvas);
		} else {
			getGamePanel(getPanelName()).add(canvas);
		}
		
		context = canvas.getContext2d();

		_spriteGraphics = new CanvasSpriteGraphics(context);

		initHandlers();

		final ScheduledCommand startGameCommand = getStartGameCommand();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				Resources.get(ImageFactory.class).loadImages(startGameCommand);
			}
		});

		_spriteManager = new SpriteManager<DrawElement>(_spriteGraphics);
		_gameSprites = new SpriteGroup<DrawElement>();
		_spriteManager.addListener(_gameSprites);
		
		AnimationScheduler.get().requestAnimationFrame(_spriteManager);
	}

	private Panel getGamePanel(final String panelName) {
		return RootPanel.get(panelName);
	}

	protected String getPanelName() {
		return "";
	}

	protected void setCanvasSize() {
		canvas.setWidth(getGameWidth() + "px");
		canvas.setHeight(getGameHeight() + "px");
		canvas.setCoordinateSpaceWidth(getGameWidth());
		canvas.setCoordinateSpaceHeight(getGameHeight());
	}

	private void initHandlers() {
		
		class MouseHandler implements MouseDownHandler, MouseUpHandler, MouseMoveHandler, 
				MouseOutHandler, DoubleClickHandler{

			private Point _mouseDown;

			@Override
			public void onDoubleClick(final DoubleClickEvent event) {
				event.preventDefault();
				doubleClick(event.getX(), event.getY());				
			}

			@Override
			public void onMouseOut(final MouseOutEvent event) {
				event.preventDefault();
				if(_mouseDown != null){
					unselectedPoint(_mouseDown.x, _mouseDown.y, event.getNativeButton());
				}
			}

			@Override
			public void onMouseMove(final MouseMoveEvent event) {
				event.preventDefault();
				BaseView.this.onMouseMove(event.getX(), event.getY());				
			}

			@Override
			public void onMouseUp(final MouseUpEvent event) {
				event.preventDefault();
				unselectedPoint(event.getX(), event.getY(), event.getNativeButton());
				_mouseDown = null;
			}

			@Override
			public void onMouseDown(final MouseDownEvent event) {
				event.preventDefault();
				final int x = event.getX();
				final int y = event.getY();
				selectedPoint(x, y, event.getNativeButton());
				_mouseDown = new Point(x, y);
			}
			
		}
		final MouseHandler mouseHandler = new MouseHandler();
		canvas.addMouseDownHandler(mouseHandler);
		canvas.addDoubleClickHandler(mouseHandler);
		canvas.addMouseUpHandler(mouseHandler);
		canvas.addMouseMoveHandler(mouseHandler);
		canvas.addMouseOutHandler(mouseHandler);

		class TouchesHandler implements TouchStartHandler, TouchMoveHandler,
				TouchEndHandler, TouchCancelHandler {
			
			private Point _mouseCoord = new Point();

			@Override
			public void onTouchCancel(final TouchCancelEvent event) {
				event.preventDefault();
			}

			@Override
			public void onTouchEnd(final TouchEndEvent event) {
				event.preventDefault();
				unselectedPoint(_mouseCoord.x, _mouseCoord.y, NativeEvent.BUTTON_LEFT);
			}

			@Override
			public void onTouchMove(final TouchMoveEvent event) {
				event.preventDefault();
				final Element relativeElement = event.getRelativeElement();
				final int x = event.getTouches().get(0)
				.getRelativeX(relativeElement);
				final int y = event.getTouches().get(0)
				.getRelativeY(relativeElement);
				_mouseCoord = new Point(x, y);
				BaseView.this.onMouseMove(x, y);
			}

			@Override
			public void onTouchStart(final TouchStartEvent event) {
				event.preventDefault();
				final Element relativeElement = event.getRelativeElement();
				final int x = event.getTouches().get(0)
						.getRelativeX(relativeElement);
				final int y = event.getTouches().get(0)
						.getRelativeY(relativeElement);
				_mouseCoord = new Point(x, y);
				selectedPoint(x, y, NativeEvent.BUTTON_LEFT);
			}
		}

		final TouchesHandler handler = new TouchesHandler();
		canvas.addTouchStartHandler(handler);
		canvas.addTouchMoveHandler(handler);
		canvas.addTouchEndHandler(handler);
		canvas.addTouchCancelHandler(handler);
	}

	protected void doubleClick(final int x, final int y) {
		if (!isUserInteractionsAllowed()) {
			return;
		}
		_spriteManager.onDoubleClick(x, y);
	}

	protected void selectedPoint(final int x, final int y, final int modifiers) {
		if (!isUserInteractionsAllowed()) {
			return;
		}
		_spriteManager.onMouseDown(x, y, modifiers);
	}
	
	protected void unselectedPoint(final int x, final int y, final int modifiers) {
		if (!isUserInteractionsAllowed()) {
			return;
		}
		_spriteManager.onMouseUp(x, y, modifiers);
	}
	
	protected void onMouseMove(final int x, final int y) {
		if (!isUserInteractionsAllowed()) {
			return;
		}
		_spriteManager.onMouseMove(x, y);
	}

	public void allowUserInteractions(final boolean allow) {
		_isUserInteractionsAllowed = allow;
	}

	public boolean isUserInteractionsAllowed() {
		return _isUserInteractionsAllowed;
	}
	
}

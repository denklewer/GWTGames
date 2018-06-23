package scrabble.client;

import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.UIUtils;
import scrabble.shared.JokerScrabbleDie;
import scrabble.shared.ScrabbleDie;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;

public class JokerDieWidget extends DieWidget {

	private static final char DEFAULT_VALUE = '\u0000';
	private static final String BOLD_30PX_SANS_SERIF = "bold 30px sans-serif";
	
	static private SpriteImage JOKER;
	
	static {
		final Canvas canvas = UIUtils.createCanvas(DieWidget.CELL_SIZE, DieWidget.CELL_SIZE);
		final Context2d context = canvas.getContext2d();
		context.setFont(BOLD_30PX_SANS_SERIF);
		context.fillText("*", DieWidget.CELL_SIZE/3, DieWidget.CELL_SIZE, DieWidget.CELL_SIZE);
		DrawElement drawElement = DrawElement.createDrawElement(canvas.getCanvasElement());
		JOKER = new SpriteImage(drawElement, DieWidget.CELL_SIZE, DieWidget.CELL_SIZE);
	}

	private boolean _mouseDrag;
	private boolean _mouseDown;

	public JokerDieWidget(ScrabbleView parent, ScrabbleDie die) {
		this(parent, die, 0, 0);
	}
	
	public JokerDieWidget(ScrabbleView parent, ScrabbleDie die, int x, int y) {
		super(parent, die, x, y);
	}
	
	private boolean selectLetterValue() {
		return ((JokerScrabbleDie) getDie()).getSelectedLetter() != DEFAULT_VALUE;
	}
	
	@Override
	protected SpriteImage getDieImage() {
		if(selectLetterValue()){
			return super.getDieImage(((JokerScrabbleDie) getDie()).getSelectedLetter());
		}
		return JOKER;
	}
	
	@Override
	public boolean onMouseDown(int x, int y, int modifiers) {
		_mouseDown = true;
		if(selectLetterValue()){
			return super.onMouseDown(x, y, modifiers);
		}
		return true;
	}
	
	@Override
	public boolean onMouseMove(int x, int y) {
		if(_parent.dialogIsVisible()){
			return true;
		}
		if(_mouseDown){
			_mouseDrag = true;
		}
		if(selectLetterValue()){
			return super.onMouseMove(x, y);
		}
		return true;
	}
	
	@Override
	public boolean onMouseUp(int x, int y, int modifiers) {
		_mouseDown = false;
		if(selectLetterValue() && _mouseDrag){
			_mouseDrag = false;
			return super.onMouseUp(x, y, modifiers);
		}
		_mouseDrag = false;
		_parent.changeChooseDieDialogVisible(this);
		return true;
	}
	
	public void setSelectedLetter(char value){
		((JokerScrabbleDie) getDie()).setSelectedLetter(value);
		ScrabbleCellWidget cellWidget = _parent.getCellWidget(getX(), getY());
		if(cellWidget == null || cellWidget.getDieWidget() == null){
			return;
		}
		cellWidget.widgetTaken(this);
		cellWidget.widgetPut(this);
	}
	
	public void resetSelectedLetter(){
		setSelectedLetter(DEFAULT_VALUE);
		markDirty();
	}
	
}

package scrabble.client;

import games.client.sprites.SpriteGroup;
import games.client.util.DrawElement;

import java.util.ArrayList;
import java.util.List;

import scrabble.shared.ScrabbleDie;

public class ChooseDieWidgetDialog extends SpriteGroup<DrawElement> {

	private static final int WIDTH_IN_DIES = 8;
	private static final int SHIFT_X = 141;
	private static final int SHIFT_Y = 510 - 4 * ScrabbleCellWidget.CELL_SIZE;

	private List<DieWidget> _dies = new ArrayList<DieWidget>();
	private JokerDieWidget _jokerDieWidget;
	private final ScrabbleView _parent;
	private boolean _isVisible;

	public ChooseDieWidgetDialog(ScrabbleView sender) {
		_parent = sender;
	}
	
	public void init(){
		for (int i = 0; i < _parent.getLetters().length; i++) {
			char letter = _parent.getLetters()[i];
			DieWidgetShape dieWidget = new DieWidgetShape(new ScrabbleDie(letter, 0), _parent);
			_dies.add(dieWidget);
			addSprite(dieWidget);
		}
		doLayout();
		setDialogVisible(false);
	}

	public void doLayout() {
		int h = _dies.size() / WIDTH_IN_DIES;
		int heightInDieces = _dies.size() % WIDTH_IN_DIES == 0 ? h : h + 1;
		int index = 0;
		for (int j = 0; j < heightInDieces; j++) {
			for (int i = 0; i < WIDTH_IN_DIES; i++) {
				DieWidget dieWidget = _dies.get(index);
				dieWidget.setBounds(SHIFT_X + i * ScrabbleCellWidget.CELL_SIZE, SHIFT_Y + j * ScrabbleCellWidget.CELL_SIZE, ScrabbleCellWidget.CELL_SIZE, ScrabbleCellWidget.CELL_SIZE);
				index++;
				if(index == _dies.size()){
					return;
				}
			}
		}
	}

	public void setSelectedValue(char value){
		_jokerDieWidget.setSelectedLetter(value);
		_jokerDieWidget.markDirty();
	}

	public void setDialogVisible(boolean value) {
		_isVisible = value;
		for (DieWidget die : _dies) {
			die.setVisible(value);
		}
	}

	public void changeChooseDieDialogVisibleAndSetJoker(JokerDieWidget jokerDieWidget) {
		_jokerDieWidget = jokerDieWidget;
		setDialogVisible(!_isVisible);
	}

	public boolean isVisible() {
		return _isVisible;
	}
	
}

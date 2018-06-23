package scrabble.client;

import games.client.sprites.ImageEngine;
import games.client.sprites.Rectangle;
import games.client.sprites.Sprite;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.client.util.UIUtils;
import games.shared.Resources;
import scrabble.shared.ScrabbleCell;
import scrabble.shared.ScrabbleProtocol;

public class ScrabbleCellWidget extends Sprite<DrawElement> {
	
	private ScrabbleCell _cell;
	private boolean _lightBackground;
	private boolean _isStar;
	public static final int CELL_SIZE = 34;
	private DieWidget _dieWidget;
	
	static private SpriteImage[] _cells = UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("tile-02"), CELL_SIZE, CELL_SIZE * 7, 7);
	private final ScrabbleView _scrabbleView;

	public ScrabbleCellWidget(final ScrabbleView scrabbleView, final ScrabbleCell cell, final boolean lightBackground, final boolean isStar, final int x, final int y) {
		super(x, y, CELL_SIZE, CELL_SIZE);
		_scrabbleView = scrabbleView;
		_cell = cell;
		_lightBackground = lightBackground;
		_isStar = isStar;
		
		setImageEngine(new ImageEngine<DrawElement>() {
			
			@Override
			public boolean updateTick(final long timestamp) {
				return false;
			}
			
			@Override
			public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image,
					final int x, final int y, final Rectangle dirtyRegion) {
				final SpriteImage bck = _lightBackground ? _cells[0] : _cells[1];
				bck.draw(graphics, x, y, dirtyRegion);
				if (_isStar) {
					_cells[6].draw(graphics, x, y, dirtyRegion);
				}
				if (_cell.getTypeOfBonus() == ScrabbleProtocol.BONUS_LETTER) {
					if (_cell.getPoint() == 2) {
						_cells[5].draw(graphics, x, y, dirtyRegion);
					} else if (_cell.getPoint() == 3) {
						_cells[4].draw(graphics, x, y, dirtyRegion);
					}
				} else if (_cell.getTypeOfBonus() == ScrabbleProtocol.BONUS_WORD) {
					if (_cell.getPoint() == 2) {
						_cells[3].draw(graphics, x, y, dirtyRegion);
					} else if (_cell.getPoint() == 3) {
						_cells[2].draw(graphics, x, y, dirtyRegion);
					}
				}				
			}
		});
	}
	
	protected void widgetPut(final DieWidget widget) {
		_dieWidget = widget;
		_cell.putDie(_dieWidget.getDie());
		_dieWidget.setBg(_cell);
		_scrabbleView.execAddDie(_cell);
	}
	
	public void widgetTaken(final DieWidget widget) {
		_scrabbleView.execRemoveDie(_cell);
		removeDie();
	}

	public DieWidget getDieWidget() {
		return _dieWidget;
	}

	public void removeDie() {
		_cell.takeDie();
		_dieWidget = null;
	}

}

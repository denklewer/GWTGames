package sudoku.client;

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
import sudoku.shared.CommonConstants;

public class DifficultyChoicePanel extends SpriteGroup {

	private static final SpriteImage FRAME;
	private static final SpriteImage[] DOTS;
	
	private SudokuView _view;

	static {
		final DrawElement frameElement = Resources.get(ImageFactory.class).getDrawElement("endFrame");
		FRAME = new SpriteImage(frameElement, frameElement.getWidth(), frameElement.getHeight());
		final DrawElement dotsElement = Resources.get(ImageFactory.class).getDrawElement("choiceDots");
		DOTS = UIUtils.cutSpriteVertically(dotsElement, dotsElement.getWidth(), dotsElement.getHeight(), 2);
	}

	public DifficultyChoicePanel(final SudokuView view) {
		_view = view;
	}

	public void init() {
		final int x = (_view.getGameWidth() - FRAME.width()) >> 1;
		final int y = (_view.getGameHeight() - FRAME.height()) >> 1;
		final Sprite<DrawElement> frame = new Sprite<DrawElement>(FRAME, x, y, FRAME.width(), FRAME.height());
		addSprite(frame);
		final int xy0 = 22;
		addDifficLabel("easy", CommonConstants.DIFFICULTY_EASY, xy0 + x, xy0 + y);
		addDifficLabel("intermediate", CommonConstants.DIFFICULTY_INTERMIDIATE, xy0 + x, xy0 + 21 + y);
		addDifficLabel("hard", CommonConstants.DIFFICULTY_HARD, xy0 + x, xy0 + 42 + y);
	}

	private void addDifficLabel(final String string, final int level, final int x, final int y) {
		final ChoiceLabel label = new ChoiceLabel(string, level, x, y);
		addSprite(label);
	}


	public void setDifficulty(final int difficulty) {
		_view.sendDifficulty(difficulty);
		hideSprites();
	}

	protected class ChoiceLabel extends Sprite {
		private String _word;
		private int _difficulty = 0;
		private boolean _activated = false;

		public ChoiceLabel(final String word, final int difficulty, final int x, final int y) {
			super(x, y, 111, 21);
			_word = word;
			_difficulty = difficulty;

			setImageEngine(new ImageEngine<DrawElement>() {
				@Override
				public boolean updateTick(final long timestamp) {
					return false;
				}

				@Override
				public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image, final int x, final int y, final Rectangle dirtyRegion) {
					final DrawElement word = UIUtils.createLeftTextElement(_word, getWidth(), getHeight(), SudokuClientConstants.BLACK_COLOR, 
							SudokuClientConstants.WHITE_COLOR, 0d, SudokuClientConstants.BOLD_14PX_SANS_SERIF, 14);
					final SpriteImage wordSprite = new SpriteImage<DrawElement>(word, word.getWidth(), word.getHeight());
					wordSprite.draw(graphics, x + 20, y, dirtyRegion);
					DOTS[_activated ? 0 : 1].draw(graphics, x + 5, y + (getHeight() - DOTS[0].height()) / 2, dirtyRegion);
				}
			});
		}
		
		@Override
		public boolean onMouseEntered(final int mousex, final int mousey) {
			_activated = true;
			markDirty();
			return true;
		}
		
		@Override
		public boolean onMouseExit() {
			_activated = false;
			markDirty();
			return true;
		}

		@Override
		public boolean onMouseUp(final int mousex, final int mousey, final int modifiers) {
			setDifficulty(_difficulty);
			return true;
		}
	}
}

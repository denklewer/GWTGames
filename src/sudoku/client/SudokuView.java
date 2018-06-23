package sudoku.client;

import games.client.BaseView;
import games.client.GameController;
import games.client.sprites.ImageEngine;
import games.client.sprites.Rectangle;
import games.client.sprites.Sprite;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.client.util.InformationDialog;
import games.client.util.UIUtils;
import games.shared.Point;
import games.shared.Resources;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import sudoku.shared.Cell;
import sudoku.shared.CommonConstants;
import sudoku.shared.Move;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.HTML;

public class SudokuView extends BaseView {
	
	private SudokuController _controller;
	private SudokuFieldElement[][] _field;
	private SudokuSimpleButton _newGameButton;
	private SudokuSimpleButton _cleanButton;
	private SudokuSimpleButton _checkButton;
	private SudokuSimpleButton _redoButton;
	private SudokuSimpleButton _undoButton;
	
	private Vector _history;
	private Vector _reserveHistory;
	private DifficultyChoicePanel _diffChoicePanel;
	private SudokuChoicePanel _choicePanel;

	@Override
	public void init(final GameController controller) {
		_controller = (SudokuController) controller;
		super.init();
	}

	@Override
	protected Map<String, String> getImageNames() {
		final Map<String, String> imageNames = new HashMap<String, String>();
		imageNames.put("fon", "images/sudoku/fon.gif");
		imageNames.put("granica-payki", "images/sudoku/granica-payki.gif");
		imageNames.put("ramka", "images/sudoku/ramka.gif");
		imageNames.put("kvadrat", "images/sudoku/kvadrat.gif");
		imageNames.put("eyes", "images/sudoku/eyes1.gif");
		imageNames.put("spider", "images/sudoku/spider1.gif");

		imageNames.put("staticDigits", "images/sudoku/cifrybig.gif");
		imageNames.put("editableDigits", "images/sudoku/cifrybig-01.gif");
		imageNames.put("tileBg", "images/sudoku/osnova.gif");

		imageNames.put("undo", "images/sudoku/nazad.gif");
		imageNames.put("redo", "images/sudoku/vpered.gif");
		imageNames.put("check", "images/sudoku/proverit.gif");
		imageNames.put("clean", "images/sudoku/ochistit.gif");
		imageNames.put("newGame", "images/sudoku/new.gif");
		imageNames.put("button", "images/sudoku/knopka.gif");
		
		imageNames.put("endFrame", "images/sudoku/end.gif");
		imageNames.put("choiceDots", "images/sudoku/vybor.gif");
		
		imageNames.put("field", "images/sudoku/polecifr.gif");
		imageNames.put("smallDigits", "images/sudoku/cifrysmall.gif");
		imageNames.put("rubber", "images/sudoku/lastik.gif");
		imageNames.put("cross", "images/sudoku/krestik.gif");
		
		return imageNames;
	}

	@Override
	protected ScheduledCommand getStartGameCommand() {
		return new ScheduledCommand() {

			@Override
			public void execute() {
				initView();
				_controller.uiReady();
			}
		};
	}

	private void initView() {
		addBackground();
		createField();
		addButtons();
		addDiffChoicePanel();
		addChoicePanel();
		_history = new Vector(10);
		_reserveHistory = new Vector(10);
	}

	private void addChoicePanel() {
		_choicePanel = new SudokuChoicePanel(this);
		_spriteManager.addListener(_choicePanel);
		_choicePanel.init();
	}

	private void addDiffChoicePanel() {
		_diffChoicePanel = new DifficultyChoicePanel(this);
		_spriteManager.addListener(_diffChoicePanel);
		_diffChoicePanel.init();
	}

	private void addButtons() {
		final SpriteImage<DrawElement>[] buttonImages = UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("button"), 43, 152, 4);
		
		final DrawElement undoElement = Resources.get(ImageFactory.class).getDrawElement("undo");
		_undoButton = new SudokuSimpleButton(7, 327, undoElement.getWidth(), undoElement.getHeight(), undoElement, buttonImages, new Runnable() {
			@Override
			public void run() {
				moveBack();
			}
		});
		_gameSprites.addSprite(_undoButton);
		_undoButton.setDisabled();
	
		final DrawElement redoElement = Resources.get(ImageFactory.class).getDrawElement("redo");
		_redoButton = new SudokuSimpleButton(7, 374, redoElement.getWidth(), redoElement.getHeight(), redoElement, buttonImages, new Runnable() {
			@Override
			public void run() {
				moveForward();
			}
		});
		_gameSprites.addSprite(_redoButton);
		_redoButton.setDisabled();
		
		final DrawElement checkElement = Resources.get(ImageFactory.class).getDrawElement("check");
		_checkButton = new SudokuSimpleButton(7, 266, checkElement.getWidth(), checkElement.getHeight(), checkElement, buttonImages, new Runnable() {
			@Override
			public void run() {
				getFieldAndSend();
			}
		});
		_gameSprites.addSprite(_checkButton);
		_checkButton.setDisabled();
		
		final DrawElement cleanElement = Resources.get(ImageFactory.class).getDrawElement("clean");
		_cleanButton = new SudokuSimpleButton(7, 216, cleanElement.getWidth(), cleanElement.getHeight(), cleanElement, buttonImages, new Runnable() {
			@Override
			public void run() {
				cleanField();
			}
		});
		 _gameSprites.addSprite(_cleanButton);
		 
		final DrawElement newGameElement = Resources.get(ImageFactory.class).getDrawElement("newGame");
		_newGameButton = new SudokuSimpleButton(7, 423, newGameElement.getWidth(), newGameElement.getHeight(), newGameElement, buttonImages,
				new Runnable() {
					@Override
					public void run() {
						showDifficultyChoisePanel();
					}
				}
		);
		_gameSprites.addSprite(_newGameButton);
	}

	private void moveBack() {
		if (_history.size() <= 0) {
			return;
		}
		final Move move = (Move) _history.lastElement();
		_history.removeElement(move);
		_reserveHistory.addElement(move);
		final SudokuFieldElement cell = _field[move.getCellIndex().x][move.getCellIndex().y];
		if (cell != null) {
			cell.setNumber(move.getOldNumber(), false, false);
		}
		checkButtonDisability();
	}

	private void moveForward() {
		if (_reserveHistory.size() <= 0) {
			return;
		}
		final Move move = (Move) _reserveHistory.lastElement();
		_reserveHistory.removeElement(move);
		final SudokuFieldElement cell = _field[move.getCellIndex().x][move.getCellIndex().y];
		if (cell != null) {
			cell.setNumber(move.getNewNumber(), false, false);
			if (_history.size() >= 10) {
				_history.removeElementAt(0);
			}
			_history.addElement(move);
		}
		checkButtonDisability();
	}

	private void getFieldAndSend() {
		final Cell[][] cells = new Cell[9][9];
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				cells[i][j] = new Cell(j, i, _field[i][j].getNumber());
			}
		}
		if(cells != null){
			_controller.sendTask(cells);
		}
	}

	private void cleanField() {
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				_field[j][i].setNumber(0, false, true);
			}
		}
		newMove(new Move(true));
		_history.removeAllElements();
		_reserveHistory.removeAllElements();
		checkButtonDisability();
	}

	private void showDifficultyChoisePanel() {
		_diffChoicePanel.showSprites();
	}

	private void createField() {
		int y0 = 29;
		SudokuFieldElement fieldElement;
		_field = new SudokuFieldElement[9][9];
		for (int i = 0; i < CommonConstants.FIELD_HEIGTH; i++) {
			if (i == 3 || i == 6) {
				y0 += 4;
			}
			int x0 = 72;
			for (int j = 0; j < CommonConstants.FIELD_WIDTH; j++) {
				if (j == 3 || j == 6) {
					x0 += 4;
				}
				fieldElement = new SudokuFieldElement(this, new Point(i, j), x0, y0);
				_gameSprites.addSprite(fieldElement);
				_field[i][j] = fieldElement;
			}
		}
	}

	private void addBackground() {
		addBGRect();
		addFon();
		addWeb();
		addFrame();
		addSquares();
		addBlinkingEyes();
		addSpiders();
	}

	private void addSpiders() {
		addSpider(26 - (34 >> 2), 62, CommonConstants.DEFAULT_WAY);
		addSpider(561, 30, 225);
	}

	private void addBlinkingEyes() {
		addEyes(5, 5);
		addEyes(575, 350);
		addEyes(35, 510);
	}

	private void addSpider(final int x, final int y, final int webLength) {
		final DrawElement spiderElement = Resources.get(ImageFactory.class).getDrawElement("spider");
		final SpiderSprite spiderSprite = new SpiderSprite(spiderElement, x, y, webLength);
		_gameSprites.addSprite(spiderSprite);
	}
	
	private void addEyes(final int x, final int y) {
		final DrawElement eyesElement = Resources.get(ImageFactory.class).getDrawElement("eyes");
		final Sprite<DrawElement> eyesSprite = new Sprite<DrawElement>(x, y, eyesElement.getWidth(), eyesElement.getHeight());
		eyesSprite.setImageEngine(new BlinkingEyeEngine());
		_gameSprites.addSprite(eyesSprite);

	}

	private void addSquares() {
		final DrawElement squaresElement = Resources.get(ImageFactory.class).getDrawElement("kvadrat");
		final SpriteImage squaresImage = new SpriteImage(squaresElement, squaresElement.getWidth(), squaresElement.getHeight());
		final Sprite<DrawElement> squares = new Sprite<DrawElement>(71, 27, 9 * squaresElement.getWidth(), 9 * squaresElement.getHeight());
		final int width = squaresElement.getWidth();
		final int height = squaresElement.getHeight();
		squares.setImageEngine(new ImageEngine<DrawElement>() {
			@Override
			public boolean updateTick(final long timestamp) {
				return false;
			}

			@Override
			public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image, final int x, final int y, final Rectangle dirtyRegion) {
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						squaresImage.draw(graphics, 71 + width * j, 27 + height * i, dirtyRegion);
					}
				}
			}
		});
		_gameSprites.addSprite(squares);
	}

	private void addFrame() {
		final DrawElement frameElement = Resources.get(ImageFactory.class).getDrawElement("ramka");
		final SpriteImage<DrawElement> frameImage = new SpriteImage<DrawElement>(frameElement, frameElement.getWidth(), frameElement.getHeight());
		_gameSprites.addSprite(new Sprite<DrawElement>(frameImage, 56, 12, frameElement.getWidth(), frameElement.getHeight()));
	}

	private void addFon() {
		final DrawElement fonElement = Resources.get(ImageFactory.class).getDrawElement("fon");
		final SpriteImage fonImage = new SpriteImage(fonElement, fonElement.getWidth(), fonElement.getHeight());
		final Sprite<DrawElement> fon = new Sprite<DrawElement>(0, 0, CommonConstants.GAME_WIDTH, CommonConstants.GAME_HEIGHT);
		final int height = fonElement.getHeight();
		final int width = fonElement.getWidth();
		fon.setImageEngine(new ImageEngine<DrawElement>() {
			@Override
			public boolean updateTick(final long timestamp) {
				return false;
			}

			@Override
			public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image, final int x, final int y, final Rectangle dirtyRegion) {
				for (int i = 0; i < CommonConstants.GAME_HEIGHT; i += height) {
					for (int j = 0; j < CommonConstants.GAME_WIDTH; j += width) {
						fonImage.draw(graphics, x + j, y + i, dirtyRegion);
					}
				}
			}
		});
		_gameSprites.addSprite(fon);
	}

	private void addWeb() {
		final DrawElement webElement = Resources.get(ImageFactory.class).getDrawElement("granica-payki");
		final SpriteImage<DrawElement> webImage = new SpriteImage<DrawElement>(webElement, CommonConstants.GAME_WIDTH, CommonConstants.GAME_HEIGHT);
		_gameSprites.addSprite(new Sprite<DrawElement>(webImage, 0, 0, CommonConstants.GAME_WIDTH, CommonConstants.GAME_HEIGHT));
	}

	private void addBGRect() {
		final DrawElement bg = UIUtils.createRect(CommonConstants.GAME_WIDTH, CommonConstants.GAME_HEIGHT, SudokuClientConstants.BG_COLOR);
		final SpriteImage<DrawElement> bgImage = new SpriteImage<DrawElement>(bg, CommonConstants.GAME_WIDTH, CommonConstants.GAME_HEIGHT);
		_gameSprites.addSprite(new Sprite<DrawElement>(bgImage, 0, 0, CommonConstants.GAME_WIDTH, CommonConstants.GAME_HEIGHT));
	}

	@Override
	protected int getGameHeight() {
		return CommonConstants.GAME_HEIGHT;
	}

	@Override
	public int getGameWidth() {
		return CommonConstants.GAME_WIDTH;
	}

	public void sendDifficulty(final int difficulty) {
		_controller.sendDifficulty(difficulty);
	}

	public void setTask(final Cell[][] cells) {
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				if(cells[i][j].isStyleTask() || cells[i][j].isStyleNone()) {
					_field[j][i].setStaticNumber(cells[i][j].getNumber());
				} else {
					_field[j][i].setNumber(cells[i][j].getNumber(), false, true);
				}
			}
		}
	}

	public void addHistoryPoint(final Point index, final int oldNumber, final int newNumber) {
		if(_reserveHistory.size() > 0){
			_reserveHistory.removeAllElements();
		}
		if(_history.size() >= 10) {
			_history.removeElementAt(0);
		}
		_history.addElement(new Move(index, oldNumber, newNumber));
		checkButtonDisability();
	}

	public void newMove(final Move move) {
		_controller.newMove(move);
	}

	public void checkFieldIfComplete() {
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				if(!_field[j][i].containsNumber()){
					_checkButton.setDisabled();
					return;
				}
			}
		}
		_checkButton.setEnabled();
	}

	public void showChoicePanel(final SudokuFieldElement sudokuFieldElement) {
		_choicePanel.show(sudokuFieldElement);
	}
	
	private void checkButtonDisability(){
		if(_reserveHistory != null && _redoButton != null){
			if(_reserveHistory.size() <= 0 && !_redoButton.isDisabled()) {
				_redoButton.setDisabled();
			} else if(_reserveHistory.size() > 0 && _redoButton.isDisabled()) {
				_redoButton.setEnabled();
			}
		}
		if(_history != null && _undoButton != null){
			if(_history.size() > 0 && _undoButton.isDisabled()) {
				_undoButton.setEnabled();
			} else if(_history.size() < 1 && !_undoButton.isDisabled()) {
				_undoButton.setDisabled();
			}
		}
	}

	public void showVictoryMessage() {
		final HTML message = new HTML("<center>You won the game!</center>");
		new InformationDialog("Congratulations!", message, new Runnable() {
			@Override
			public void run() {
				showDifficultyChoisePanel();
			}
		}).show();	
	}

	public void showLossMessage() {
		final HTML message = new HTML("<center>Unfortunately, You aren't right!</center>");
		final InformationDialog informationDialog = new InformationDialog("Oops!", message);
		informationDialog.show();	
	}

	public SudokuChoicePanel getChoicePanel() {
		return _choicePanel;
	}

}

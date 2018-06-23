package scrabble.client;

import games.client.BaseView;
import games.client.GameController;
import games.client.sprites.ImageEngine;
import games.client.sprites.Rectangle;
import games.client.sprites.Sprite;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteGroup;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.client.util.InformationDialog;
import games.client.util.SpecifyDialog;
import games.shared.Resources;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import scrabble.shared.JokerScrabbleDie;
import scrabble.shared.ScrabbleBoard;
import scrabble.shared.ScrabbleCell;
import scrabble.shared.ScrabbleDie;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ScrabbleView extends BaseView {

	private static final int BG_IMAGE_SIZE = 8;
	private static final int GAME_WIDTH = 600;
	private static final int GAME_HEIGHT = 600;

	private static final int PANEL_INDENT_X = 517;

	private static final String BOLD_12PX_SANS_SERIF = "bold 12px sans-serif";
	
	private ScrabbleController _controller;
	private ScrabbleCellWidget[][] _cellWidgets;

	private boolean _isActive = true;
	private PassChangeButton _changeButton;
	private PassChangeButton _resetButton;
	private PassChangeButton _passButton;
	private ReadyButton _readyButton;
	private ScrabbleStandWidget _standWidget;

	protected int _passCounter;
	protected int _changeLetterCounter;

	private PlayersWidget _playersWidget;

	protected SpriteGroup<DrawElement> _topSprites;
	private CourseOfGameButton _courceOfGameButton;

	protected String _text = "";
	
	private char[] _letters;
	private ChooseDieWidgetDialog _chooseDieWidgetDialog;
	
	private Sprite<DrawElement> _restBones;
	protected int _restBonesNum;

	@Override
	protected Map<String, String> getImageNames() {
		final Map<String, String> imageNames = new HashMap<String, String>();
		imageNames.put("alphabet-01", "images/scrabble/alphabet-01.png");
		imageNames.put("bg-01", "images/scrabble/bg-01.gif");

		imageNames.put("border-01", "images/scrabble/border-01.gif");
		imageNames.put("border-02", "images/scrabble/border-02.gif");
		imageNames.put("box-01", "images/scrabble/box-01.gif");
		imageNames.put("but-big-01", "images/scrabble/but-big-01.gif");
		imageNames.put("but-name-01", "images/scrabble/but-name-01.gif");
		imageNames.put("but-ready-01", "images/scrabble/but-ready-01.gif");
		imageNames.put("but-small-01", "images/scrabble/but-small-01.gif");
		imageNames.put("but-small-02", "images/scrabble/but-small-02.gif");
		imageNames.put("field-score-01", "images/scrabble/field-score-01.gif");
		imageNames.put("place-pl-01", "images/scrabble/place-pl-01.gif");
		imageNames.put("players-01", "images/scrabble/players-01.gif");
		imageNames.put("score-01", "images/scrabble/score-01.gif");
		imageNames.put("score-02", "images/scrabble/score-02.gif");
		imageNames.put("score-alph-01", "images/scrabble/score-alph-01.gif");
		imageNames.put("select-01", "images/scrabble/select-01.gif");
		imageNames.put("tile-02", "images/scrabble/tile-02.gif");
		imageNames.put("timer", "images/scrabble/timer.gif");
		return imageNames;
	}

	@Override
	protected ScheduledCommand getStartGameCommand() {
		return new ScheduledCommand() {
			@Override
			public void execute() {
				initGameView();
				_controller.uiReady();
			}
		};
	}

	private void initGameView() {
		addBackGround();
		initButtons();
		_playersWidget.init();
		addRestBones();
	}

	private void addRestBones() {
		final DrawElement restBonesDrawElement = createRestBonesDrawElement();
		final SpriteImage<DrawElement> restBonesImage = new SpriteImage<DrawElement>(restBonesDrawElement, restBonesDrawElement.getWidth(), restBonesDrawElement.getHeight());
		_restBones = new Sprite<DrawElement>(restBonesImage, 141, PANEL_INDENT_X + 40, restBonesDrawElement.getWidth(), restBonesDrawElement.getHeight());
		_topSprites.addSprite(_restBones);
	}

	private DrawElement createRestBonesDrawElement() {
		return new DrawElement() {
			
			@Override
			public void draw(final Context2d context, final int offsetX, final int offsetY,
					final int width, final int height, final int destX, final int destY, final int destWidth,
					final int destHeight) {
				context.setFont(BOLD_12PX_SANS_SERIF);
				final int fontHeight = 12;
				final String text = "rest bones: " + _restBonesNum;
				context.fillText(text, destX + width/2 - (fontHeight/2) * text.length() - 7, destY + 20, width);				
			}

			@Override
			public int getWidth() {
				return 270;
			}

			@Override
			public int getHeight() {
				return 124;
			}
		};
	}

	private void initButtons() {
		initChangeButton();
		initPassButton();
		initReadyButton();
		initResetButton();
		initCourceOfGameButton();
	}

	private void initResetButton() {
		_resetButton = new PassChangeButton(PassChangeButton.RESET, PANEL_INDENT_X + 2, 335);
		_resetButton.setAction(new Runnable() {

			@Override
			public void run() {
				_controller.execReset();
			}

		});
		_gameSprites.addSprite(_resetButton);
	}

	private void initReadyButton() {
		_readyButton = new ReadyButton(PANEL_INDENT_X + 2, 461); 
		_readyButton.setAction(new Runnable() {

			@Override
			public void run() {
				resetPassCounter();
				resetChangeLettersCounter();
				setActive(false);
				_controller.scrabbleMove();
			}

		});
		_gameSprites.addSprite(_readyButton);
	}

	private void initPassButton() {
		_passButton = new PassChangeButton(PassChangeButton.PASS, PANEL_INDENT_X + 2, 403);
		_passButton.setAction(new Runnable() {

			@Override
			public void run() {
				if(_passCounter > 0) {
					final SpecifyDialog dialog = new SpecifyDialog("Do pass?", 120, 80, new Runnable() {
						@Override
						public void run() {
							doAndSendPass();
						}
					});
					dialog.show();
				} else {
					doAndSendPass();
				}
			}

		});
		_gameSprites.addSprite(_passButton);
	}

	private void initChangeButton() {
		_changeButton = new PassChangeButton(PassChangeButton.CHANGE, PANEL_INDENT_X + 2, 369);
		_changeButton.setAction(new Runnable() {

			@Override
			public void run() {
				if(_changeLetterCounter > 1) {
					final SpecifyDialog dialog = new SpecifyDialog("Change letter?", 170, 80, new Runnable() {

						@Override
						public void run() {
							changeLetter();
						}
					});
					dialog.show();
				} else {
					changeLetter();
				}
			}

		});
		_gameSprites.addSprite(_changeButton);
	}

	class CourceOfGameDialog extends DialogBox {

		public CourceOfGameDialog(final String text) {
			super(true);
			final VerticalPanel boxContents = new VerticalPanel();
			setText("Cource of game");
			setGlassEnabled(true);
			final Button ok = new Button("OK");
			ok.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					CourceOfGameDialog.this.hide();
				}
			});
			final ScrollPanel scroll = new ScrollPanel(new HTML(text));
			scroll.setSize("300px", "300px");
			boxContents.add(scroll);
			ok.setStyleName("button-center");
			boxContents.add(ok);
			setWidget(boxContents);
			center();
			hide();
		}

	}

	private void initCourceOfGameButton() {
		_courceOfGameButton = new CourseOfGameButton(PANEL_INDENT_X - 2, 5);
		_courceOfGameButton.setAction(new Runnable() {

			@Override
			public void run() {
				final CourceOfGameDialog courceOfGame = new CourceOfGameDialog(_text);
				courceOfGame.show();
			}

		});
		_courceOfGameButton.setEnabled(true);
		_gameSprites.addSprite(_courceOfGameButton);
	}

	private void changeLetter(){
		increaseChangeLettersCounter();
		resetPassCounter();
		setActive(false);
		final Vector<ScrabbleDie> selectedDices = _standWidget.getSelectedDices();
		_standWidget.removeSelectedDices();
		_controller.execChangeDice(selectedDices);
	}

	private void addBackGround() {
		final Sprite<DrawElement> bg = new Sprite<DrawElement>(0, 0, GAME_WIDTH, GAME_HEIGHT);
		bg.setImageEngine(new ImageEngine<DrawElement>() {

			@Override
			public boolean updateTick(final long timestamp) {
				return false;
			}

			@Override
			public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image,
					final int x, final int y, final Rectangle dirtyRegion) {
				final SpriteImage bgr = new SpriteImage<DrawElement>(Resources.get(ImageFactory.class).getDrawElement("bg-01"), BG_IMAGE_SIZE, BG_IMAGE_SIZE);
				for (int i = 0; i * BG_IMAGE_SIZE < 600; i++) {
					for (int j = 0; j * BG_IMAGE_SIZE < 600; j++) {
						bgr.draw(graphics, x + i * BG_IMAGE_SIZE, y + j * BG_IMAGE_SIZE, dirtyRegion);
					}
				}				
			}
		});
		_gameSprites.addSprite(bg);
	}

	@Override
	protected int getGameHeight() {
		return GAME_HEIGHT;
	}

	@Override
	public int getGameWidth() {
		return GAME_WIDTH;
	}

	@Override
	public void init(final GameController controller) {
		_controller = (ScrabbleController) controller;
		super.init();

		_standWidget = new ScrabbleStandWidget(this);
		_spriteManager.addListener(_standWidget);
		_standWidget.init();

		_playersWidget = new PlayersWidget();
		_spriteManager.addListener(_playersWidget);

		_chooseDieWidgetDialog = new ChooseDieWidgetDialog(this);
		_spriteManager.addListener(_chooseDieWidgetDialog);
		
		_topSprites = new SpriteGroup<DrawElement>();
		_spriteManager.addListener(_topSprites);
	}

	public void setBoard(final ScrabbleBoard board) {
		if(_cellWidgets == null){
			_cellWidgets = new ScrabbleCellWidget[board.getWidth()][board.getHeight()];
		}
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				final ScrabbleCell cell = board.getCell(i, j);
				if(_cellWidgets[i][j] == null){
					final int x = 2 + i * ScrabbleCellWidget.CELL_SIZE;
					final int y = 2 + j * ScrabbleCellWidget.CELL_SIZE;
					final ScrabbleCellWidget cellWidget = new ScrabbleCellWidget(this, cell, 
							i==0 || j==0 || i==board.getWidth() - 1 || j== board.getHeight() - 1 || 
							(i > 3 && j > 3 && i < board.getWidth() - 3 - 1 && j < board.getHeight() - 3 - 1),
							i == board.getWidth() / 2 && j == board.getHeight() / 2, x, y);
					_gameSprites.addSprite(cellWidget);
					_cellWidgets[i][j] = cellWidget;
				}
				final ScrabbleDie die = cell.getDie();
				if (die != null) {
					putDie(cell, true, i, j);
				}
			}
		}		
	}

	private void doAndSendPass() {
		doPass();
		_controller.execPass();
	}

	private void doPass() {
		increasePassCounter();
		setActive(false);
	}

	private void increasePassCounter() {
		_passCounter++;
		_passButton.changeType(PassChangeButton.PASS_2);
	}

	private void resetPassCounter(){
		_passCounter = 0;
		_passButton.changeType(PassChangeButton.PASS);
	}

	private void increaseChangeLettersCounter() {
		_changeLetterCounter++;
	}

	private void resetChangeLettersCounter(){
		_changeLetterCounter = 0;
	}

	public void putDie(final ScrabbleCell cell, final boolean isJoined, final int i, final int j) {
		final DieWidget dieWidget = createDieWidget(cell.getDie(), i, j);
		dieWidget.setBg(cell);
		dieWidget.setJoined(isJoined);
		cell.putDie(cell.getDie());
		_gameSprites.addSprite(dieWidget);
	}

	private DieWidget createDieWidget(final ScrabbleDie die, final int i, final int j) {
		if(die.getLetter() == '*'){
			return new JokerDieWidget(this, new JokerScrabbleDie(die), i * ScrabbleCellWidget.CELL_SIZE, j * ScrabbleCellWidget.CELL_SIZE);
		}
		return new DieWidget(this, die, i * ScrabbleCellWidget.CELL_SIZE, j * ScrabbleCellWidget.CELL_SIZE);
	}

	public boolean isActive() {
		return _isActive;
	}

	private void setActive(final boolean isActive) {
		if (isActive) {
		} else {
			disableButtons();
		}
		_isActive = isActive;
	}

	private void disableButtons() {
		_changeButton.setEnabled(false);
		_passButton.setEnabled(false);
		_readyButton.setEnabled(false);
		_resetButton.setEnabled(false);
	}

	public void toTop(final Sprite<DrawElement> widget) {
		_gameSprites.toTop(widget);
	}

	public void addStand(final byte side, final Vector<ScrabbleDie> newDice) {
		_standWidget.addStand(side, newDice);		
	}

	public ScrabbleCellWidget getCellWidget(final int x, final int y) {
		for (int i = 0; i < _cellWidgets.length; i++) {
			for (int j = 0; j < _cellWidgets[i].length; j++) {
				if(_cellWidgets[i][j].contains(x, y)){
					return _cellWidgets[i][j];
				}
			}
		}
		return null;
	}

	public void execAddDie(final ScrabbleCell cell) {
		_readyButton.setEnabled(false);
		_controller.addDie(cell);
	}

	public void execMoveIsPossible() {
		_readyButton.setEnabled(true);
	}

	public void execRemoveDie(final ScrabbleCell cell) {
		_readyButton.setEnabled(false);
		_controller.removeDie(cell);
	}

	public void moveApproved() {
		for (int i = 0; i < _cellWidgets.length; i++) {
			for (int j = 0; j < _cellWidgets[i].length; j++) {
				final DieWidget dieWidget = _cellWidgets[i][j].getDieWidget();
				if (dieWidget != null) {
					dieWidget.setJoined(true);
				}
			}
		}		
	}

	public void askMove() {
		setActive(true);
		_passButton.setEnabled(true);
		_resetButton.setEnabled(true);		
	}

	public DieWidget getDieOnBoard(final int x, final int y) {
		for (int i = 0; i < _cellWidgets.length; i++) {
			for (int j = 0; j < _cellWidgets[i].length; j++) {
				final ScrabbleCellWidget cell = _cellWidgets[i][j];
				if(cell != null && cell.contains(x, y) && cell.getDieWidget() != null){
					return cell.getDieWidget();
				}
			}
		}
		return null;
	}

	public void clearBoard() {
		for (int i = 0; i < _cellWidgets.length; i++) {
			for (int j = 0; j < _cellWidgets[i].length; j++) {
				final DieWidget dieWidget = _cellWidgets[i][j].getDieWidget();
				if (dieWidget != null && !dieWidget.isJoined()) {
					_cellWidgets[i][j].removeDie();
					_standWidget.removeSprite(dieWidget);
				}
			}
		}		
	}

	public void handleBusEvent() {
		_changeButton.setEnabled(_standWidget.getSelectedDices().size() > 0);		
	}

	public void execEndGame(final String reason, final byte winnerSide) {
		final HTML message = new HTML("<center>End game!</center>");
		removeScrabbleCells();
		removeScrabbleStand();
		final InformationDialog dialog = new InformationDialog(reason + " Winner on side " + winnerSide + "!", message);
		dialog.show();
		resetPassCounter();
		resetChangeLettersCounter();
	}

	private void removeScrabbleStand() {
		for (int i = 0; i < _cellWidgets.length; i++) {
			for (int j = 0; j < _cellWidgets[i].length; j++) {
				_cellWidgets[i][j].removeDie();
			}
		}
		_standWidget.removeDies();
	}

	private void removeScrabbleCells() {
		for (int i = 0; i < _cellWidgets.length; i++) {
			for (int j = 0; j < _cellWidgets[i].length; j++) {
				final ScrabbleCellWidget cell = _cellWidgets[i][j];
				if(cell.getDieWidget() != null){
					_standWidget.removeSprite(cell.getDieWidget());
				}
			}
		}
	}

	public void playerAdded(final byte side, final String name, final int playerScore) {
		_playersWidget.addPlayer(side, name, playerScore);
	}

	public void switchTurn(final byte side) {
		_playersWidget.switchTurn(side);
	}

	public void scoreChanged(final int score, final byte side) {
		_playersWidget.scoreChanged(score, side);
		final ScrabbleScoreWidget scoreWidget = new ScrabbleScoreWidget(_topSprites, score);
		final int width = _cellWidgets.length * ScrabbleCellWidget.CELL_SIZE;
		final int height = _cellWidgets[0].length * ScrabbleCellWidget.CELL_SIZE;
		final int x = (width - scoreWidget.getWidth()) / 2;
		final int y = height * 2 / 3 - scoreWidget.getHeight();
		scoreWidget.moveTo(x, y);
		scoreWidget.setX0(x);
		scoreWidget.setY0(y);
		_topSprites.addSprite(scoreWidget);
	}

	public void addMoveMadeInfo(final String info) {
		_text += info + "<BR>";
	}
	
	public void setLetters(final char[] letters) {
		_letters = letters;
		Arrays.sort(_letters);
		_chooseDieWidgetDialog.init();
	}
	
	public char[] getLetters() {
		return _letters;
	}

	public void setJokerValue(final char letter) {
		_chooseDieWidgetDialog.setDialogVisible(false);
		_chooseDieWidgetDialog.setSelectedValue(letter);
	}
	
	public void setDialogVisible(final boolean value){
		_chooseDieWidgetDialog.setDialogVisible(value);
	}

	public void changeChooseDieDialogVisible(final JokerDieWidget jokerDieWidget) {
		_chooseDieWidgetDialog.changeChooseDieDialogVisibleAndSetJoker(jokerDieWidget);
	}

	public boolean dialogIsVisible() {
		return _chooseDieWidgetDialog.isVisible();
	}

	public void updateBagSize(final int size) {
		_restBonesNum = size;
		_restBones.markDirty();
	}

}

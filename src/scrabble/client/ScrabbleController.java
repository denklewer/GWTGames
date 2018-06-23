package scrabble.client;

import games.client.GenericController;
import games.shared.RandomGenerator;

import java.util.List;
import java.util.Vector;

import scrabble.shared.ModelListener;
import scrabble.shared.ScrabbleCell;
import scrabble.shared.ScrabbleDictionary;
import scrabble.shared.ScrabbleDie;
import scrabble.shared.ScrabbleModel;
import scrabble.shared.ScrabblePlayer;

public class ScrabbleController extends GenericController<ScrabbleModel> implements ModelListener {

	private final ScrabbleView _view;
	private final ScrabbleDictionary _scrabbleDictionary;
	private final ScrabbleLoader _scrabbleLoader;
	private final RandomGenerator _random;

	public ScrabbleController(ScrabbleModel model, ScrabbleView view, ScrabbleDictionary scrabbleDictionary, ScrabbleLoader scrabbleLoader, RandomGenerator random) {
		super(model);
		_view = view;
		_scrabbleDictionary = scrabbleDictionary;
		_scrabbleLoader = scrabbleLoader;
		_random = random;
	}

	@Override
	public void start() {
		_model.addPlayer((byte) 0, true);
	}

	@Override
	public void gamerAdded(byte side) {
		_view.setLetters(_scrabbleDictionary.getLetters());
		_view.setBoard(_model.getBoard().copy());
		_view.playerAdded(side, "player", _model.getPlayerScore());
		if (_model.getTurn() == side) {
			_view.askMove();
		}		
	}

	@Override
	public void standFilled(byte side, Vector<ScrabbleDie> newDice) {
		_view.addStand(side, newDice);
	}

	public void addDie(ScrabbleCell cell) {
		if (!_model.checkTurn((byte) 0)){
			return;
		}
		ScrabbleCell cellToAdd = cell.copy();
		if (!_model.checkHasCell(cellToAdd)){
			return;
		}
		cellToAdd.setNew(true);
		_model.addCell(cellToAdd);
		if (_model.checkMove()) {
			_view.execMoveIsPossible();
		}
		_model.clear();
		
	}

	public void removeDie(ScrabbleCell cell) {
		if (!_model.checkTurn((byte) 0))
			return;
		ScrabbleCell cellToRemove = cell.copy();
		_model.removeCell(cellToRemove);
		if (_model.checkMove()) {
			_view.execMoveIsPossible();
		}
		_model.clear();		
	}

	public void scrabbleMove() {
		if (!_model.checkTurn((byte) 0))
			return;
		_model.makeMove();		
	}

	@Override
	public void moveMade() {
		_view.moveApproved();
	}

	public void execReset() {
		if (!_model.checkTurn((byte) 0))
			return;
		clearBoard();
	}

	private void clearBoard() {
		List<ScrabbleCell> cells = _model.getCells();
		if (cells.size() > 0) {
			_view.clearBoard();
			Vector<ScrabbleDie> diceCopy = new Vector<ScrabbleDie>();
			for (ScrabbleDie die : _model.getDice()) {
				diceCopy.add(die.copy());
			}
			_view.addStand((byte) 0, diceCopy);
			cells.clear();
		}
	}

	public void execChangeDice(Vector<ScrabbleDie> selectedDices) {
		if (!_model.checkTurn((byte) 0))
			return;
		clearBoard();
		_model.changeDice((byte) 0, selectedDices);		
	}

	public void execPass() {
		doPlayerPass(false);
	}

	public void doPlayerPass(boolean byTimeout) {
		byte playerSide = 0;
		if (!_model.checkTurn(playerSide))
			return;
		clearBoard();
		_model.pass(playerSide, byTimeout);
	}

	@Override
	public void gameEnded(String reason, byte winnerSide) {
		_view.execEndGame(reason, winnerSide);
		startNewGame();
	}
	
	private void startNewGame() {
		Vector<ScrabblePlayer> players = _model.getRealServerPlayers();
		createModel();
		for (int i = 0; i < players.size(); i++) {
			ScrabblePlayer player = players.elementAt(i);
			_model.addPlayer(player.getSide(), false);
		}
	}
	
	private void createModel(){
		_model = new ScrabbleModel(_random, _scrabbleLoader.getBoard(), _scrabbleLoader.getBag(), _scrabbleDictionary);
		_model.addListener(this);
	}

	@Override
	public void turnSwitched(byte side) {
		_view.switchTurn(side);
		_view.askMove();
	}

	@Override
	public void scoreChanged(int score, byte side) {
		_view.scoreChanged(score, side);
	}

	@Override
	public void addMoveMadeInfo(String info) {
		_view.addMoveMadeInfo(info);
	}

	@Override
	public void bagSizeChanged(int size) {
		_view.updateBagSize(size);
	}

}

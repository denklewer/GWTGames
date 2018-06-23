package seabattle.shared;

import games.shared.GameModel;

import java.util.ArrayList;
import java.util.List;

public class SeaBattleModel implements GameModel<ModelListener> {
	private static final byte BLACK = SeaBattleProtocol.BLACK;
	private static final byte WHITE = SeaBattleProtocol.WHITE;
	private SeaBattleCondition _condition;
	private int _moveNumber;
	
	protected int _state;
	
	private SeaBattleRobot _bot;

	private List<ModelListener> _listeners = new ArrayList<ModelListener>();
	
	public void addListener(final ModelListener listener) {
		_listeners.add(listener);
	}
	
	public void removeListener(final ModelListener listener) {
		_listeners.remove(listener);
	}
	
	public static int getOpponent(int player) {
		return player == SeaBattleProtocol.WHITE ? SeaBattleProtocol.BLACK : SeaBattleProtocol.WHITE;
	}
	
	public SeaBattleModel() {
		_bot = new SeaBattleRobot();
		_state = SeaBattleProtocol.GAME_NOT_STARTED;
		_condition = new SeaBattleCondition();
	}

	public void makeMove(SeaBattleMove move) {
        int result;

        if(!_condition.isMoveValid(move)) {
            fireAskMove();
            return;
        }
        
        result = _condition.doMove((SeaBattleMove) move);
        ((SeaBattleMove) move).setResult(result);
        fireMoveDone(move);
        _moveNumber++;
        
        switch(result){
        
		case SeaBattleProtocol.GAME_OVER:
			byte shooterSide = ((SeaBattleMove) move).getSide();
			_state = shooterSide;
			_condition.setWinner(shooterSide);
			fireGameOver(_condition.getField(shooterSide), _condition.getField((byte) getOpponent(shooterSide)),shooterSide);
			_condition = new SeaBattleCondition();
			break;
			
		case SeaBattleProtocol.SHOT:
		case SeaBattleProtocol.KILLED:
			if(_bot != null && ((SeaBattleMove) move).getSide() == _bot.getSide()){
				byte anside = (_bot.getSide() == WHITE) ? BLACK : WHITE;
				_bot.setChangedField(getHiddenField(_condition.getField(anside)));
			}		
			break;
			
		case SeaBattleProtocol.WIDE:
			if (_bot != null){
				if (((SeaBattleMove) move).getSide() == _bot.getSide()){
					byte anSide1 = (_bot.getSide() == WHITE) ? BLACK : WHITE;
					_bot.setChangedField(getHiddenField(_condition.getField(anSide1)));
				}
			}
			break;
        }
        if(result != SeaBattleProtocol.GAME_OVER)
        	fireAskMove();
    }

	private void fireGameOver(int[][] lastShooterField, int[][] anotherField,byte side) {
		for (int i = 0; i < _listeners.size(); i++)
			_listeners.get(i).gameOver(lastShooterField, anotherField,side);
	}

	public int[][] getHiddenField(int[][] fieldSrc){
		int SIZE = SeaBattleProtocol.SIZE;
		int[][] field = new int[SIZE][SIZE];
		
		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++){
				if (fieldSrc[i][j] == SeaBattleProtocol.ALIVE)
					field[i][j] = SeaBattleProtocol.EMPTY;
				else
					field[i][j] = fieldSrc[i][j];
			}
		return field;
	}
	
	public void newGame(){
		if (_state == SeaBattleProtocol.GAME_NOT_STARTED) {
			_state = SeaBattleProtocol.GAME_NOT_FINISHED;
			_condition.setWinner(0);
		}
		_moveNumber = 0;
//		fireGameStarted();
		_condition.setFieldsSet(0);
	}
	
	public int getWinner() {
		return _condition.getWinner();
	}
	
	//TODO
//	private void fireAskField(Address senderAddress) {
//		for (int i = 0; i < _listeners.size(); i++)
//			((ModelListener) _listeners.get(i)).askField(senderAddress);
//	}
	

	public int getMoveNumber() {
		return _moveNumber;
	}

	
	public int getMoverSide() {
		return _condition.getSide();
	}


	public void generateNewMove(byte playerSide) {
		if(_bot != null){
			int side = _bot.getSide();
			makeMove(_bot.doMove(playerSide));
			_bot.setSide((byte) side);
		}
	}

	
	public SeaBattleCondition getCondition() {
		return _condition;
	}

	
	public void askEndGame(boolean surrender, int opposite) {
		// TODO Auto-generated method stub
	}
	
	public void checkField(int[][] field, byte side) {
		if(SeaBattleCondition.CheckComplect(field)){
			_condition.setField(field, side);
			byte anSide = (side == WHITE) ? BLACK : WHITE;

			if(_bot != null){
				_bot.setSide(anSide);
				_condition.setField(_bot.getRandomField(), anSide);
			}
			if(_condition.allFieldsAreSet() /*|| _bot != null*/){
				fireAskMove();//TODO ?!
			}
		}
		//TODO
//		else
//			fireAskField(cmd.getSenderAddress());
	}
	
//TODO copy from service
	public void setWinner(int side) {
		_condition.setWinner(side);
	}
	
	protected void fireGameOver() {
	    for (ModelListener listener : _listeners)
	        listener.gameOver(false);
	}

//	protected void fireGameStarted() {
//	    for (ModelListener listener : _listeners)
//	        listener.gameStarted();
//	}

	public void fireMoveDone(SeaBattleMove move) {
	    for (ModelListener listener : _listeners)
	        listener.moveDone(move);
	}

	protected void fireAskMove() {
		for (ModelListener listener : _listeners)
			listener.askMove(getMoveNumber() + 1);
	}


	public int getState() {
		return _state;
	}
	
	public void setState(int state) {
		_state = state;
	}

	public void startGame() {
		// TODO Auto-generated method stub
		
	}
}

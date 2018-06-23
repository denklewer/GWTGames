package seabattle.client;

import games.client.BaseController;
import games.client.BaseView;
import seabattle.shared.ModelListener;
import seabattle.shared.SeaBattleModel;
import seabattle.shared.SeaBattleMove;

public class SeaBattleController extends BaseController implements ModelListener {
	
	private final SeaBattleModel _model;
	private final SeaBattleView _view;
	
	public SeaBattleController(SeaBattleModel model, SeaBattleView view) {
		super(model);
		_view = view;
		_model = model;
	}

	@Override
	protected BaseView getView() {
		return _view;
	}

	public void init() {
	}

	@Override
	public void gameOver(int[][] lastShooterField, int[][] anotherField, byte side) {
	
	}

	@Override
	public void gameOver(boolean b) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public void gameStarted() {
//		//TODO
////		String name1 = getPlayer(Game42Protocol.WHITE).getName();
////		String name2 = getPlayer(Game42Protocol.BLACK).getName();
//		_view.startNewGame("name1", "name2");
//	}

	@Override
	public void moveDone(SeaBattleMove move) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void askMove(int i) {
//		_view.askMove();
		
	}

	public void sendAnswerMove(SeaBattleMove move, int moveNumber) {
//		if (cmd.getMoveNumber() != _game.getMoveNumber() + 1) {
//			_log.debug("Illegal moveNumber");
//			return;
//		}
//
//		int playerSide = getPlayerSide(cmd.getSenderAddress());
//
//		if (playerSide != 0) {
//			GamePlayer player = getPlayer(playerSide);
//			if (player.isHuman()) {
//				((MultiPlayerHuman) player).moveAnswered();
//			}
//			_game.makeMove(cmd.getMove());
//		}
		if (moveNumber != _model.getMoveNumber() + 1) {
//			_log.debug("Illegal moveNumber");
			return;
		}

		byte playerSide = move.getSide();

		if (playerSide != 0) {
//			GamePlayer player = getPlayer(playerSide);
//			if (player.isHuman()) {
//				_model.moveAnswered();
//			}
			_model.makeMove(move);
		}
		
	}

	public void startGame() {
		_model.newGame();
	}

	public void sendFieldMatrix(int[][] fieldMatrix, byte playerSide) {
		_model.checkField(fieldMatrix, playerSide);
		
	}

	@Override
	protected void start() {
		// TODO Auto-generated method stub
		
	}
	
	//
//	public void exec(CmdStart cmd){
//		((SeaBattleModel) _game).checkField(cmd, getPlayerSide(cmd.getSenderAddress()), getHumanCount());			
//	}
//	
//	public void exec (CmdAnswerMove cmd){
//		if (cmd.getMoveNumber() != _game.getMoveNumber() + 1) {
//			_log.debug("Illegal moveNumber");
//			return;
//		}
//
//		int playerSide = getPlayerSide(cmd.getSenderAddress());
//
//		if (playerSide != 0) {
//			GamePlayer player = getPlayer(playerSide);
//			if (player.isHuman()) {
//				((MultiPlayerHuman) player).moveAnswered();
//			}
//			_game.makeMove(cmd.getMove());
//		}
//	}
//	
//	public void gameStarted() {
//		super.gameStarted();
//		String name1 = getPlayer(Game42Protocol.WHITE).getName();
//		String name2 = getPlayer(Game42Protocol.BLACK).getName();
//		broadcast(new CmdStartNewGame(name1, name2));
//	}
//	
//	public void gameOver(int[][] lastShooterField, int[][] anotherField, byte side){
//		broadcast(new CmdSetState(lastShooterField, anotherField, side));
//		gameOver(false, _game.getWinner(), null);
//	}
//	
//	@SuppressWarnings("unchecked")
//	public void generateTurn(GamePlayer player, int moveNumber) {
//		_game.generateNewMove(getPlayerSide(player));
//	}
//
//	public void askField(Address senderAddress) {
//		deliver(senderAddress, new CmdMakeFieldAgain());
//	}

}

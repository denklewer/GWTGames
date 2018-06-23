package scrabble.shared;

import games.shared.GameModel;
import games.shared.RandomGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class ScrabbleModel implements GameModel<ModelListener>{

	private static final List<ScrabbleWord> EMPTY_WORD_LIST = new ArrayList<ScrabbleModel.ScrabbleWord>();
	
	private static class PlayerWithMaxScore{
		private byte _side;
		private int _score;
		
		public PlayerWithMaxScore() {
		}
		
		public byte getSide() {
			return _side;
		}
		public int getScore() {
			return _score;
		}
		public void setSide(byte side) {
			_side = side;
		}
		public void setScore(int score) {
			_score = score;
		}
	}
	
	static class ScrabbleWordLocation{
		String _x;
		String _y;
		@Override
		public String toString() {
			return _x + _y;
		}
	}
	
	static class ScrabbleWord {
		private String _word;
		private String _markedWord;
		private ScrabbleWordLocation _location;
		public ScrabbleWord() {
		}
		public ScrabbleWord(String word, String markedWord, ScrabbleWordLocation location) {
			_word = word;
			_markedWord = markedWord;
			_location = location;
		}
		public String getWord() {
			return _word;
		}
		public String getMarkedWord() {
			return _markedWord;
		}
		public ScrabbleWordLocation getLocation() {
			return _location;
		}
	}
	
	private String[] LETTERS = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o"};
	
	private static final PlayerWithMaxScore DUMMY_PLAYER_WITH_MAX_SCORE = new PlayerWithMaxScore();
	
	private List<ModelListener> _listeners = new ArrayList<ModelListener>();

	private ScrabbleBoard _board;
	private ScrabbleBag _bag;
	
	private ArrayList<ScrabbleCell> _cells = new ArrayList<ScrabbleCell>();
	
	protected Map<Byte, ScrabblePlayer> _players = new HashMap<Byte, ScrabblePlayer>();
	protected Map<Byte, ScrabblePlayer> _savedPlayers = new HashMap<Byte, ScrabblePlayer>();
	
	protected byte _whoseTurn;
	private List<Byte> _sides = new ArrayList<Byte>();
	private int _tempscore;
	
	private PlayerWithMaxScore _playerWithMaxScore = DUMMY_PLAYER_WITH_MAX_SCORE;
	
	private List<String> _currentMatchHistoryForWatcher = new ArrayList<String>();
	
	private boolean _gameStarted = false;
	
	private ScrabbleSettings _settings;

	private final ScrabbleDictionary _scrabbleDictionary;
	
	public ScrabbleModel(RandomGenerator randomGenerator, ScrabbleBoard board, ScrabbleBag bag, ScrabbleDictionary scrabbleDictionary) {
		_scrabbleDictionary = scrabbleDictionary;
		cancelWaitingPlayerSchedule();
		_board = board;
		_bag = bag;
		_settings = new FakeSettings();
		printInfo("Create ScrabbleModel");
	}

	public void addListener(ModelListener listener) {
		_listeners.add(listener);
	}
	
	public void addPlayer(byte side) {
		addPlayer(side, true);
	}
	
	public void addPlayer(byte side, boolean firstGame) {
		Byte playerSavedSide = playerSavedSide();
		if(!canAddPlayer(side) && playerSavedSide == -1){
			return;
		}
		try{
			if(addingFirstPlayer()){
				int numOfJokers = _settings.numOfJokers();
				_bag.addJokers(numOfJokers);
			}
			ScrabblePlayer player = null;
			boolean isNewPlayer = playerSavedSide == -1;
			if(isNewPlayer){
				player = createServerScrabblePlayer(side);
				_savedPlayers.remove(side);
			} else{
//				player = _savedPlayers.remove(playerSavedSide);
//				player.setTransmitter(tr);
//				player.setSide(side);
			}
			
			if(player == null){
				return;
			}

			_players.put(side, player);
			if(!_sides.contains(side)){
				_sides.add(side);
			}
			printInfo("Add player: " + player.getName() + " to side " + side);
			
			if(checkReadyToStartAndSendInfo()){
				sendStandInfo(side, player);
				fireBagSize();
				if (!_gameStarted) {
					setWhoStartGame(side);
					createWaitingPlayerTask();
					fireMoveMadeInfo("Новая игра " + new Date() + ":");
					_gameStarted = true;
				}
				sendGamerAdded(side, firstGame);
				if(player.getSide() == _whoseTurn){
					fireTurnSwitched(_whoseTurn);
				}
			}
			
		} finally{
//			_playersByIp.storeIp(tr);
		}
	}

	protected void sendGamerAdded(byte side, boolean firstGame) {
		fireGamerAdded(side, firstGame);
	}

	protected void sendStandInfo(byte side, ScrabblePlayer player) {
		fireStandFilled(side, player.getStand().getDiceOfGamer());
	}
	
	protected void setWhoStartGame(byte side){
		_whoseTurn = side;
	}

	protected boolean checkReadyToStartAndSendInfo() {
		return true;
	}

	private ScrabblePlayer createServerScrabblePlayer(byte side) {
		ScrabbleStand stand = new ScrabbleStand(_bag);
		return new ScrabblePlayer(stand, side, "");
	}
	
	private boolean addingFirstPlayer(){
		return _sides.isEmpty() && _savedPlayers.isEmpty();
	}
	
	Byte playerSavedSide() {
//		Set<Byte> keys = _savedPlayers.keySet();
//		for (Byte key : keys) {
//			ServerScrabblePlayer player = _savedPlayers.get(key);
//			if(player.getTransmitter().getUserID() == userID){
//				return key;
//			}
//		}
		return -1;
	}

	private boolean canAddPlayer(byte side) {
//		String userName = tr.getUserName();
//		Byte playerSavedSide = playerSavedSide(tr.getUserID());
//		boolean isNewPlayer = playerSavedSide == -1;
//		if(_players.get(side) != null && playerSavedSide != side){
//			printInfo(userName + " trying to sit on " + side + " side! Oops! This side is busy!");
//			return false;
//		}
//		if(alreadyAddedToPlayers(tr) && isNewPlayer){
//			printInfo(userName + " trying to sit on " + side + " but player has already added to players!!");
//			return false;
//		}
		if(side < 0 || side >= ScrabbleProtocol._MAX_OF_GAMERS){
			printInfo("Can't add to side " + side);
			return false;
		}
//		Transmitter sameIpAlreadySits = _playersByIp.checkOnSameIpAndInformUser(tr);
//		if(sameIpAlreadySits != null){
//			printInfo("Can't add player " + userName + " because " + sameIpAlreadySits.getUserName() + " with same ip already sits!");
//			return false;
//		}
		return true;
	}

	private boolean alreadyAddedToPlayers() {
//		Set<Byte> keySet = _players.keySet();
//		for (Byte key : keySet) {
//			if(_players.get(key).getTransmitter() == tr){
//				return true;
//			}
//		}
		return false;
	}

	protected void fireGamerAdded(byte side, boolean firstGame) {
		for (ModelListener listener : _listeners) {
			listener.gamerAdded(side);
		}
	}

	private int removeGamer(byte side) {
		ScrabblePlayer player = _players.get(side);
		int score = player.getScore();
		if(side == _whoseTurn){
			// TODO fire clear board
//			_server.clearBoard(player.getTransmitter());
		}
		removeFromPlayersAndSwitchTurn(side);
		_savedPlayers.put(side, player);
		return score;
	}

	protected void removeFromPlayersAndSwitchTurn(byte side) {
		ScrabblePlayer player = _players.remove(side);
		if (!gameWithTimeout() && _whoseTurn == side && _players.size() > 0) {
			switchTurn();
		}
		_sides.remove(new Byte(side));
		printInfo("Remove player: " + player.getName() + " sitting on side " + side + "!");
		fireGamerRemoved(side);
	}
	
	private boolean gameWithTimeout() {
//		return _server.getTimeForMoveInSeconds() > 0;
		return false;
	}

	public int removePlayer() {
//		for (ServerScrabblePlayer player : _players.values()) {
//			if (player.getTransmitter() == tr){
//				_playersByIp.forgetIp(tr);
//				return removeGamer(player.getSide());
//			}
//		}
		return removeGamer((byte) 0);
	}
	
	private String getNames() {
		String names = "";
//		for (Transmitter player : players) {
//			names += player.getUserName() + " ";
//		}
		return names;
	}

	private void fireGamerRemoved(byte side) {
		for (ModelListener listener : _listeners) {
//			listener.gamerRemoved(side);
		}
	}
	
	private void fireMoveMadeInfo(String info) {
		for (ModelListener listener : _listeners) {
			listener.addMoveMadeInfo(info);
		}
	}

	public void makeMove() {
		ArrayList<ScrabbleCell> cells = (ArrayList<ScrabbleCell>) _cells.clone();
		int cellsSize = cells.size();
		List<ScrabbleWord> move = createMove();
		if (move != EMPTY_WORD_LIST) {
			_board.add(new Vector(cells));
			fireMoveMade();
			ScrabblePlayer gamer = _players.get(_whoseTurn);
			int playerCells = gamer.getStand().getDiceOfGamer().size();
			
			gamer.addScore(_tempscore);
			String gamerName = gamer.getName();
			printInfo(gamerName + " make correct move! Player use " + cellsSize + " from " + playerCells + " cells! Added score: " + _tempscore + ", total gamer score = " + gamer.getScore());
			fireScoreChanged(_tempscore, _whoseTurn);
			sendInfoAboutMadeWords(gamer, move);
			if(cellsSize == ScrabbleProtocol.DIES_IN_STAND){
				String systemMessage = gamerName + " использовал(а) все свои буквы за ход и получает бонус " + ScrabbleProtocol.BONUS_FOR_USE_ALL_CELLS + " очков!";
				String description = " used all cells! Added bonus: ";
				addBonusScore(gamer, ScrabbleProtocol.BONUS_FOR_USE_ALL_CELLS, description , systemMessage);
			}
			// check on current max score
			if(_playerWithMaxScore.getScore() < gamer.getScore()){
				_playerWithMaxScore.setSide(gamer.getSide());
				_playerWithMaxScore.setScore(gamer.getScore());
			}
			ScrabbleStand stand = gamer.getStand();
			
			String prevDices = dicesToString(stand.getDiceOfGamer());
			
			Vector<ScrabbleDie> dice = getDice();
			stand.removeDice(dice);
			Vector<ScrabbleDie> newDice = stand.fillScrabbleStand();
			//TODO: check if it is correct
			_cells.clear();
			String newDices = dicesToString(gamer.getStand().getDiceOfGamer());
			String moveInfo = generateMoveInfo(gamer, move, prevDices, newDices);
			useMoveInfo(moveInfo);
			printInfo(moveInfo);
			if (stand.getDiceOfGamer().size() == 0) {
				endGame("reason_win", true);
				return;
			}
			fireStandFilled(_whoseTurn, newDice);
			fireBagSize();
			gamer.clearPass();
			gamer.clearChangeLetters();
			switchTurn();
		} else {
			fireTurnSwitched(_whoseTurn);
		}
		_tempscore = 0;
	}
	
	protected void useMoveInfo(String moveInfo){
		// to override
	}
	
	private String dicesToString(Vector<ScrabbleDie> diceOfGamer) {
		String result = "";
		for (int i = 0; i < diceOfGamer.size(); i++) {
			result += (diceOfGamer.get(i).getLetter() + "").toUpperCase();
		}
		return result;
	}

	private String generateMoveInfo(ScrabblePlayer gamer,	List<ScrabbleWord> move, String prevDices, String newDices) {
		if(move.isEmpty()){
			return "";
		}
//		int userID = gamer.getTransmitter().getUserID();
		String userName = gamer.getName();
		String result = userName /* + "(" + userID + ") " */;
		ScrabbleWord scrabbleWord = move.get(0);
		result += scrabbleWord.getMarkedWord() + " " + scrabbleWord.getLocation() + " +" + _tempscore + " " + gamer.getScore();
		for (int i = 1; i < move.size(); i++) {
			result += " " + move.get(i).getMarkedWord();
		}
		result += " " + prevDices + " " + newDices;
		return result;
	}

	private void sendInfoAboutMadeWords(ScrabblePlayer gamer, List<ScrabbleWord> move) {
		String message = gamer.getName() + " составил(а) слово(а)";
		for (int i = 0; i < move.size(); i++) {
			ScrabbleWord scrabbleWord = move.get(i);
			String word = scrabbleWord.getMarkedWord();
			message += " " + word + " " + scrabbleWord.getLocation();
			if(i == 0){
				word += " ---";
			}
		}
		message += " и получил(а) " + _tempscore + " очков! Общее количество очков " + gamer.getScore();
		fireMoveMadeInfo(message);
	}
	
	public void cancelWaitingPlayerSchedule(){
//		if(_waitingPlayerSchedule != null){
//			_waitingPlayerSchedule.cancel(true);
//			_server.broadcast(new CmdRemoveTimerForMove());
//		}
	}
	
	private void createWaitingPlayerTask() {
//		int delay = _server.getTimeForMoveInSeconds();
//		if(delay <= 0){
//			return;
//		}
//		Runnable waitingPlayerTask = new Runnable() {
//			
//			@Override
//			public void run() {
//				ServerScrabblePlayer player = _players.get(_whoseTurn);
//				if(player != null){
//					_server.broadcastSystemMessage(timeoutString(player.getName()));
//					Transmitter tr = player.getTransmitter();
//					_server.doPlayerPass(tr, true);
//				}
//			}
//
//		};
//		_waitingPlayerSchedule = schedule(waitingPlayerTask, delay, TimeUnit.SECONDS);
//		_server.broadcast(new CmdAddTimerForMove(delay));
	}
	
	private String timeoutString(String playerName) {
		return playerName + ", время на ход закончилось!";
	}
	
	public long getTimerForMoveDelay(){
//		return (_waitingPlayerSchedule != null && !_waitingPlayerSchedule.isCancelled()) ? _waitingPlayerSchedule.getDelay(TimeUnit.SECONDS) : 0;
		return 0;
	}

	private void addBonusScore(ScrabblePlayer gamer, int bonus, String description, String systemMessage){
		String gamerName = gamer.getName();
		gamer.addScore(bonus);
		String gameInfo = gamerName + description + bonus + ", total gamer score = " + gamer.getScore();
		printInfo(gameInfo);
		useMoveInfo(gameInfo);
		fireMoveMadeInfo(systemMessage +  " Общее количество очков " + gamer.getScore());
		fireScoreChanged(bonus, _whoseTurn);
	}

	private void fireDiceFromStandRemoved(byte side, Vector dice) {
		for (ModelListener listener : _listeners) {
//			listener.diceFromStandRemoved(side, dice);
		}
	}

	public Vector<ScrabbleDie> getDice() {
		Vector<ScrabbleDie> result = new Vector<ScrabbleDie>();
		for (ScrabbleCell cell : _cells) {
			result.add(cell.getDie());
		}
		return result;
	}

	private void fireScoreChanged(int score, byte side) {
		for (ModelListener l : _listeners) {
			l.scoreChanged(score, side);
		}
	}

	private void switchTurn() {
		cancelWaitingPlayerSchedule();
		String prevName = getWhoseTurnName();
		printInfo("Switch turn! Prev whose turn --> " + prevName + ", side = " + _whoseTurn);
		int i = _sides.indexOf(_whoseTurn);
		i = (i + 1) % _sides.size();
		_whoseTurn = _sides.get(i);
		String newName = getWhoseTurnName();
		printInfo("Switch turn! New whose turn --> " + newName + ", side = " + _whoseTurn);
		fireTurnSwitched(_whoseTurn);
		createWaitingPlayerTask();
	}

	private String getWhoseTurnName() {
		ScrabblePlayer prevPlayer = _players.get(_whoseTurn);
		String prevName = "";
		if(prevPlayer != null){
			prevName = prevPlayer.getName();
		}
		return prevName;
	}

	private void fireTurnSwitched(byte turn) {
		for (ModelListener listener : _listeners) {
			listener.turnSwitched(turn);
		}
	}

	private void fireMoveMade() {
		for (ModelListener listener : _listeners) {
			listener.moveMade();
		}
	}

	protected void fireStandFilled(byte side, Vector<ScrabbleDie> newDice) {
		Vector<ScrabbleDie> copy = new Vector<ScrabbleDie>();
		for (int i = 0; i < newDice.size(); i++) {
			copy.add(newDice.get(i).copy());
		}
		for (ModelListener listener : _listeners) {
			listener.standFilled(side, copy);
		}
	}
	
	private void fireBagSize() {
		for (ModelListener listener : _listeners) {
			listener.bagSizeChanged(_bag.size());
		}
	}
	
	public boolean checkMove(){
		return createMove() != EMPTY_WORD_LIST;
	}

	private List<ScrabbleWord> createMove() {
		List<ScrabbleCell> cells = (List<ScrabbleCell>) _cells.clone();
		if (cells.size() == 0) {
			return EMPTY_WORD_LIST;
		}
		
		if (!checkDiceInStand(cells, _players.get(_whoseTurn).getStand())) {
			return EMPTY_WORD_LIST;
		}
		
		if (!checkDiceOnDice(cells)) {
			return EMPTY_WORD_LIST;
		}
		
		List<ScrabbleWord> checkHorizontally = checkHorizontally(cells);
		if (checkHorizontally != EMPTY_WORD_LIST) {
			return checkHorizontally;
		}
		
		List<ScrabbleWord> checkVertically = checkVertically(cells);
		if (checkVertically != EMPTY_WORD_LIST) {
			return checkVertically;
		}
		
		return EMPTY_WORD_LIST;
	}

	private boolean checkDiceInStand(List<ScrabbleCell> cells, ScrabbleStand scrabbleStand) {
		Vector dice = (Vector) scrabbleStand.getDiceOfGamer().clone();
		for (ScrabbleCell scrabbleCell : cells) {
			if (!dice.contains(scrabbleCell.getDie())) {
				return false;
			}
			dice.remove(scrabbleCell.getDie());
		}
		return true;
	}

	private List<ScrabbleWord> checkVertically(List<ScrabbleCell> cells) {
		Collections.sort(cells, new CellYComparator());
		if(checkCol(cells)) {
			addToHeadVer(cells);
			addToTailVer(cells);
			if(fillBodyVer(cells)) {
				if (!(hasJoinedCells(cells) || (_board.firstMove() && hasStar(cells)))) {
					return EMPTY_WORD_LIST;
				}
				List<ScrabbleWord> words = makeCollectionOfWordsToVer(cells);
				if (checkWords(words)) {
					return words;
				}
			}
		}
		return EMPTY_WORD_LIST;
	}

	private List<ScrabbleWord> checkHorizontally(List<ScrabbleCell> cells) {
		Collections.sort(cells, new CellXComparator());
		if (checkRow(cells)) {
			addToHeadHor(cells);
			addToTailHor(cells);
			if(fillBodyHor(cells)) {
				if (!(hasJoinedCells(cells) || (_board.firstMove() && hasStar(cells)))) {
					return EMPTY_WORD_LIST;
				}
				List<ScrabbleWord> words = makeCollectionOfWordsToHor(cells);
				if (checkWords(words)) {
					return words;
				} 
			}
		}
		return EMPTY_WORD_LIST;
	}

	private boolean hasStar(List<ScrabbleCell> cells) {
		for (ScrabbleCell scrabbleCell : cells) {
			if (scrabbleCell.getCoord().x ==_board.getWidth() / 2 && 
					scrabbleCell.getCoord().y == _board.getHeight() / 2) {
				return true;
			}
		}
		return false;
	}

	private boolean hasJoinedCells(List<ScrabbleCell> cells) {
		for (ScrabbleCell scrabbleCell : cells) {
			if (!scrabbleCell.isNew())
				return true;
		}
		return false;
	}

	private List<ScrabbleWord> makeCollectionOfWordsToVer(
			List<ScrabbleCell> cells) {
		List<ScrabbleWord> result = new ArrayList<ScrabbleWord>();
		ScrabbleWord mainWord = makeWord(cells, false);
		result.add(mainWord);
		for (ScrabbleCell scrabbleCell : cells) {
			if (!scrabbleCell.isNew())
				continue;
			List<ScrabbleCell> cellsList = new ArrayList<ScrabbleCell>();
			cellsList.add(scrabbleCell);
			addToHeadHor(cellsList);
			addToTailHor(cellsList);
			if (cellsList.size() > 1) {
				result.add(makeWord(cellsList, false));
			}
		}
		return result;
	}

	private boolean checkWords(Collection<ScrabbleWord> words) {
		for (ScrabbleWord wordWithMarks : words) {
			String word = wordWithMarks.getWord();
			if (!_scrabbleDictionary.searchWord(word))
				return false;
		}
		return words.size() > 0;
	}

	private List<ScrabbleWord> makeCollectionOfWordsToHor(List<ScrabbleCell> cells) {
		List<ScrabbleWord> result = new ArrayList<ScrabbleWord>();
		ScrabbleWord mainWord = makeWord(cells, true);
		result.add(mainWord);
		for (ScrabbleCell scrabbleCell : cells) {
			if (!scrabbleCell.isNew())
				continue;
			List<ScrabbleCell> cellsList = new ArrayList<ScrabbleCell>();
			cellsList.add(scrabbleCell);
			addToHeadVer(cellsList);
			addToTailVer(cellsList);
			if (cellsList.size() > 1) {
				result.add(makeWord(cellsList, true));
			}
		}
		return result;
	}

	private ScrabbleWord makeWord(List<ScrabbleCell> cells, boolean horWord) {
		if(cells.isEmpty()){
			return new ScrabbleWord();
		}
		int score = 0;
		String markedWord = "";
		String word = "";
		int wordMultiplier = 1;
		ScrabbleCell firstLetter = null;
		for (ScrabbleCell scrabbleCell : cells) {
			if(firstLetter == null){
				firstLetter = scrabbleCell;
			}
			ScrabbleDie die = scrabbleCell.getDie();
			if(!scrabbleCell.isNew()){
				markedWord += "(";
			}
			if(die instanceof JokerScrabbleDie){
				markedWord += ((JokerScrabbleDie) die).getSelectedLetter();
				word += ((JokerScrabbleDie) die).getSelectedLetter();
			} else{
				String letter = die.getLetter() + "";
				markedWord += letter.toUpperCase();
				word += die.getLetter();
			}
			if(!scrabbleCell.isNew()){
				markedWord += ")";
			}
			int letterMultiplier = 1;
			if (isBonusWorld(scrabbleCell)) {
				wordMultiplier *= scrabbleCell.getPoint();
			} 
			if(isBonusLetter(scrabbleCell)) {
				letterMultiplier = scrabbleCell.getPoint();
			}
			score += letterMultiplier * die.getPoint();
		}
		score *= wordMultiplier;
		_tempscore += score;
		ScrabbleWordLocation location = new ScrabbleWordLocation();
		if(horWord){
			location._x = (firstLetter.getCoord().y + 1) + "";
			location._y = LETTERS[firstLetter.getCoord().x];
		} else{
			location._x = LETTERS[firstLetter.getCoord().x];
			location._y = (firstLetter.getCoord().y + 1) + "";
		}
		return new ScrabbleWord(word, markedWord, location);
	}
	
	private boolean isBonusWorld(ScrabbleCell scrabbleCell){
		return isBonusInThisGame(scrabbleCell, ScrabbleProtocol.BONUS_WORD);
	}
	
	private boolean isBonusLetter(ScrabbleCell scrabbleCell){
		return isBonusInThisGame(scrabbleCell, ScrabbleProtocol.BONUS_LETTER);
	}
	
	private boolean isBonusInThisGame(ScrabbleCell scrabbleCell, int bonusType){
		if(scrabbleCell.getTypeOfBonus() != bonusType){
			return false;
		}
		if(!scrabbleCell.isNew() && _settings.isClassicGame()){
			return false;
		}
		return true;
	}

	private boolean fillBodyVer(List<ScrabbleCell> cells) {
		int prevY = cells.get(0).getCoord().y;
		for (int i = 1; i < cells.size(); i++) {
			ScrabbleCell cell = cells.get(i);
			int curX = cell.getCoord().y;
			if(curX - prevY > 1) {
				int k = i;
				for(int j = prevY + 1; j < curX; j++) {
					ScrabbleCell ins = _board.getCell(cell.getCoord().x, j);
					if (ins.getDie() == null) {
						return false;
					} 
					cells.add(k++, ins);
				}
			}
			prevY = curX;
		}
		return true;
	}

	private void addToTailVer(List<ScrabbleCell> cells) {
		int x = cells.get(cells.size() - 1).getCoord().x;
		int y = cells.get(cells.size() - 1).getCoord().y;
		ScrabbleCell cell;
		while((cell = _board.getCell(x, ++y)) != null && cell.getDie() != null) {
			cells.add(cell);
		}		
	}

	private void addToHeadVer(List<ScrabbleCell> cells) {
		int x = cells.get(0).getCoord().x;
		int y = cells.get(0).getCoord().y;
		ScrabbleCell cell;
		while((cell = _board.getCell(x, --y)) != null && cell.getDie() != null) {
			cells.add(0, cell);
		}		
	}

	private boolean checkCol(List<ScrabbleCell> cells) {
		int x = cells.get(0).getCoord().x;
		for (ScrabbleCell scrabbleCell : cells) {
			if (x != scrabbleCell.getCoord().x) {
				return false;
			}
		}
		return true;
	}

	private boolean checkDiceOnDice(List<ScrabbleCell> cells) {
		for (ScrabbleCell scrabbleCell : cells) {
			if(_board.getCell(scrabbleCell.getCoord().x, scrabbleCell.getCoord().y).getDie() != null)
				return false;
		}
		return true;
	}

	private boolean fillBodyHor(List<ScrabbleCell> cells) {
		int prevX = cells.get(0).getCoord().x;
		for (int i = 1; i < cells.size(); i++) {
			ScrabbleCell cell = cells.get(i);
			int curX = cell.getCoord().x;
			if(curX - prevX > 1) {
				int k = i;
				for(int j = prevX + 1; j < curX; j++) {
					ScrabbleCell ins = _board.getCell(j, cell.getCoord().y);
					if (ins.getDie() == null) {
						return false;
					} 
					cells.add(k++, ins);
				}
			}
			prevX = curX;
		}
		return true;
	}

	private void addToTailHor(List<ScrabbleCell> cells) {
		int x = cells.get(cells.size() - 1).getCoord().x;
		int y = cells.get(cells.size() - 1).getCoord().y;
		ScrabbleCell cell;
		while((cell = _board.getCell(++x, y)) != null && cell.getDie() != null) {
			cells.add(cell);
		}
	}

	private void addToHeadHor(List<ScrabbleCell> cells) {
		int x = cells.get(0).getCoord().x;
		int y = cells.get(0).getCoord().y;
		ScrabbleCell cell;
		while((cell = _board.getCell(--x, y)) != null && cell.getDie() != null) {
			cells.add(0, cell);
		}
	}

	private boolean checkRow(List<ScrabbleCell> cells) {
		int y = cells.get(0).getCoord().y;
		for (ScrabbleCell scrabbleCell : cells) {
			if (y != scrabbleCell.getCoord().y) {
				return false;
			}
		}
		return true;
	}

	public void changeDice(byte side, Vector<ScrabbleDie> dice) {
		ScrabblePlayer player = _players.get(side);
		ScrabbleStand stand = player.getStand();
		Vector<ScrabbleDie> diceOfGamer = stand.getDiceOfGamer();
		String prevDices = dicesToString(diceOfGamer);
		stand.removeDice(dice);
//		fireDiceFromStandRemoved(side, dice);
		
		_bag.addDice(dice);
		Vector<ScrabbleDie> newDice = stand.fillScrabbleStand();
		fireStandFilled(side, newDice);
		player.clearPass();
		player.incChangeLetters();
		fireMoveMadeInfo((player.getName() + " поменял(а) буквы!"));
		if (checkEndOfGame()) {
			endGame("reason_change_letters");
		} else {
			printInfo(getWhoseTurnName() + " changed dices!");
			switchTurn();
		}
		diceOfGamer = stand.getDiceOfGamer();
		String changeDiceString = generateChangeDiceString(player, prevDices, newDice);
		useMoveInfo(changeDiceString);
		printInfo(changeDiceString);
	}

	private String generateChangeDiceString(ScrabblePlayer player, String prevDices, Vector<ScrabbleDie> newDice) {
		String changeDiceString = player.getName() /* + "(" + player.getTransmitter().getUserID() + ") " */;
		changeDiceString += "exchange " + prevDices + " --> ";
		Vector<ScrabbleDie> diceOfGamer = player.getStand().getDiceOfGamer();
		for (int i = 0; i < diceOfGamer.size(); i++) {
			if(newDice.contains(diceOfGamer.get(i))){
				changeDiceString += (diceOfGamer.get(i).getLetter() + "").toUpperCase();
			} else{
				changeDiceString += ("(" + diceOfGamer.get(i).getLetter() + ")").toUpperCase();
			}
		}
		return changeDiceString;
	}

	public Vector<ScrabblePlayer> getPlayers() {
		Vector<ScrabblePlayer> result = new Vector<ScrabblePlayer>();
		for (ScrabblePlayer player : _players.values()) {
			result.add(player);
		}
		return result;
	}
	
	public Vector<ScrabblePlayer> getRealServerPlayers() {
		Vector<ScrabblePlayer> result = new Vector<ScrabblePlayer>();
		Set<Byte> keys = _players.keySet();
		for (Byte key : keys) {
			ScrabblePlayer player = _players.get(key);
			if(!_savedPlayers.containsKey(key)){
				result.add(player);
			}
		}
		return result;
	}
	
	public int getNumOfPlayers(){
		return _players.size();
	}
	
	public Vector<ScrabblePlayer> getServerScrabblePlayers() {
		Vector<ScrabblePlayer> result = new Vector<ScrabblePlayer>();
		for (ScrabblePlayer player : _players.values()) {
			result.add(player);
		}
		return result;
	}
	
	public ScrabblePlayer getServerScrabblePlayer(int side) {
		for (ScrabblePlayer player : _players.values()) {
			if(player.getSide() == side){
				return player;
			}
		}
		return null;
	}
	
	public String getSrabblePlayerName(byte side){
		ScrabblePlayer player = getServerScrabblePlayer(side);
		return player == null ? "" : player.getName();
	}

	public void pass(byte playerSide, boolean byTimeout) {
		if (playerSide != _whoseTurn) 
			return;
		ScrabblePlayer player = _players.get(_whoseTurn);
		player.incPass();
//		if(!byTimeout){
//			_server.sendMoveMadeInfo((player.getName() + " сделал(а) пас!"));
//		} else {
//			_server.sendMoveMadeInfo(timeoutString(player.getName()));
//			_server.deliver(player.getTransmitter(), new CmdDoAutoPass());
//		}
		String passString = player.getName() + /* "(" + player.getTransmitter().getUserID() + ") */ "pass";
		if(byTimeout){
			passString += " by timeout";
		}
		Vector<ScrabbleDie> diceOfGamer = player.getStand().getDiceOfGamer();
		passString += " (letters --> ";
		for (int i = 0; i < diceOfGamer.size(); i++) {
			passString += (diceOfGamer.get(i).getLetter() + "").toUpperCase();
		}
		passString += ")";
		useMoveInfo(passString);
		printInfo(passString);
		if (checkEndOfGame()) {
			endGame("reason_double_pass");
		} else {
			printInfo(getWhoseTurnName() + " did pass!");
			switchTurn();
		}
	}

	private void endGame(String reason) {
		endGame(reason, false);
	}
	
	private void endGame(String reason, boolean winnerUsedAllLetters) {
		cancelWaitingPlayerSchedule();
		printInfo(reason);
		Map<Byte, Integer> scoreBeforeReducing = saveScoreBeforeReducing();
		if(winnerUsedAllLetters){
			int bonus = reduceScoreForPlayersAndGetBonusForPlayerUsedAllLetters(_whoseTurn);
			ScrabblePlayer player = _players.get(_whoseTurn);
			String systemMessage = player.getName() + " получил(а) бонус " + bonus + " очков!";
			String description = " Added bonus: ";
			addBonusScore(player, bonus, description , systemMessage);
		} else{
			reduceScoreForPlayers();
		}
		List<Byte> winners = searchWinners(scoreBeforeReducing);
		byte winnerSide;
		if(winners.size() == 1){
			winnerSide = winners.get(0);
		} else{
			winnerSide = _playerWithMaxScore.getSide();
		}
		actionsWithWinner(winnerSide);
		fireGameEnded(reason, winnerSide);
		actionsWithPlayers();
	}

	protected void actionsWithPlayers() {
		// to override
	}

	protected void actionsWithWinner(byte winnerSide) {
		// to override
	}

	private List<Byte> searchWinners(Map<Byte, Integer> scoreBeforeReducing) {
		Map<Byte, Integer> scoresBySide = new HashMap<Byte, Integer>();
		Set<Byte> sides = _players.keySet();
		for (Byte side : sides) {
			ScrabblePlayer player = _players.get(side);
			scoresBySide.put(side, player.getScore());
		}
		List<Byte> winners = searchWinnerSides(scoresBySide);
		if(winners.size() > 1){
			winners = searchWinnerSides(scoreBeforeReducing);
		}
		return winners;
	}

	private List<Byte> searchWinnerSides(Map<Byte, Integer> scoreBySide) {
		List<Byte> winners = new ArrayList<Byte>();
		int maxScore = findMaxScore(scoreBySide);
		Set<Byte> keySet = scoreBySide.keySet();
		for (Byte side : keySet) {
			if(_players.get(side).getScore() == maxScore){
				winners.add(side);
			}
		}
		return winners;
	}

	private int findMaxScore(Map<Byte, Integer> scoreBySide) {
		int maxScore = 0;
		for (Integer score : scoreBySide.values()) {
			if(maxScore < score){
				maxScore = score;
			}
		}
		return maxScore;
	}

	private Map<Byte, Integer> saveScoreBeforeReducing() {
		Map<Byte, Integer> scoreBeforeReducing = new HashMap<Byte, Integer>();
		Set<Byte> keySet = _players.keySet();
		for (Byte side : keySet) {
			ScrabblePlayer player = _players.get(side);
			if(player != null){
				scoreBeforeReducing.put(side, player.getScore());
			}
		}
		return scoreBeforeReducing;
	}
	
	private void reduceScoreForPlayers(){
		reduceScoreForPlayersAndGetBonusForPlayerUsedAllLetters((byte) -1);
	}
	
	private int reduceScoreForPlayersAndGetBonusForPlayerUsedAllLetters(byte winnerSide){
		Set<Byte> keySet = _players.keySet();
		int bonus = 0;
		for (Byte side : keySet) {
			ScrabblePlayer player = _players.get(side);
			if(player != null && winnerSide != side){
				Vector<ScrabbleDie> dices = player.getStand().getDiceOfGamer();
				// antiBonus is negative
				int antiBonus = 0;
				String antiBonusString = "";
				for (ScrabbleDie die : dices) {
					int points = die.getPoint();
					bonus += points;
					antiBonus -= points;
					antiBonusString += die.getLetter() + "(" + points + ") ";
				}
				String systemMessage = player.getName() + " получил(а) анти-бонус " + antiBonus + " очков за несыгранные кости: " + antiBonusString + "!";
				String description = " Added anti-bonus: ";
				addBonusScore(player, antiBonus, description , systemMessage);
			}
		}
		return bonus;
	}

	private void fireGameEnded(String reason, byte winnerSide) {
		for (ModelListener listener : _listeners) {
			listener.gameEnded(reason, winnerSide);
		}
	}

	private boolean checkEndOfGame() {
		if(_players.isEmpty()){
			return true;
		}
		for (ScrabblePlayer player : _players.values()) {
			if (player.getPass() < 2 && player.getChangeLetters() < 3){
				return false;
			}
		}
		return true;
	}
	
	public ScrabbleBoard getBoard() {
		return _board;
	}

	public Vector getStand(byte side) {
		return _players.get(side).getStand().getDiceOfGamer();
	}

	public boolean checkTurn(byte playerSide) {
		return _whoseTurn == playerSide;
	}

	public void clear() {
		_tempscore = 0;
	}

	public byte getTurn() {
		return _whoseTurn;
	}

	public void removeCell(ScrabbleCell cell) {
		_cells.remove(cell);
	}
	
	public void addCell(ScrabbleCell cell) {
		_cells.add(cell);
	}
	
	public void eraseCells() {
		_cells.clear();
	}
	
	public List<ScrabbleCell> getCells() {
		return _cells;
	}

	public ScrabblePlayer getScrabblePlayer(byte turn) {
		return _players.get(turn) == null ? null : _players.get(turn);
	}

	public byte getPlayerSide() {
//		Set<Byte> keys = _players.keySet();
//		for (Byte key : keys) {
//			if(_players.get(key).getTransmitter() == tr){
//				return _players.get(key).getSide();
//			}
//		}
		for (ScrabblePlayer player : _players.values()) {
			return player.getSide();
		}
		return 0;
	}
	
	public int getPlayerScore(){
//		Set<Byte> keys = _players.keySet();
//		for (Byte key : keys) {
//			if(_players.get(key).getTransmitter() == tr){
//				return _players.get(key).getScore();
//			}
//		}
		for (ScrabblePlayer player : _players.values()) {
			return player.getScore();
		}
		return 0;
	}

	public void addWatcher() {
//		_watchers.add(tr);
//		printInfo("Added watcher --> " + tr.getUserName() + ", current watchers: " + getNames(_watchers));
	}
	
	public boolean canAddWatcher(){
//		return !_watchers.contains(tr);
		return true;
	}

	public boolean needToRemoveTable() {
//		printInfo("Num of players = " + _players.size() + ", num of watchers = " + _watchers.size());
//		return _players.size() + _watchers.size() <= 1;
		return false;
	}
	
	void printInfo(String info){
//		String fullInfo = "[" + new Date() + "]  " + "Room = " + _server.getRID() + ", table = " + _server.getID();
//		System.err.println(fullInfo + ": " + info);
	}

	public boolean gameIsStarted() {
		for (ScrabblePlayer player : _players.values()) {
			if(player.getPass() == 0 && player.getScore() == 0 && player.getChangeLetters() == 0){
				return false;
			}
		}
		return true;
	}

	public Map<Byte, ScrabblePlayer> getSavedPlayers() {
		return _savedPlayers;
	}

	public boolean checkHasCell(ScrabbleCell cell) {
		ScrabblePlayer player = getScrabblePlayer(_whoseTurn);
		Vector<ScrabbleDie> diceOfGamer = player.getStand().getDiceOfGamer();
		for (ScrabbleDie die : diceOfGamer) {
			if(cell.getDie().equals(die)){
				return true;
			}
		}
		return false;
	}

	public int getBagSize() {
		return _bag.size();
	}

	public void addMoveMadeInfoForWatcher(String info) {
		_currentMatchHistoryForWatcher.add(info);		
	}

	public List<String> getCurrentMatchHistoryForWatcher() {
		return _currentMatchHistoryForWatcher;
	}

}

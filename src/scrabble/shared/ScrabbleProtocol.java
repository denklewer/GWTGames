package scrabble.shared;

public interface ScrabbleProtocol {
	
	public static final int AlPHABET_SIZE = 32;

	final int CELLS_IN_LINE = 15;
	
	final int DIES_IN_STAND = 7;
	
	final byte _MAX_OF_GAMERS = 4;
	
	final int _PASS_MOVE = 0;
	final int _ACTION_MOVE = 1;
	final int _TRANSFER_MOVE = 2;
	
	final int BONUS_WORD = 1;
	final int BONUS_LETTER = 2;
	
	final int _ACTION_BUTTON = 0;
	final int _TRANSFER_BUTTON = 1;
	final int _PASS_BUTTON = 2;
	final int _UNDO_BUTTON = 3;
	
	final int BONUS_FOR_USE_ALL_CELLS = 50;
	
	public static final int CMD_ADD_DIE_ID = 0;
	public static final int CMD_ADD_UNJOINED_DICE_ID = 1;
	public static final int CMD_ASK_MOVE_ID = 2;
	public static final int CMD_CHANGE_DICE_ID = 3;
	public static final int CMD_CLEAR_BORD_ID = 4;
	public static final int CMD_CONTAINING_CELL_ID = 5;
	public static final int CMD_DICE_REMOVED_ID = 6;
	public static final int CMD_DIE_ADDED_ID = 7;
	public static final int CMD_DIE_FROM_STAND_REMOVED_ID = 8;
	public static final int CMD_DIE_REMOVED_ID = 9;
	public static final int CMD_END_GAME_ID = 10;
	public static final int CMD_MOVE_IS_POSSIBLE_ID = 11;
	public static final int CMD_PASS_ID = 12;
	public static final int CMD_PLAYER_ADDED_ID = 13;
	public static final int CMD_PLAYER_REMOVED_ID = 14;
	public static final int CMD_PUT_DIE_TO_STAND_ID = 15;
	public static final int CMD_REMOVE_DIE_ID = 16;
	public static final int CMD_REMOVE_DIE_FROM_STAND_ID = 17;
	public static final int CMD_RESET_ID = 18;
	public static final int CMD_SCORE_CHANGED_ID = 19;
	public static final int CMD_SCRABBLE_MOVE_ID = 20;
	public static final int CMD_SCRABBLE_MOVE_APPROVED_ID = 21;
	public static final int CMD_SET_BOARD_ID = 22;
	public static final int CMD_SET_PLAYERS_ID = 23;
	public static final int CMD_SET_TURN_ID = 24;
	public static final int CMD_STAND_ADDED_ID = 25;
	public static final int CMD_DIE_TO_STAND_PUT_ID = 26;
	
	public static final int TOKENS_COEFF = 10;
	
}

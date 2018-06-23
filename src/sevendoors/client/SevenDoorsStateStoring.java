package sevendoors.client;

import games.shared.RandomGenerator;
import sevendoors.shared.Jungle;
import sevendoors.shared.Secret;
import sevendoors.shared.SevenDoorsBoard;
import sevendoors.shared.SevenDoorsProtocol;
import sevendoors.shared.SevenDoorsState;
import sevendoors.shared.Tile;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class SevenDoorsStateStoring implements Store {
	
	@Override
	public String store(SevenDoorsState state) throws Exception{
		JSONArray result = new JSONArray();
		result.set(0, storeBoard(state.getBoard()));
		result.set(1, storeJungle(state.getJungle()));
		result.set(2, storeSecret(state.getSecretNum()));
		
		result.set(3, new JSONString(state.getScore() + ""));
		result.set(4, new JSONString(state.getKeys() + ""));
		result.set(5, new JSONString(state.getLastScore() + ""));
		result.set(6, new JSONString(state.getLevel() + ""));
		result.set(7, new JSONString(state.getNumOfMachete() + ""));
		result.set(8, new JSONString(state.getNumOfMadeMoves() + ""));
		return result.toString();
	}

	@Override
	public SevenDoorsState restore(String game, RandomGenerator randomGenerator) throws Exception{
		SevenDoorsState state = new SevenDoorsState(randomGenerator);
		JSONValue jsonValue = JSONParser.parseStrict(game);
		
		JSONArray object = (JSONArray) jsonValue;
		SevenDoorsBoard board = new SevenDoorsBoard(SevenDoorsProtocol.BOARD_SIZE, randomGenerator);
		restoreBoard(board, object.get(0));
		state.setBoard(board);
		Jungle jungle = new Jungle(randomGenerator);
		restoreJungle(jungle, object.get(1));
		state.setJungle(jungle);
		Secret secret = new Secret(board);
		restoreSecret(secret, object.get(2));
		state.setSecret(secret);
		
		state.setScore(Long.parseLong(((JSONString) object.get(3)).stringValue()));
		state.setKeys(Integer.parseInt(((JSONString) object.get(4)).stringValue()));
		state.setLastScore(Integer.parseInt(((JSONString) object.get(5)).stringValue()));
		state.setLevel(Integer.parseInt(((JSONString) object.get(6)).stringValue()));
		state.setNumOfMachete(Integer.parseInt(((JSONString) object.get(7)).stringValue()));
		state.setNumOfMadeMoves(Integer.parseInt(((JSONString) object.get(8)).stringValue()));
		return state;
	}
	
	private JSONValue storeBoard(SevenDoorsBoard board) throws Exception {
		JSONArray result = new JSONArray();
		int index = 0;
		for (int i = 0; i < SevenDoorsProtocol.BOARD_SIZE; i++) {
			for (int j = 0; j < SevenDoorsProtocol.BOARD_SIZE; j++) {
				result.set(index, storeTile(board.getTile(i, j)));
				index++;
			}
		}
		return result;
	}
	
	private JSONValue storeTile(Tile storedTile) throws Exception {
		JSONArray tile = new JSONArray();
		tile.set(0, new JSONString(storedTile.getId() + ""));
		tile.set(1, JSONBoolean.getInstance(storedTile.isLightBonus()));
		tile.set(2, JSONBoolean.getInstance(storedTile.hasLiana()));
		tile.set(3, JSONBoolean.getInstance(storedTile.isDeleted()));
		tile.set(4, JSONBoolean.getInstance(storedTile.givesScore()));
		return tile;
	}
	
	private JSONValue storeJungle(boolean[][] jungle) throws Exception {
		JSONArray result = new JSONArray();
		int index = 0;
		for(int i = 0; i < SevenDoorsProtocol.BOARD_SIZE; i++) {
			for(int j = 0; j < SevenDoorsProtocol.BOARD_SIZE; j++) {
				result.set(index, JSONBoolean.getInstance(jungle[i][j]));
				index++;
			}
		}
		return result;
	}

	private JSONValue storeSecret(int secret) throws Exception {
		return new JSONString(secret + "");
	}
	
	private void restoreSecret(Secret secret, JSONValue value) throws Exception {
		JSONString secretString = (JSONString) value;
		secret.setCurrentSecret(Integer.parseInt(secretString.stringValue()));
	}
	
	private void restoreJungle(Jungle jungle, JSONValue value) throws Exception {
		JSONArray jungleList = (JSONArray) value;
		int index = 0;
		for(int i = 0; i < SevenDoorsProtocol.BOARD_SIZE; i++) {
			for(int j = 0; j < SevenDoorsProtocol.BOARD_SIZE; j++) {
				jungle.setJungle(((JSONBoolean) jungleList.get(index)).booleanValue(), i, j);
				index++;
			}
		}		
	}

	private Tile restoreTile(JSONValue value) throws Exception {
		JSONArray tile = (JSONArray) value;
		int id = Integer.parseInt(((JSONString) tile.get(0)).stringValue());
		Tile curTile = new Tile(id);
		curTile.setLightBonus(((JSONBoolean) tile.get(1)).booleanValue());
		curTile.setLiana(((JSONBoolean) tile.get(2)).booleanValue());
		curTile.setDeleted(((JSONBoolean) tile.get(3)).booleanValue());
		curTile.setGivesScore(((JSONBoolean) tile.get(4)).booleanValue());
		return curTile;
	}

	private void restoreBoard(SevenDoorsBoard board, JSONValue value) throws Exception {
		JSONArray tilesList = (JSONArray) value;
		int index = 0;
		Tile[][] tiles = new Tile[SevenDoorsProtocol.BOARD_SIZE][SevenDoorsProtocol.BOARD_SIZE];
		for (int i = 0; i < SevenDoorsProtocol.BOARD_SIZE; i++) {
			tiles[i] = new Tile[SevenDoorsProtocol.BOARD_SIZE];
			for (int j = 0; j < SevenDoorsProtocol.BOARD_SIZE; j++) {
				tiles[i][j] = restoreTile(tilesList.get(index));
				board.setTile(tiles[i][j], i, j);
				index++;
			}
		}
	}

}

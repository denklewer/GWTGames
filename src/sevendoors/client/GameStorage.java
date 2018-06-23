package sevendoors.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class GameStorage {
	

	private static final String NOT_SUPPORTED = "You browser doesn't support HTML5.";
	private Storage _localStorage;

	public GameStorage() {
		_localStorage = Storage.getLocalStorageIfSupported();
		if(_localStorage == null){
			RootPanel.get().add(new Label(NOT_SUPPORTED));
		}
	}
	
	public boolean haveSavedGame(String key){
		return _localStorage.getItem(key) != null;
	}

	public void saveGame(String key, String state){
		if(_localStorage == null){
			return;
		}
		_localStorage.setItem(key, state);
	}
	
	public String loadGame(String key){
		if(_localStorage == null){
			return "";
		}
		String item = _localStorage.getItem(key);
		return item;
	}
	
	public void resetGame(String key){
		if(_localStorage == null){
			return;
		}
		_localStorage.removeItem(key);
	}

	public void saveScore(String game, long score) {
		if(_localStorage == null){
			return;
		}
		if(score <= 0){
			return;
		}
		String item = _localStorage.getItem(game);
		JSONArray scores;
		if(item != null){
			scores = (JSONArray) JSONParser.parseStrict(item);
			List<TopScore> topScores = new ArrayList<TopScore>();
			for (int i = 0; i < scores.size(); i++) {
				topScores.add(new TopScore(scores.get(i).isArray().get(0).isString().stringValue(),
						scores.get(i).isArray().get(1).isString().stringValue()));
			}
			topScores.add(new TopScore(score, new Date()));
			Collections.sort(topScores);
			JSONArray result = new JSONArray();
			for (int i = 0; i < topScores.size(); i++) {
				JSONArray sc = new JSONArray();
				sc.set(0, new JSONString(topScores.get(i).getScore() + ""));
				sc.set(1, new JSONString(topScores.get(i).getDate().getTime() + ""));
				result.set(i, sc);
			}
			scores = result;
		} else {
			scores = new JSONArray();
			JSONArray sc = new JSONArray();
			sc.set(0, new JSONString(score + ""));
			sc.set(1, new JSONString(new Date().getTime() + ""));
			scores.set(0, sc);
		}
		_localStorage.setItem(game, scores.toString());
	}
	
	public List<TopScore> getTopScore(String game, int num){
		if(_localStorage == null){
			return Collections.emptyList();
		}
		String item = _localStorage.getItem(game);
		if(item == null){
			return Collections.emptyList();
		}
		List<TopScore> result = new ArrayList<TopScore>();
		JSONArray topscores = (JSONArray) JSONParser.parseStrict(item);
		int min = Math.min(num, topscores.size());
		for (int i = 0; i < min; i++) {
			result.add(new TopScore(topscores.get(i).isArray().get(0).isString().stringValue(),
					topscores.get(i).isArray().get(1).isString().stringValue()));
		}
		return result;
	}
	
	static class TopScore implements Comparable<TopScore>{
		private long _score;
		private Date _date;
		
		public TopScore(String score, String date) {
			this(Long.parseLong(score), new Date(Long.parseLong(date)));
		}
		
		public TopScore(long score, Date date) {
			_score = score;
			_date = date;
		}
		@Override
		public int compareTo(TopScore o) {
			if(o == null){
				return -1;
			}
			if(_score > o._score){
				return -1;
			} else if(_score < o._score){
				return 1;
			} else {
				if(_date.before(o._date)){
					return -1;
				} else if (_date.after(o._date)){
					return 1;
				}
				return 0;
			}
		}

		public long getScore() {
			return _score;
		}

		public Date getDate() {
			return _date;
		}

	}
}

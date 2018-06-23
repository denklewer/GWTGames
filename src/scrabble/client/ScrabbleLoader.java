package scrabble.client;

import games.client.StopListener;
import games.shared.RandomGenerator;

import java.util.Vector;

import scrabble.shared.ScrabbleBag;
import scrabble.shared.ScrabbleBoard;
import scrabble.shared.ScrabbleCell;
import scrabble.shared.ScrabbleDie;
import scrabble.shared.ScrabbleProtocol;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class ScrabbleLoader implements ScrabbleProtocol {
	
	private ScrabbleBoard _board;
	private ScrabbleBag _bag;
	
	public void loadBoard(final StopListener stopListener) {
		final ScrabbleCell cells[][] = new ScrabbleCell[CELLS_IN_LINE][CELLS_IN_LINE];
		try {
			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "data/scrabble/cells.xml");
			builder.sendRequest(null, new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {
					Document document = XMLParser.parse(response.getText());
					NodeList elementsByTagName = document.getElementsByTagName("cell");
					for (int i = 0; i < CELLS_IN_LINE; i++) {
						for (int j = 0; j < CELLS_IN_LINE; j++) {
							Node item = elementsByTagName.item(i * CELLS_IN_LINE + j);
							int type = Integer.parseInt(((Element) item).getAttribute("type"));
							int point = Integer.parseInt(((Element) item).getAttribute("point"));
							cells[i][j] = new ScrabbleCell(i, j, type, point);
						}
					}
					_board = new ScrabbleBoard(cells);
					stopListener.arrived();
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					stopListener.arrived();
				}
			});
		} catch (RequestException e1) {
			e1.printStackTrace();
		}
		
	}
	
	public void loadBag(final StopListener stopListener, final RandomGenerator randomGenerator) {
		final Vector<ScrabbleDie> dice = new Vector<ScrabbleDie>();
		try {
			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "data/scrabble/alphabet.xml");
			builder.sendRequest(null, new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {
					Document document = XMLParser.parse(response.getText());
					NodeList elementsByTagName = document.getElementsByTagName("letter");
					for (int i = 0; i < elementsByTagName.getLength(); i++) {
						Node item = elementsByTagName.item(i);
						char letter = ((Element) item).getAttribute("name").charAt(0);
						int quantityOfLetter = Integer.parseInt(((Element) item).getAttribute("quantity"));
						int point = Integer.parseInt(((Element) item).getAttribute("point"));
						for (int k = 0; k < quantityOfLetter; k++) {
							dice.add(new ScrabbleDie(letter, point));
						}
					}
					_bag = new ScrabbleBag(dice, randomGenerator);
					stopListener.arrived();
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					stopListener.arrived();
				}
			});
		} catch (RequestException e1) {
			e1.printStackTrace();
		}
	}

	public ScrabbleBoard getBoard() {
		return _board;
	}

	public ScrabbleBag getBag() {
		return _bag;
	}

}

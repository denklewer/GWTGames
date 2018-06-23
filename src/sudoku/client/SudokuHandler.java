package sudoku.client;

import games.client.StopListener;

import java.util.ArrayList;
import java.util.List;

import sudoku.shared.FieldsByLevel;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class SudokuHandler {

	public static void parseAllXMLFiles(final StopListener stopListener, final FieldsByLevel fieldsByLevel){
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "data/sudoku/SudokuMaps.txt");
		try {
			builder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request, Response response) {
					String text = response.getText();
					String[] files = text.split(" \n ");
					for (int i = 0; i < files.length; i++) {
						String prefix = files[i].split("_", 2)[0];
						stopListener.incrementExpectedBy(1);
						parseXML(prefix, files[i], stopListener, fieldsByLevel);
					}
					stopListener.arrived();
				}

				@Override
				public void onError(Request request, Throwable exception) {
					stopListener.arrived();
				}
			});
		} catch (RequestException e) {
			e.printStackTrace();
		}
	}

	public static void parseXML(final String prefix, String filePath, final StopListener stopListener, final FieldsByLevel fieldsByLevel){
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "data/sudoku/" + filePath);
		try {
			final List<List<Integer>> digits = new ArrayList<List<Integer>>();
			builder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request, Response response) {
					Document document = XMLParser.parse(response.getText());
					NodeList rowList = document.getElementsByTagName("int-array");
					int rowListLength = rowList.getLength();
					for (int i = 0; i < rowListLength; i++) {
						digits.add(new ArrayList<Integer>());
					}
					for (int i = 0; i < rowListLength; i++) {
						Node row = rowList.item(i);
						NodeList nodes = row.getChildNodes();

						for (int j = 0; j < nodes.getLength(); j++) {
							Node item = nodes.item(j);
							if(item != null && item.getFirstChild() != null){
								Node firstChild = item.getFirstChild();
								digits.get(i).add(Integer.parseInt(firstChild.getNodeValue()));
							}
						}
					}
					fieldsByLevel.addField(prefix, digits);
					stopListener.arrived();
				}

				@Override
				public void onError(Request request, Throwable exception) {
					stopListener.arrived();
				}
			});
		} catch (RequestException e) {
			e.printStackTrace();
		}
	}

}

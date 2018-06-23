package sevendoors.client;

import games.client.StopListener;
import sevendoors.shared.BonusProbabilityInfo;
import sevendoors.shared.BonusProbabilityInfo.BonusElement;
import sevendoors.shared.BonusProbabilityInfo.BonusProbab;

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

public class SevenDoorsSAXHandler {
	private static final int MAX_SCORE = 100;
	
	public static void parseBonuses(final StopListener stopListener, final BonusProbabilityInfo info){
		try {
			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "data/sevendoors/bonuses.xml");
			builder.sendRequest(null, new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {
					Document document = XMLParser.parse(response.getText());
					NodeList bonusList = document.getElementsByTagName("bonus");
					for (int i = 0; i < bonusList.getLength(); i++) {
						Node bonus = bonusList.item(i);
						String name = ((Element)bonus).getAttribute("name");
						int sincelevel = 1;
						String levelString = ((Element) bonus).getAttribute("sincelevel");
						if(levelString != null){
							sincelevel = Integer.parseInt(levelString);
						}
						
						BonusElement currentBonus = new BonusElement();
						currentBonus.setSinceLevel(sincelevel);
						info.addBonusInfo(name, currentBonus);

						NodeList childNodes = bonus.getChildNodes();
						for (int j = 0; j < childNodes.getLength(); j++) {
							Node score = childNodes.item(j);
							if("score".equals(score.getNodeName())){

								BonusProbab scoreElement = new BonusProbab();

								String fromString = ((Element) score).getAttribute("from");
								if(fromString != null){
									scoreElement.setFrom(Integer.parseInt(fromString));
								}

								String toString = ((Element) score).getAttribute("to");
								if(toString != null){
									scoreElement.setTo(Integer.parseInt(toString));
								} else {
									scoreElement.setTo(MAX_SCORE);
								}

								String probabString = ((Element) score).getAttribute("probab");
								if(probabString != null){
									scoreElement.setProbab(Integer.parseInt(probabString));
								}

								currentBonus.getProbabs().add(scoreElement);
							}
						}
					}
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

}

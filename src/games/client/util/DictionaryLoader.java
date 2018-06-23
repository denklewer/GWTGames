package games.client.util;

import games.client.StopListener;
import games.shared.Dictionary;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

public class DictionaryLoader {

	public static void loadDictionary(final StopListener stopListener,
			final Dictionary dictionary) {
		if (!dictionary.isEmpty()) {
			stopListener.arrived();
			return;
		}
		try {
			final RequestBuilder builder = new RequestBuilder(
					RequestBuilder.GET, dictionary.getDictionaryFilePath());
			builder.sendRequest(null, new RequestCallback() {
				
				@Override
				public void onResponseReceived(final Request request,
						final Response response) {
					dictionary.loadDictionary(response.getText());
					stopListener.arrived();
				}
				
				@Override
				public void onError(final Request request,
						final Throwable exception) {
					stopListener.arrived();
				}
			});
		} catch (final RequestException e1) {
			e1.printStackTrace();
		}
	}
	
	public static void loadDictionary(final ScheduledCommand command,
			final Dictionary dictionary) {
		final StopListener stopListener = new StopListener(command);
		stopListener.setExpected(1);
		loadDictionary(stopListener, dictionary);
	}

}

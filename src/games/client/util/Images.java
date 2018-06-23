package games.client.util;

import games.client.StopListener;
import games.client.Stopper;
import games.client.sprites.EngineFinishListener;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

public class Images implements ImageFactory {

	private final Map<String, DrawElement> _images = new HashMap<String, DrawElement>();
	private final Map<String, String> _imageNames;

	public Images(Map<String, String> imageNames) {
		_imageNames = imageNames;
	}

	@Override
	public void loadImage(final EngineFinishListener loadedListener, final String name, final String location) {
		final Image image = new Image(location);
		image.addLoadHandler(new LoadHandler() {

			@Override
			public void onLoad(final LoadEvent event) {
				_images.put(name, DrawElement.createDrawElement((ImageElement) (image.getElement().cast())));
				RootPanel.get().remove(image);
				loadedListener.arrived();
			}
		});
		image.setVisible(false);
		RootPanel.get().add(image);

	}

	@Override
	public DrawElement getDrawElement(final String name) {
		return _images.get(name);
	}
	
	@Override
	public void loadImages(final ScheduledCommand startGame) {
		final Stopper imagesLoadedListener = new StopListener(startGame);
		imagesLoadedListener.setExpected(_imageNames.size());
		for (final String name : _imageNames.keySet()) {
			final String location = _imageNames.get(name);
			loadImage(imagesLoadedListener, name, location);
		}
	}

}

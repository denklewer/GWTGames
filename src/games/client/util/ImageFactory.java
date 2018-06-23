package games.client.util;

import games.client.sprites.EngineFinishListener;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;


public interface ImageFactory {

	public void loadImage(final EngineFinishListener loadedListener, final String name, final String location);
	
	public void loadImages(final ScheduledCommand startGame);
	
	public DrawElement getDrawElement(final String name);

}

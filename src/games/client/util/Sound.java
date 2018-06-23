package games.client.util;

import com.google.gwt.media.client.Audio;

public class Sound {

	private final Audio audio;
	private boolean played = false;
	private final String name;



	public Sound(final Audio audio, final boolean played, final String name) {
		this.audio = audio;
		this.setPlayed(played);
		this.name = name;
	}



	public boolean hasEnded() {
		return audio.hasEnded();
	}



	public boolean isPlayed() {
		return played;
	}

	public void setPlayed(final boolean played) {
		this.played = played;
	}

	public Object getName() {
		return name;
	}



	public Audio getAudio() {
		return audio;
	}



}

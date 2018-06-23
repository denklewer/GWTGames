package games.client.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.AudioElement;
import com.google.gwt.media.client.Audio;
import com.google.gwt.user.client.ui.RootPanel;

public class AudioManager {

	private static final int MAX_SOUNDS = 20;

	public static String supportedAudioFormat(final Audio audio) {

		String returnExtension = "";
		if (audio.canPlayType("audio/ogg").equals(AudioElement.CAN_PLAY_PROBABLY)
				|| audio.canPlayType("audio/ogg").equals(AudioElement.CAN_PLAY_MAYBE)) {
			returnExtension = "ogg";
		} else if (audio.canPlayType("audio/wav").equals(AudioElement.CAN_PLAY_PROBABLY)
				|| audio.canPlayType("audio/wav").equals(AudioElement.CAN_PLAY_MAYBE)) {
			returnExtension = "wav";
		} else if (audio.canPlayType("audio/mp3").equals(AudioElement.CAN_PLAY_PROBABLY)
				|| audio.canPlayType("audio/mp3").equals(AudioElement.CAN_PLAY_MAYBE)) {
			returnExtension = "mp3";
		}

		return returnExtension;

	}

	private static List<Sound> soundPool = new ArrayList<Sound>();
	private static String audioType;

	public static void init() {
		createSound("audio/del",0);
		createSound("audio/drop",0);
		createSound("audio/drop",0);
		createSound("audio/drop",0);
		createSound("audio/drop",0);
		createSound("audio/move",0);
		createSound("audio/level",0);
	}

	public static void playSound(final String soundName, final double volume) {

		boolean soundFound = false;
		Sound tempSound = null;
		final Audio tempAudio;

		for (final Sound aSound : soundPool) {
			if ((aSound.hasEnded() || !aSound.isPlayed()) && aSound.getName().equals(soundName)) {
				soundFound = true;
				aSound.setPlayed(true);
				tempSound = aSound;
				break;
			}
		}


		if (soundFound) {
			tempAudio = tempSound.getAudio();
			tempAudio.setVolume(volume);
			tempAudio.play();

		} else if (soundPool.size() < MAX_SOUNDS){
			createSound(soundName, volume);
		}




	}

	public static void createSound(final String soundName,
			final double volume) {
		final Audio tempAudio;
		tempAudio = Audio.createIfSupported();
		tempAudio.setSrc(soundName + "." + getAudioType());
		tempAudio.setVolume(volume);
		tempAudio.play();
		soundPool.add(new Sound(tempAudio, false, soundName));
	}

	private static String getAudioType() {
		if (audioType == null) {
			final Audio audio = Audio.createIfSupported();
			RootPanel.get().add(audio);
			if (audio != null) {
				audioType = supportedAudioFormat(audio);
			}
		}

		return audioType;
	}



}


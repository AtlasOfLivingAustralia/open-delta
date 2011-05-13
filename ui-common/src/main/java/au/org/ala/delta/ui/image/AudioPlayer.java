package au.org.ala.delta.ui.image;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;

/**
 * Helper class for playing audio.
 */
public class AudioPlayer {

	/**
	 * Plays a short sound using the java sound Clip mechanism which loads the
	 * sound file into memory.
	 * @param sound the URL containing the audio stream.
	 */
	public static void playClip(URL sound) {
		
		try {
			Line.Info lineInfo = new Line.Info(Clip.class);
			Line line = AudioSystem.getLine(lineInfo);
			Clip clip = (Clip)line;
			
			AudioInputStream ais = AudioSystem.getAudioInputStream(sound);
			clip.open(ais);
			clip.start();
		}
		catch (Exception e) {
			throw new RuntimeException("Error opening file: "+sound);
		}
	}
}

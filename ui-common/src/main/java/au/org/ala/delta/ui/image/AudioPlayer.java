/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.ui.image;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

/**
 * Helper class for playing audio.
 */
public class AudioPlayer {

	/**
	 * Plays a short sound using the java sound Clip mechanism which loads the sound file into memory.
	 * 
	 * @param sound
	 *            the URL containing the audio stream.
	 */
	public static void playClip(URL sound) {

		try {
			System.err.println("Getting line info");
			Line.Info lineInfo = new Line.Info(Clip.class);
			System.err.println("Getting line");
			Line line = AudioSystem.getLine(lineInfo);
			System.err.println("Casting line to clip");
			Clip clip = (Clip) line;
			System.err.println("Getting stream");
			AudioInputStream ais = AudioSystem.getAudioInputStream(sound);
			System.err.println("Opening clip");
			clip.open(ais);

			clip.addLineListener(new LineListener() {
				public void update(LineEvent evt) {
					if (evt.getType() == LineEvent.Type.STOP) {
						System.err.println("Closing line");
						evt.getLine().close();
					}
				}
			});

			System.err.println("Starting Clip");
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error opening file: " + sound);
		}
	}
}

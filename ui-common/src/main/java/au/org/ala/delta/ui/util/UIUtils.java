package au.org.ala.delta.ui.util;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JInternalFrame.JDesktopIcon;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.ui.RichTextDialog;

public class UIUtils {

	public static RichTextDialog createCharacterDetailsDialog(Window owner, Character character) {
		CharacterFormatter formatter = new CharacterFormatter();
		StringBuilder text = new StringBuilder();
		text.append(formatter.formatCharacterDescription(character));

		if (character instanceof MultiStateCharacter) {
			MultiStateCharacter multiStateChar = (MultiStateCharacter) character;
			for (int i = 1; i <= multiStateChar.getNumberOfStates(); i++) {
				text.append("\\par ");
				text.append(formatter.formatState(multiStateChar, i));
			}
		} else if (character instanceof NumericCharacter<?>) {
			NumericCharacter<?> numericChar = (NumericCharacter<?>) character;
			text.append("\\par ");
			text.append(numericChar.getUnits());
		}
		RichTextDialog dialog = new RichTextDialog(owner, text.toString());
		return dialog;
	}

	private static void cascade(JInternalFrame[] frames, Rectangle dBounds, int separation) {
		int margin = 10 * separation;
		int width = dBounds.width - margin;
		int height = dBounds.height - margin;
		for (int i = 0; i < frames.length; i++) {
			int offset = (frames.length - i - 1) * separation;
			int xOffset = dBounds.x + offset;
			if (xOffset > (dBounds.x + dBounds.width) - width) {
				xOffset -= margin;
			}
			
			int yOffset = dBounds.y + offset;
			if (yOffset > (dBounds.y + dBounds.height) - height) {
				yOffset -= margin;
			}
			
			frames[i].setBounds(xOffset, yOffset, width, height);
		}
	}

	public static void cascade(JDesktopPane desktopPane, int layer) {
		JInternalFrame[] frames = desktopPane.getAllFramesInLayer(layer);
		if (frames.length == 0) {
			return;
		}

		cascade(frames, desktopPane.getBounds(), 24);
	}

	public static void cascade(JDesktopPane desktopPane) {
		JInternalFrame[] frames = desktopPane.getAllFrames();
		if (frames.length == 0) {
			return;
		}

		cascade(frames, desktopPane.getBounds(), 24);
	}

	public static void arrangeMinifiedWindows(JDesktopPane desktop) {
		List<JInternalFrame> minified = new ArrayList<JInternalFrame>();
		JInternalFrame[] frames = desktop.getAllFrames();
		for (JInternalFrame frame : frames) {
			if (frame.isIcon()) {
				minified.add(frame);
			}
		}
		
		if (minified.size() > 0) {
			
			Rectangle desktopBounds = desktop.getBounds();
			
			JDesktopIcon i = minified.get(0).getDesktopIcon();
			Rectangle bounds = i.getBounds();
			int x = 0;
			int y = desktopBounds.height - bounds.height;
			
			for (JInternalFrame f : minified) {
				JDesktopIcon icon = f.getDesktopIcon();
				icon.setLocation(new Point(x,y));
				x += bounds.width;				
				if (x + bounds.width > desktopBounds.width) {
					x = 0;
					y -= bounds.height;
				}
			}
		}
		
	}

}

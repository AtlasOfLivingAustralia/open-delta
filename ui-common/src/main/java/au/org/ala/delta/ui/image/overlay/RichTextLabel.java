package au.org.ala.delta.ui.image.overlay;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.ui.image.ImageViewer;
import au.org.ala.delta.ui.rtf.RtfEditorPane;

/**
 * A RichTextLabel displays text marked up using RTF.  It is used to display
 * the image overlay types:
 * Text, Feature, State, Units and Subject.
 */
public class RichTextLabel extends RtfEditorPane implements OverlayLocationProvider {

	private static final long serialVersionUID = -8231701667247672309L;
	protected ImageOverlay _overlay;
	
	public RichTextLabel(ImageOverlay overlay, String text) {
		_overlay = overlay;
		setEditable(false);
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setOpaque(true);
		setFont(UIManager.getFont("Label.font"));
		
		setText(text);	
		if (overlay.centreText()) {
			centreText();
		}
		
	}
	
	public void centreText() {
		StyledDocument doc = getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
	}
	
	@Override
	public Dimension getPreferredSize() {
		
		
		if (_overlay.getHeight(0, 0) < 0) {
			Font f = getFont();
			FontMetrics m = getFontMetrics(f);
			m.getHeight();
		}
		return super.getPreferredSize();
	}
	
	
	@Override
	public OverlayLocation location(ImageViewer viewer) {
		return new FixedCentreOverlayLocation(viewer, this, _overlay.getLocation(0));
	}
}

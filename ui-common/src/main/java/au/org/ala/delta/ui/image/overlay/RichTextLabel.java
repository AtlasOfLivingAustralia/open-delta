package au.org.ala.delta.ui.image.overlay;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.UIManager;

import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.ui.image.ImageViewer;
import au.org.ala.delta.ui.rtf.RtfEditorPane;

public class RichTextLabel extends RtfEditorPane implements OverlayLocationProvider {

	private static final long serialVersionUID = -8231701667247672309L;
	private ImageOverlay _overlay;
	
	public RichTextLabel(ImageOverlay overlay, String text) {
		_overlay = overlay;
		setEditable(false);

		setBackground(UIManager.getColor("Label.background"));
		setFont(UIManager.getFont("Label.font"));
		
		setText(text);
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

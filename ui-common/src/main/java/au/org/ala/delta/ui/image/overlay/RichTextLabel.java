package au.org.ala.delta.ui.image.overlay;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
public class RichTextLabel extends JPanel implements OverlayLocationProvider {

	private static final long serialVersionUID = -8231701667247672309L;
	protected ImageOverlay _overlay;
	protected RtfEditorPane _editor;
	
	public RichTextLabel(ImageOverlay overlay, String text) {
		_overlay = overlay;
		_editor = new RtfEditorPane();
		
		_editor.setEditable(false);
		_editor.setBackground(Color.WHITE);
		_editor.setForeground(Color.BLACK);
		_editor.setBorder(null);
		_editor.setOpaque(true);
		_editor.setFont(UIManager.getFont("Label.font"));
		
		_editor.setText(text);	
		_editor.setHighlighter(null);
		_editor.setDragEnabled(false);
		if (overlay.centreText()) {
			centreText();
		}
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setLayout(new BorderLayout());
		JScrollPane scroller = new JScrollPane(_editor);
		scroller.setViewportBorder(null);
		scroller.setBorder(null);
		add(scroller, BorderLayout.CENTER);
		
	}
	
	public void centreText() {
		StyledDocument doc = _editor.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
	}
	
	@Override
	public Dimension getPreferredSize() {
		
		
		if (_overlay.getHeight(0) < 0) {
			Font f = getFont();
			FontMetrics m = getFontMetrics(f);
			m.getHeight();
		}
		return super.getPreferredSize();
	}
	
	@Override
	public void setFont(Font font) {
		super.setFont(font);
		// setFont gets called during the panels constructor when setting
		// UI defaults so _editor can be null.
		if (_editor != null) {
			_editor.setFont(font);
		}
	}
	
	@Override
	public void addMouseListener(MouseListener l) {
		super.addMouseListener(l);
		_editor.addMouseListener(l);
	}
	
	@Override
	public void removeMouseListener(MouseListener l) {
		super.removeMouseListener(l);
		_editor.removeMouseListener(l);
	}
	
	@Override
	public void addMouseMotionListener(MouseMotionListener l) {
		super.addMouseMotionListener(l);
		_editor.addMouseMotionListener(l);
	}
	
	@Override
	public void removeMouseMotionListener(MouseMotionListener l) {
		super.removeMouseMotionListener(l);
		_editor.removeMouseMotionListener(l);
	}
	
	@Override
	public OverlayLocation location(ImageViewer viewer) {
		return new FixedCentreOverlayLocation(viewer, this, _overlay.getLocation(0));
	}
}

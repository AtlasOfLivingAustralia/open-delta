package au.org.ala.delta.editor.ui.image;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;

import au.org.ala.delta.editor.DeltaView;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.ui.ReorderableList;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.ui.image.ImageViewer;

/**
 * Displays Character and Taxon images and allows the addition of 
 * ImageOverlays to the Image to assist with IntKey identifications.
 */
public class ImageEditor extends JInternalFrame implements DeltaView {

	private static final long serialVersionUID = 4867008707368683722L;

	private Image _image;
	
	public ImageEditor(EditorViewModel model) {
	
		_image = model.getSelectedImage();
		
		ImageViewer viewer = new ImageViewer(model.getImagePath(), _image, model);
		
		getContentPane().add(viewer, BorderLayout.CENTER);
	}
	
	@Override
	public String getViewTitle() {
		return _image.getFileName();
	}

	@Override
	public void open() {}

	@Override
	public boolean editsValid() {
		return true;
	}

	@Override
	public ReorderableList getCharacterListView() {
		return null;
	}

	@Override
	public ReorderableList getItemListView() {
		return null;
	}

}

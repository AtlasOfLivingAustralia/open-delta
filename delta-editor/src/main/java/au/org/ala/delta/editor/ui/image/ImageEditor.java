package au.org.ala.delta.editor.ui.image;

import javax.swing.JInternalFrame;

import au.org.ala.delta.editor.DeltaView;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.ui.ReorderableList;
import au.org.ala.delta.model.image.Image;

/**
 * Displays Character and Taxon images and allows the addition of 
 * ImageOverlays to the Image to assist with IntKey identifications.
 */
public class ImageEditor extends JInternalFrame implements DeltaView {

	private static final long serialVersionUID = 4867008707368683722L;

	private EditorViewModel _model;
	
	public ImageEditor(EditorViewModel model) {
	
		Image image = model.getSelectedImage();
	}
	
	@Override
	public String getViewTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void open() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean editsValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ReorderableList getCharacterListView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReorderableList getItemListView() {
		// TODO Auto-generated method stub
		return null;
	}

}

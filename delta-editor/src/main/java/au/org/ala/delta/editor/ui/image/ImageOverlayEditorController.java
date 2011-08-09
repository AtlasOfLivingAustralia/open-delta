package au.org.ala.delta.editor.ui.image;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPopupMenu;

import org.apache.commons.lang.NotImplementedException;
import org.jdesktop.application.Action;

public class ImageOverlayEditorController {

	private ImageEditor _imageEditor;
	
	public ImageOverlayEditorController(ImageEditor imageEditor) {
		_imageEditor = imageEditor;
	}
	
	private JPopupMenu buildPopupMenu() {
		List<String> popupMenuActions = new ArrayList<String>();
		popupMenuActions.add("editSelectedOverlay");
		popupMenuActions.add("deleteSelectedOverlay");
		popupMenuActions.add("-");
		popupMenuActions.add("deleteAllOverlays");
		popupMenuActions.add("-");
		popupMenuActions.add("displayImageSettings");
		popupMenuActions.add("-");
		popupMenuActions.add("cancelPopup");
		
		List<String> stackOverlayMenuActions = new ArrayList<String>();
		stackOverlayMenuActions.add("stackSelectedOverlayHigher");
		stackOverlayMenuActions.add("stackSelectedOverlayLower");
		stackOverlayMenuActions.add("stackSelectedOverlayOnTop");
		stackOverlayMenuActions.add("stackSelectedOverlayOnBottom");
		
		List<String> overlayMenuActions = new ArrayList<String>();
		overlayMenuActions.add("addTextOverlay");
		overlayMenuActions.add("-");
		overlayMenuActions.add("addAllUsualOverlays");
		overlayMenuActions.add("addFeatureDescriptionOverlay");
		overlayMenuActions.add("addStateOverlay");
		overlayMenuActions.add("addHotspot");
		overlayMenuActions.add("-");
		overlayMenuActions.add("addOkOverlay");
		overlayMenuActions.add("addCancelOverlay");
		overlayMenuActions.add("addNotesOverlay");
		
		List<String> alignButtonsMenuActions = new ArrayList<String>();
		alignButtonsMenuActions.add("useDefaultButtonAlignment");
		alignButtonsMenuActions.add("alignButtonsVertically");
		alignButtonsMenuActions.add("alignButtonsHorizontally");
		alignButtonsMenuActions.add("dontAlignButtons");
		
		throw new NotImplementedException();
	}
	
	@Action
	public void editSelectedOverlay() {
		throw new NotImplementedException();
	}
	
	@Action
	public void deleteSelectedOverlay() {
		throw new NotImplementedException();
	}
	
	@Action
	public void deleteAllOverlays() {
		throw new NotImplementedException();
	}
	
	@Action
	public void displayImageSettings() {
		throw new NotImplementedException();
	}
	
	@Action
	public void cancelPopup() {
		throw new NotImplementedException();
	}
	
	@Action
	public void stackSelectedOverlayHigher() {
		throw new NotImplementedException();
	}
	
	@Action
	public void stackSelectedOverlayLower() {
		throw new NotImplementedException();
	}
	
	@Action
	public void stackSelectedOverlayOnTop() {
		throw new NotImplementedException();
	}
	
	@Action
	public void stackSelectedOverlayOnBottom() {
		throw new NotImplementedException();
	}
	
	@Action
	public void useDefaultButtonAlignment() {
		throw new NotImplementedException();
	}
	
	@Action
	public void alignButtonsVertically() {
		throw new NotImplementedException();
	}
	
	@Action
	public void alignButtonsHorizontally() {
		throw new NotImplementedException();
	}
	
	@Action
	public void dontAlignButtons() {
		throw new NotImplementedException();
	}
	
	
	@Action
	public void addTextOverlay() {
		throw new NotImplementedException();
	}
	
	@Action
	public void addAllUsualOverlays() {
		throw new NotImplementedException();
	}
	
	@Action
	public void addFeatureDescriptionOverlay() {
		throw new NotImplementedException();
	}
	
	@Action
	public void addStateOverlay() {
		throw new NotImplementedException();
	}
	
	@Action
	public void addHotspot() {
		throw new NotImplementedException();
	}
	
	@Action
	public void addOkOverlay() {
		throw new NotImplementedException();
	}
	
	@Action
	public void addCancelOverlay() {
		throw new NotImplementedException();
	}
	
	@Action
	public void addNotesOverlay() {
		throw new NotImplementedException();
	}
}

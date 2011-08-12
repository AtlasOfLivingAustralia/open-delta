package au.org.ala.delta.editor.ui.image;

import java.awt.Point;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;

import au.org.ala.delta.editor.DeltaEditor;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.image.OverlayLocation;
import au.org.ala.delta.model.image.OverlayType;

/**
 * Handles actions performed on an ImageEditorPanel. (add/delete/update overlays).
 */
public class ImageOverlayEditorController {

	private ImageOverlay _selectedOverlay;
	private ButtonAlignment _alignment;
	private ImageSettings _imageSettings;
	private ImageEditorSelectionModel _selection;

	enum ButtonAlignment {
		ALIGN_VERTICAL, ALIGN_HORIZONTAL, ALIGN_NONE
	};

	public ImageOverlayEditorController(ImageEditorSelectionModel selection, EditorViewModel model) {
		_imageSettings = model.getImageSettings();
		
		_selection = selection;
	}
	
	@Action
	public void editSelectedOverlay() {
		DeltaEditor editor = (DeltaEditor)Application.getInstance();
		OverlayEditDialog overlayEditor = new OverlayEditDialog(editor.getMainFrame(), 
				_selection.getSelectedImage(), _selection.getSelectedOverlay());
		editor.show(overlayEditor);
	}

	@Action
	public void deleteSelectedOverlay() {
		_selection.getSelectedImage().deleteOverlay(_selectedOverlay);
	}

	@Action
	public void deleteAllOverlays() {
		// TODO are you sure?
		_selection.getSelectedImage().deleteAllOverlays();
	}

	@Action
	public void displayImageSettings() {
		throw new NotImplementedException();
	}

	@Action
	public void cancelPopup() {
	}

	@Action
	public void stackSelectedOverlayHigher() {
		Image image = _selection.getSelectedImage();
		image.moveUp(_selection.getSelectedOverlay());
	
	}

	@Action
	public void stackSelectedOverlayLower() {
		Image image = _selection.getSelectedImage();
		image.moveDown(_selection.getSelectedOverlay());
	}

	@Action
	public void stackSelectedOverlayOnTop() {
		Image image = _selection.getSelectedImage();
		image.moveToTop(_selection.getSelectedOverlay());
	}

	@Action
	public void stackSelectedOverlayOnBottom() {
		Image image = _selection.getSelectedImage();
		image.moveToBottom(_selection.getSelectedOverlay());
	}

	@Action
	public void useDefaultButtonAlignment() {
		_alignment = ButtonAlignment.ALIGN_NONE;
	}

	@Action
	public void alignButtonsVertically() {
		_alignment = ButtonAlignment.ALIGN_VERTICAL;
	}

	@Action
	public void alignButtonsHorizontally() {
		_alignment = ButtonAlignment.ALIGN_HORIZONTAL;
	}

	@Action
	public void dontAlignButtons() {
		_alignment = ButtonAlignment.ALIGN_NONE;
	}

	@Action
	public void addTextOverlay() {
		addOverlay(OverlayType.OLTEXT);
	}

	@Action
	public void addAllUsualOverlays() {
		if (_selection.getSelectedImage().getSubject() instanceof Character) {
			Character character = (Character) _selection.getSelectedImage().getSubject();
			Point origin = new Point(150, 300);
			if (_selection.getSelectedImage().getOverlay(OverlayType.OLFEATURE) == null) {
				ImageOverlay newOverlay = newOverlay(OverlayType.OLFEATURE);

				newOverlay.setX(origin.x);
				newOverlay.setY(origin.y);
				origin.x += 25;
				origin.y += 50;

			}
			if (character.getCharacterType().isMultistate()) {
				addStateOverlays((MultiStateCharacter) character, origin);
			} else if (character.getCharacterType().isNumeric()) {
				if (_selection.getSelectedImage().getOverlay(OverlayType.OLENTER) == null) {
					ImageOverlay newOverlay = newOverlay(OverlayType.OLENTER);
					newOverlay.setX(400);
					newOverlay.setY(600);

				}
				if (_selection.getSelectedImage().getOverlay(OverlayType.OLUNITS) == null
						&& StringUtils.isNotEmpty(((NumericCharacter<?>) character).getUnits())) {
					ImageOverlay newOverlay = newOverlay(OverlayType.OLUNITS);

					newOverlay.setX(Short.MIN_VALUE);
					newOverlay.setY(Short.MIN_VALUE);

				}
			}
			if (_selection.getSelectedImage().getOverlay(OverlayType.OLOK) == null) {
				newOverlay(OverlayType.OLOK);

			}
			if (_selection.getSelectedImage().getOverlay(OverlayType.OLCANCEL) == null) {
				newOverlay(OverlayType.OLCANCEL);

			}
			if (StringUtils.isNotEmpty(character.getNotes()) && _selection.getSelectedImage().getOverlay(OverlayType.OLNOTES) == null) {
				newOverlay(OverlayType.OLNOTES);

			}
		}
	}

	private void addStateOverlays(MultiStateCharacter character, Point origin) {
		int stateNum;
		List<ImageOverlay> overlays = _selection.getSelectedImage().getOverlaysOfType(OverlayType.OLSTATE);
		Set<Integer> states = new HashSet<Integer>();
		for (ImageOverlay overlay : overlays) {
			states.add(overlay.stateId);
		}

		for (stateNum = 1; stateNum <= character.getNumberOfStates(); stateNum++) {
			if (states.contains(stateNum)) {
				continue;
			}
			ImageOverlay newOverlay = newStateOverlay(stateNum);

			newOverlay.setX(origin.x);
			newOverlay.setY(origin.y);

			ImageOverlay hsOverlay = newOverlay(OverlayType.OLHOTSPOT);
			hsOverlay.setX(origin.x);
			hsOverlay.setY(origin.y);

			origin.x += 25;
			origin.y += 50;
			if (origin.x > 350)
				origin.x -= 170;
			if (origin.y > 650)
				origin.y -= 330;
		}
	}

	@Action
	public void addFeatureDescriptionOverlay() {
		addOverlay(OverlayType.OLFEATURE);
	}
	
	@Action
	public void addItemNameOverlay() {
		addOverlay(OverlayType.OLITEM);
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
		addOverlay(OverlayType.OLOK);
	}

	@Action
	public void addCancelOverlay() {
		addOverlay(OverlayType.OLCANCEL);
	}

	@Action
	public void addNotesOverlay() {
		addOverlay(OverlayType.OLNOTES);
	}
	
	@Action
	public void addImageNotesOverlay() {
		addOverlay(OverlayType.OLIMAGENOTES);
	}

	private void addOverlay(int overlayType) {
		ImageOverlay overlay = newOverlay(overlayType);
		_selection.getSelectedImage().updateOverlay(overlay);
	}

	private ImageOverlay newStateOverlay(int stateNum) {
		ImageOverlay anOverlay = _selection.getSelectedImage().addOverlay(OverlayType.OLSTATE);
		anOverlay.stateId = stateNum;
		configureOverlay(anOverlay);
		return anOverlay;
	}

	private ImageOverlay newOverlay(int overlayType) {
		ImageOverlay anOverlay = _selection.getSelectedImage().addOverlay(overlayType);
		configureOverlay(anOverlay);
		return anOverlay;
	}

	private void configureOverlay(ImageOverlay anOverlay) {
		Point menuPoint = _selection.getSelectedPoint();
		if (menuPoint == null) {
			menuPoint = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
		}
		OverlayLocation newLocation;
		if (anOverlay.location.size() == 0) {
			newLocation = new OverlayLocation();
			anOverlay.location.add(newLocation);
		}
		else {
			newLocation = anOverlay.getLocation(0);
		}
		
		if (anOverlay.type == OverlayType.OLHOTSPOT) {
			_imageSettings.configureHotSpotDefaults(newLocation);
			newLocation.H = 200;
		} else {
			_imageSettings.configureOverlayDefaults(anOverlay);
		}
		
		if (menuPoint.x != Integer.MIN_VALUE) {
			
			newLocation.setX(menuPoint.x);
			newLocation.setY(menuPoint.y);
		} else {
			newLocation.X = 350;
			newLocation.Y = 450;
		}
		if (anOverlay.IsButton()) {
			int bhClient = newLocation.H;
			int bwClient = newLocation.W;
			newLocation.W = newLocation.H = Short.MIN_VALUE;
			ButtonAlignment align = _alignment;
			if (align == ButtonAlignment.ALIGN_NONE)
				align = ButtonAlignment.ALIGN_VERTICAL;
			if (menuPoint.x == Integer.MIN_VALUE) {
				newLocation.setX(align == ButtonAlignment.ALIGN_VERTICAL ? 800 : 500);
				newLocation.setY(align == ButtonAlignment.ALIGN_VERTICAL ? 500 : 800);
			}
			newLocation.setX(Math.max(0, Math.min(1000 - bwClient, (int) newLocation.X)));
			newLocation.setY(Math.max(0, Math.min(1000 - bhClient, (int) newLocation.Y)));
			int okWhere = Integer.MIN_VALUE, cancelWhere = Integer.MIN_VALUE, notesWhere = Integer.MIN_VALUE;
			ImageOverlay okOverlay = _selection.getSelectedImage().getOverlay(OverlayType.OLOK);
			if (okOverlay != null) {
				if (align == ButtonAlignment.ALIGN_VERTICAL) {
					newLocation.setX(okOverlay.getX());
					okWhere = okOverlay.getY();
				} else if (align == ButtonAlignment.ALIGN_HORIZONTAL) {
					newLocation.setY(okOverlay.getY());
					okWhere = okOverlay.getX();
				}
			}
			ImageOverlay cancelOverlay = _selection.getSelectedImage().getOverlay(OverlayType.OLCANCEL);
			if (cancelOverlay != null) {
				if (align == ButtonAlignment.ALIGN_VERTICAL) {
					newLocation.setX(cancelOverlay.getX());
					cancelWhere = cancelOverlay.getY();
				} else if (align == ButtonAlignment.ALIGN_HORIZONTAL) {
					newLocation.setY(cancelOverlay.getY());
					cancelWhere = cancelOverlay.getX();
				}
			}
			ImageOverlay notesOverlay;
			if (_selection.getSelectedImage().getSubject() instanceof au.org.ala.delta.model.Character)
				notesOverlay = _selection.getSelectedImage().getOverlay(OverlayType.OLNOTES);
			else
				notesOverlay = _selection.getSelectedImage().getOverlay(OverlayType.OLIMAGENOTES);
			if (notesOverlay != null) {
				if (align == ButtonAlignment.ALIGN_VERTICAL) {
					newLocation.setX(notesOverlay.getX());
					notesWhere = notesOverlay.getY();
				} else if (align == ButtonAlignment.ALIGN_HORIZONTAL) {
					newLocation.setY(notesOverlay.getY());
					notesWhere = notesOverlay.getX();
				}
			}
			int newWhere = Integer.MIN_VALUE;
			int size = 0;
			if (align == ButtonAlignment.ALIGN_VERTICAL)
				size = bhClient;
			else if (align == ButtonAlignment.ALIGN_HORIZONTAL)
				size = bwClient;
			int space = size + (size + 1) / 2;
			if (anOverlay.type == OverlayType.OLOK) {
				if (cancelWhere != Integer.MIN_VALUE && notesWhere != Integer.MIN_VALUE)
					newWhere = cancelWhere - Math.abs(notesWhere - cancelWhere);
				else if (cancelWhere != Integer.MIN_VALUE || notesWhere != Integer.MIN_VALUE)
					newWhere = Math.max(cancelWhere, notesWhere) - space;
			} else if (anOverlay.type == OverlayType.OLCANCEL) {
				if (okWhere != Integer.MIN_VALUE && notesWhere != Integer.MIN_VALUE)
					newWhere = (okWhere + notesWhere) / 2;
				else if (okWhere != Integer.MIN_VALUE)
					newWhere = okWhere + space;
				else if (notesWhere != Integer.MIN_VALUE)
					newWhere = notesWhere - space;
			}
			if (anOverlay.type == OverlayType.OLNOTES || anOverlay.type == OverlayType.OLIMAGENOTES) {
				if (okWhere != Integer.MIN_VALUE && cancelWhere != Integer.MIN_VALUE)
					newWhere = cancelWhere + Math.abs(cancelWhere - okWhere);
				else if (okWhere != Integer.MIN_VALUE || cancelWhere != Integer.MIN_VALUE)
					newWhere = Math.max(okWhere, cancelWhere) + space;
			}
			if (newWhere + size > 1000)
				newWhere = 1000 - size;
			if (newWhere != Integer.MIN_VALUE) {
				if (newWhere < 0)
					newWhere = 0;
				if (align == ButtonAlignment.ALIGN_VERTICAL)
					newLocation.setY(newWhere);
				else if (align == ButtonAlignment.ALIGN_HORIZONTAL)
					newLocation.setX(newWhere);
			}
		} else if (anOverlay.type == OverlayType.OLHOTSPOT)
			newLocation.setW(Math.min(200, 1000 - newLocation.X));
		else
			newLocation.setW(Math.min(300, 1000 - newLocation.X));
		
	}
}

package au.org.ala.delta.editor.ui.image;

import java.beans.Beans;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.DeltaTestCase;
import au.org.ala.delta.editor.model.EditorDataModel;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;
import au.org.ala.delta.editor.slotfile.model.SlotFileRepository;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.OverlayType;

/**
 * Tests the ImageOverlayEditorController.
 */
public class ImageOverlayEditorControllerTest extends DeltaTestCase {

	private ImageOverlayEditorController _controller;
	private EditorDataModel _model;
	private ImageEditorSelectionModel _selection;
	private Image _image;
	
	@Before
	public void setUp() {
		Beans.setDesignTime(true);
		SlotFileRepository repo = new SlotFileRepository();
		SlotFileDataSet dataSet = (SlotFileDataSet) repo.newDataSet();
		_model = new EditorDataModel(dataSet);
		_selection = new ImageEditorSelectionModel();
		_controller = new ImageOverlayEditorController(_selection, _model, null) {
			public void editSelectedOverlay() {}
			protected void editOverlay(ImageOverlay overlay) {}
			protected boolean confirmDeleteAllOverlays() {
				return true;
			}
		};
		MultiStateCharacter character = (MultiStateCharacter)_model.addCharacter(CharacterType.UnorderedMultiState);
		character.setNumberOfStates(2);
		_image = character.addImage("test.jpg", "");
		_selection.setSelectedImage(_image);
	}

	@After
	public void tearDown() {
		_model.close();
	}

	@Test
	public void testDeleteAllOverlays() {
		_image.addOverlay(OverlayType.OLTEXT);
		_controller.deleteAllOverlays();
		
		assertEquals(0, _image.getOverlays().size());
	}
	
	@Test
	public void testAddTextOverlay() {
		_controller.addTextOverlay();
		ImageOverlay overlay = _image.getOverlay(OverlayType.OLTEXT);
		assertEquals(350, overlay.getX());
		assertEquals(450, overlay.getY());
		assertEquals(300, overlay.getWidth());
		assertEquals(-1, overlay.getHeight());
	}

	
	@Test
	public void testAddAllUsualOverlays() {
		_controller.addAllUsualOverlays();
		System.out.println(_image.getOverlays());
	}
}

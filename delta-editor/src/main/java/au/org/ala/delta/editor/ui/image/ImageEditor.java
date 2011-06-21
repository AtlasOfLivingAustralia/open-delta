package au.org.ala.delta.editor.ui.image;

import java.awt.BorderLayout;

import javax.swing.ActionMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;

import au.org.ala.delta.editor.DeltaView;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.ui.ReorderableList;
import au.org.ala.delta.editor.ui.util.MenuBuilder;
import au.org.ala.delta.model.image.Image;

/**
 * Displays Character and Taxon images and allows the addition of 
 * ImageOverlays to the Image to assist with IntKey identifications.
 */
public class ImageEditor extends JInternalFrame implements DeltaView {

	private static final long serialVersionUID = 4867008707368683722L;

	private Image _image;
	private ActionMap _actionMap;
	
	public ImageEditor(EditorViewModel model) {
	
		_image = model.getSelectedImage();
		_actionMap = Application.getInstance().getContext().getActionMap(this);
		ImageEditorPanel viewer = new ImageEditorPanel(model.getImagePath(), _image, model);
		
		getContentPane().add(viewer, BorderLayout.CENTER);
		
		buildMenus();
	}
	
	private void buildMenus() {
		JMenuBar menuBar = new JMenuBar();
		
		menuBar.add(buildSubjectMenu());
		menuBar.add(buildControlMenu());
		menuBar.add(buildWindowMenu());
		
		setJMenuBar(menuBar);
	}
	
	/**
	 * Builds and returns the Subject menu.
	 * 
	 * @return a new JMenu ready to be added to the menu bar.
	 */
	private JMenu buildSubjectMenu() {
		JMenu subjectMenu = new JMenu();
		subjectMenu.setName("subjectMenu");

		JMenuItem subject = new JCheckBoxMenuItem(_image.getFileName());
		subject.setSelected(true);
		
		subjectMenu.add(subject);

		return subjectMenu;
	}
	
	/**
	 * Builds and returns the Control menu.
	 * 
	 * @return a new JMenu ready to be added to the menu bar.
	 */
	private JMenu buildControlMenu() {
		JMenu controlMenu = new JMenu();
		controlMenu.setName("controlMenu");

		String[] controlMenuActions = { "nextImage", "previousImage", "-"};

		MenuBuilder.buildMenu(controlMenu, controlMenuActions, _actionMap);

		return controlMenu;
	}
	
	/**
	 * Builds and returns the View menu.
	 * 
	 * @return a new JMenu ready to be added to the menu bar.
	 */
	private JMenu buildWindowMenu() {
		JMenu windowMenu = new JMenu();
		windowMenu.setName("windowMenu");

		String[] windowMenuActions = { "toggleScaling", "-", "togglePreviewMode"};

		MenuBuilder.buildMenu(windowMenu, windowMenuActions, _actionMap);

		return windowMenu;
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

	@Action
	public void nextImage() {}
	
	@Action
	public void previousImage() {}
	
	@Action
	public void toggleScaling() {}
	
	@Action
	public void togglePreviewMode() {}
	
	@Action
	public void nextItemWithImage() {}
	
	@Action
	public void previousItemWithImage() {}
	
	@Action
	public void nextCharacterWithImage() {}
	
	@Action
	public void previousCharacterWithImage() {}
	
}

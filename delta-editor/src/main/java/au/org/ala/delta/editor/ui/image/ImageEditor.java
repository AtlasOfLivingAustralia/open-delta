package au.org.ala.delta.editor.ui.image;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;

import au.org.ala.delta.editor.DeltaView;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.ui.ReorderableList;
import au.org.ala.delta.editor.ui.util.MenuBuilder;
import au.org.ala.delta.model.Illustratable;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.image.Image;

/**
 * Displays Character and Taxon images and allows the addition of 
 * ImageOverlays to the Image to assist with IntKey identifications.
 */
public class ImageEditor extends JInternalFrame implements DeltaView {

	private static final long serialVersionUID = 4867008707368683722L;

	private Image _selectedImage;
	private Illustratable _subject;
	private ActionMap _actionMap;
	private CardLayout _layout;
	private EditorViewModel _model;
	
	private Map<String, ImageEditorPanel> _imageEditors;
	
	public ImageEditor(EditorViewModel model) {
	
		_selectedImage = model.getSelectedImage();
		_model = model;
		_subject = _selectedImage.getSubject();
		_imageEditors = new HashMap<String, ImageEditorPanel>();
		
		setName("ImageEditor-"+_subject.toString());
		_actionMap = Application.getInstance().getContext().getActionMap(this);
		
		_layout = new CardLayout();
		getContentPane().setLayout(_layout);
		
		addCardFor(_selectedImage);
		
		buildMenus();
	}
	
	private void buildMenus() {
		JMenuBar menuBar = new JMenuBar();
		
		menuBar.add(buildSubjectMenu());
		menuBar.add(buildControlMenu());
		menuBar.add(buildWindowMenu());
		
		setJMenuBar(menuBar);
	}
	
	private void addCardFor(Image image) {
		ImageEditorPanel viewer = new ImageEditorPanel(_model.getImagePath(), image, _model);
		String text = subjectTextOrFileName(image);
		
		_imageEditors.put(text, viewer);
		
		getContentPane().add(viewer, text);
		
	}
	
	/**
	 * Builds and returns the Subject menu.
	 * 
	 * @return a new JMenu ready to be added to the menu bar.
	 */
	private JMenu buildSubjectMenu() {
		JMenu subjectMenu = new JMenu();
		subjectMenu.setName("subjectMenu");

		List<Image> images = _subject.getImages();
		ButtonGroup group = new ButtonGroup();
		for (final Image image : images) {
			String text = subjectTextOrFileName(image);
			JMenuItem subject = new JCheckBoxMenuItem(text);
			group.add(subject);
			subject.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					displayImage(image);
				}
			});
			if (image.equals(_selectedImage)) {
				subject.setSelected(true);
			}
			subjectMenu.add(subject);
		}
		
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
		
		String[] controlMenuActions = { 
				"nextImage", "previousImage", "-",
				"nextCharacterWithImage", "previousCharacterWithImage", "-",
				"showFullImageText", "showImageNotes"};
		
		
		if (_selectedImage.getSubject() instanceof Item) {
			controlMenuActions[4] = "nextItemWithImage";
			controlMenuActions[5] = "previousItemWithImage";
		}

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

		String[] windowMenuActions = { 
				"toggleScaling", "toggleHideText", "toggleHideHotSpots", 
				"replaySound", "replayVideo", "-", 
				"reloadImage", "fitToImage", "fullScreen", "-",
				"togglePreviewMode", "-",
				"aboutImage", "-",
				"closeImage"};

		MenuBuilder.buildMenu(windowMenu, windowMenuActions, _actionMap);

		return windowMenu;
	}
	
	@Override
	public String getViewTitle() {
		return _selectedImage.getFileName();
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

	public void displayImage(Image image) {
		String text = subjectTextOrFileName(image);
		if (!_imageEditors.containsKey(text)) {
			addCardFor(image);
		}
		_layout.show(getContentPane(), text);
	}
	
	public String subjectTextOrFileName(Image image) {
		String text = image.getSubjectText();
		if (StringUtils.isEmpty(text)) {
			text = image.getFileName();
		}
		return text;
	}
	
	@Action
	public void nextImage() {}
	
	@Action
	public void previousImage() {}
	
	
	@Action
	public void nextItemWithImage() {}
	
	@Action
	public void previousItemWithImage() {}
	
	@Action
	public void nextCharacterWithImage() {}
	
	@Action
	public void previousCharacterWithImage() {}
	
	@Action
	public void showFullImageText() {}
	
	@Action
	public void showImageNotes() {}
	
	@Action
	public void toggleScaling() {}
	
	@Action
	public void toggleHideText() {}
	
	@Action
	public void toggleHideHotSpots() {}
	
	@Action
	public void replaySound() {}
	
	@Action
	public void replayVideo() {}
	
	@Action
	public void reloadImage() {}
	
	@Action
	public void fitToImage() {}
	
	@Action
	public void fullScreen() {}
	
	@Action
	public void togglePreviewMode() {}
	
	@Action
	public void aboutImage() {}
	
	@Action
	public void closeImage() {}
}

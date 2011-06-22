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
	private List<Image> _images;
	
	private Map<String, ImageEditorPanel> _imageEditors;
	
	public ImageEditor(EditorViewModel model) {
	
		_selectedImage = model.getSelectedImage();
		_model = model;
		_subject = _selectedImage.getSubject();
		_images = _subject.getImages();
		_imageEditors = new HashMap<String, ImageEditorPanel>();
		
		setName("ImageEditor-"+_subject.toString());
		_actionMap = Application.getInstance().getContext().getActionMap(this);
		
		_layout = new CardLayout();
		getContentPane().setLayout(_layout);
		
		displayImage(_selectedImage);
		
		buildMenus();
	}
	
	private void buildMenus() {
		JMenuBar menuBar = new JMenuBar();
		
		menuBar.add(buildSubjectMenu());
		menuBar.add(buildControlMenu());
		menuBar.add(buildWindowMenu());
		
		if (_subject.getImages().size() <= 1) {
			_actionMap.get("nextImage").setEnabled(false);
			_actionMap.get("previousImage").setEnabled(false);
		}
		
		setJMenuBar(menuBar);
	}
	
	/**
	 * Creates an ImageEditorPanel for the supplied Image and adds it to the
	 * layout.
	 * @param image the image to add.
	 */
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

		ButtonGroup group = new ButtonGroup();
		for (final Image image : _images) {
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
	
	/**
	 * @param image the image to get the text for.
	 * @return the subject text of an image, or the filename if none has
	 * been specified.
	 */
	private String subjectTextOrFileName(Image image) {
		String text = image.getSubjectText();
		if (StringUtils.isEmpty(text)) {
			text = image.getFileName();
		}
		return text;
	}

	/**
	 * Creates an ImageEditorPanel to display the image if necessary then
	 * switches the layout to display the image.
	 * @param image the image to display.
	 */
	public void displayImage(Image image) {
		String text = subjectTextOrFileName(image);
		if (!_imageEditors.containsKey(text)) {
			addCardFor(image);
		}
		_selectedImage = image;
		int index = _images.indexOf(_selectedImage);
		_actionMap.get("nextImage").setEnabled(index < (_images.size()-1));
		_actionMap.get("previousImage").setEnabled(index > 0);
		_layout.show(getContentPane(), text);
	}
	
	
	/**
	 * Displays the next image of the current subject (Character or Item)
	 */
	@Action
	public void nextImage() {
		int nextIndex = _images.indexOf(_selectedImage) + 1;
		if (nextIndex < _images.size()) {
			displayImage(_images.get(nextIndex));
		}
	}
	
	/**
	 * Displays the previous image of the current subject (Character or Item)
	 */
	@Action
	public void previousImage() {
		int prevIndex = _images.indexOf(_selectedImage) -1;
		if (prevIndex >= 0) {
			displayImage(_images.get(prevIndex));
		}
	}
	
	
	@Action
	public void nextItemWithImage() {
		Item item = (Item)_subject;
		int itemNumber = item.getItemNumber();
		
		for (int i=itemNumber+1; i<=_model.getMaximumNumberOfItems(); i++) {
			Item next = _model.getItem(i);
			
		}
		
	}
	
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

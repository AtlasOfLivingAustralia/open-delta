package au.org.ala.delta.editor.ui.image;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GraphicsDevice;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;

import au.org.ala.delta.editor.DeltaView;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.ui.ReorderableList;
import au.org.ala.delta.editor.ui.util.MenuBuilder;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Illustratable;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.OverlayType;
import au.org.ala.delta.ui.image.ImagePanel.ScalingMode;
import au.org.ala.delta.ui.image.OverlaySelectionObserver;
import au.org.ala.delta.ui.image.SelectableOverlay;
import au.org.ala.delta.util.DataSetHelper;

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
	private JMenu _subjectMenu;
	private ScalingMode _scalingMode;
	private JPanel _contentPanel;
	private boolean _hideHotSpots;
	private boolean _hideTextOverlays;
	private boolean _previewMode;
	private Map<String, ImageEditorPanel> _imageEditors;
	private DataSetHelper _helper;
	private PreviewController _previewController;
	
	public ImageEditor(EditorViewModel model) {

		_model = model;
		_helper = new DataSetHelper(model);
		_actionMap = Application.getInstance().getContext().getActionMap(this);
		_layout = new CardLayout();
		_contentPanel = new JPanel();
		_contentPanel.setLayout(_layout);
		_scalingMode = ScalingMode.FIXED_ASPECT_RATIO;
		_hideHotSpots = false;
		_hideTextOverlays = false;
		_previewMode = false;
		_previewController = new PreviewController();
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(_contentPanel, BorderLayout.CENTER);
		
		_subjectMenu = new JMenu();
		_subjectMenu.setName("subjectMenu");
		
		Image image =  model.getSelectedImage();
		Illustratable subject = image.getSubject();
		if (subject instanceof Character) {
			displaySubject((Character)subject, image);
		}
		else {
			displaySubject((Item)subject, image);
		}
		
		
		setName("ImageEditor-"+_subject.toString());
		
		buildMenus();
	}
	
	private void buildMenus() {
		JMenuBar menuBar = new JMenuBar();
		
		menuBar.add(_subjectMenu);
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
		
		_contentPanel.add(viewer, text);	
	}
	
	/**
	 * Builds and returns the Subject menu.
	 * 
	 * @return a new JMenu ready to be added to the menu bar.
	 */
	private void buildSubjectMenu() {
		_subjectMenu.removeAll();
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
			_subjectMenu.add(subject);
		}
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
				"*toggleScaling", "*toggleHideText", "*toggleHideHotSpots", 
				"replaySound", "replayVideo", "-", 
				"reloadImage", "fitToImage", "fullScreen", "-",
				"*togglePreviewMode", "-",
				"aboutImage", "-",
				"closeImage"};

		JMenuItem[] items = MenuBuilder.buildMenu(windowMenu, windowMenuActions, _actionMap);
		((JCheckBoxMenuItem)items[0]).setSelected(true);
		
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

	
	private void displaySubject(Character subject, Image image) {
		displaySubject((Illustratable)subject, image);
		
		Character character = _helper.getNextCharacterWithImage((Character)_subject);
		_actionMap.get("nextCharacterWithImage").setEnabled(character != null);
		
		character = _helper.getPreviousCharacterWithImage((Character)_subject);
		_actionMap.get("previousCharacterWithImage").setEnabled(character != null);
	}
	
	private void displaySubject(Item subject, Image image) {
		displaySubject((Illustratable)subject, image);
		
		Item item = _helper.getNextItemWithImage((Item)_subject);
		_actionMap.get("nextItemWithImage").setEnabled(item != null);
		
		item = _helper.getPreviousItemWithImage((Item)_subject);
		_actionMap.get("previousItemWithImage").setEnabled(item != null);
	}
	
	private void displaySubject(Illustratable subject, Image image) {
		_subject = image.getSubject();
		_images = _subject.getImages();
		_contentPanel.removeAll();
		
		_imageEditors = new HashMap<String, ImageEditorPanel>();
		
		displayImage(image);
		
		buildSubjectMenu();
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
		_layout.show(_contentPanel, text);
		revalidate();
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
		Item item = _helper.getNextItemWithImage((Item)_subject);
		if (item != null) {
			displaySubject(item, item.getImages().get(0));
		}	
	}
	
	@Action
	public void previousItemWithImage() {
		
		Item item = _helper.getPreviousItemWithImage((Item)_subject);
		if (item != null) {
			displaySubject(item, item.getImages().get(0));
		}	
	}
	
	
	@Action
	public void nextCharacterWithImage() {
		Character character = _helper.getNextCharacterWithImage((Character)_subject);
		if (character != null) {
			displaySubject(character, character.getImages().get(0));	
		}
	}

	@Action
	public void previousCharacterWithImage() {
		Character character = _helper.getPreviousCharacterWithImage((Character)_subject);
		if (character != null) {
			displaySubject(character, character.getImages().get(0));	
		}
	}
	
	@Action
	public void showFullImageText() {}
	
	@Action
	public void showImageNotes() {}
	
	@Action
	public void toggleScaling() {
		if (_scalingMode == ScalingMode.NO_SCALING) {
			setScalingMode(ScalingMode.FIXED_ASPECT_RATIO);
		}
		else {
			setScalingMode(ScalingMode.NO_SCALING);
		}
	}
	
	private void setScalingMode(ScalingMode mode) {
		if (mode == ScalingMode.NO_SCALING) {
			getContentPane().remove(_contentPanel);
			getContentPane().add(new JScrollPane(_contentPanel), BorderLayout.CENTER);
		}
		else if (_scalingMode == ScalingMode.NO_SCALING){
			getContentPane().removeAll();
			getContentPane().add(_contentPanel, BorderLayout.CENTER);
		}
		_scalingMode = mode;
		for (ImageEditorPanel editor : _imageEditors.values()) {
			editor.setScalingMode(mode);
		}
		revalidate();
	}
	
	@Action
	public void toggleHideText() {
		setHideTextOverlays(!_hideTextOverlays);
	}
	
	private void setHideTextOverlays(boolean hideTextOverlays) {
		if (_hideTextOverlays != hideTextOverlays) {
			_hideTextOverlays = hideTextOverlays;
			for (ImageEditorPanel editor : _imageEditors.values()) {
				editor.setDisplayTextOverlays(!hideTextOverlays);
			}
		}
	}
	
	@Action
	public void toggleHideHotSpots() {
		setHideHotSpots(!_hideHotSpots);
	}
	
	public void setHideHotSpots(boolean hideHotSpots) {
		if (_hideHotSpots != hideHotSpots) {
			_hideHotSpots = hideHotSpots;
			for (ImageEditorPanel editor : _imageEditors.values()) {
				editor.setDisplayHotSpots(!hideHotSpots);
			}
		}
	}
	
	@Action
	public void replaySound() {}
	
	@Action
	public void replayVideo() {}
	
	/**
	 * Reloads the image from disk in response to the menu selection.
	 */
	@Action
	public void reloadImage() {
		String key = subjectTextOrFileName(_selectedImage);
		ImageEditorPanel editor = _imageEditors.get(key);
		_imageEditors.remove(key);
		remove(editor);
		
		displayImage(_selectedImage);
		revalidate();
	}
	
	/**
	 * Resizes this JInternalFrame so that the image is displayed at 
	 * it's natural size.
	 */
	@Action
	public void fitToImage() {
		pack();
	}
	
	/**
	 * Displays the image in a full screen window.  Clicking the mouse
	 * will dismiss the window and return to normal mode.
	 */
	@Action
	public void fullScreen() {
		Window parent = SwingUtilities.getWindowAncestor(this);
		final Window w = new Window(parent);
		w.setLayout(new BorderLayout());
		
		final String key = subjectTextOrFileName(_selectedImage);
		final ImageEditorPanel editor = _imageEditors.get(key);
		w.add(editor, BorderLayout.CENTER);
		final GraphicsDevice gd = parent.getGraphicsConfiguration().getDevice();
		
		w.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
						
				w.dispose();
				gd.setFullScreenWindow(null);
				add(editor, key);
				_layout.show(_contentPanel, key);
				revalidate();
			}
		});
		
		gd.setFullScreenWindow(w);
	}
	
	@Action
	public void togglePreviewMode() {
		setPreviewMode(!_previewMode);
	}
	
	private void setPreviewMode(boolean preview) {
		if (_previewMode != preview) {
			_previewMode = preview;
					
			setHideHotSpots(preview);
			for (ImageEditorPanel editor : _imageEditors.values()) {
				editor.setEditingEnabled(!preview);
				if (!preview) {
					editor.removeOverlaySelectionObserver(_previewController);
				}
				else {
					editor.addOverlaySelectionObserver(_previewController);
				}
			}
		}
	}
	
	@Action
	public void aboutImage() {}
	
	@Action
	public void closeImage() {
		try {
			setClosed(true);
		}
		catch (PropertyVetoException e) {}
	}
	
	class PreviewController implements OverlaySelectionObserver {

		@Override
		public void overlaySelected(SelectableOverlay overlay) {
			overlay.setSelected(!overlay.isSelected());
			ImageOverlay imageOverlay = overlay.getImageOverlay();
			if (imageOverlay.isType(OverlayType.OLOK) ||
				imageOverlay.isType(OverlayType.OLCANCEL)) {
				closeImage();
			}
					
		}
		
	}
}

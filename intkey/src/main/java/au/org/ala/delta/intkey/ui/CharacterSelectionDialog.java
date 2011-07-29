package au.org.ala.delta.intkey.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.border.EmptyBorder;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageOverlayParser;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.image.ImageType;
import au.org.ala.delta.model.impl.DefaultImageData;
import au.org.ala.delta.model.impl.ImageData;
import au.org.ala.delta.ui.image.ImageViewer;

public class CharacterSelectionDialog extends ListSelectionDialog {

    private List<Character> _selectedCharacters;
    private JButton _btnOk;
    private JButton _btnSelectAll;
    private JButton _btnKeywords;
    private JButton _btnImages;
    private JButton _btnSearch;
    private JButton _btnCancel;
    private JButton _btnDeselectAll;
    private JButton _btnFullText;
    private JButton _btnNotes;
    private JButton _btnHelp;

    private CharacterListModel _listModel;

    // The name of the directive being processed
    private String _directiveName;

    // The name of the keyword that these characters belong to
    private String _keyword;

    // controls display of character images
    private ImageSettings _imageSettings;

    @Resource
    String title;

    @Resource
    String titleFromKeyword;

    public CharacterSelectionDialog(Dialog owner, List<Character> characters, String directiveName, String keyword, ImageSettings imageSettings) {
        this(owner, characters, directiveName, imageSettings);
        _keyword = keyword;

        setTitle(String.format(titleFromKeyword, _directiveName, _keyword));
    }

    public CharacterSelectionDialog(Frame owner, List<Character> characters, String directiveName, String keyword, ImageSettings imageSettings) {
        this(owner, characters, directiveName, imageSettings);
        _keyword = keyword;
        _imageSettings= imageSettings;
        setTitle(String.format(titleFromKeyword, _directiveName, _keyword));
    }

    public CharacterSelectionDialog(Dialog owner, List<Character> characters, String directiveName, ImageSettings imageSettings) {
        super(owner);
        _directiveName = directiveName;
        _imageSettings= imageSettings;
        init(characters);
    }

    public CharacterSelectionDialog(Frame owner, List<Character> characters, String directiveName, ImageSettings imageSettings) {
        super(owner);
        _directiveName = directiveName;
        _imageSettings= imageSettings;
        init(characters);
    }

    private void init(List<Character> characters) {
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(CharacterSelectionDialog.class);
        resourceMap.injectFields(this);
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(CharacterSelectionDialog.class, this);

        setTitle(String.format(title, _directiveName));

        _panelButtons.setBorder(new EmptyBorder(0, 20, 10, 20));
        _panelButtons.setLayout(new GridLayout(0, 5, 5, 2));

        _btnOk = new JButton();
        _btnOk.setAction(actionMap.get("characterSelectionDialog_OK"));
        _panelButtons.add(_btnOk);

        _btnSelectAll = new JButton();
        _btnSelectAll.setAction(actionMap.get("characterSelectionDialog_SelectAll"));
        _panelButtons.add(_btnSelectAll);

        _btnKeywords = new JButton();
        _btnKeywords.setAction(actionMap.get("characterSelectionDialog_Keywords"));
        _btnKeywords.setEnabled(false);
        _panelButtons.add(_btnKeywords);

        _btnImages = new JButton();
        _btnImages.setAction(actionMap.get("characterSelectionDialog_Images"));
        _panelButtons.add(_btnImages);

        _btnSearch = new JButton();
        _btnSearch.setAction(actionMap.get("characterSelectionDialog_Search"));
        _btnSearch.setEnabled(false);
        _panelButtons.add(_btnSearch);

        _btnCancel = new JButton();
        _btnCancel.setAction(actionMap.get("characterSelectionDialog_Cancel"));
        _panelButtons.add(_btnCancel);

        _btnDeselectAll = new JButton();
        _btnDeselectAll.setAction(actionMap.get("characterSelectionDialog_DeselectAll"));
        _panelButtons.add(_btnDeselectAll);

        _btnFullText = new JButton();
        _btnFullText.setAction(actionMap.get("characterSelectionDialog_FullText"));
        _btnFullText.setEnabled(false);
        _panelButtons.add(_btnFullText);

        _btnNotes = new JButton();
        _btnNotes.setAction(actionMap.get("characterSelectionDialog_Notes"));
        _btnNotes.setEnabled(false);
        _panelButtons.add(_btnNotes);

        _btnHelp = new JButton();
        _btnHelp.setAction(actionMap.get("characterSelectionDialog_Help"));
        _btnHelp.setEnabled(false);
        _panelButtons.add(_btnHelp);

        _selectedCharacters = new ArrayList<Character>();

        if (characters != null) {
            _listModel = new CharacterListModel(characters);
            _list.setModel(_listModel);
        }
    }

    @Action
    public void characterSelectionDialog_OK() {
        for (int i : _list.getSelectedIndices()) {
            _selectedCharacters.add(_listModel.getCharacterAt(i));
        }

        this.setVisible(false);
    }

    @Action
    public void characterSelectionDialog_SelectAll() {
        _list.setSelectionInterval(0, _listModel.getSize());
    }

    @Action
    public void characterSelectionDialog_Keywords() {

    }

    @Action
    public void characterSelectionDialog_Images() {
        Character ch = (Character) _listModel.getCharacterAt(_list.getSelectedIndex());
        
        String imageDataString = ch.getImageData();
        int firstOpenBracketIndex = imageDataString.indexOf('<');
        String fileName = imageDataString.substring(0, firstOpenBracketIndex).trim();
        String overlayData = imageDataString.substring(firstOpenBracketIndex).trim();
        System.out.println(overlayData);

        try {
            List<ImageOverlay> overlayList = new ImageOverlayParser().parseOverlays(overlayData, ImageType.IMAGE_CHARACTER);

            ImageData imageData = new DefaultImageData(fileName);
            imageData.setOverlays(overlayList);

            Image img = new Image(imageData);
            img.setSubject(ch);

            ImageViewer viewer = new ImageViewer("src/test/resources/dataset/sample/images", img, _imageSettings);
            JDialog dlg = new JDialog(this, true);
            dlg.add(viewer);
            dlg.setVisible(true);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

    }

    @Action
    public void characterSelectionDialog_Search() {

    }

    @Action
    public void characterSelectionDialog_Cancel() {
        this.setVisible(false);
    }

    @Action
    public void characterSelectionDialog_DeselectAll() {
        _list.clearSelection();
    }

    @Action
    public void characterSelectionDialog_FullText() {

    }

    @Action
    public void characterSelectionDialog_Notes() {

    }

    @Action
    public void characterSelectionDialog_Help() {

    }

    public List<Character> getSelectedCharacters() {
        return new ArrayList<Character>(_selectedCharacters);
    }
}

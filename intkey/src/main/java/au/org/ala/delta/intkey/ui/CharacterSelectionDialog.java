/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.intkey.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.ReportUtils;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.rtf.RTFBuilder;
import au.org.ala.delta.ui.rtf.SimpleRtfEditorKit;

public class CharacterSelectionDialog extends ListSelectionDialog implements SearchableListDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -3782045491747441657L;
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

    private DefaultListModel _listModel;

    // The name of the directive being processed
    private String _directiveName;

    // The name of the keyword that these characters belong to
    private String _keyword;

    private IntkeyContext _context;

    // controls display of character images
    private ImageSettings _imageSettings;

    // names of keywords selected via the "keywords" button;
    private List<String> _selectedKeywords;

    @Resource
    String title;

    @Resource
    String titleFromKeyword;

    @Resource
    String fullTextOfCharacterCaption;

    @Resource
    String notesCaption;

    public CharacterSelectionDialog(Dialog owner, List<Character> characters, String directiveName, String keyword, ImageSettings imageSettings, boolean displayNumbering, IntkeyContext context) {
        this(owner, characters, directiveName, imageSettings, displayNumbering, context);
        _keyword = keyword;

        setTitle(MessageFormat.format(titleFromKeyword, _directiveName, _keyword));
    }

    public CharacterSelectionDialog(Frame owner, List<Character> characters, String directiveName, String keyword, ImageSettings imageSettings, boolean displayNumbering, IntkeyContext context) {
        this(owner, characters, directiveName, imageSettings, displayNumbering, context);
        _keyword = keyword;
        _imageSettings = imageSettings;
        setTitle(MessageFormat.format(titleFromKeyword, _directiveName, _keyword));
    }

    public CharacterSelectionDialog(Dialog owner, List<Character> characters, String directiveName, ImageSettings imageSettings, boolean displayNumbering, IntkeyContext context) {
        super(owner);
        _directiveName = directiveName;
        _imageSettings = imageSettings;
        _context = context;
        init(characters, displayNumbering);
    }

    public CharacterSelectionDialog(Frame owner, List<Character> characters, String directiveName, ImageSettings imageSettings, boolean displayNumbering, IntkeyContext context) {
        super(owner);
        _directiveName = directiveName;
        _imageSettings = imageSettings;
        _context = context;
        init(characters, displayNumbering);
    }

    private void init(List<Character> characters, boolean displayNumbering) {
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(CharacterSelectionDialog.class);
        resourceMap.injectFields(this);
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(CharacterSelectionDialog.class, this);

        setTitle(MessageFormat.format(title, _directiveName));

        _panelButtons.setBorder(new EmptyBorder(0, 20, 10, 20));
        _panelButtons.setLayout(new GridLayout(0, 5, 5, 5));

        _btnOk = new JButton();
        _btnOk.setAction(actionMap.get("characterSelectionDialog_OK"));
        _panelButtons.add(_btnOk);

        _btnSelectAll = new JButton();
        _btnSelectAll.setAction(actionMap.get("characterSelectionDialog_SelectAll"));
        _panelButtons.add(_btnSelectAll);

        _btnKeywords = new JButton();
        _btnKeywords.setAction(actionMap.get("characterSelectionDialog_Keywords"));
        _panelButtons.add(_btnKeywords);

        _btnImages = new JButton();
        _btnImages.setAction(actionMap.get("characterSelectionDialog_Images"));
        _btnImages.setEnabled(false);
        _panelButtons.add(_btnImages);

        _btnSearch = new JButton();
        _btnSearch.setAction(actionMap.get("characterSelectionDialog_Search"));
        _panelButtons.add(_btnSearch);

        _btnCancel = new JButton();
        _btnCancel.setAction(actionMap.get("characterSelectionDialog_Cancel"));
        _panelButtons.add(_btnCancel);

        _btnDeselectAll = new JButton();
        _btnDeselectAll.setAction(actionMap.get("characterSelectionDialog_DeselectAll"));
        _panelButtons.add(_btnDeselectAll);

        _btnFullText = new JButton();
        _btnFullText.setAction(actionMap.get("characterSelectionDialog_FullText"));
        _panelButtons.add(_btnFullText);

        _btnNotes = new JButton();
        _btnNotes.setAction(actionMap.get("characterSelectionDialog_Notes"));
        _btnNotes.setEnabled(false);
        _panelButtons.add(_btnNotes);

        _btnHelp = new JButton();
        _btnHelp.setAction(actionMap.get("characterSelectionDialog_Help"));
        _panelButtons.add(_btnHelp);

        // Some of the buttons should not be displayed if not in advanced mode
        if (_context.getUI().isAdvancedMode()) {
            _panelButtons.add(_btnOk);
            _panelButtons.add(_btnSelectAll);
            _panelButtons.add(_btnKeywords);
            _panelButtons.add(_btnImages);
            _panelButtons.add(_btnSearch);
            _panelButtons.add(_btnCancel);
            _panelButtons.add(_btnDeselectAll);
            _panelButtons.add(_btnFullText);
            _panelButtons.add(_btnNotes);
            _panelButtons.add(_btnHelp);
        } else {
            _panelButtons.setLayout(new GridLayout(0, 4, 5, 0));
            _panelButtons.add(_btnOk);
            _panelButtons.add(_btnCancel);
            _panelButtons.add(_btnSelectAll);
            _panelButtons.add(_btnDeselectAll);
        }

        _selectedCharacters = null;

        if (characters != null) {
            _listModel = new DefaultListModel();
            for (Character ch : characters) {
                _listModel.addElement(ch);
            }
            _list.setCellRenderer(new CharacterCellRenderer(displayNumbering));
            _list.setModel(_listModel);
        }

        _list.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (_list.getSelectedIndices().length == 1) {
                    Character ch = (Character) _list.getSelectedValue();
                    _btnImages.setEnabled(ch.getImages().size() > 0);
                    _btnFullText.setEnabled(true);
                    _btnNotes.setEnabled(StringUtils.isNotBlank(ch.getNotes()));
                } else {
                    _btnImages.setEnabled(false);
                    _btnFullText.setEnabled(false);
                    _btnNotes.setEnabled(false);
                }
            }
        });

        _list.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    // Treat double click on a list item as the ok button being
                    // pressed.
                    characterSelectionDialog_OK();
                }
            }

        });

        _selectedKeywords = new ArrayList<String>();
    }

    @Action
    public void characterSelectionDialog_OK() {
        _selectedCharacters = new ArrayList<Character>();
        for (int i : _list.getSelectedIndices()) {
            _selectedCharacters.add((Character) _listModel.getElementAt(i));
        }

        this.setVisible(false);
    }

    @Action
    public void characterSelectionDialog_SelectAll() {
        _list.setSelectionInterval(0, _listModel.getSize() - 1);
    }

    @Action
    public void characterSelectionDialog_Keywords() {
        if (this.getOwner() instanceof CharacterKeywordSelectionDialog) {
            // If this window was spawned by a CharacterKeywordSelectionDialog
            // (keyword selection), don't
            // create another one.
            _selectedCharacters = null;
            this.setVisible(false);
        } else {
            CharacterKeywordSelectionDialog dlg = new CharacterKeywordSelectionDialog(this, _context, _directiveName, false);
            ((SingleFrameApplication) Application.getInstance()).show(dlg);
            if (dlg.getSelectedCharacters() != null) {
                _selectedCharacters = dlg.getSelectedCharacters();
                _selectedKeywords = dlg.getSelectedKeywords();
                this.setVisible(false);
            }
        }
    }

    @Action
    public void characterSelectionDialog_Images() {
        // images button will only be enabled if a single character is selected
        Character ch = (Character) _list.getSelectedValue();
        List<Character> charInList = new ArrayList<Character>();
        charInList.add(ch);
        CharacterImageDialog dlg = new CharacterImageDialog(this, charInList, null, _imageSettings, false, !_context.displayContinuous(), _context.displayScaled());
        dlg.displayImagesForCharacter(ch);
        dlg.showImage(0);
        ((SingleFrameApplication) Application.getInstance()).show(dlg);
    }

    @Action
    public void characterSelectionDialog_Search() {
        SimpleSearchDialog dlg = new SimpleSearchDialog(this, this);
        ((SingleFrameApplication) Application.getInstance()).show(dlg);
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
        // full text button will only be enabled if a single character is
        // selected
        Character ch = (Character) _list.getSelectedValue();

        String rtfFullCharacterText = ReportUtils.generateFullCharacterTextRTF(ch);

        RtfReportDisplayDialog rtfDlg = new RtfReportDisplayDialog(this, new SimpleRtfEditorKit(null), rtfFullCharacterText, fullTextOfCharacterCaption);
        ((SingleFrameApplication) Application.getInstance()).show(rtfDlg);
    }

    @Action
    public void characterSelectionDialog_Notes() {
        // notes button will only be enabled if a single character is
        // selected
        Character ch = (Character) _list.getSelectedValue();
        if (StringUtils.isNotBlank(ch.getNotes())) {
            RTFBuilder rtfBuilder = new RTFBuilder();
            rtfBuilder.startDocument();
            rtfBuilder.appendText(ch.getNotes());
            rtfBuilder.endDocument();

            RtfReportDisplayDialog rtfDlg = new RtfReportDisplayDialog(this, new SimpleRtfEditorKit(null), rtfBuilder.toString(), notesCaption);
            ((SingleFrameApplication) Application.getInstance()).show(rtfDlg);
        }
    }

    @Action
    public void characterSelectionDialog_Help(ActionEvent e) {
        UIUtils.displayHelpTopic(UIUtils.getHelpIDForDirective(_directiveName), this, e);
    }

    public List<Character> getSelectedCharacters() {
        return _selectedCharacters;
    }

    public List<String> getSelectedKeywords() {
        return _selectedKeywords;
    }

    @Override
    public int searchForText(String searchText, int startingIndex) {
        int matchedIndex = -1;

        CharacterFormatter formatter = new CharacterFormatter(false, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REMOVE_SURROUNDING_REPLACE_INNER, true, false);

        for (int i = startingIndex; i < _listModel.size(); i++) {
            Character ch = (Character) _listModel.getElementAt(i);
            String charText = formatter.formatCharacterDescription(ch);
            if (charText.trim().toLowerCase().contains(searchText.trim().toLowerCase())) {
                matchedIndex = i;
                _list.setSelectedIndex(i);
                _list.ensureIndexIsVisible(i);
                break;
            }
        }

        return matchedIndex;
    }
}

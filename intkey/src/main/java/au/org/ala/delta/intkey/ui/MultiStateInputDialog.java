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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import org.apache.commons.lang.ArrayUtils;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.image.ImageSettings;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import java.awt.SystemColor;
import javax.swing.border.EmptyBorder;

public class MultiStateInputDialog extends CharacterValueInputDialog implements SearchableListDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -7594452342030275494L;

    Set<Integer> _inputData;

    private JList _list;
    private DefaultListModel _listModel;
    private JScrollPane _scrollPane;

    @Resource
    String title;

    @Resource
    String selectionConfirmationMessage;

    @Resource
    String selectionConfirmationTitle;

    @Resource
    String setControllingCharacterMessage;

    private JPanel _pnlControllingCharacterMessage;
    private JLabel _lblWarningIcon;
    private JTextArea _txtControllingCharacterMessage;

    /**
     * ctor
     * 
     * @param owner
     *            Owner frame of dialog
     * @param ch
     *            the character whose states are being set
     * @param initialSelectedStates
     *            initial states that should be selected in the dialog. In
     *            general this should be any states already set for the
     *            character. In the case that this is a controlling character
     *            being set before its dependent character, all states that make
     *            the dependent character applicable should be selected.
     * @param dependentCharacter
     *            the dependent character - if the dialog is being used to set a
     *            controlling character before its dependent character, this
     *            argument should be a reference to the dependent character. In
     *            all other cases it should be null.
     * @param imageSettings
     *            image settings
     * @param displayNumbering
     *            true if numbering should be displayed
     * @param enableImagesButton
     *            the if the images button should be enabled
     * @param imagesStartScaled
     *            true if images should start scaled.
     */
    public MultiStateInputDialog(Frame owner, MultiStateCharacter ch, Set<Integer> initialSelectedStates, au.org.ala.delta.model.Character dependentCharacter, ImageSettings imageSettings,
            boolean displayNumbering, boolean enableImagesButton, boolean imagesStartScaled) {
        super(owner, ch, imageSettings, displayNumbering, enableImagesButton, imagesStartScaled);

        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(MultiStateInputDialog.class);
        resourceMap.injectFields(this);

        setTitle(title);
        setPreferredSize(new Dimension(600, 350));

        if (dependentCharacter != null) {
            _pnlControllingCharacterMessage = new JPanel();
            _pnlControllingCharacterMessage.setFocusable(false);
            _pnlControllingCharacterMessage.setBorder(new EmptyBorder(5, 0, 0, 0));
            _pnlMain.add(_pnlControllingCharacterMessage, BorderLayout.SOUTH);
            _pnlControllingCharacterMessage.setLayout(new BorderLayout(0, 0));

            _lblWarningIcon = new JLabel("");
            _lblWarningIcon.setFocusable(false);
            _lblWarningIcon.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
            _pnlControllingCharacterMessage.add(_lblWarningIcon, BorderLayout.WEST);

            _txtControllingCharacterMessage = new JTextArea();
            _txtControllingCharacterMessage.setText(MessageFormat.format(setControllingCharacterMessage, _formatter.formatCharacterDescription(dependentCharacter),
                    _formatter.formatCharacterDescription(ch)));
            _txtControllingCharacterMessage.setFocusable(false);
            _txtControllingCharacterMessage.setBorder(new EmptyBorder(0, 5, 0, 0));
            _txtControllingCharacterMessage.setEditable(false);
            _pnlControllingCharacterMessage.add(_txtControllingCharacterMessage);
            _txtControllingCharacterMessage.setWrapStyleWord(true);
            _txtControllingCharacterMessage.setFont(UIManager.getFont("Button.font"));
            _txtControllingCharacterMessage.setLineWrap(true);
            _txtControllingCharacterMessage.setBackground(SystemColor.control);
        }

        _scrollPane = new JScrollPane();
        _pnlMain.add(_scrollPane, BorderLayout.CENTER);

        _list = new JList();
        _scrollPane.setViewportView(_list);

        _listModel = new DefaultListModel();
        for (int i = 0; i < ch.getNumberOfStates(); i++) {
            _listModel.addElement(_formatter.formatState(ch, i + 1));
        }

        _list.setModel(_listModel);

        // Select the list items that correspond to the initial selected states.
        if (initialSelectedStates != null) {
            List<Integer> listIndiciesToSelect = new ArrayList<Integer>();
            for (int stateNumber : new ArrayList<Integer>(initialSelectedStates)) {
                listIndiciesToSelect.add(stateNumber - 1);
            }

            Integer[] wrappedPrimitivesList = listIndiciesToSelect.toArray(new Integer[initialSelectedStates.size()]);
            _list.setSelectedIndices(ArrayUtils.toPrimitive(wrappedPrimitivesList));
        }

        _inputData = new HashSet<Integer>();

    }

    @Override
    void handleBtnOKClicked() {
        int[] selectedIndicies = _list.getSelectedIndices();

        // Show confirmation dialog if all of the states have been
        // selected.
        if (selectedIndicies.length == _list.getModel().getSize()) {
            int dlgSelection = JOptionPane.showConfirmDialog(this, selectionConfirmationMessage, selectionConfirmationTitle, JOptionPane.YES_NO_OPTION);
            if (dlgSelection == JOptionPane.NO_OPTION) {
                return;
            }
        }

        for (int i : selectedIndicies) {
            _inputData.add(i + 1);
        }

        setVisible(false);
    }

    @Override
    void handleBtnCancelClicked() {
        _inputData = null;
        this.setVisible(false);
    }

    @Override
    void handleBtnImagesClicked() {
        CharacterImageDialog dlg = new CharacterImageDialog(this, Arrays.asList(new au.org.ala.delta.model.Character[] { _ch }), null, _imageSettings, true, true, _imagesStartScaled);
        dlg.displayImagesForCharacter(_ch);

        Set<Integer> selectedStatesInList = new HashSet<Integer>();
        for (int i : _list.getSelectedIndices()) {
            selectedStatesInList.add(i + 1);
        }

        dlg.setInitialSelectedStates(selectedStatesInList);
        dlg.showImage(0);
        dlg.setVisible(true);

        Set<Integer> selectedStates = dlg.getSelectedStates();
        if (selectedStates != null && dlg.okButtonPressed()) {
            _inputData.addAll(selectedStates);
            this.setVisible(false);
        }

    }

    public Set<Integer> getInputData() {
        return _inputData;
    }

    @Override
    public int searchForText(String searchText, int startingIndex) {
        int matchedIndex = -1;

        for (int i = startingIndex; i < _listModel.size(); i++) {
            String stateText = (String) _listModel.get(i);
            if (stateText.trim().toLowerCase().contains(searchText.trim().toLowerCase())) {
                matchedIndex = i;
                _list.setSelectedIndex(i);
                _list.ensureIndexIsVisible(i);
                break;
            }
        }

        return matchedIndex;
    }

    @Override
    void handleBtnSearchClicked() {
        SimpleSearchDialog dlg = new SimpleSearchDialog(this, this);
        ((SingleFrameApplication) Application.getInstance()).show(dlg);
    }

}

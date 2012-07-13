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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.image.ImageSettings;

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

    public MultiStateInputDialog(Frame owner, MultiStateCharacter ch, ImageSettings imageSettings, boolean displayNumbering, boolean enableImagesButton, boolean imagesStartScaled) {
        super(owner, ch, imageSettings, displayNumbering, enableImagesButton, imagesStartScaled);

        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(MultiStateInputDialog.class);
        resourceMap.injectFields(this);

        setTitle(title);
        setPreferredSize(new Dimension(600, 350));

        _scrollPane = new JScrollPane();
        _pnlMain.add(_scrollPane, BorderLayout.CENTER);

        _list = new JList();
        _scrollPane.setViewportView(_list);

        _listModel = new DefaultListModel();
        for (int i = 0; i < ch.getNumberOfStates(); i++) {
            _listModel.addElement(_formatter.formatState(ch, i + 1));
        }

        _list.setModel(_listModel);

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
        CharacterImageDialog dlg = new CharacterImageDialog(this, Arrays.asList(new au.org.ala.delta.model.Character[] { _ch }), _imageSettings, true, true, _imagesStartScaled);
        dlg.setVisible(true);
        dlg.showImage(0);

        Set<Integer> selectedStates = dlg.getSelectedStates();
        if (selectedStates != null) {
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

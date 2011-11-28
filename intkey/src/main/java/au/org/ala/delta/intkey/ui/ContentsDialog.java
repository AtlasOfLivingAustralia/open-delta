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
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;

import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class ContentsDialog extends JDialog {

    @Resource
    String title;

    // Use linked hash map to maintain order of keys
    private LinkedHashMap<String, String> _contentsMap;
    private IntkeyContext _context;
    private JPanel _listPanel;
    private JScrollPane _sclPnList;
    private JList _list;
    private JPanel _pnlButtons;
    private JButton _btnOk;
    private JButton _btnCancel;

    public ContentsDialog(Frame owner, LinkedHashMap<String, String> contentsMap, IntkeyContext context) {
        super(owner, true);

        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(ContentsDialog.class);
        resourceMap.injectFields(this);
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(ContentsDialog.class, this);

        setTitle(title);

        _listPanel = new JPanel();
        _listPanel.setBorder(new EmptyBorder(10, 10, 0, 10));
        getContentPane().add(_listPanel, BorderLayout.CENTER);
        _listPanel.setLayout(new BorderLayout(0, 0));

        _sclPnList = new JScrollPane();
        _listPanel.add(_sclPnList);

        _list = new JList();
        _list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _sclPnList.setViewportView(_list);

        _pnlButtons = new JPanel();
        getContentPane().add(_pnlButtons, BorderLayout.SOUTH);

        _btnOk = new JButton();
        _btnOk.setAction(actionMap.get("contentsDialogOkButtonPressed"));
        _pnlButtons.add(_btnOk);

        _btnCancel = new JButton();
        _btnCancel.setAction(actionMap.get("contentsDialogCancelButtonPressed"));
        _pnlButtons.add(_btnCancel);

        _context = context;
        _contentsMap = new LinkedHashMap<String, String>(contentsMap);

        DefaultListModel listModel = new DefaultListModel();

        for (String title : contentsMap.keySet()) {
            listModel.addElement(title);
        }

        _list.setModel(listModel);
        
        _list.addMouseListener(new MouseInputAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    contentsDialogOkButtonPressed();
                }
            }
        });
        
        if (listModel.size() > 0) {
            _list.setSelectedIndex(0);
        }
    }

    @Action
    public void contentsDialogOkButtonPressed() {
        String topicTitle = (String) _list.getSelectedValue();
        String command = _contentsMap.get(topicTitle);
        _context.parseAndExecuteDirective(command);
        this.setVisible(false);
    }

    @Action
    public void contentsDialogCancelButtonPressed() {
        this.setVisible(false);
    }

}

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
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.intkey.directives.invocation.BasicIntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class ReExecuteDialog extends JDialog {
    /**
     * 
     */
    private static final long serialVersionUID = 5026213030206139590L;

    private JPanel _pnlButtons;
    private JButton _btnExecute;
    private JButton _btnCancel;
    private JButton _btnEdit;
    private JScrollPane _scrollPane;
    private JList _listDirectives;

    private IntkeyDirectiveInvocation _directiveToExecute = null;
    private IntkeyContext _context;

    @Resource
    String windowTitle;

    @Resource
    String editPrompt;

    public ReExecuteDialog(Frame owner, List<IntkeyDirectiveInvocation> directives, IntkeyContext context) {
        super(owner, true);

        ActionMap actionMap = Application.getInstance().getContext().getActionMap(this);
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(ReExecuteDialog.class);
        resourceMap.injectFields(this);

        setResizable(false);
        setSize(new Dimension(450, 300));
        setLocationRelativeTo(owner);

        setTitle(windowTitle);

        _pnlButtons = new JPanel();
        FlowLayout flowLayout = (FlowLayout) _pnlButtons.getLayout();
        flowLayout.setHgap(10);
        getContentPane().add(_pnlButtons, BorderLayout.SOUTH);

        _btnExecute = new JButton("Execute");
        _btnExecute.setAction(actionMap.get("reExecuteDialog_Execute"));

        // execute button only become enabled when a directive from the list is
        // selected
        _btnExecute.setEnabled(false);

        _pnlButtons.add(_btnExecute);

        _btnCancel = new JButton("Cancel");
        _btnCancel.setAction(actionMap.get("reExecuteDialog_Cancel"));

        _pnlButtons.add(_btnCancel);

        _btnEdit = new JButton("Edit");
        _btnEdit.setAction(actionMap.get("reExecuteDialog_Edit"));
        // execute button only become enabled when a directive from the list is
        // selected
        _btnEdit.setEnabled(false);

        _pnlButtons.add(_btnEdit);

        _scrollPane = new JScrollPane();
        _scrollPane.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new EtchedBorder(EtchedBorder.LOWERED, null, null)));
        getContentPane().add(_scrollPane, BorderLayout.CENTER);

        _listDirectives = new JList();
        _listDirectives.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _scrollPane.setViewportView(_listDirectives);

        // Most recently executed directive should appear at the top of the list
        Collections.reverse(directives);

        DefaultListModel listModel = new DefaultListModel();
        for (IntkeyDirectiveInvocation dir : directives) {
            listModel.addElement(dir);
        }

        _listDirectives.setModel(listModel);
        _listDirectives.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (_listDirectives.getSelectedIndex() != -1) {
                    _btnExecute.setEnabled(true);
                    _btnEdit.setEnabled(true);
                } else {
                    _btnExecute.setEnabled(false);
                    _btnEdit.setEnabled(false);
                }
            }
        });

        _context = context;
    }

    public IntkeyDirectiveInvocation getDirectiveToExecute() {
        return _directiveToExecute;
    }

    @Action
    public void reExecuteDialog_Execute() {
        IntkeyDirectiveInvocation selectedDirective = (IntkeyDirectiveInvocation) _listDirectives.getSelectedValue();
        _context.executeDirective(selectedDirective);
        this.setVisible(false);
    }

    @Action
    public void reExecuteDialog_Cancel() {
        this.setVisible(false);
    }

    @Action
    public void reExecuteDialog_Edit() {
        IntkeyDirectiveInvocation selectedDirective = (IntkeyDirectiveInvocation) _listDirectives.getSelectedValue();
        String editedDirective = JOptionPane.showInputDialog(this, editPrompt, selectedDirective.toString());
        if (editedDirective != null) {
            _context.parseAndExecuteDirective(editedDirective);
            this.setVisible(false);
        }
    }

}

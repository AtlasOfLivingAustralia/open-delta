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

import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.ui.rtf.SimpleRtfEditorKit;

public class ToolbarHelpDialog extends IntkeyDialog {
    /**
     * 
     */
    private static final long serialVersionUID = -4602083501945853669L;
    private JButton _btnOk;
    private JPanel _pnlButton;
    private JTextPane _textPane;
    private JLabel _lblIcon;
    private JPanel _pnlIcon;
    private JScrollPane _scrollPane;
    
    @Resource
    String title;
    
    public ToolbarHelpDialog(Frame owner, String rtfText, Icon icon) {
        super(owner, true);
        
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(ToolbarHelpDialog.class, this);
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(ToolbarHelpDialog.class);
        resourceMap.injectFields(this);
        
        setPreferredSize(new Dimension(450, 300));
        setTitle(title);
        
        _pnlIcon = new JPanel();
        getContentPane().add(_pnlIcon, BorderLayout.NORTH);
        
        _lblIcon = new JLabel();
        _lblIcon.setIcon(icon);
        _pnlIcon.add(_lblIcon);
        
        _pnlButton = new JPanel();
        getContentPane().add(_pnlButton, BorderLayout.SOUTH);
        
        _btnOk = new JButton();
        _btnOk.setAction(actionMap.get("ToolbarHelpDialog_OK"));
        _pnlButton.add(_btnOk);
        
        _scrollPane = new JScrollPane();
        getContentPane().add(_scrollPane, BorderLayout.CENTER);
        
        _textPane = new JTextPane();
        _textPane.setEditorKit(new SimpleRtfEditorKit(null));
        _textPane.setEditable(false);
        _textPane.setText(rtfText);
        _scrollPane.setViewportView(_textPane);
        _textPane.setCaretPosition(0);
    }
    
    @Action
    public void ToolbarHelpDialog_OK() {
        this.setVisible(false);
    }

}

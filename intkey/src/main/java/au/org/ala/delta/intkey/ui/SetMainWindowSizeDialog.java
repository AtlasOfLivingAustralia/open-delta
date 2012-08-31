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
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.util.Pair;

public class SetMainWindowSizeDialog extends JDialog {
    /**
     * 
     */
    private static final long serialVersionUID = 1747213352390756734L;
    private JTextField _txtFldWidth;
    private JTextField _txtFldHeight;
    
    @Resource
    String title;
    
    @Resource
    String widthCaption;
    
    @Resource
    String heightCaption;
    private JPanel _pnlButtons;
    private JButton _btnOk;
    private JPanel _pnlMain;
    private JPanel _pnlWidth;
    private JLabel _lblWidth;
    private JPanel _pnlHeight;
    private JLabel _lblHeight;

    public SetMainWindowSizeDialog(Frame owner, int initWidth, int initHeight) {
        super(owner, true);
        setName("setMainWindowSizeDialog");

        ActionMap actionMap = Application.getInstance().getContext().getActionMap(SetMainWindowSizeDialog.class, this);
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(SetMainWindowSizeDialog.class);
        resourceMap.injectFields(this);

        setPreferredSize(new Dimension(250, 150));
        setTitle(title);

        _pnlButtons = new JPanel();
        getContentPane().add(_pnlButtons, BorderLayout.SOUTH);

        _btnOk = new JButton();
        _btnOk.setAction(actionMap.get("SetMainWindowSizeDialog_OK"));
        _pnlButtons.add(_btnOk);

        _pnlMain = new JPanel();
        getContentPane().add(_pnlMain, BorderLayout.CENTER);
        _pnlMain.setLayout(new BorderLayout(0, 0));

        _pnlWidth = new JPanel();
        _pnlMain.add(_pnlWidth, BorderLayout.NORTH);

        _lblWidth = new JLabel(widthCaption);
        _pnlWidth.add(_lblWidth);

        _txtFldWidth = new JTextField();
        _pnlWidth.add(_txtFldWidth);
        _txtFldWidth.setColumns(10);

        _pnlHeight = new JPanel();
        _pnlMain.add(_pnlHeight);

        _lblHeight = new JLabel(heightCaption);
        _pnlHeight.add(_lblHeight);

        _txtFldHeight = new JTextField();
        _pnlHeight.add(_txtFldHeight);
        _txtFldHeight.setColumns(10);
        
        _txtFldWidth.setText(Integer.toString(initWidth));
        _txtFldHeight.setText(Integer.toString(initHeight));
    }

    @Action
    public void SetMainWindowSizeDialog_OK() {
        this.setVisible(false);
    }
    
    public Pair<Integer, Integer> getWidthAndHeight() {
        int newWidth = Integer.parseInt(_txtFldWidth.getText());
        int newHeight = Integer.parseInt(_txtFldHeight.getText());
        return new Pair<Integer, Integer>(newWidth, newHeight);
    }

}

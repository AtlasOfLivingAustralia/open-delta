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
import java.awt.Component;
import java.awt.Frame;

import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import java.awt.Dimension;

public class OnOffPromptDialog extends IntkeyDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -8777819451525700580L;

    boolean _okButtonPressed;

    private JPanel _btnPanel;
    private JButton btnOk;
    private JButton btnCancel;
    private JPanel _rdBtnPanel;
    private JRadioButton _rdbtnOn;
    private JRadioButton _rdbtnOff;
    private ButtonGroup _btnGroup;

    public OnOffPromptDialog(Frame owner, String title, boolean initialValue) {
        super(owner, true);
        setMinimumSize(new Dimension(330, 144));

        setTitle(title);

        ActionMap actionMap = Application.getInstance().getContext().getActionMap(OnOffPromptDialog.class, this);
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(OnOffPromptDialog.class);
        resourceMap.injectFields(this);

        _btnPanel = new JPanel();
        getContentPane().add(_btnPanel, BorderLayout.SOUTH);

        btnOk = new JButton();
        btnOk.setAction(actionMap.get("onOffPromptDialog_OkPressed"));
        _btnPanel.add(btnOk);

        btnCancel = new JButton();
        btnCancel.setAction(actionMap.get("onOffPromptDialog_CancelPressed"));
        _btnPanel.add(btnCancel);

        _rdBtnPanel = new JPanel();
        _rdBtnPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        getContentPane().add(_rdBtnPanel, BorderLayout.CENTER);
        _rdBtnPanel.setLayout(new BoxLayout(_rdBtnPanel, BoxLayout.Y_AXIS));

        _rdbtnOn = new JRadioButton();
        _rdbtnOn.setName("rdBtnOn");
        _rdbtnOn.setAlignmentY(Component.CENTER_ALIGNMENT);
        _rdbtnOn.setAlignmentX(Component.CENTER_ALIGNMENT);
        _rdBtnPanel.add(_rdbtnOn);

        _rdbtnOff = new JRadioButton();
        _rdbtnOff.setName("rdBtnOff");
        _rdbtnOff.setAlignmentX(Component.CENTER_ALIGNMENT);
        _rdbtnOff.setHorizontalAlignment(SwingConstants.CENTER);
        _rdBtnPanel.add(_rdbtnOff);

        _btnGroup = new ButtonGroup();
        _btnGroup.add(_rdbtnOn);
        _btnGroup.add(_rdbtnOff);

        _okButtonPressed = false;

        if (initialValue == true) {
            _rdbtnOn.setSelected(true);
        } else {
            _rdbtnOff.setSelected(true);
        }
    }

    @Action
    public void onOffPromptDialog_OkPressed() {
        _okButtonPressed = true;
        this.setVisible(false);
    }

    @Action
    public void onOffPromptDialog_CancelPressed() {
        _okButtonPressed = false;
        this.setVisible(false);
    }

    public boolean isOkButtonPressed() {
        return _okButtonPressed;
    }

    public boolean getSelectedValue() {
        return _rdbtnOn.isSelected();
    }
}

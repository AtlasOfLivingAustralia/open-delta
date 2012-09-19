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
import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;

import java.awt.GridLayout;

/**
 * This panel is shown in place of the available characters list in the main
 * Intkey window when no matching taxa or available characters remain.
 * 
 * @author ChrisF
 * 
 */
public class MessagePanel extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 6179211982119585445L;
    private JLabel _lblMessage;
    private JPanel _pnlButton;
    private JButton _btnHelp;
    
    private String _helpTopicId;
    private IntkeyUI _ui;
    
    public MessagePanel(String message, String helpTopicId) {
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(MessagePanel.class, this);
        
        _helpTopicId = helpTopicId;
        setBackground(Color.WHITE);
        setLayout(new GridLayout(0, 1, 0, 0));
        
        _lblMessage = new JLabel(message);
        _lblMessage.setVerticalAlignment(SwingConstants.BOTTOM);
        _lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
        add(_lblMessage);
        
        _pnlButton = new JPanel();
        _pnlButton.setBackground(Color.WHITE);
        add(_pnlButton);
        
        _btnHelp = new JButton();
        _btnHelp.setAction(actionMap.get("MessagePanel_Help"));
        _pnlButton.add(_btnHelp);
    }
    
    @Action
    public void MessagePanel_Help(ActionEvent event) {
        UIUtils.displayHelpTopic(_helpTopicId, UIUtils.getMainFrame(), event);
    }

}

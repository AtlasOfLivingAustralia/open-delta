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

import javax.swing.JDialog;

public class IntkeyDialog extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -2611321681800876175L;
    
    public IntkeyDialog(Frame owner, boolean modal, boolean registerDialog) {
        super(owner, modal);
        init(registerDialog);
    }
    
    public IntkeyDialog(Frame owner, boolean modal) {
        super(owner, modal);
        init(false);
    }

    /**
     * 
     * @param owner owner dialog
     * @param modal true if the dialog should be modal
     * @param registerDialog if true, dialog will be registered to obey the tile/cascade/close 
     * menu commands
     */
    public IntkeyDialog(Dialog owner, boolean modal, boolean registerDialog) {
        super(owner, modal);
        init(registerDialog);
    }
    
    public IntkeyDialog(Dialog owner, boolean modal) {
        super(owner, modal);
        init(false);
    }
    
    private void init(boolean registerDialog) {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        if (registerDialog) {
        	IntKeyDialogController.registerDialog(this);
        }
    }
        
    @Override
    public String getName() {
        return null;
    }
}

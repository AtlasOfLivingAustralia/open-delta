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

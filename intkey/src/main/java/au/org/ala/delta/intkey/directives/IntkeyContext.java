package au.org.ala.delta.intkey.directives;

import javax.swing.JFrame;

import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.intkey.Intkey;

/**
 * Controller? Handles input and updates UI, model accordingly.
 * @author Chris
 *
 */
public class IntkeyContext extends AbstractDeltaContext {
    
    //dataset
    //other settings
    
    //set of commands that have been run
    //other stuff
    
    private Intkey _appUI;
    
    public IntkeyContext(Intkey appUI) {
        _appUI = appUI;
    }
    
    public void setFileTaxa(String fileName) {
        System.out.println("Setting Taxa file to: " + fileName);
    }
    
    public void newDataSet(String fileName) {
        System.out.println("Reading in new Data Set file from: " + fileName);
    }
    
    public void executeFunctor(IntkeyDirectiveInvocation invoc) {
        invoc.execute(this);
    }
    
    public JFrame getMainFrame() {
        return _appUI.getMainFrame();
    }
    
}

package au.org.ala.delta.intkey.directives.invocation;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.intkey.IntkeySession;


/**
 * This class contains a set of factory methods for creating command pattern
 * objects that encapsulate the various Intkey commands. These methods are
 * interactive in the sense that they will create dialogs to prompt the user for
 * input if necessary.
 * 
 * @author Chris
 * 
 */
public class InvocationFactory {

    public static FileTaxaDirectiveInvocation createFileTaxaInvocation(String fileName) {
        JFrame mainFrame = ((SingleFrameApplication)Application.getInstance()).getMainFrame();
        
        if (fileName == null || fileName.length() == 0) {
            JFileChooser chooser = new JFileChooser();
            FileFilter filter = new FileFilter() {

                @Override
                public boolean accept(File f) {
                    // TODO Auto-generated method stub
                    return f.isDirectory() || f.getName().toLowerCase().startsWith("iitems");
                }

                @Override
                public String getDescription() {
                    return "Files (iitems*)";
                }
                
            };
            
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(mainFrame);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                fileName = chooser.getSelectedFile().getAbsolutePath();
            } else {
                return null;
            }
        }
        
        return new FileTaxaDirectiveInvocation(fileName);
    }

}

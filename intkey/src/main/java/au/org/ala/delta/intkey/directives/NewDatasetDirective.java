package au.org.ala.delta.intkey.directives;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.intkey.ui.SelectDataSetDialog;

public class NewDatasetDirective extends IntkeyDirective {

    public NewDatasetDirective() {
        super("newdataset");
    }

    @Override
    public IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        String filePath = data;

        /*if (filePath == null) {
            SelectDataSetDialog dlg = new SelectDataSetDialog(context.getMainFrame());
            ((SingleFrameApplication)Application.getInstance()).show(dlg);
            //dlg.setVisible(true);
            if (dlg.isFileSelected()) {
                filePath = dlg.getSelectedFilePath();
            }
        }*/
        
        //TODO - This is temporary until the SelectDataSetDialog has been completed.
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Data Initialization Files (*.ini, *.ink)", "ini", "ink");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(context.getMainFrame());
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           filePath = chooser.getSelectedFile().getAbsolutePath();
        }

        if (filePath != null) {
            NewDataSetDirectiveInvocation invoc = new NewDataSetDirectiveInvocation(filePath);
            return invoc;
        }
        
        return null;
    }
    
    class NewDataSetDirectiveInvocation implements IntkeyDirectiveInvocation {

        private String _fileName;
        
        public NewDataSetDirectiveInvocation(String fileName) {
            _fileName = fileName;
        }
        
        @Override
        public boolean execute(IntkeyContext context) {
            context.newDataSetFile(_fileName);
            return true;
        }
        
        @Override
        public String toString() {
            return String.format("%s %s", StringUtils.join(_controlWords, " "), _fileName);
        }

    }

}

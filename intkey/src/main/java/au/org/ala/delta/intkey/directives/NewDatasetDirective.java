package au.org.ala.delta.intkey.directives;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.intkey.ui.SelectDataSetDialog;

public class NewDatasetDirective extends IntkeyDirective {

    public NewDatasetDirective() {
        super("newdataset");
    }

    @Override
    public IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        String filePath = data;

        if (filePath == null) {
            SelectDataSetDialog dlg = new SelectDataSetDialog(context.getMainFrame());
            dlg.setVisible(true);
            if (dlg.isFileSelected()) {
                filePath = dlg.getSelectedFilePath();
            }
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
        public void execute(IntkeyContext context) {
            context.newDataSet(_fileName);
        }
        
        @Override
        public String toString() {
            return String.format("%s %s", StringUtils.join(_controlWords, " "), _fileName);
        }

    }

}

package au.org.ala.delta.intkey.directives;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class FileTaxaDirective extends IntkeyDirective {

    public FileTaxaDirective() {
        super("file", "taxa");
    }

    @Override
    public IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) {
        String fileName = data;

        if (fileName == null) {
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
            int returnVal = chooser.showOpenDialog(context.getMainFrame());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                fileName = chooser.getSelectedFile().getAbsolutePath();
            } else {
                fileName = null;
            }
        }

        if (fileName != null) {
            FileTaxaDirectiveInvocation invoc = new FileTaxaDirectiveInvocation(fileName);
            return invoc;
        }

        return null;
    }

    class FileTaxaDirectiveInvocation implements IntkeyDirectiveInvocation {
        private String _fileName;

        public FileTaxaDirectiveInvocation(String fileName) {
            _fileName = fileName;
        }

        @Override
        public boolean execute(IntkeyContext context) {
            try {
                context.setFileTaxa(_fileName);
                return true;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(context.getMainFrame(), ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        @Override
        public String toString() {
            return String.format("%s %s", StringUtils.join(_controlWords, " "), _fileName);
        }

    }
}

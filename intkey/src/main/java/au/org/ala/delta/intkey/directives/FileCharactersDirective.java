package au.org.ala.delta.intkey.directives;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang.StringUtils;

public class FileCharactersDirective extends IntkeyDirective {

    public FileCharactersDirective() {
        super("file", "characters");
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
                    return "Files (ichars*)";
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
            FileCharactersDirectiveInvocation invoc = new FileCharactersDirectiveInvocation(fileName);
            return invoc;
        }

        return null;
    }

    class FileCharactersDirectiveInvocation implements IntkeyDirectiveInvocation {
        private String _fileName;

        public FileCharactersDirectiveInvocation(String fileName) {
            _fileName = fileName;
        }

        @Override
        public boolean execute(IntkeyContext context) {
            try {
                context.setFileCharacters(_fileName);
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

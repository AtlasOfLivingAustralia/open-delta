package au.org.ala.delta.intkey.directives;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.ui.UIUtils;

/**
 * The FILE TAXA directive - specifies the name of the intkey taxa (items) file.
 * It is normally only used in the initialization file - intkey.ini.
 * 
 * @author ChrisF
 * 
 */
public class FileTaxaDirective extends IntkeyDirective {

    public FileTaxaDirective() {
        super("file", "taxa");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) {
        File selectedFile = null;

        if (data == null) {
            JFileChooser chooser = new JFileChooser();
            FileFilter filter = new FileFilter() {

                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().startsWith("iitems");
                }

                @Override
                public String getDescription() {
                    return "Files (iitems*)";
                }

            };

            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(UIUtils.getMainFrame());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                selectedFile = chooser.getSelectedFile();

            }
        } else {
            selectedFile = new File(data);
            if (!selectedFile.isAbsolute()) {
                selectedFile = new File(context.getDatasetDirectory(), data);
            }
        }

        if (selectedFile != null) {
            FileTaxaDirectiveInvocation invoc = new FileTaxaDirectiveInvocation(selectedFile);
            return invoc;
        }

        return null;
    }

    class FileTaxaDirectiveInvocation implements IntkeyDirectiveInvocation {
        private File _file;

        public FileTaxaDirectiveInvocation(File taxaFile) {
            _file = taxaFile;
        }

        @Override
        public boolean execute(IntkeyContext context) {
            try {
                context.setFileTaxa(_file);
                return true;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(UIUtils.getMainFrame(), ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        @Override
        public String toString() {
            return String.format("%s %s", StringUtils.join(_controlWords, " ").toUpperCase(), _file);
        }

    }
}

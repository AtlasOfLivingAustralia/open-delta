package au.org.ala.delta.intkey.directives;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.intkey.IntkeyUI;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.ui.UIUtils;

/**
 * The FILE CHARACTERS directive - specifies the name of the intkey characters
 * file. It is normally only used in the initialization file - intkey.ini.
 * 
 * @author ChrisF
 * 
 */
public class FileCharactersDirective extends IntkeyDirective {

    public FileCharactersDirective() {
        super("file", "characters");
    }

    @Override
    public int getArgType() {
        return DirectiveArgType.DIRARG_TEXT;
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) {
        _data = data;
        String fileName = data;

        if (fileName == null) {
            JFileChooser chooser = new JFileChooser();
            FileFilter filter = new FileFilter() {

                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().startsWith("iitems");
                }

                @Override
                public String getDescription() {
                    return "Files (ichars*)";
                }

            };

            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(UIUtils.getMainFrame());
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
                JOptionPane.showMessageDialog(UIUtils.getMainFrame(), ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        @Override
        public String toString() {
            return String.format("%s %s", StringUtils.join(_controlWords, " ").toUpperCase(), _fileName);
        }
    }
}

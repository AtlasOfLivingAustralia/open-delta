package au.org.ala.delta.intkey.directives;

import java.io.File;
import java.net.URL;
import java.util.Arrays;

import au.org.ala.delta.intkey.directives.invocation.FileDisplayDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.ui.UIUtils;

public class FileDisplayDirective extends IntkeyDirective {

    public FileDisplayDirective() {
        super(false, "file", "display");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        String filePath = data;
        FileDisplayDirectiveInvocation invoc = null;

        if (filePath == null || filePath.equals(IntkeyDirectiveArgument.DEFAULT_DIALOG_WILDCARD)) {
            File file = context.getDirectivePopulator().promptForFile(Arrays.asList(new String[] { "rtf", "doc", "htm", "html", "wav", "ink" }), "Files (*.rtf, *.doc, *.htm, *.wav, *.ink)", false);
            invoc = new FileDisplayDirectiveInvocation(file.toURI().toURL(), file.getName());
        } else if (filePath.startsWith("http://")) {
            try {
                URL url = new URL(filePath);
                invoc = new FileDisplayDirectiveInvocation(url, filePath);
            } catch (Exception ex) {

            }
        } else {
            File file = UIUtils.findFile(filePath, context.getDatasetDirectory());
            invoc = new FileDisplayDirectiveInvocation(file.toURI().toURL(), file.getName());
        }

        return invoc;
    }

}

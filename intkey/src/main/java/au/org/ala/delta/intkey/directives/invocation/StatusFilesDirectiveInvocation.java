package au.org.ala.delta.intkey.directives.invocation;

import java.awt.Color;
import java.io.File;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.rtf.RTFBuilder;
import au.org.ala.delta.rtf.RTFUtils;

public class StatusFilesDirectiveInvocation extends IntkeyDirectiveInvocation {

    @Override
    public boolean execute(IntkeyContext context) throws IntkeyDirectiveInvocationException {
        RTFBuilder builder = new RTFBuilder();
        builder.startDocument();

        File journalFile = context.getJournalFile();
        File logFile = context.getLogFile();
        File outputFile = context.getOutputFile();

        builder.setTextColor(Color.BLUE);
        builder.appendText(UIUtils.getResourceString("Status.Files.title"));
        builder.setTextColor(Color.BLACK);
        builder.appendText(UIUtils.getResourceString("Status.Files.heading"));

        if (journalFile != null) {
            builder.appendText(RTFUtils.escapeRTF(UIUtils.getResourceString("Status.Files.journalFile", journalFile.getAbsolutePath())));
        }

        if (logFile != null) {
            builder.appendText(RTFUtils.escapeRTF(UIUtils.getResourceString("Status.Files.logFile", logFile.getAbsolutePath())));
        }

        if (outputFile != null) {
            builder.appendText(RTFUtils.escapeRTF(UIUtils.getResourceString("Status.Files.outputFile", outputFile.getAbsolutePath())));
        }

        builder.endDocument();

        context.getUI().displayRTFReport(builder.toString(), UIUtils.getResourceString("Status.title"));

        return true;
    }

}

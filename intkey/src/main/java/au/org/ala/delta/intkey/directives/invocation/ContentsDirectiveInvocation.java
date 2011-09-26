package au.org.ala.delta.intkey.directives.invocation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class ContentsDirectiveInvocation extends IntkeyDirectiveInvocation {

    private File file;

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            // use LinkedHashMap to maintain insertion order of keys
            LinkedHashMap<String, String> contentsMap = new LinkedHashMap<String, String>();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("*")) {
                    String[] tokens = line.split("\\*");
                    if (tokens.length != 2) {
                        context.getUI().displayErrorMessage("Badly formed contents file.");
                        return false;
                    }

                    contentsMap.put(tokens[0].trim(), tokens[1].trim());
                } else {
                    String[] tokens = line.split(" ");
                    if (tokens.length > 1) {
                        String description = StringUtils.join(Arrays.copyOf(tokens, tokens.length - 1), " ");
                        String fileName = tokens[tokens.length - 1];

                        // TODO massive hack here. Really should be building
                        // IntkeyDirectiveInvocation objects
                        // from both line formats and passing them to the
                        // contents directive, rather than
                        // getting the contents directive to do directive
                        // parsing.
                        String command = "FILE DISPLAY " + fileName.trim();
                        contentsMap.put(description.trim(), command);
                    } else {

                    }
                }
            }
            context.getUI().displayContents(contentsMap);
        } catch (IOException ex) {

        }

        return true;
    }
}

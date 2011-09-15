package au.org.ala.delta.intkey.directives.invocation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class ContentsDirectiveInvocation implements IntkeyDirectiveInvocation {

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
                String[] tokens = line.split("\\*");
                if (tokens.length != 2) {
                    context.getUI().displayErrorMessage("Badly formed contents file.");
                    return false;
                }

                contentsMap.put(tokens[0].trim(), tokens[1].trim());
            }
            context.getUI().displayContents(contentsMap);
        } catch (IOException ex) {

        }

        return true;
    }

}

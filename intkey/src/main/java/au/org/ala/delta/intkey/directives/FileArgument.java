package au.org.ala.delta.intkey.directives;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Queue;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class FileArgument extends IntkeyDirectiveArgument<File> {

    private List<String> _fileExtensions;
    private boolean _createFileIfNonExistant;

    public FileArgument(String name, String promptText, File initialValue, List<String> fileExtensions, boolean createFileIfNonExistant) {
        super(name, promptText, initialValue);
        _fileExtensions = fileExtensions;
        _createFileIfNonExistant = createFileIfNonExistant;
    }

    @Override
    public File parseInput(Queue<String> inputTokens, IntkeyContext context, String directiveName, StringBuilder stringRepresentationBuilder) throws IntkeyDirectiveParseException {
        String filePath = inputTokens.poll();

        File file = null;

        if (filePath == null || filePath.equals(DEFAULT_DIALOG_WILDCARD)) {
            try {
                file = context.getDirectivePopulator().promptForFile(_fileExtensions, getPromptText(), _createFileIfNonExistant);
            } catch (IOException ex) {
                throw new IntkeyDirectiveParseException("Error creating file");
            }
        } else {
            // If the supplied file path starts with one of the file system
            // roots, then it is absolute. Otherwise, assume that
            // it is relative to the directory in which the dataset is located.
            boolean fileAbsolute = false;
            for (File root : File.listRoots()) {
                if (filePath.toLowerCase().startsWith(root.getAbsolutePath().toLowerCase())) {
                    fileAbsolute = true;
                    break;
                }
            }

            if (fileAbsolute) {
                file = new File(filePath);
            } else {
                file = new File(context.getDatasetDirectory(), filePath);
            }

            if (!file.exists() && _createFileIfNonExistant) {
                try {
                    file.createNewFile();
                } catch (IOException ex) {
                    throw new IntkeyDirectiveParseException(String.format("Error creating file %s", file.getAbsolutePath()), ex);
                }
            }
        }

        if (file != null && !file.exists()) {
            throw new IntkeyDirectiveParseException(String.format("Could not open file %s", file.getAbsolutePath()));
        }

        if (file != null) {
            stringRepresentationBuilder.append(" ");
            stringRepresentationBuilder.append(file.getAbsolutePath());
        }

        return file;
    }

}

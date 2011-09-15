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
    public File parseInput(Queue<String> inputTokens, IntkeyContext context, String directiveName) throws IntkeyDirectiveParseException {
        String filePath = inputTokens.poll();
        
        File file = null;
        
        if (filePath == null) {
            try {
            file = context.getDirectivePopulator().promptForFile(_fileExtensions, getPromptText(), _createFileIfNonExistant);
            } catch (IOException ex) {
                throw new IntkeyDirectiveParseException("Error creating file");
            }
        } else {
            file = new File(context.getDatasetDirectory(), filePath);    
        }
        
        if (file != null && !file.exists()) {
            throw new IntkeyDirectiveParseException(String.format("Could not open file %s", file.getAbsolutePath()));
        }
        
        return file;
    }

}

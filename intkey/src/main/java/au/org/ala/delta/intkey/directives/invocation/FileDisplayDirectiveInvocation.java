package au.org.ala.delta.intkey.directives.invocation;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.crypto.URIReferenceException;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class FileDisplayDirectiveInvocation extends IntkeyDirectiveInvocation {

    private URL _fileURL;
    private String _fileDescription;

    public FileDisplayDirectiveInvocation(URL fileURL, String fileDescription) {
        _fileURL = fileURL;
        _fileDescription = fileDescription;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        context.getUI().displayFile(_fileURL, _fileDescription);
        return true;
    }

    @Override
    public String toString() {
        return String.format("FILE DISPLAY \"%s\"", _fileURL.getPath());
    }

}

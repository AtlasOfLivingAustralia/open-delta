package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.IntkeySession;

public class FileTaxaDirectiveInvocation extends IntkeyDirectiveInvocation {

    private String _fileName;
    
    public FileTaxaDirectiveInvocation(String fileName) {
        _fileName = fileName;
    }
    
    @Override
    protected void doExecute() {
        IntkeySession.getInstance().setFileTaxa(_fileName);
    }
    
    public String toString() {
        return String.format("File Taxa %s", _fileName);
    }

}

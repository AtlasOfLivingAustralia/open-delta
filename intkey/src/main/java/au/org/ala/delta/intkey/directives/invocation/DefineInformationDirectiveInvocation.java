package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class DefineInformationDirectiveInvocation implements IntkeyDirectiveInvocation {

    private String _subject;
    private String _command;
    
    public DefineInformationDirectiveInvocation(String subject, String command) {
        _subject = subject;
        _command = command;
    }
    
    @Override
    public boolean execute(IntkeyContext context) {
        context.addTaxonInformationDialogCommand(_subject, _command);
        return true;
    }

}

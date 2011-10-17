package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class DefineInformationDirectiveInvocation extends IntkeyDirectiveInvocation {

    private String _subject;
    private String _command;

    public void setSubject(String subject) {
        this._subject = subject;
    }

    public void setCommand(String command) {
        this._command = command;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        context.addTaxonInformationDialogCommand(_subject, _command);
        return true;
    }

}

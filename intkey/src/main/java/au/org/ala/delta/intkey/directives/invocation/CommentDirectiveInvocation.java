package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class CommentDirectiveInvocation implements IntkeyDirectiveInvocation {

    private String _text;

    public void setText(String text) {
        this._text = text;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        context.appendToLogFile(_text);
        context.appendToJournalFile(_text);
        return true;
    }

}

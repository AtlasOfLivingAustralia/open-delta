package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class OutputCommentDirectiveInvocation extends IntkeyDirectiveInvocation {

    private String _text;

    public void setText(String text) {
        this._text = text;
    }

    @Override
    public boolean execute(IntkeyContext context) throws IntkeyDirectiveInvocationException {
        context.appendToOutputFile(_text);
        context.setLastOutputLineWasComment(true);
        return true;
    }
}

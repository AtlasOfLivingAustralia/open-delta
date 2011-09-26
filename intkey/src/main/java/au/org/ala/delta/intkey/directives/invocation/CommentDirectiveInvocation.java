package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class CommentDirectiveInvocation extends IntkeyDirectiveInvocation {

    private String _text;

    public void setText(String text) {
        this._text = text;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        // Do nothing. This directive exists purely to add the supplied text to
        // the
        // log and journal files. This is done elsewhere.
        return true;
    }

}

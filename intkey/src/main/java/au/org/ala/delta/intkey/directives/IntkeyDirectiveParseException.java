package au.org.ala.delta.intkey.directives;

import java.text.MessageFormat;

import au.org.ala.delta.intkey.ui.UIUtils;

public class IntkeyDirectiveParseException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 2156835375263847602L;

    public IntkeyDirectiveParseException() {
        super();
    }

    public IntkeyDirectiveParseException(String messageKey, Throwable cause, Object... messageArguments) {
        super(UIUtils.getResourceString(messageKey, messageArguments), cause);
    }

    public IntkeyDirectiveParseException(String messageKey, Object... messageArguments) {
        super(UIUtils.getResourceString(messageKey, messageArguments));
    }
}

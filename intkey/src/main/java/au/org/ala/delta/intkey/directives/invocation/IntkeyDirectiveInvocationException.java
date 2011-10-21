package au.org.ala.delta.intkey.directives.invocation;

import java.text.MessageFormat;

import au.org.ala.delta.intkey.ui.UIUtils;

public class IntkeyDirectiveInvocationException extends Exception {
    public IntkeyDirectiveInvocationException(String messageKey, Throwable cause, Object... messageArguments) {
        super(MessageFormat.format(UIUtils.getResourceString(messageKey), messageArguments), cause);
    }

    public IntkeyDirectiveInvocationException(String messageKey, Object... messageArguments) {
        super(MessageFormat.format(UIUtils.getResourceString(messageKey), messageArguments));
    }
}

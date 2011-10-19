package au.org.ala.delta.intkey.directives;

import java.text.MessageFormat;
import java.text.ParseException;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.ui.UIUtils;

public abstract class IntkeyDirective extends AbstractDirective<IntkeyContext> {

    protected DirectiveArguments _args;
    protected boolean _errorIfNoDatasetLoaded;

    public IntkeyDirective(boolean errorIfNoDatasetLoaded, String... controlWords) {
        super(controlWords);
        _errorIfNoDatasetLoaded = errorIfNoDatasetLoaded;
    }

    @Override
    public final int getArgType() {
        // Not relevant for Intkey. This is only used for import/export of
        // directives
        // in the delta editor.
        return 0;
    }

    @Override
    public final DirectiveArguments getDirectiveArgs() {
        return _args;
    }

    @Override
    public final void parse(IntkeyContext context, String data) throws ParseException {
        _args = DirectiveArguments.textArgument(data);
    }

    @Override
    public final void process(IntkeyContext context, DirectiveArguments directiveArguments) throws Exception {
        parseAndProcess(context, directiveArguments.getFirstArgumentText());
    }

    @Override
    public final void parseAndProcess(IntkeyContext context, String data) throws Exception {
        if (context.getDataset() == null && _errorIfNoDatasetLoaded) {
            context.getUI().displayErrorMessage(MessageFormat.format(UIUtils.getResourceString("DirectiveCallNoDatasetLoaded.error"), getControlWordsAsString()));
        }

        if (data != null) {
            data = data.trim();
        }

        IntkeyDirectiveInvocation invoc = doProcess(context, data);

        if (invoc != null) {
            context.executeDirective(invoc);
        }
    }

    protected abstract IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception;

    public String getControlWordsAsString() {
        return StringUtils.join(getControlWords(), " ").toUpperCase();
    }

}

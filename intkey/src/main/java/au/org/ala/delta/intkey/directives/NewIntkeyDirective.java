package au.org.ala.delta.intkey.directives;

import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public abstract class NewIntkeyDirective extends AbstractDirective<IntkeyContext> {

    protected DirectiveArguments _args;

    protected List<IntkeyDirectiveFlag> _intkeyFlagsList;
    protected List<IntkeyDirectiveArgument> _intkeyArgsList;

    public NewIntkeyDirective(String... controlWords) {
        super(controlWords);
        _intkeyArgsList = buildArguments();
        _intkeyFlagsList = buildFlags();
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
        List<String> tokens = ParsingUtils.tokenizeDirectiveCall(data);
        Queue<String> tokenQueue = new ArrayDeque<String>(tokens);

        IntkeyDirectiveInvocation invoc = buildCommandObject();

        if (_intkeyFlagsList != null && tokenQueue.size() > 0) {
            boolean matchingFlags = true;
            while (matchingFlags) {
                boolean tokenMatched = false;
                String token = tokenQueue.peek();

                for (IntkeyDirectiveFlag flag : _intkeyFlagsList) {
                    if (token.equalsIgnoreCase("/" + flag.getSymbol())) {
                        BeanUtils.setProperty(invoc, flag.getName(), true);
                        tokenQueue.remove();
                        tokenMatched = true;
                        break;
                    }
                }

                matchingFlags = tokenMatched;
            }
        }

        if (_intkeyArgsList != null) {
            for (IntkeyDirectiveArgument arg : _intkeyArgsList) {
                Object parsedArgumentValue = arg.parseInput(tokenQueue, context, StringUtils.join(_controlWords, " "));
                BeanUtils.setProperty(invoc, arg.getName(), parsedArgumentValue);
            }
        }

        if (context != null) {
            context.executeDirective(invoc);
        }
    }

    protected abstract List<IntkeyDirectiveArgument> buildArguments();

    protected abstract List<IntkeyDirectiveFlag> buildFlags();

    protected abstract IntkeyDirectiveInvocation buildCommandObject();
}

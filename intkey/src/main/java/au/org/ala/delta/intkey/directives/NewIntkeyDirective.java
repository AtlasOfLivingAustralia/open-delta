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

    public NewIntkeyDirective(String... controlWords) {
        super(controlWords);
        _intkeyFlagsList = buildFlagsList();
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

                if (token != null) {
                    for (IntkeyDirectiveFlag flag : _intkeyFlagsList) {
                        if (token.equalsIgnoreCase("/" + flag.getSymbol())) {
                            BeanUtils.setProperty(invoc, flag.getName(), true);
                            tokenQueue.remove();
                            tokenMatched = true;
                            break;
                        }
                    }

                    matchingFlags = tokenMatched;
                } else {
                    matchingFlags = false;
                }
            }
        }

        // The arguments list needs to be generated each time a call to the
        // directive is processed. This is
        // because most arguments need to have provided with an initial value
        // which is used when prompting the user.
        // This initial value needs to be read out of the IntkeyContext at the
        // time of parsing.
        // E.g. the integer argument for the SET TOLERANCE directive will have
        // an initial value equal to the
        // the value of the tolerance setting before the call to the directive.
        List<IntkeyDirectiveArgument<?>> intkeyArgsList = generateArgumentsList(context);

        if (intkeyArgsList != null) {
            for (IntkeyDirectiveArgument<?> arg : intkeyArgsList) {
                Object parsedArgumentValue = arg.parseInput(tokenQueue, context, StringUtils.join(_controlWords, " "));
                if (parsedArgumentValue != null) {
                    BeanUtils.setProperty(invoc, arg.getName(), parsedArgumentValue);
                } else {
                    // No argument value supplied, user cancelled out of the
                    // prompt dialog.
                    return;
                }
            }
        }

        if (context != null) {
            context.executeDirective(invoc);
        }
    }

    protected abstract List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context);

    protected abstract List<IntkeyDirectiveFlag> buildFlagsList();

    protected abstract IntkeyDirectiveInvocation buildCommandObject();
}

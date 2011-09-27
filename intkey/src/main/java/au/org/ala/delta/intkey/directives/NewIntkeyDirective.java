package au.org.ala.delta.intkey.directives;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

/** TODO NEED A NAME FOR THIS CLASS **/
public abstract class NewIntkeyDirective extends IntkeyDirective {

    protected DirectiveArguments _args;

    protected List<IntkeyDirectiveFlag> _intkeyFlagsList;

    public NewIntkeyDirective(String... controlWords) {
        super(controlWords);
        _intkeyFlagsList = buildFlagsList();
    }

    @Override
    public final IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        StringBuilder stringRepresentationBuilder = new StringBuilder();
        stringRepresentationBuilder.append(StringUtils.join(getControlWords(), " ").toUpperCase());

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

                            stringRepresentationBuilder.append("/");
                            stringRepresentationBuilder.append(flag.getSymbol());

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
                Object parsedArgumentValue = arg.parseInput(tokenQueue, context, StringUtils.join(_controlWords, " "), stringRepresentationBuilder);
                if (parsedArgumentValue != null) {
                    BeanUtils.setProperty(invoc, arg.getName(), parsedArgumentValue);
                } else {
                    // No argument value supplied, user cancelled out of the
                    // prompt dialog.
                    return null;
                }
            }
        }

        invoc.setStringRepresentation(stringRepresentationBuilder.toString());

        return invoc;
    }

    protected abstract List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context);

    protected abstract List<IntkeyDirectiveFlag> buildFlagsList();

    protected abstract IntkeyDirectiveInvocation buildCommandObject();
}

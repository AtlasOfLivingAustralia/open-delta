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

    public NewIntkeyDirective(boolean errorIfNoDatasetLoaded, String... controlWords) {
        super(errorIfNoDatasetLoaded, controlWords);
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
                        if (flag.takesStringValue()) {
                            // Flag can have a string value supplied with it in
                            // format "/X=string", where X is the character
                            // symbol. Note that
                            // it is acceptable to supply such a flag without a
                            // following equals sign and string value.
                            if (token.matches("^/[" + Character.toLowerCase(flag.getSymbol()) + Character.toUpperCase(flag.getSymbol()) + "](=.+)?")) {

                                // If string value is not supplied, it defaults
                                // to empty string
                                String flagStringValue = "";

                                String[] tokenPieces = token.split("=");

                                // There should only be 0 or 1 equals sign. If
                                // more than none is supplied, no match.
                                if (tokenPieces.length < 3) {
                                    if (tokenPieces.length == 2) {
                                        flagStringValue = tokenPieces[1];
                                    }

                                    BeanUtils.setProperty(invoc, flag.getName(), flagStringValue);
                                    tokenQueue.remove();
                                    tokenMatched = true;
                                    stringRepresentationBuilder.append(token);
                                    break;
                                }
                            }
                        } else {
                            if (token.equalsIgnoreCase("/" + flag.getSymbol())) {

                                BeanUtils.setProperty(invoc, flag.getName(), true);
                                tokenQueue.remove();
                                tokenMatched = true;
                                stringRepresentationBuilder.append(token);
                                break;
                            }
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

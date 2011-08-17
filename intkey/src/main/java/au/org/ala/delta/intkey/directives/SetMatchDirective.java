package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.SetMatchDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.MatchType;

public class SetMatchDirective extends IntkeyDirective {

    public SetMatchDirective() {
        super("set", "match");
    }

    @Override
    public int getArgType() {
        return DirectiveArgType.DIRARG_TEXTLIST;
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {

        boolean matchUnknowns = false;
        boolean matchInapplicables = false;
        MatchType matchType = MatchType.EXACT;

        if (data == null) {
            return null;
        } else {
            for (char c : data.toCharArray()) {
                switch (c) {
                case 'o':
                case 'O':
                    matchType = MatchType.OVERLAP;
                    break;
                case 's':
                case 'S':
                    matchType = MatchType.SUBSET;
                    break;
                case 'e':
                case 'E':
                    matchUnknowns = false;
                    matchInapplicables = false;
                    break;
                case 'u':
                case 'U':
                    matchUnknowns = true;
                    break;
                case 'i':
                case 'I':
                    matchInapplicables = true;
                    break;
                default:
                    if (!Character.isWhitespace(c)) {
                        context.getUI().displayErrorMessage("Invalid option for SET MATCH: " + c);
                        return null;
                    }
                }
            }
        }

        return new SetMatchDirectiveInvocation(matchInapplicables, matchUnknowns, matchType);
    }
}

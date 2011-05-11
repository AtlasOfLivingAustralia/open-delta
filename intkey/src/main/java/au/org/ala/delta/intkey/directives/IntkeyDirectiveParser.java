package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.DirectiveParser;
import au.org.ala.delta.directives.DirectiveSearchResult;
import au.org.ala.delta.directives.ParsingContext;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class IntkeyDirectiveParser extends DirectiveParser<IntkeyContext> {

    // private constructor - use factory method to get an instance.
    private IntkeyDirectiveParser() {
    }

    public static IntkeyDirectiveParser createInstance() {
        IntkeyDirectiveParser instance = new IntkeyDirectiveParser();

        instance.registerDirective(new FileCharactersDirective());
        instance.registerDirective(new FileTaxaDirective());
        instance.registerDirective(new NewDatasetDirective());
        instance.registerDirective(new UseDirective());
        instance.registerDirective(new DefineCharactersDirective());
        instance.registerDirective(new RestartDirective());
        return instance;
    }

    @Override
    protected void processDirective(StringBuilder data, IntkeyContext context) {
        if (data.length() > 0) {
            ParsingContext pc = context.getCurrentParsingContext();
            char ch = data.charAt(0);
            if (Character.isDigit(ch)) {
                DirectiveSearchResult r = getDirectiveTree().findDirective(new ArrayList<String>(Arrays.asList("use")));
                IntkeyDirective useDirective = (IntkeyDirective) r.getDirective();
                try {
                    useDirective.doProcess(context, data.toString());
                } catch (Exception ex) {
                    if (pc.getFile() != null) {
                        throw new RuntimeException(String.format("Exception occured trying to process directive: %s (%s %d:%d)", useDirective.getName(), pc.getFile().getName(),
                                pc.getCurrentDirectiveStartLine(), pc.getCurrentDirectiveStartOffset()), ex);
                    } else {
                        throw new RuntimeException(String.format("Exception occured trying to process directive: %s (%d:%d)", useDirective.getName(), pc.getCurrentDirectiveStartLine(),
                                pc.getCurrentDirectiveStartOffset()), ex);
                    }
                }
            } else {
                super.processDirective(data, context);
            }
        }
    }

    @Override
    protected void handleUnrecognizedDirective(ParsingContext pc, List<String> controlWords) {
        // TODO eventually all unrecognized directives need to be properly
        // handled. This is here so that
        // intkey dataset can be used with milestone release without implemented
        // directives causing
        // errors
        Logger.log("Ignoring unrecognized directive: %s ", StringUtils.join(controlWords, " "));
    }

}

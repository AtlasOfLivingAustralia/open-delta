package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.FloatRange;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.UseDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.specimen.IntegerSpecimenValue;
import au.org.ala.delta.intkey.model.specimen.MultiStateSpecimenValue;
import au.org.ala.delta.intkey.model.specimen.RealSpecimenValue;
import au.org.ala.delta.intkey.model.specimen.TextSpecimenValue;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;

/**
 * The USE directive - allow the user to enter information about a specimen
 * 
 * @author ChrisF
 * 
 */
public class UseDirective extends IntkeyDirective {

    private static Pattern COMMA_SEPARATED_VALUE_PATTERN = Pattern.compile("^.+,.*$");

    private CharacterFormatter _charFormatter;

    public UseDirective() {
        super(true, "use");
        _charFormatter = new CharacterFormatter(false, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REPLACE, true, false);
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        return doProcess(context, data, false);
    }

    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data, boolean change) throws Exception {
        if (context.getDataset() != null) {
            boolean suppressAlreadySetWarning = false;

            List<Integer> characterNumbers = new ArrayList<Integer>();
            List<String> specifiedValues = new ArrayList<String>();

            if (data != null && data.trim().length() > 0) {
                List<String> subCommands = ParsingUtils.tokenizeDirectiveCall(data);

                for (String subCmd : subCommands) {
                    // TODO need to handle additional undocumented flags
                    if (subCmd.equalsIgnoreCase("/M")) {
                        suppressAlreadySetWarning = true;
                    } else {
                        parseSubcommands(subCmd, characterNumbers, specifiedValues, context);
                    }
                }

            } else {
                // No characters specified, prompt the user to select characters

                String directiveName = change ? directiveName = StringUtils.join(new ChangeDirective().getControlWords(), " ").toUpperCase() : StringUtils.join(_controlWords, " ").toUpperCase();
                List<Character> selectedCharacters = context.getDirectivePopulator().promptForCharactersByKeyword(directiveName, true, false);
                if (selectedCharacters.size() > 0) {
                    for (Character ch : selectedCharacters) {
                        characterNumbers.add(ch.getCharacterId());
                        specifiedValues.add(null);
                    }
                } else {
                    // User has hit cancel, nothing to execute
                    return null;
                }
            }

            UseDirectiveInvocation invoc = new UseDirectiveInvocation(change, suppressAlreadySetWarning);

            for (int i = 0; i < characterNumbers.size(); i++) {
                int charNum = characterNumbers.get(i);
                au.org.ala.delta.model.Character ch;
                try {
                    ch = context.getDataset().getCharacter(charNum);
                } catch (IllegalArgumentException ex) {
                    throw new IntkeyDirectiveParseException("UseDirective.InvalidCharacterNumber", ex, charNum, context.getDataset().getNumberOfCharacters());
                }

                // Parse the supplied value for each character, or prompt for
                // one if no value was supplied

                String charValue = specifiedValues.get(i);

                if (charValue != null) {
                    try {
                        if (ch instanceof MultiStateCharacter) {
                            MultiStateCharacter msCh = (MultiStateCharacter) ch;

                            try {
                                Set<Integer> setStateValues = ParsingUtils.parseMultistateOrIntegerCharacterValue(charValue);

                                for (int val : setStateValues) {
                                    if (val < 0 || val > msCh.getNumberOfStates()) {
                                        throw new IntkeyDirectiveParseException("UseDirective.InvalidStateValue", charValue, _charFormatter.formatCharacterDescription(ch), Integer.toString(
                                                ch.getCharacterId(), msCh.getNumberOfStates()));
                                    }
                                }

                                invoc.addCharacterValue((MultiStateCharacter) ch, new MultiStateSpecimenValue((MultiStateCharacter) ch, setStateValues));
                            } catch (IllegalArgumentException ex) {
                                throw new IntkeyDirectiveParseException("UseDirective.InvalidStateValue", ex, charValue, Integer.toString(ch.getCharacterId()),
                                        _charFormatter.formatCharacterDescription(ch), msCh.getNumberOfStates());
                            }
                        } else if (ch instanceof IntegerCharacter) {

                            try {
                                Set<Integer> intValues = ParsingUtils.parseMultistateOrIntegerCharacterValue(charValue);
                                invoc.addCharacterValue((IntegerCharacter) ch, new IntegerSpecimenValue((IntegerCharacter) ch, intValues));
                            } catch (IllegalArgumentException ex) {
                                throw new IntkeyDirectiveParseException("UseDirective.InvalidIntegerValue", ex, charValue, Integer.toString(ch.getCharacterId()),
                                        _charFormatter.formatCharacterDescription(ch));
                            }
                        } else if (ch instanceof RealCharacter) {

                            try {
                                FloatRange floatRange = ParsingUtils.parseRealCharacterValue(charValue);
                                invoc.addCharacterValue((RealCharacter) ch, new RealSpecimenValue((RealCharacter) ch, floatRange));
                            } catch (IllegalArgumentException ex) {
                                throw new IntkeyDirectiveParseException("UseDirective.InvalidRealValue", ex, charValue, Integer.toString(ch.getCharacterId()),
                                        _charFormatter.formatCharacterDescription(ch));
                            }
                        } else if (ch instanceof TextCharacter) {
                            List<String> stringList = ParsingUtils.parseTextCharacterValue(charValue);
                            invoc.addCharacterValue((TextCharacter) ch, new TextSpecimenValue((TextCharacter) ch, stringList));
                        } else {
                            throw new RuntimeException("Unrecognized character type");
                        }
                    } catch (IllegalArgumentException ex) {
                        throw new IntkeyDirectiveParseException(ex.getMessage());
                    }
                } else {
                    invoc.addCharacterValue(ch, null);
                }
            }
            return invoc;
        } else {
            throw new IntkeyDirectiveParseException("UseDirective.NoDataSetMsg");
        }
    }

    private void parseSubcommands(String subCmd, List<Integer> characterNumbers, List<String> specifiedValues, IntkeyContext context) throws Exception {

        List<Integer> parsedCharacterNumbers;

        // switch
        if (COMMA_SEPARATED_VALUE_PATTERN.matcher(subCmd).matches()) {
            // comma separated value: c,v

            String lhs = null;
            String rhs = null;

            String[] innerPieces = subCmd.split(",");
            if (innerPieces.length > 2) {
                throw new Exception("Bad Syntax");
            }

            lhs = innerPieces[0];

            if (innerPieces.length == 2) {
                rhs = innerPieces[1];
            }

            parsedCharacterNumbers = parseLHS(lhs, context);
            for (int c : parsedCharacterNumbers) {
                specifiedValues.add(rhs);
            }
        } else {
            parsedCharacterNumbers = parseLHS(subCmd, context);
            for (int c : parsedCharacterNumbers) {
                specifiedValues.add(null);
            }
        }
        characterNumbers.addAll(parsedCharacterNumbers);
    }

    private List<Integer> parseLHS(String lhs, IntkeyContext context) throws IntkeyDirectiveParseException {
        List<Integer> retList = new ArrayList<Integer>();

        List<Character> charList = ParsingUtils.parseCharacterToken(lhs, context);

        for (Character c : charList) {
            retList.add(c.getCharacterId());
        }

        return retList;
    }

}

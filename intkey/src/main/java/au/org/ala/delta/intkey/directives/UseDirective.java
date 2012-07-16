/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.intkey.directives.invocation.BasicIntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.UseDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.AttributeFactory;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.impl.SimpleAttributeData;
import au.org.ala.delta.util.Pair;

/**
 * The USE directive - allow the user to enter information about a specimen
 * 
 * @author ChrisF
 * 
 */
public class UseDirective extends IntkeyDirective {

    private static Pattern COMMA_SEPARATED_VALUE_PATTERN = Pattern.compile("^.+,.*$");
    public static String SUPPRESS_ALREADY_SET_WARNING_FLAG = "/M";

    private CharacterFormatter _charFormatter;

    public UseDirective() {
        super(true, "use");
        _charFormatter = new CharacterFormatter(false, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REPLACE, true, false);
    }

    @Override
    protected BasicIntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        return doProcess(context, data, false);
    }

    protected BasicIntkeyDirectiveInvocation doProcess(IntkeyContext context, String data, boolean change) throws Exception {
        if (context.getDataset() != null) {
            boolean suppressAlreadySetWarning = false;

            List<Integer> characterNumbers = new ArrayList<Integer>();
            List<String> specifiedValues = new ArrayList<String>();

            // Use a linked hash map to maintain key ordering.
            List<String> stringRepresentationParts = new ArrayList<String>();

            // Selection mode to use if prompting for characters
            SelectionMode selectionMode = context.displayKeywords() ? SelectionMode.KEYWORD : SelectionMode.LIST;

            boolean needToPrompt = false;
            if (data != null && data.trim().length() > 0) {
                List<String> subCommands = ParsingUtils.tokenizeDirectiveCall(data);

                for (String subCmd : subCommands) {
                    // Need to prompt for characters if data contains a wildcard
                    if (subCmd.equalsIgnoreCase(IntkeyDirectiveArgument.KEYWORD_DIALOG_WILDCARD)) {
                        selectionMode = SelectionMode.KEYWORD;
                        needToPrompt = true;
                        break;
                    } else if (subCmd.equalsIgnoreCase(IntkeyDirectiveArgument.LIST_DIALOG_WILDCARD)) {
                        selectionMode = SelectionMode.LIST;
                        needToPrompt = true;
                        break;
                    } else if (subCmd.equalsIgnoreCase(IntkeyDirectiveArgument.DEFAULT_DIALOG_WILDCARD)) {
                        // Use default selection mode as set above
                        needToPrompt = true;
                        break;
                    } else if (subCmd.equalsIgnoreCase(SUPPRESS_ALREADY_SET_WARNING_FLAG)) {
                        suppressAlreadySetWarning = true;
                    } else if (subCmd.startsWith("/")) {
                        // ignore additional undocumented flags.
                    } else {
                        parseSubcommands(subCmd, characterNumbers, specifiedValues, context, stringRepresentationParts);
                    }
                }
            }

            if (needToPrompt) {
                // No characters specified, prompt the user to select characters

                String directiveName = change ? StringUtils.join(new ChangeDirective().getControlWords(), " ").toUpperCase() : StringUtils.join(_controlWords, " ").toUpperCase();
                List<String> selectedKeywords = new ArrayList<String>(); // Not
                                                                         // interested
                                                                         // in
                                                                         // this.
                List<Character> selectedCharacters;
                if (selectionMode == SelectionMode.KEYWORD) {
                    selectedCharacters = context.getDirectivePopulator().promptForCharactersByKeyword(directiveName, true, false, selectedKeywords);
                } else {
                    selectedCharacters = context.getDirectivePopulator().promptForCharactersByList(directiveName, false, selectedKeywords);
                }

                if (selectedCharacters != null && selectedCharacters.size() > 0) {
                    for (Character ch : selectedCharacters) {
                        characterNumbers.add(ch.getCharacterId());
                        specifiedValues.add(null);
                        stringRepresentationParts.add(Integer.toString(ch.getCharacterId()));
                    }
                } else {
                    // User has hit cancel, nothing to execute
                    return null;
                }
            }

            UseDirectiveInvocation invoc = new UseDirectiveInvocation(change, suppressAlreadySetWarning, stringRepresentationParts);

            for (int i = 0; i < characterNumbers.size(); i++) {
                int charNum = characterNumbers.get(i);
                au.org.ala.delta.model.Character ch;
                try {
                    ch = context.getDataset().getCharacter(charNum);
                } catch (IllegalArgumentException ex) {
                    throw new IntkeyDirectiveParseException("UseDirective.InvalidCharacterNumber", charNum, context.getDataset().getNumberOfCharacters());
                }

                // Parse the supplied value for each character, if one was supplied. If one was no supplied, the user will be prompted to enter 
                // a value - this is done in UseDirectiveInvocation
                String charValue = specifiedValues.get(i);

                if (charValue != null) {
                    try {
                        if (ch instanceof MultiStateCharacter) {
                            MultiStateCharacter msCh = (MultiStateCharacter) ch;

                            try {
                                Set<Integer> setStateValues = ParsingUtils.parseMultistateOrIntegerCharacterValue(charValue);

                                for (int val : setStateValues) {
                                    if (val < 1 || val > msCh.getNumberOfStates()) {
                                        throw new IntkeyDirectiveParseException("UseDirective.InvalidStateValue", charValue, Integer.toString(ch.getCharacterId()),
                                                _charFormatter.formatCharacterDescription(ch), msCh.getNumberOfStates());
                                    }
                                }

                                SimpleAttributeData impl = new SimpleAttributeData(false, false);
                                impl.setPresentStateOrIntegerValues(setStateValues);
                                Attribute attr = AttributeFactory.newAttribute(ch, impl);
                                attr.setSpecimenAttribute(true);
                                invoc.addCharacterValue(ch, attr);
                            } catch (IllegalArgumentException ex) {
                                throw new IntkeyDirectiveParseException("UseDirective.InvalidStateValue", charValue, Integer.toString(ch.getCharacterId()),
                                        _charFormatter.formatCharacterDescription(ch), msCh.getNumberOfStates());
                            }
                        } else if (ch instanceof IntegerCharacter) {

                            try {
                                Set<Integer> intValues = ParsingUtils.parseMultistateOrIntegerCharacterValue(charValue);

                                SimpleAttributeData impl = new SimpleAttributeData(false, false);
                                impl.setPresentStateOrIntegerValues(intValues);
                                Attribute attr = AttributeFactory.newAttribute(ch, impl);
                                attr.setSpecimenAttribute(true);
                                invoc.addCharacterValue(ch, attr);
                            } catch (IllegalArgumentException ex) {
                                throw new IntkeyDirectiveParseException("UseDirective.InvalidIntegerValue", ex, charValue, Integer.toString(ch.getCharacterId()),
                                        _charFormatter.formatCharacterDescription(ch));
                            }
                        } else if (ch instanceof RealCharacter) {

                            try {
                                FloatRange floatRange = ParsingUtils.parseRealCharacterValue(charValue);

                                SimpleAttributeData impl = new SimpleAttributeData(false, false);
                                impl.setRealRange(floatRange);
                                Attribute attr = AttributeFactory.newAttribute(ch, impl);
                                attr.setSpecimenAttribute(true);
                                invoc.addCharacterValue(ch, attr);
                            } catch (IllegalArgumentException ex) {
                                throw new IntkeyDirectiveParseException("UseDirective.InvalidRealValue", ex, charValue, Integer.toString(ch.getCharacterId()),
                                        _charFormatter.formatCharacterDescription(ch));
                            }
                        } else if (ch instanceof TextCharacter) {
                            List<String> stringList = ParsingUtils.parseTextCharacterValue(charValue);

                            SimpleAttributeData impl = new SimpleAttributeData(false, false);
                            impl.setValueFromString(StringUtils.join(stringList, '/'));
                            Attribute attr = AttributeFactory.newAttribute(ch, impl);
                            invoc.addCharacterValue(ch, attr);
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

    private void parseSubcommands(String subCmd, List<Integer> characterNumbers, List<String> specifiedValues, IntkeyContext context, List<String> stringRepresentationParts) throws Exception {

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

            // ignore the right hand side if it is a wildcard - user will be
            // prompted to enter the value later.
            if (!rhs.startsWith(IntkeyDirectiveArgument.DEFAULT_DIALOG_WILDCARD)) {
                for (int c : parsedCharacterNumbers) {
                    specifiedValues.add(rhs);
                }
            }
            stringRepresentationParts.add(subCmd);

        } else {
            parsedCharacterNumbers = parseLHS(subCmd, context);
            for (int c : parsedCharacterNumbers) {
                specifiedValues.add(null);
                stringRepresentationParts.add(Integer.toString(c));
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

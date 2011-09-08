package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.intkey.directives.invocation.DifferencesDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.model.MatchType;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

public class DifferencesDirective extends IntkeyDirective {

    public DifferencesDirective() {
        super("differences");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        IntkeyDataset ds = context.getDataset();

        // Match settings default to those set on the context
        boolean matchUnknowns = context.getMatchUnkowns();
        boolean matchInapplicables = context.getMatchInapplicables();
        MatchType matchType = context.getMatchType();

        boolean omitTextCharacters = false;

        List<Item> taxa = new ArrayList<Item>();
        List<Character> characters = new ArrayList<Character>();

        List<String> taxaTokens = new ArrayList<String>();
        List<String> characterTokens = new ArrayList<String>();

        boolean includeSpecimen = false;

        if (data != null && data.trim().length() > 0) {
            List<String> tokens = ParsingUtils.tokenizeDirectiveCall(data);

            boolean processingTaxa = true;
            boolean inBracket = false;

            for (String token : tokens) {
                if (token.equalsIgnoreCase("/O")) {
                    matchType = MatchType.OVERLAP;
                } else if (token.equalsIgnoreCase("/S")) {
                    matchType = MatchType.SUBSET;
                } else if (token.equalsIgnoreCase("/E")) {
                    matchType = MatchType.EXACT;

                    // if match type is set to exact, match inapplicables and
                    // unknown will always be false
                    matchInapplicables = false;
                    matchUnknowns = false;
                } else if (token.equalsIgnoreCase("/U")) {
                    matchUnknowns = true;
                } else if (token.equalsIgnoreCase("/I")) {
                    matchInapplicables = true;
                } else if (token.equalsIgnoreCase("/X")) {
                    omitTextCharacters = true;
                } else if (token.equals("(")) {
                    if (processingTaxa) {
                        inBracket = true;
                    } else {
                        // TODO throw exception
                    }
                } else if (token.equals(")")) {
                    if (processingTaxa && inBracket) {
                        inBracket = false;
                        processingTaxa = false;
                    } else {
                        // TODO throw exception
                    }
                } else {
                    if (processingTaxa) {
                        taxaTokens.add(token);
                    } else {
                        characterTokens.add(token);
                    }
                }
            }

            for (String taxonToken : taxaTokens) {
                IntRange range = ParsingUtils.parseIntRange(taxonToken);
                if (range != null) {
                    for (int i : range.toArray()) {
                        Item t = ds.getTaxon(i);
                        taxa.add(t);
                    }
                } else {
                    if (taxonToken.equalsIgnoreCase(IntkeyContext.SPECIMEN_KEYWORD)) {
                        includeSpecimen = true;
                    } else {
                        List<Item> keywordTaxa = context.getTaxaForKeyword(taxonToken);
                        taxa.addAll(keywordTaxa);
                    }
                }
            }

            for (String characterToken : characterTokens) {
                IntRange range = ParsingUtils.parseIntRange(characterToken);
                if (range != null) {
                    for (int i : range.toArray()) {
                        Character c = ds.getCharacter(i);
                        characters.add(c);
                    }
                } else {
                    List<Character> keywordCharacters = context.getCharactersForKeyword(characterToken);
                    characters.addAll(keywordCharacters);
                }
            }
        }

        if (taxa.size() == 0) {
            taxa = context.getDirectivePopulator().promptForTaxaByKeyword("DIFFERENCES", true);
            if (taxa.size() == 0) {
                // user hit cancel or did not select anything
                return null;
            }

            // If specimen values have been entered, ask the user if they want
            // the specimen
            // included in the comparison
            if (context.getSpecimen().getUsedCharacters().size() > 0) {
                includeSpecimen = context.getDirectivePopulator().promptForYesNoOption("Compare specimen against selected taxa?");
            }
        } else {
            // If taxa not input using dialog, filter any taxa that are not
            // currently included
            List<Item> includedTaxa = context.getIncludedTaxa();
            taxa.retainAll(includedTaxa);
            if (taxa.isEmpty()) {
                context.getUI().displayErrorMessage("All selected taxa have been excluded");
                return null;
            }
        }

        int numTaxa = taxa.size();
        if (includeSpecimen) {
            numTaxa++;
        }
        
        if (numTaxa < 2) {
            throw new IllegalStateException("At least two taxa required for comparison");
        }

        if (characters.size() == 0) {
            characters = context.getDirectivePopulator().promptForCharactersByKeyword("DIFFERENCES", true);
            if (characters.size() == 0) {
                // user hit cancel or did not select anything
                return null;
            }
        } else {
            // If characters not input using dialog, filter any characters that
            // are not currently included
            List<Character> includedCharacters = context.getIncludedCharacters();
            characters.retainAll(includedCharacters);
            if (characters.isEmpty()) {
                context.getUI().displayErrorMessage("All selected characters have been excluded");
                return null;
            }
        }

        Collections.sort(taxa);
        Collections.sort(characters);

        DifferencesDirectiveInvocation invoc = new DifferencesDirectiveInvocation(matchUnknowns, matchInapplicables, matchType, omitTextCharacters, includeSpecimen, characters, taxa);

        return invoc;
    }
}

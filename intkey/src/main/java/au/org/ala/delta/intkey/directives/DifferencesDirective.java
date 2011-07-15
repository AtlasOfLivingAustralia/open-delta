package au.org.ala.delta.intkey.directives;

import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.rtf.RTFEditorKit;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.intkey.model.DiffUtils;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.model.MatchType;
import au.org.ala.delta.intkey.model.specimen.Specimen;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.rtf.RTFBuilder;
import au.org.ala.delta.rtf.RTFBuilder.Alignment;
import au.org.ala.delta.ui.rtf.RtfEditorPane;
import au.org.ala.delta.ui.rtf.SimpleRtfEditorKit;

public class DifferencesDirective extends IntkeyDirective {

    public DifferencesDirective() {
        super("differences");
    }

    @Override
    public int getArgType() {
        // TODO Auto-generated method stub
        return 0;
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
                        // throw exception
                    }
                } else if (token.equals(")")) {
                    if (processingTaxa && inBracket) {
                        inBracket = false;
                        processingTaxa = false;
                    } else {
                        // throw exception
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
                    if (taxonToken.equalsIgnoreCase("specimen")) {
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

        if (taxa.size() < 2) {
            throw new IllegalStateException("Need 2 or more taxa");
        }
        
        if (characters.size() == 0) {
            throw new IllegalStateException("Need 1 or more characters");
        }
        
        Collections.sort(taxa);
        Collections.sort(characters);

        DifferencesDirectiveInvocation invoc = new DifferencesDirectiveInvocation(matchUnknowns, matchInapplicables, matchType, omitTextCharacters, includeSpecimen, characters, taxa);

        return invoc;
    }

    private class DifferencesDirectiveInvocation implements IntkeyDirectiveInvocation {

        private boolean _matchUnknowns;
        private boolean _matchInapplicables;
        private MatchType _matchType;

        private boolean _omitTextCharacters;

        private List<Character> _characters;
        private List<Item> _taxa;

        private CharacterFormatter _characterFormatter;
        private ItemFormatter _taxonFormatter;
        private AttributeFormatter _attributeFormatter;

        private boolean _includeSpecimen;

        public DifferencesDirectiveInvocation(boolean matchUnknowns, boolean matchInapplicables, MatchType matchType, boolean omitTextCharacters, boolean includeSpecimen, List<Character> characters,
                List<Item> taxa) {
            _matchUnknowns = matchUnknowns;
            _matchInapplicables = matchInapplicables;
            _matchType = matchType;
            _includeSpecimen = includeSpecimen;
            _omitTextCharacters = omitTextCharacters;
            _characters = new ArrayList<Character>(characters);
            _taxa = new ArrayList<Item>(taxa);
            _characterFormatter = new CharacterFormatter(false, false, false, false);
            _taxonFormatter = new ItemFormatter(false, false, false, false, false);
            _attributeFormatter = new AttributeFormatter(false, false, false);
        }

        @Override
        public boolean execute(IntkeyContext context) {
            List<Character> differences = new ArrayList<Character>();

            Specimen specimen = null;
            if (_includeSpecimen) {
                specimen = context.getSpecimen();
            }
            
            for (Character ch : _characters) {
                if (ch instanceof TextCharacter && _omitTextCharacters) {
                    continue;
                }
                boolean match = DiffUtils.compareForTaxa(context.getDataset(), ch, _taxa, specimen, _matchUnknowns, _matchInapplicables, _matchType);
                if (!match) {
                    differences.add(ch);
                }
            }

            RTFBuilder builder = new RTFBuilder();
            builder.startDocument();

            for (Character ch : differences) {
                List<Attribute> attrs = context.getDataset().getAttributesForCharacter(ch.getCharacterId());

                String charDescription = _characterFormatter.formatCharacterDescription(ch);
                builder.appendText(charDescription);

                builder.increaseIndent();

                for (Item taxon : _taxa) {
                    Attribute taxonAttr = attrs.get(taxon.getItemNumber() - 1);

                    String taxonDescription = _taxonFormatter.formatItemDescription(taxon);
                    builder.appendText(taxonDescription);

                    builder.increaseIndent();

                    String attributeDescription = _attributeFormatter.formatAttribute(taxonAttr);

                    if (StringUtils.isBlank(attributeDescription)) {
                        builder.appendText("not recorded");
                    } else {
                        builder.appendText(attributeDescription);
                    }

                    builder.decreaseIndent();

                }

                builder.decreaseIndent();
                builder.appendText("");
            }

            builder.setTextColor(Color.RED);
            builder.setFont(1);

            if (differences.size() == 0) {
                builder.appendText("No differences");
            } else if (differences.size() == 1) {
                builder.appendText(String.format("%s difference", differences.size()));
            } else {
                builder.appendText(String.format("%s differences", differences.size()));
            }

            builder.endDocument();

            JTextPane rtfEditorPane = new JTextPane();
            rtfEditorPane.setEditorKit(new SimpleRtfEditorKit());
            rtfEditorPane.setText(builder.toString());
            rtfEditorPane.setEditable(false);
            JDialog dlg = new JDialog();
            JScrollPane sclPn = new JScrollPane();
            sclPn.setViewportView(rtfEditorPane);
            dlg.add(sclPn);
            UIUtils.showDialog(dlg);

            return true;
        }
    }
}

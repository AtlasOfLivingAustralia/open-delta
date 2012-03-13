package au.org.ala.delta.intkey.directives.invocation;

import java.awt.Color;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import au.org.ala.delta.best.Best;
import au.org.ala.delta.best.DiagType;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MatchType;
import au.org.ala.delta.model.Specimen;
import au.org.ala.delta.util.Pair;

public class OutputDiagnoseDirectiveInvocation extends IntkeyDirectiveInvocation {
    
    private List<Item> _taxa;
    private List<Character> _presetCharacters;

    public void setSelectedTaxaSpecimen(Pair<List<Item>, Boolean> pair) {
        this._taxa = pair.getFirst();
        // the SPECIMEN cannot be selected with the taxa for this directive.
        // Simply ignore
        // it if "SPECIMEN" is supplied
    }

    public void setCharacters(List<Character> characters) {
        this._presetCharacters = characters;
    }

    @Override
    public boolean execute(IntkeyContext context) throws IntkeyDirectiveInvocationException {
        /*int targetDiagLevel = context.getDiagLevel();
        DiagType diagType = context.getDiagType();
        IntkeyDataset dataset = context.getDataset();

        // Remove any exclude taxa from the supplied list of taxa, and any
        // excluded characters from the
        // supplied list of preset characters
        _taxa.removeAll(context.getExcludedTaxa());
        _presetCharacters.removeAll(context.getExcludedCharacters());
        
        // derive diagnostic character set for specified items from set of
        // masked-in characters.
        for (Item taxon : _taxa) {
            // output taxon name

            Specimen specimen = new Specimen(context.getDataset(), true, true, true, MatchType.OVERLAP);

            List<Character> remainingCharacters = context.getIncludedCharacters();
            remainingCharacters.removeAll(context.getDataset().getCharactersToIgnoreForBest());
            updateRemainingCharactersFromSpecimen(remainingCharacters, specimen);

            List<Item> remainingTaxa = new ArrayList<Item>(_taxa);
            updateRemainingTaxaFromSpecimen(remainingTaxa, specimen, targetDiagLevel);

            // process preset characters first
            for (Character ch : _presetCharacters) {
                Attribute attr = dataset.getAttribute(taxon.getItemNumber(), ch.getCharacterId());
                useAttribute(specimen, attr, diagType, targetDiagLevel, remainingCharacters, remainingTaxa, characterFormatter, attributeFormatter, builder);
            }
            
            if (remainingTaxa.size() > 1) {
                for (int currentDiagLevel = 1; currentDiagLevel < targetDiagLevel + 1; currentDiagLevel++) {
                    List<Item> remainingTaxaForDiagLevel = new ArrayList<Item>(remainingTaxa);
                    updateRemainingTaxaFromSpecimen(remainingTaxaForDiagLevel, specimen, currentDiagLevel);

                    while (remainingTaxaForDiagLevel.size() > 1) {
                        LinkedHashMap<Character, Double> bestOrdering = Best.orderDiagnose(taxon.getItemNumber(), diagType, context.getStopBest(), dataset,
                                characterListToIntegerList(remainingCharacters), taxonListToIntegerList(remainingTaxaForDiagLevel), context.getRBase(), context.getVaryWeight());
                        if (bestOrdering.isEmpty()) {
                            break;
                        }

                        Character firstDiagnoseChar = bestOrdering.keySet().iterator().next();
                        Attribute attr = dataset.getAttribute(taxon.getItemNumber(), firstDiagnoseChar.getCharacterId());
                        useAttribute(specimen, attr, diagType, currentDiagLevel, remainingCharacters, remainingTaxaForDiagLevel, characterFormatter, attributeFormatter, builder);
                    }
                }
            }
        */
        return true;
    }

}

package au.org.ala.delta.intkey.directives.invocation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.org.ala.delta.best.Best;
import au.org.ala.delta.best.DiagType;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.model.ReportUtils;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MatchType;
import au.org.ala.delta.model.Specimen;
import au.org.ala.delta.util.Pair;

/**
 * Abstract class for DiagnoseDirectiveInvocation and
 * OutputDiagnoseDirectiveInvocation. Provides some shared utility methods
 * 
 * @author ChrisF
 * 
 */
public abstract class AbstractDiagnoseDirectiveInvocation extends LongRunningIntkeyDirectiveInvocation<String> {

    protected List<Item> _taxa;
    protected List<Character> _presetCharacters;

    public void setSelectedTaxaSpecimen(Pair<List<Item>, Boolean> pair) {
        this._taxa = pair.getFirst();
        // the SPECIMEN cannot be selected with the taxa for this directive.
        // Simply ignore
        // it if "SPECIMEN" is supplied
    }

    public void setCharacters(List<Character> characters) {
        this._presetCharacters = characters;
    }

    protected boolean doDiagnose(IntkeyContext context, String progressMessageTemplate) {
        int diagLevel = context.getDiagLevel();
        DiagType diagType = context.getDiagType();

        // Remove any exclude taxa from the supplied list of taxa, and any
        // excluded characters from the
        // supplied list of preset characters
        _taxa.removeAll(context.getExcludedTaxa());
        _presetCharacters.removeAll(context.getExcludedCharacters());

        IntkeyDataset dataset = context.getDataset();

        int numTaxaProcessed = 0;
        updateProgess(numTaxaProcessed, _taxa.size(), progressMessageTemplate);
        
        // derive diagnostic character set for specified items from set of
        // masked-in characters.
        for (Item taxon : _taxa) {
            handleStartProcessingTaxon(taxon);

            Specimen specimen = new Specimen(context.getDataset(), true, true, true, MatchType.OVERLAP);

            List<Character> remainingCharacters = context.getIncludedCharacters();
            remainingCharacters.removeAll(context.getDataset().getCharactersToIgnoreForBest());
            updateRemainingCharactersFromSpecimen(remainingCharacters, specimen);

            // Each diagnostic description will (if possible) differ in at least
            // DIAGLEVEL characters
            // from the full description of *every other included taxon (not
            // just the taxa specified in this command)*
            List<Item> remainingTaxa = context.getIncludedTaxa();

            updateRemainingTaxaFromSpecimen(remainingTaxa, specimen, diagLevel);

            // process preset characters first
            for (Character ch : _presetCharacters) {
                Attribute attr = dataset.getAttribute(taxon.getItemNumber(), ch.getCharacterId());
                useAttribute(specimen, attr, diagType, diagLevel, remainingCharacters, remainingTaxa);
            }

            // calculate further separation characters for current taxon
            boolean diagLevelNotAttained = false;
            if (remainingTaxa.size() > 1) {
                for (int i = 1; i < diagLevel + 1; i++) {
                    List<Item> remainingTaxaForDiagLevel = new ArrayList<Item>(remainingTaxa);
                    updateRemainingTaxaFromSpecimen(remainingTaxaForDiagLevel, specimen, i);

                    while (remainingTaxaForDiagLevel.size() > 1) {
                        LinkedHashMap<Character, Double> bestOrdering = Best
                                .orderDiagnose(taxon.getItemNumber(), diagType, context.getStopBest(), dataset, ReportUtils.characterListToIntegerList(remainingCharacters),
                                        ReportUtils.taxonListToIntegerList(remainingTaxaForDiagLevel), context.getRBase(), context.getVaryWeight());
                        if (bestOrdering.isEmpty()) {
                            break;
                        }

                        Character firstDiagnoseChar = bestOrdering.keySet().iterator().next();

                        Attribute attr = dataset.getAttribute(taxon.getItemNumber(), firstDiagnoseChar.getCharacterId());

                        useAttribute(specimen, attr, diagType, i, remainingCharacters, remainingTaxaForDiagLevel);
                    }

                    if (remainingTaxaForDiagLevel.size() == 1) {
                        handleDiagLevelAttained(i);
                    } else {
                        // If we have failed to reach a diagnostic level, output
                        // a message to this effect (RTF version of the diagnose
                        // report only -
                        // this is not done for OUTPUT DIAGNOSE.
                        // Continue to use and output further diagnostic
                        // characters however.
                        if (!diagLevelNotAttained) {
                            diagLevelNotAttained = true;
                            handleDiagLevelNotAttained(i);
                        }
                    }
                }
            }

            updateRemainingTaxaFromSpecimen(remainingTaxa, specimen, diagLevel);
            handleEndProcessingTaxon(taxon, diagLevelNotAttained, specimen, remainingTaxa);
            
            updateProgess(++numTaxaProcessed, _taxa.size(), progressMessageTemplate);
        }

        return true;
    }
    
    private void updateProgess(int numTaxaProcessed, int totalNumTaxa, String messageTemplate) {
        int progressPercent = (int) Math.floor((((double) numTaxaProcessed) / totalNumTaxa) * 100);
        progress(MessageFormat.format(messageTemplate, progressPercent));
    }

    protected void useAttribute(Specimen specimen, Attribute attr, DiagType diagType, int diagLevel, List<Character> remainingCharacters, List<Item> remainingTaxa) {

        // Ignore "maybe inapplicable" characters if DiagType is SPECIMENS
        if (!attr.isUnknown() && (!attr.isInapplicable() || diagType == DiagType.TAXA)) {

            specimen.setAttributeForCharacter(attr.getCharacter(), attr);

            handleCharacterUsed(attr);

            updateRemainingCharactersFromSpecimen(remainingCharacters, specimen);
            updateRemainingTaxaFromSpecimen(remainingTaxa, specimen, diagLevel);
        }
    }

    protected abstract void handleStartProcessingTaxon(Item taxon);

    protected abstract void handleCharacterUsed(Attribute attr);

    protected abstract void handleDiagLevelAttained(int diagLevel);

    protected abstract void handleDiagLevelNotAttained(int diagLevel);

    protected abstract void handleEndProcessingTaxon(Item taxon, boolean diagLevelNotAttained, Specimen specimen, List<Item> remainingTaxa);

    protected void updateRemainingCharactersFromSpecimen(List<Character> remainingCharacters, Specimen specimen) {
        remainingCharacters.removeAll(specimen.getUsedCharacters());
        remainingCharacters.removeAll(specimen.getInapplicableCharacters());
    }

    protected void updateRemainingTaxaFromSpecimen(List<Item> remainingTaxa, Specimen specimen, int diagLevel) {
        Map<Item, Set<Character>> taxonDifferences = specimen.getTaxonDifferences();

        for (Item t : taxonDifferences.keySet()) {
            // tolerance = diaglevel - 1
            if (taxonDifferences.get(t).size() > diagLevel - 1) {
                remainingTaxa.remove(t);
            }
        }
    }
}

package au.org.ala.delta.intkey.directives.invocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.org.ala.delta.best.DiagType;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.Specimen;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.rtf.RTFBuilder;

/**
 * Abstract class for DiagnoseDirectiveInvocation and OutputDiagnoseDirectiveInvocation. Provides some
 * shared utility methods
 * @author ChrisF
 *
 */
public abstract class AbstractDiagnoseDirectiveInvocation extends IntkeyDirectiveInvocation {

    protected void useAttribute(Specimen specimen, Attribute attr, DiagType diagType, int diagLevel, List<Character> remainingCharacters, List<Item> remainingTaxa,
            CharacterFormatter characterFormatter, AttributeFormatter attributeFormatter, RTFBuilder builder) {

        // Ignore "maybe inapplicable" characters if DiagType is SPECIMENS
        if (!attr.isUnknown() && (!attr.isInapplicable() || diagType == DiagType.TAXA)) {
            builder.appendText(String.format("%s %s", characterFormatter.formatCharacterDescription(attr.getCharacter()), attributeFormatter.formatAttribute(attr)));

            specimen.setAttributeForCharacter(attr.getCharacter(), attr);

            updateRemainingCharactersFromSpecimen(remainingCharacters, specimen);
            updateRemainingTaxaFromSpecimen(remainingTaxa, specimen, diagLevel);
        }
    }

    protected List<Integer> characterListToIntegerList(List<Character> characters) {
        List<Integer> characterNumbers = new ArrayList<Integer>();

        for (Character ch : characters) {
            characterNumbers.add(ch.getCharacterId());
        }

        return characterNumbers;
    }

    protected List<Integer> taxonListToIntegerList(List<Item> taxa) {
        List<Integer> taxaNumbers = new ArrayList<Integer>();

        for (Item taxon : taxa) {
            taxaNumbers.add(taxon.getItemNumber());
        }

        return taxaNumbers;
    }

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

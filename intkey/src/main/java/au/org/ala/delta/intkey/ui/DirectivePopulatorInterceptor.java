package au.org.ala.delta.intkey.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.mutable.MutableBoolean;

import au.org.ala.delta.intkey.model.DisplayImagesReportType;
import au.org.ala.delta.intkey.model.ImageDisplayMode;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.util.Pair;

/**
 * This class wraps another instance of IntkeyUI. Any methods called from a
 * thread other than the Swing event dispatch thread will be ignored, and will
 * return null values.
 * 
 * @author ChrisF
 * 
 */
public class DirectivePopulatorInterceptor implements DirectivePopulator {

    private DirectivePopulator _wrappedDirectivePopulator;

    public DirectivePopulatorInterceptor(DirectivePopulator wrappedDirectivePopulator) {
        _wrappedDirectivePopulator = wrappedDirectivePopulator;
    }

    @Override
    public List<Character> promptForCharactersByKeyword(String directiveName, boolean permitSelectionFromIncludedCharactersOnly, boolean noneKeywordAvailable, List<String> returnSelectedKeywords) {
        if (SwingUtilities.isEventDispatchThread()) {
            return _wrappedDirectivePopulator.promptForCharactersByKeyword(directiveName, permitSelectionFromIncludedCharactersOnly, noneKeywordAvailable, returnSelectedKeywords);
        } else {
            return null;
        }
    }

    @Override
    public List<Character> promptForCharactersByList(String directiveName, boolean selectFromAvailableCharactersOnly, List<String> returnSelectedKeywords) {
        if (SwingUtilities.isEventDispatchThread()) {
            return _wrappedDirectivePopulator.promptForCharactersByList(directiveName, selectFromAvailableCharactersOnly, returnSelectedKeywords);
        } else {
            return null;
        }
    }

    @Override
    public List<Item> promptForTaxaByKeyword(String directiveName, boolean permitSelectionFromIncludedTaxaOnly, boolean noneKeywordAvailable, boolean includeSpecimenAsOption,
            MutableBoolean returnSpecimenSelected, List<String> returnSelectedKeywords) {
        if (SwingUtilities.isEventDispatchThread()) {
            return _wrappedDirectivePopulator.promptForTaxaByKeyword(directiveName, permitSelectionFromIncludedTaxaOnly, noneKeywordAvailable, includeSpecimenAsOption, returnSpecimenSelected,
                    returnSelectedKeywords);
        } else {
            return null;
        }
    }

    @Override
    public List<Item> promptForTaxaByList(String directiveName, boolean selectFromIncludedTaxaOnly, boolean autoSelectSingleValue, boolean singleSelect, boolean includeSpecimenAsOption,
            MutableBoolean returnSpecimenSelected, List<String> returnSelectedKeywords) {
        if (SwingUtilities.isEventDispatchThread()) {
            return _wrappedDirectivePopulator.promptForTaxaByList(directiveName, selectFromIncludedTaxaOnly, autoSelectSingleValue, singleSelect, includeSpecimenAsOption, returnSpecimenSelected,
                    returnSelectedKeywords);
        } else {
            return null;
        }
    }

    @Override
    public Boolean promptForYesNoOption(String message) {
        if (SwingUtilities.isEventDispatchThread()) {
            return _wrappedDirectivePopulator.promptForYesNoOption(message);
        } else {
            return null;
        }
    }

    @Override
    public String promptForString(String message, String initialSelectionValue, String directiveName) {
        if (SwingUtilities.isEventDispatchThread()) {
            return _wrappedDirectivePopulator.promptForString(message, initialSelectionValue, directiveName);
        } else {
            return null;
        }
    }

    @Override
    public List<String> promptForTextValue(TextCharacter ch, List<String> currentValue) {
        if (SwingUtilities.isEventDispatchThread()) {
            return _wrappedDirectivePopulator.promptForTextValue(ch, currentValue);
        } else {
            return null;
        }
    }

    @Override
    public Set<Integer> promptForIntegerValue(IntegerCharacter ch, Set<Integer> currentValue) {
        if (SwingUtilities.isEventDispatchThread()) {
            return _wrappedDirectivePopulator.promptForIntegerValue(ch, currentValue);
        } else {
            return null;
        }
    }

    @Override
    public FloatRange promptForRealValue(RealCharacter ch, FloatRange currentValue) {
        if (SwingUtilities.isEventDispatchThread()) {
            return _wrappedDirectivePopulator.promptForRealValue(ch, currentValue);
        } else {
            return null;
        }
    }

    @Override
    public Set<Integer> promptForMultiStateValue(MultiStateCharacter ch, Set<Integer> currentSelectedStates, Character dependentCharacter) {
        if (SwingUtilities.isEventDispatchThread()) {
            return _wrappedDirectivePopulator.promptForMultiStateValue(ch, currentSelectedStates, dependentCharacter);
        } else {
            return null;
        }
    }

    @Override
    public File promptForFile(List<String> fileExtensions, List<String> filePrefixes, String description, boolean createFileIfNonExistant) throws IOException {
        if (SwingUtilities.isEventDispatchThread()) {
            return _wrappedDirectivePopulator.promptForFile(fileExtensions, filePrefixes, description, createFileIfNonExistant);
        } else {
            return null;
        }
    }

    @Override
    public Boolean promptForOnOffValue(String directiveName, boolean initialValue) {
        if (SwingUtilities.isEventDispatchThread()) {
            return _wrappedDirectivePopulator.promptForOnOffValue(directiveName, initialValue);
        } else {
            return null;
        }
    }

    @Override
    public List<Object> promptForMatchSettings() {
        if (SwingUtilities.isEventDispatchThread()) {
            return _wrappedDirectivePopulator.promptForMatchSettings();
        } else {
            return null;
        }
    }

    @Override
    public List<Object> promptForButtonDefinition() {
        if (SwingUtilities.isEventDispatchThread()) {
            return _wrappedDirectivePopulator.promptForButtonDefinition();
        } else {
            return null;
        }
    }

    @Override
    public Pair<ImageDisplayMode, DisplayImagesReportType> promptForImageDisplaySettings() {
        if (SwingUtilities.isEventDispatchThread()) {
            return _wrappedDirectivePopulator.promptForImageDisplaySettings();
        } else {
            return null;
        }
    }

    @Override
    public String promptForDataset() {
        if (SwingUtilities.isEventDispatchThread()) {
            return _wrappedDirectivePopulator.promptForDataset();
        } else {
            return null;
        }
    }

}

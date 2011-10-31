package au.org.ala.delta.intkey.model;

import java.io.File;
import java.net.URL;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.PreferencesDirective;
import au.org.ala.delta.model.Character;

public class PreferencesDirectiveTest extends IntkeyDatasetTestCase {

    @Test
    public void testPreferences() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        URL preferencesFileUrl = getClass().getResource("/input/test_directives_file.txt");
        File preferencesFile = new File(preferencesFileUrl.toURI());

        new PreferencesDirective().parseAndProcess(context, preferencesFile.getAbsolutePath());

        Character firstDatasetChar1 = context.getDataset().getCharacter(1);

        assertEquals(1, context.getCharactersForKeyword("preferencestest").size());
        assertEquals(firstDatasetChar1, context.getCharactersForKeyword("preferencestest").get(0));

        loadNewDatasetInExistingContext("/dataset/controlling_characters_simple/intkey.ink", context);

        Character secondDatasetChar1 = context.getDataset().getCharacter(1);
        assertEquals(1, context.getCharactersForKeyword("preferencestest").size());
        assertEquals(firstDatasetChar1, context.getCharactersForKeyword("preferencestest").get(0));
    }
}

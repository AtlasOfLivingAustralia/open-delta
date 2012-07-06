package au.org.ala.delta.intkey.model;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.DeleteDirective;
import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.Specimen;
import au.org.ala.delta.model.UnorderedMultiStateCharacter;

public class DeleteDirectiveTest extends IntkeyDatasetTestCase {

    @Test
    public void testDependentCharactersRemoved() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_simple/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        new UseDirective().parseAndProcess(context, "4,1");

        // Delete the ancestor controlling character
        new DeleteDirective().parseAndProcess(context, "2");

        Specimen specimen = context.getSpecimen();

        assertTrue(specimen.getUsedCharacters().isEmpty());
    }
}

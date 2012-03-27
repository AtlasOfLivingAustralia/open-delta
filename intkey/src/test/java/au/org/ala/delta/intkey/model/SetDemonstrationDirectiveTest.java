package au.org.ala.delta.intkey.model;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.ChangeDirective;
import au.org.ala.delta.intkey.directives.DeleteDirective;
import au.org.ala.delta.intkey.directives.IncludeTaxaDirective;
import au.org.ala.delta.intkey.directives.RestartDirective;
import au.org.ala.delta.intkey.directives.SetDemonstrationDirective;
import au.org.ala.delta.intkey.directives.SetFixDirective;
import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Specimen;

public class SetDemonstrationDirectiveTest extends IntkeyDatasetTestCase {

    @Test
    public void testSetDemonstration() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        Specimen specimen = context.getSpecimen();

        // Set state that we want to return to each time RESET is called while in demonstration mode
        new UseDirective().parseAndProcess(context, "38,1"); // this will set controlling character 32 with state 1
        new UseDirective().parseAndProcess(context, "54,1");
        new IncludeTaxaDirective().parseAndProcess(context, "1-10");
        new SetFixDirective().parseAndProcess(context, "ON");
        
        au.org.ala.delta.model.Character char32 = context.getDataset().getCharacter(32);
        au.org.ala.delta.model.Character char38 = context.getDataset().getCharacter(38);
        au.org.ala.delta.model.Character char54 = context.getDataset().getCharacter(32);
        Attribute char32Attr = specimen.getAttributeForCharacter(char32);
        Attribute char38Attr = specimen.getAttributeForCharacter(char38);
        Attribute char54Attr = specimen.getAttributeForCharacter(char54);
        
        // Enter demonstration mode
        new SetDemonstrationDirective().parseAndProcess(context, "ON");
        
        // change state
        new SetFixDirective().parseAndProcess(context, "OFF");
        assertFalse(context.charactersFixed());
        new ChangeDirective().parseAndProcess(context, "32,2");
        new UseDirective().parseAndProcess(context, "52,1");
        new DeleteDirective().parseAndProcess(context, "54");
        new IncludeTaxaDirective().parseAndProcess(context, "1-5");

        
        // Restart identification
        new RestartDirective().parseAndProcess(context, null);
        
        //check state has been reverted to how it was set before we entered demonstration mode
        assertTrue(context.charactersFixed());
        assertTrue(specimen.getAttributeForCharacter(char32).equals(char32Attr));
        assertTrue(specimen.getAttributeForCharacter(char38).equals(char38Attr));
        assertTrue(specimen.getAttributeForCharacter(char54).equals(char54Attr));
        assertFalse(specimen.hasValueFor(context.getDataset().getCharacter(52)));
        assertEquals(10, context.getIncludedTaxa().size());
        assertTrue(context.getIncludedCharacters().contains(context.getDataset().getCharacter(1)));
        assertTrue(context.getIncludedCharacters().contains(context.getDataset().getCharacter(2)));
        assertTrue(context.getIncludedCharacters().contains(context.getDataset().getCharacter(3)));
        assertTrue(context.getIncludedCharacters().contains(context.getDataset().getCharacter(4)));
        assertTrue(context.getIncludedCharacters().contains(context.getDataset().getCharacter(5)));
        assertTrue(context.getIncludedCharacters().contains(context.getDataset().getCharacter(6)));
        assertTrue(context.getIncludedCharacters().contains(context.getDataset().getCharacter(7)));
        assertTrue(context.getIncludedCharacters().contains(context.getDataset().getCharacter(8)));
        assertTrue(context.getIncludedCharacters().contains(context.getDataset().getCharacter(9)));
        assertTrue(context.getIncludedCharacters().contains(context.getDataset().getCharacter(10)));
    }
    
}

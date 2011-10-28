package au.org.ala.delta.intkey.model;

import au.org.ala.delta.intkey.directives.DefineEndIdentifyDirective;
import au.org.ala.delta.intkey.directives.DisplayEndIdentifyDirective;
import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.model.Character;

public class EndIdentifyTest extends IntkeyDatasetTestCase {

    public void testEndIdentify() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_simple/intkey.ink");

        new DisplayEndIdentifyDirective().parseAndProcess(context, "ON");
        new DefineEndIdentifyDirective().parseAndProcess(context, "\"define characters endidentify 1\"");
        new UseDirective().parseAndProcess(context, "6,2");

        Character ch = context.getDataset().getCharacter(1);

        assertEquals(1, context.getCharactersForKeyword("endidentify").size());
        assertEquals(ch, context.getCharactersForKeyword("endidentify").get(0));
    }

    public void testEndIdentifyCommandsIgnoredIfDisplayEndIdentifyOff() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_simple/intkey.ink");

        new DisplayEndIdentifyDirective().parseAndProcess(context, "OFF");
        new DefineEndIdentifyDirective().parseAndProcess(context, "\"define characters endidentify 1\"");
        new UseDirective().parseAndProcess(context, "6,2");

        // An attempt to get characters for keyword "endidentify" should result
        // in an IllegalArgumentException
        // due to the keyword having not been defined.
        boolean exceptionThrown = false;

        try {
            context.getCharactersForKeyword("endidentify");
        } catch (IllegalArgumentException ex) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }
}

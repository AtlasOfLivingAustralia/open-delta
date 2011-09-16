package au.org.ala.delta.intkey.model;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.SetAutoToleranceDirective;
import au.org.ala.delta.intkey.directives.SetToleranceDirective;
import au.org.ala.delta.intkey.directives.UseDirective;

public class SetAutoToleranceTest extends IntkeyDatasetTestCase {

    @Test
    public void testSetAutoTolerance() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        new SetToleranceDirective().parseAndProcess(context, "100");
        new UseDirective().parseAndProcess(context, "38,5");
        assertEquals(100, context.getTolerance());
        new SetAutoToleranceDirective().parseAndProcess(context, "ON");
        new UseDirective().parseAndProcess(context, "54,5");
        assertEquals(0, context.getTolerance());
    }

    public void testSetAutoToleranceOnThenOff() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        new SetToleranceDirective().parseAndProcess(context, "100");
        new SetAutoToleranceDirective().parseAndProcess(context, "ON");
        new SetAutoToleranceDirective().parseAndProcess(context, "OFF");
        new UseDirective().parseAndProcess(context, "38,5");
        assertEquals(100, context.getTolerance());
    }

}

package au.org.ala.delta.intkey.model;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.SummaryDirective;

public class SummaryDirectiveTest extends IntkeyDatasetTestCase {

    @Test
    public void testSummary() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        new SummaryDirective().parseAndProcess(context, "all 48");
    }
    
}

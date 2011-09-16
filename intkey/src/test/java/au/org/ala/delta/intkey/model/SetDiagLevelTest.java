package au.org.ala.delta.intkey.model;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.SetDiagLevelDirective;

public class SetDiagLevelTest extends TestCase {

    @Test
    public void testSetDiagLevel() throws Exception {
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        new SetDiagLevelDirective().parseAndProcess(context, "1");
    }

    @Test
    public void testSetDiagLevelWithPrompt() throws Exception {
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        new SetDiagLevelDirective().parseAndProcess(context, null);
    }

}

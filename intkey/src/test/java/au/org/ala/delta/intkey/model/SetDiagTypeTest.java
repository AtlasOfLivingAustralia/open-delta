package au.org.ala.delta.intkey.model;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.SetDiagTypeSpecimensDirective;
import au.org.ala.delta.intkey.directives.SetDiagTypeTaxaDirective;

public class SetDiagTypeTest extends TestCase {

    @Test
    public void testSetDiagTypeSpecimens() throws Exception {
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        new SetDiagTypeSpecimensDirective().parseAndProcess(context, null);
        assertEquals(DiagType.SPECIMENS, context.getDiagType());
    }

    @Test
    public void testSetDiagTypeTaxa() throws Exception {
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        new SetDiagTypeTaxaDirective().parseAndProcess(context, null);
        assertEquals(DiagType.TAXA, context.getDiagType());
    }

}

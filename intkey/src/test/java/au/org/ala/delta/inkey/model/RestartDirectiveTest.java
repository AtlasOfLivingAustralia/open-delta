package au.org.ala.delta.inkey.model;

import java.io.File;
import java.net.URL;
import java.util.Collections;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.RestartDirective;
import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.model.specimen.Specimen;

/**
 * Unit tests for the RESTART directive
 * 
 * @author ChrisF
 * 
 */
public class RestartDirectiveTest extends TestCase {

    /**
     * Set some values for characters in the specimen, then run the restart
     * directive
     * 
     * @throws Exception
     */
    @Test
    public void testRestart() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        IntkeyDataset ds = context.getDataset();

        new UseDirective().parseAndProcess(context, "2-5,1");

        new RestartDirective().parseAndProcess(context, null);

        Specimen specimen = context.getSpecimen();
        assertEquals(Collections.EMPTY_LIST, specimen.getUsedCharacters());
        assertEquals(ds.getCharacters(), specimen.getAvailableCharacters());
    }

    /**
     * Run the restart directive without having set any values for characters
     * first
     * 
     * @throws Exception
     */
    @Test
    public void testRestartImmediately() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        IntkeyDataset ds = context.getDataset();

        new RestartDirective().parseAndProcess(context, null);

        Specimen specimen = context.getSpecimen();
        assertEquals(Collections.EMPTY_LIST, specimen.getUsedCharacters());
        assertEquals(ds.getCharacters(), specimen.getAvailableCharacters());
    }

    // TODO check that characters that have had their values fixed using SET FIX
    // have their values maintained when the RESTART directive is run.

}

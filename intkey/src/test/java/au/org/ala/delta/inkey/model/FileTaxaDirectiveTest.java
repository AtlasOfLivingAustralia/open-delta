package au.org.ala.delta.inkey.model;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.FileTaxaDirective;
import au.org.ala.delta.intkey.model.IntkeyContext;

/**
 * Unit tests for the FILE TAXA directive
 * 
 * @author ChrisF
 * 
 */
public class FileTaxaDirectiveTest extends TestCase {

    @Test
    public void testSetValidTaxaFile() throws Exception {
        IntkeyContext context = new IntkeyContext(null);
        URL iitemsFileUrl = getClass().getResource("/dataset/sample/iitems");

        File fileTaxa = new File(iitemsFileUrl.toURI());

        new FileTaxaDirective().parseAndProcess(context, fileTaxa.getAbsolutePath());

        assertEquals(fileTaxa, context.getTaxaFile());
    }

    @Test
    public void testSetInvalidCharactersFile() throws Exception {
        // TODO
    }
}

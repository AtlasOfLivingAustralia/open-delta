package au.org.ala.delta.inkey.model;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import au.org.ala.delta.intkey.model.IntkeyContext;

import junit.framework.TestCase;

public class DataSetLoadTest extends TestCase {
    
    
    /**
     * Test opening the sample dataset by 
     * setting the characters file and the items file directly 
     */
    @Test
    public void testReadSampleCharactersAndItems() throws Exception {
        URL icharsFileUrl = getClass().getResource("/dataset/sample/ichars");
        URL iitemsFileUrl = getClass().getResource("/dataset/sample/iitems");

        IntkeyContext context = new IntkeyContext(null);
        context.setFileCharacters(new File(icharsFileUrl.toURI()).getAbsolutePath());
        context.setFileTaxa(new File(iitemsFileUrl.toURI()).getAbsolutePath());
        
        assertEquals(87, context.getDataset().getNumberOfCharacters());
        assertEquals(14, context.getDataset().getTaxa().size());
    }
    
    /**
     * Test opening the sample dataset by opening the initialization file
     * that is supplied with it
     */
    @Test
    public void testReadSampleFromInitializationFile() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
        IntkeyContext context = new IntkeyContext(null);
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        
        List<String> keywordsWithoutSystemDefinedOnes = new ArrayList<String>(context.getCharacterKeywords());
        keywordsWithoutSystemDefinedOnes.remove("all");
        keywordsWithoutSystemDefinedOnes.remove("available");
        keywordsWithoutSystemDefinedOnes.remove("used");
        
        assertEquals(87, context.getDataset().getNumberOfCharacters());
        assertEquals(14, context.getDataset().getTaxa().size());
        assertEquals(36, keywordsWithoutSystemDefinedOnes.size());
    }
    

}

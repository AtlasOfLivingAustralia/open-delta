package au.org.ala.delta.inkey.model;

import java.io.File;
import java.net.URL;

import org.junit.Test;

import au.org.ala.delta.intkey.model.IntkeyDatasetFileBuilder;
import au.org.ala.delta.intkey.model.ported.DataSet;

import junit.framework.TestCase;

public class DataSetTest extends TestCase {
    
    /*@Test
    public void testReadSample() throws Exception {
        URL icharsFileUrl = getClass().getResource("/dataset/sample/ichars");
        URL iitemsFileUrl = getClass().getResource("/dataset/sample/iitems");

        DataSet dataSet = new DataSet();
        dataSet.init(new File(icharsFileUrl.toURI()), new File(iitemsFileUrl.toURI()));
    }

    @Test
    public void testReadBorneo() throws Exception {
        URL icharsFileUrl = getClass().getResource("/dataset/borneo/ichars");
        URL iitemsFileUrl = getClass().getResource("/dataset/borneo/iitems");

        DataSet dataSet = new DataSet();
        dataSet.init(new File(icharsFileUrl.toURI()), new File(iitemsFileUrl.toURI()));
    }
    
    @Test
    public void testReadGrasses() throws Exception {
        URL icharsFileUrl = getClass().getResource("/dataset/grasses/YCHARS");        
        URL iitemsFileUrl = getClass().getResource("/dataset/grasses/YITEMS");        
        
        DataSet dataSet = new DataSet();
        dataSet.init(new File(icharsFileUrl.toURI()), new File(iitemsFileUrl.toURI()));
    }*/
    
    @Test
    public void testDataSetFileReader() throws Exception {
        URL icharsFileUrl = getClass().getResource("/dataset/sample/ichars");        
        URL iitemsFileUrl = getClass().getResource("/dataset/sample/iitems");    
        new IntkeyDatasetFileBuilder().readDataSet(new File(icharsFileUrl.toURI()), new File(iitemsFileUrl.toURI()));
    }
}

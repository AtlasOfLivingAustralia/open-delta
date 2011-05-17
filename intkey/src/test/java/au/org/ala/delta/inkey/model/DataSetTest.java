package au.org.ala.delta.inkey.model;

import org.junit.Test;

import au.org.ala.delta.intkey.model.IntkeyContext;

import junit.framework.TestCase;

public class DataSetTest extends TestCase {
    
    //TODO NEED TO FIX THIS SO CAN RUN IN BUILD
    
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
    
    /*@Test
    public void testDataSetFileReader() throws Exception {
        URL icharsFileUrl = getClass().getResource("/dataset/sample/ichars");        
        URL iitemsFileUrl = getClass().getResource("/dataset/sample/iitems");    
        IntkeyDataset ds = new IntkeyDatasetFileBuilder().readDataSet(new File(icharsFileUrl.toURI()), new File(iitemsFileUrl.toURI()));
    }*/
    
    @Test
    public void testOpenDataSetFile() throws Exception {
        //IntkeyContext cxt = new IntkeyContext(null);
        //cxt.newDataSetFile("intkey.ink");
        //for (String keyword: cxt.getCharacterKeywords()) {
        //    System.out.println(keyword);
        //}
        
    }
    
    /*@Test
    public void testGrassesReader() throws Exception {
        IntkeyDataset ds = new IntkeyDatasetFileBuilder().readDataSet(new File("C:/Users/Chris/DELTA resources/samples/grasses/grasses/YCHARS"),
                new File("C:/Users/Chris/DELTA resources/samples/grasses/grasses/YITEMS"));
    }*/
    
    //TODO - Test dataset with excluded characters in the middle of the character
    // range, e.g. dataset with 10 characters, exclude 5 and 6 in CONFOR.
}

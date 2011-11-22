package au.org.ala.delta.directives;

import java.io.File;
import java.net.URL;

import org.junit.Before;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.MutableDeltaDataSet;
import junit.framework.TestCase;

/**
 * Tests the CharacterReliabilities and CharacterWeights classes (as they are
 * very similar)
 */
public class CharacterWeightsTest extends TestCase {
    
    private MutableDeltaDataSet _dataSet;
    private DeltaContext _context;

    @Before 
    public void setUp() throws Exception {
        DefaultDataSetFactory factory = new DefaultDataSetFactory();
        _dataSet = factory.createDataSet("test");
        _context = new DeltaContext(_dataSet);
        _context.setNumberOfCharacters(88);
        ConforDirectiveFileParser parser = ConforDirectiveFileParser.createInstance();
        File specs = urlToFile("/dataset/sample/specs");
        parser.parse(specs, _context);
    }

    private File urlToFile(String urlString) throws Exception {
        URL url = CharacterWeightsTest.class.getResource(urlString);
        File file = new File(url.toURI());
        return file;
    }

    /**
     * Tests processing (and use of defaults) with correct data.
     */
    public void testCharacterReliabilitiesProcessing() throws Exception {
        String data = "1,0 2-5,7 6,5 7-10,7 11-13,8 14-24,7 25,0 26,7 27,8 28-38,7 39,5 40-43,7 44,8\n" + "45-47,7 48,8 49-63,7 64,6 65,7 66,8 67,7 68,3 69,0 70,5 71-76,0 77,7.1 78-87,0";

        CharacterReliabilities directive = new CharacterReliabilities();

        directive.parseAndProcess(_context, data);

        assertEquals(0.0, _context.getCharacterReliability(1));

        // Make sure ranges are ok.
        assertEquals(7.0, _context.getCharacterReliability(2));
        assertEquals(7.0, _context.getCharacterReliability(3));
        assertEquals(7.0, _context.getCharacterReliability(4));
        assertEquals(7.0, _context.getCharacterReliability(5));

        assertEquals(7.1, _context.getCharacterReliability(77), 0.001d);

        // This directive initialises reliabilities to 1...
        assertEquals(1.0, _context.getCharacterWeight(88));
    }

    /**
     * Tests processing of invalid (out of range) data.
     */
    public void testCharacterReliabilitiesProcessingWithOutOfRangeValues() throws Exception {
        String data = "1,-0.01";

        CharacterReliabilities directive = new CharacterReliabilities();

        try {
            directive.parseAndProcess(_context, data);
            fail("should have thrown an exception");
        } catch (IllegalArgumentException e) {
        }

        data = "1,10.01";

        try {
            directive.parseAndProcess(_context, data);
            fail("should have thrown an exception");
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Tests processing (and use of defaults) with correct data.
     */
    public void testCharacterWeightsProcessing() throws Exception {
        String data = "1,3 2-10,15.7 12,32.0 13,0.03125";

        CharacterWeights directive = new CharacterWeights();

        directive.parseAndProcess(_context, data);

        assertEquals(3.0, _context.getCharacterWeight(1));

        // Make sure ranges are ok.
        assertEquals(15.7, _context.getCharacterWeight(2));
        assertEquals(15.7, _context.getCharacterWeight(3));
        assertEquals(15.7, _context.getCharacterWeight(10));

        // make sure defaults are ok
        assertEquals(1.0, _context.getCharacterWeight(11));

        assertEquals(32.0, _context.getCharacterWeight(12));
        assertEquals(0.03125, _context.getCharacterWeight(13));
    }

    /**
     * Tests processing of invalid (out of range) data.
     */
    public void testCharacterWeightsProcessingWithOutOfRangeValues() throws Exception {
        String data = "1,0.03124";

        CharacterWeights directive = new CharacterWeights();

        try {
            directive.parseAndProcess(_context, data);
            fail("should have thrown an exception");
        } catch (IllegalArgumentException e) {
        }

        data = "1,32.001";

        try {
            directive.parseAndProcess(_context, data);
            fail("should have thrown an exception");
        } catch (IllegalArgumentException e) {
        }
    }

}

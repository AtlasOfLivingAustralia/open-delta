package au.org.ala.delta.key;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

import au.org.ala.delta.key.directives.KeyDirectiveParser;
import au.org.ala.delta.model.MultiStateAttribute;

public class TreatCharactersAsVariableTest extends TestCase {

    @Test
    public void testTreatCharactersAsVariable() throws Exception {
        // Use dummy temp file for data directory seeing as we don't have a use
        // for it here

        URL dataDirectoryURL = getClass().getResource("/sample");
        File dataDirectory= new File(dataDirectoryURL.toURI());
        
        URL directivesFileURL = getClass().getResource("/sample/testTreatCharactersAsVariableInputFile");
        File directivesFile = new File(directivesFileURL.toURI());
        
        KeyContext context = new KeyContext(dataDirectory);
        
        KeyDirectiveParser parser = KeyDirectiveParser.createInstance();
        parser.parse(directivesFile, context);

        assertEquals(new HashSet<Integer>(Arrays.asList(ArrayUtils.toObject(new int[] { 44, 66 }))), context.getVariableCharactersForTaxon(1));
        assertEquals(new HashSet<Integer>(Arrays.asList(ArrayUtils.toObject(new int[] { 4, 5, 6, 7, 8, 9, 10 }))), context.getVariableCharactersForTaxon(4));
        
        KeyUtils.loadDataset(context);
        
        assertTrue(isVariable( (MultiStateAttribute) context.getDataSet().getAttribute(1, 44)));
        assertTrue(isVariable( (MultiStateAttribute) context.getDataSet().getAttribute(1, 66)));
        assertTrue(isVariable( (MultiStateAttribute) context.getDataSet().getAttribute(4, 4)));
        assertTrue(isVariable( (MultiStateAttribute) context.getDataSet().getAttribute(4, 5)));
        assertTrue(isVariable( (MultiStateAttribute) context.getDataSet().getAttribute(4, 6)));
        assertTrue(isVariable( (MultiStateAttribute) context.getDataSet().getAttribute(4, 7)));
        assertTrue(isVariable( (MultiStateAttribute) context.getDataSet().getAttribute(4, 8)));
        assertTrue(isVariable( (MultiStateAttribute) context.getDataSet().getAttribute(4, 9)));
        assertTrue(isVariable( (MultiStateAttribute) context.getDataSet().getAttribute(4, 10)));
    }
    
    private boolean isVariable(MultiStateAttribute attr) {
        int numberOfStates = attr.getCharacter().getNumberOfStates();
        Set<Integer> presentStates = attr.getPresentStates();
        
        for (int i = 0; i < numberOfStates; i++) {
            int stateNumber = i + 1;
            if (!presentStates.contains(stateNumber)) {
                return false;
            }
        }
        
        return true;
    }
}

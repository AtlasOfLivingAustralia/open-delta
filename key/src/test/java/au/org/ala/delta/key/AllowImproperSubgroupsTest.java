package au.org.ala.delta.key;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.model.Character;

import junit.framework.TestCase;

public class AllowImproperSubgroupsTest extends TestCase {

    public void testAllowImproperSubgroups() throws Exception {
        URL directivesFileURL = getClass().getResource("/sample/mykey");
        File directivesFile = new File(directivesFileURL.toURI());
        
        URL charFileURL = getClass().getResource("/sample/kchars");
        File charFile = new File(charFileURL.toURI());

        URL itemsFileURL = getClass().getResource("/sample/kitems");
        File itemsFile = new File(itemsFileURL.toURI());

        // Use dummy temp file for data directory seeing as we don't have a use
        // for it here
        KeyContext context = new KeyContext(directivesFile);
        context.setABase(1.0);
        context.setVaryWt(1.0);
        context.setRBase(1.0);
        context.setReuse(1.0);
        context.setCharactersFile(charFile);
        context.setItemsFile(itemsFile);

        context.setAllowImproperSubgroups(false);

        KeyUtils.loadDataset(context);

        List<Integer> availableCharacterNumbers = Arrays.asList(ArrayUtils.toObject(new IntRange(1, context.getNumberOfCharacters()).toArray()));
        List<Integer> availableTaxaNumbers = Arrays.asList(ArrayUtils.toObject(new IntRange(1, context.getMaximumNumberOfItems()).toArray()));

        Map<Character, Double> bestMap = KeyBest.orderBest(context.getDataSet(), context.getCharacterCostsAsArray(), context.getCalculatedItemAbundanceValuesAsArray(), availableCharacterNumbers,
                availableTaxaNumbers, context.getRBase(), context.getABase(), context.getReuse(), context.getVaryWt(), context.getAllowImproperSubgroups());

        assertFalse(bestMap.containsKey(context.getCharacter(10)));

        context.setAllowImproperSubgroups(true);
        
        Map<Character, Double> bestMap2 = KeyBest.orderBest(context.getDataSet(), context.getCharacterCostsAsArray(), context.getCalculatedItemAbundanceValuesAsArray(), availableCharacterNumbers,
                availableTaxaNumbers, context.getRBase(), context.getABase(), context.getReuse(), context.getVaryWt(), context.getAllowImproperSubgroups());
        
        assertTrue(bestMap2.containsKey(context.getCharacter(10)));
    }

}

package au.org.ala.delta.key;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;

public class DatasetLoadTest extends TestCase {

    @Test
    public void testLoad() throws Exception {
        URL directivesFileURL = getClass().getResource("/controlling_characters_simple/key");
        File directivesFile = new File(directivesFileURL.toURI());

        Key key = new Key();
        key.calculateKey(directivesFile);

        KeyContext context = key.getContext();
        MutableDeltaDataSet dataset = context.getDataSet();

        assertEquals(9, dataset.getNumberOfCharacters());
        assertEquals(5, dataset.getMaximumNumberOfItems());

        List<Integer> excludedCharactersList = new ArrayList<Integer>(context.getExcludedCharacters());
        Collections.sort(excludedCharactersList);
        assertEquals(6, excludedCharactersList.size());
        assertEquals(excludedCharactersList, Arrays.asList(1, 4, 5, 7, 8, 9));

        List<Integer> excludedItemsList = new ArrayList<Integer>(context.getExcludedItems());
        Collections.sort(excludedItemsList);        
        assertEquals(0, excludedItemsList.size());

        MultiStateCharacter ch2 = (MultiStateCharacter) dataset.getCharacter(2);
        MultiStateCharacter ch3 = (MultiStateCharacter) dataset.getCharacter(3);
        MultiStateCharacter ch6 = (MultiStateCharacter) dataset.getCharacter(6);

        assertEquals("seed presence", ch2.getDescription());
        assertEquals("seed in shell", ch3.getDescription());
        assertEquals("colour", ch6.getDescription());

        assertEquals(Arrays.asList(ch2.getStates()), Arrays.asList("present", "absent"));
        assertEquals(Arrays.asList(ch3.getStates()), Arrays.asList("in shell", "not in shell"));
        assertEquals(Arrays.asList(ch6.getStates()), Arrays.asList("purple", "red", "orange", "yellow", "green"));

        assertEquals(5f, ch2.getReliability());
        assertEquals(5f, ch3.getReliability());
        assertEquals(5f, ch6.getReliability());

        List<CharacterDependency> c2DepChars = ch2.getDependentCharacters();
        assertEquals(1, c2DepChars.size());
        assertEquals(2, c2DepChars.get(0).getControllingCharacterId());
        assertEquals(new HashSet<Integer>(Arrays.asList(2)), c2DepChars.get(0).getStates());
        assertEquals(new HashSet<Integer>(Arrays.asList(3, 5)), c2DepChars.get(0).getDependentCharacterIds());

        List<CharacterDependency> c3DepChars = ch3.getDependentCharacters();
        assertEquals(1, c3DepChars.size());
        assertEquals(3, c3DepChars.get(0).getControllingCharacterId());
        assertEquals(new HashSet<Integer>(Arrays.asList(2)), c3DepChars.get(0).getStates());
        assertEquals(new HashSet<Integer>(Arrays.asList(4)), c3DepChars.get(0).getDependentCharacterIds());

        List<CharacterDependency> c6DepChars = ch6.getDependentCharacters();
        assertEquals(0, c6DepChars.size());

        Item it1 = dataset.getItem(1);
        Item it2 = dataset.getItem(2);
        Item it3 = dataset.getItem(3);
        Item it4 = dataset.getItem(4);
        Item it5 = dataset.getItem(5);

        assertEquals("Carrot", it1.getDescription());
        assertEquals("Apricot", it2.getDescription());
        assertEquals("Strawberry", it3.getDescription());
        assertEquals("Plum", it4.getDescription());
        assertEquals("Potato", it5.getDescription());

        assertEquals(5.0, context.getItemAbundancy(1));
        assertEquals(5.0, context.getItemAbundancy(2));
        assertEquals(5.0, context.getItemAbundancy(3));
        assertEquals(5.0, context.getItemAbundancy(4));
        assertEquals(5.0, context.getItemAbundancy(5));
    }
}

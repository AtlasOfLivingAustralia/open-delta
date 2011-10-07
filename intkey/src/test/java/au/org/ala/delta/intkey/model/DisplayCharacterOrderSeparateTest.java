package au.org.ala.delta.intkey.model;

import java.util.Map;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.model.Character;

public class DisplayCharacterOrderSeparateTest extends IntkeyDatasetTestCase {

    @Test
    public void testSeparate() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        
        new UseDirective().parseAndProcess(context, "11,1");

        Map<Character, Double> bestMap = SortingUtils.orderSeparate(context, context.getDataset().getTaxon(1));

        System.out.println(bestMap.size());
        
        for (Character ch: bestMap.keySet()) {
            double sepPower = bestMap.get(ch);
            System.out.println(String.format("%.2f %s %s", sepPower, ch.getCharacterId(), ch.getDescription()));
        }

//        List<Character> bestChars = new ArrayList<Character>(bestMap.keySet());
//        Collections.sort(bestChars);
//
//        List<Integer> expectedChars = Arrays.asList(new Integer[] { 11, 35, 38, 39, 34, 58, 13, 66, 52, 27, 44, 19, 28, 63, 26, 60, 12, 3, 7, 53, 61, 67, 48, 77, 4, 5, 9, 15, 16, 20, 54, 57, 59, 62,
//                65, 2, 8, 10, 14, 29, 30, 31, 32, 33, 36, 37, 45, 46, 47, 49, 50, 51, 56, 64, 70, 68, 6 });
//
//        Collections.sort(expectedChars);
//
//        //System.out.println(bestMap.size());
//
//        for (int charNum : expectedChars) {
//            boolean found = false;
//            for (Character ch : bestChars) {
//                if (ch.getCharacterId() == charNum) {
//                    found = true;
//                    break;
//                }
//            }
//
//            if (!found) {
//                System.out.println(charNum);
//            }
//        }
    }
    
//    @Test
//    public void testSeparate2() throws Exception {
//        IntkeyContext context = loadDataset("/dataset/controlling_characters_simple/intkey.ink");
//
//        Map<Character, Double> bestMap = SortingUtils.orderSeparate(context, context.getDataset().getTaxon(1));
//
//        //List<Character> bestChars = new ArrayList<Character>(bestMap.keySet());
//
//        for (Character ch: bestMap.keySet()) {
//            double sepPower = bestMap.get(ch);
//            System.out.println(String.format("%.2f %s %s", sepPower, ch.getCharacterId(), ch.getDescription()));
//        }
//
//    }
}

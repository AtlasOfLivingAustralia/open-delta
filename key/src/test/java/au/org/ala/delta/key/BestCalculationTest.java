package au.org.ala.delta.key;

import java.io.File;
import java.net.URL;
import java.util.LinkedHashMap;

import junit.framework.TestCase;
import au.org.ala.delta.best.Best;
import au.org.ala.delta.model.MutableDeltaDataSet;

public class BestCalculationTest extends TestCase {

    public void testBestCalculation() throws Exception {
        URL directivesFileURL = getClass().getResource("/sample/mykey");
        File directivesFile = new File(directivesFileURL.toURI());

        Key key = new Key();
        key.calculateKey(directivesFile);

        KeyContext context = key.getContext();

        MutableDeltaDataSet dataset = context.getDataSet();

        LinkedHashMap<au.org.ala.delta.model.Character, Double> bestMap = Best.orderBest(dataset, context.getIncludedCharacters(), context.getIncludedItems(), context.getRBase(), context.getVaryWt());

        for (au.org.ala.delta.model.Character ch : bestMap.keySet()) {
            double sepPower = bestMap.get(ch);
            System.out.println(String.format("%s %s", sepPower, ch));
        }
    }
}

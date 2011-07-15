package au.org.ala.delta.inkey.model;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.DifferencesDirective;
import au.org.ala.delta.intkey.directives.ParsingUtils;
import au.org.ala.delta.intkey.model.DiffUtils;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.model.MatchType;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;

public class DifferencesDirectiveTest extends TestCase {

    @Test
    public void testDifferences() throws Exception {
        //URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
        //IntkeyContext context = new IntkeyContext(new MockIntkeyUI());
        //context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        //IntkeyDataset ds = context.getDataset();
        
        //CharacterFormatter formatter = new CharacterFormatter(false, true, false, false);
        
        //System.out.println(formatter.formatCharacterDescription(ds.getCharacter(2)));
        
//        List<String> tokens = ParsingUtils.tokenizeDirectiveCall("(1 2)");
//        for (String token: tokens) {
//            System.out.println(token);
//        }
        
//        long startTime = System.currentTimeMillis();
//
//        new DifferencesDirective().parseAndProcess(context, "");
//        
//        long endTime = System.currentTimeMillis();
//
//        long timeDiff = endTime - startTime;
//        long diffSeconds = timeDiff / 1000;
//
//        System.out.println(diffSeconds + " seconds");
    }
}

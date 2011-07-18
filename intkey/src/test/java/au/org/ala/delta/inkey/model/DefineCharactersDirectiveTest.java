package au.org.ala.delta.inkey.model;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.DefineCharactersDirective;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;

import junit.framework.TestCase;

/**
 * Unit tests for the DEFINE CHARACTERS directive
 * @author ChrisF
 *
 */
public class DefineCharactersDirectiveTest extends TestCase {

    /**
     * Create a character keyword comprising a single character
     * @throws Exception
     */
    @Test
    public void testSingleCharacter() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        IntkeyDataset ds = context.getDataset();

        new DefineCharactersDirective().parseAndProcess(context, "foo 1");

        List<au.org.ala.delta.model.Character> keywordCharacters = context.getCharactersForKeyword("foo");

        assertEquals(Arrays.asList(ds.getCharacter(1)), keywordCharacters);
    }

    /**
     * Create a character keyword comprising multiple characters
     * @throws Exception
     */
    @Test
    public void testMultipleCharacters() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        IntkeyDataset ds = context.getDataset();

        new DefineCharactersDirective().parseAndProcess(context, "foo 48 11 2");

        List<au.org.ala.delta.model.Character> keywordCharacters = context.getCharactersForKeyword("foo");

        assertEquals(Arrays.asList(ds.getCharacter(2), ds.getCharacter(11), ds.getCharacter(48)), keywordCharacters);
    }

    /**
     * Create a character keyword comprising multiple characters specified using a range
     * @throws Exception
     */
    @Test
    public void testCharacterRange() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        IntkeyDataset ds = context.getDataset();

        new DefineCharactersDirective().parseAndProcess(context, "foo 10-15");

        List<au.org.ala.delta.model.Character> keywordCharacters = context.getCharactersForKeyword("foo");

        assertEquals(Arrays.asList(ds.getCharacter(10), ds.getCharacter(11), ds.getCharacter(12), ds.getCharacter(13), ds.getCharacter(14), ds.getCharacter(15)), keywordCharacters);
    }

    /**
     * Create a character keyword comprising multiple characters specified using
     * more than one range
     * @throws Exception
     */
    @Test
    public void testMultipleRanges() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        IntkeyDataset ds = context.getDataset();

        new DefineCharactersDirective().parseAndProcess(context, "foo 10-15 47-49");

        List<au.org.ala.delta.model.Character> keywordCharacters = context.getCharactersForKeyword("foo");

        assertEquals(Arrays.asList(ds.getCharacter(10), ds.getCharacter(11), ds.getCharacter(12), ds.getCharacter(13), ds.getCharacter(14), ds.getCharacter(15), ds.getCharacter(47), ds.getCharacter(48), ds.getCharacter(49)), keywordCharacters);
    }

    /**
     * Create a keyword comprising the same characters used in a previously defined keyword
     * @throws Exception
     */
    @Test
    public void testKeyword() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        IntkeyDataset ds = context.getDataset();

        new DefineCharactersDirective().parseAndProcess(context, "foo habit");

        List<au.org.ala.delta.model.Character> keywordCharacters = context.getCharactersForKeyword("foo");

        assertEquals(Arrays.asList(ds.getCharacter(2), ds.getCharacter(3), ds.getCharacter(4), ds.getCharacter(5), ds.getCharacter(13)), keywordCharacters);
    }
    
    /**
     * Create a keyword using a previously defined keyword. The previously defined keyword is 
     * specified using only the beginning of its name.
     * @throws Exception
     */
    @Test
    public void testPartialKeyword() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        IntkeyDataset ds = context.getDataset();

        new DefineCharactersDirective().parseAndProcess(context, "foo hab");

        List<au.org.ala.delta.model.Character> keywordCharacters = context.getCharactersForKeyword("foo");

        assertEquals(Arrays.asList(ds.getCharacter(2), ds.getCharacter(3), ds.getCharacter(4), ds.getCharacter(5), ds.getCharacter(13)), keywordCharacters);
    }

    /**
     * Create a keyword using a combination of single characters, ranges and previously 
     * defined keywords.
     * @throws Exception
     */
    @Test
    public void testCombination() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        IntkeyDataset ds = context.getDataset();

        new DefineCharactersDirective().parseAndProcess(context, "foo 20-23 habit 48");

        List<au.org.ala.delta.model.Character> keywordCharacters = context.getCharactersForKeyword("foo");

        assertEquals(Arrays.asList(ds.getCharacter(2), ds.getCharacter(3), ds.getCharacter(4), ds.getCharacter(5), ds.getCharacter(13), ds.getCharacter(20), ds.getCharacter(21), ds.getCharacter(22), ds.getCharacter(23), ds.getCharacter(48)), keywordCharacters);
    }

}

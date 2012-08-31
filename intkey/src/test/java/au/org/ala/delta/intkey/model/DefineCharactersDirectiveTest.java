/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 * 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.intkey.model;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.DefineCharactersDirective;

/**
 * Unit tests for the DEFINE CHARACTERS directive
 * @author ChrisF
 *
 */
public class DefineCharactersDirectiveTest extends IntkeyDatasetTestCase {

    /**
     * Create a character keyword comprising a single character
     * @throws Exception
     */
    @Test
    public void testSingleCharacter() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        new DefineCharactersDirective().parseAndProcess(context, "foo 1");

        List<au.org.ala.delta.model.Character> keywordCharacters = context.getCharactersForKeyword("foo");

        assertEquals(Arrays.asList(context.getDataset().getCharacter(1)), keywordCharacters);
    }

    /**
     * Create a character keyword comprising multiple characters
     * @throws Exception
     */
    @Test
    public void testMultipleCharacters() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
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
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
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
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
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
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
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
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
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
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        new DefineCharactersDirective().parseAndProcess(context, "foo 20-23 habit 48");

        List<au.org.ala.delta.model.Character> keywordCharacters = context.getCharactersForKeyword("foo");

        assertEquals(Arrays.asList(ds.getCharacter(2), ds.getCharacter(3), ds.getCharacter(4), ds.getCharacter(5), ds.getCharacter(13), ds.getCharacter(20), ds.getCharacter(21), ds.getCharacter(22), ds.getCharacter(23), ds.getCharacter(48)), keywordCharacters);
    }

}

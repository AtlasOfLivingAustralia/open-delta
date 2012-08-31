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

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;

import au.org.ala.delta.intkey.directives.FileOutputDirective;
import au.org.ala.delta.intkey.directives.OutputCharactersDirective;
import au.org.ala.delta.intkey.directives.OutputCommentDirective;
import au.org.ala.delta.intkey.directives.OutputDifferencesDirective;
import au.org.ala.delta.intkey.directives.OutputSimilaritiesDirective;
import au.org.ala.delta.intkey.directives.OutputTaxaDirective;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.impl.ControllingInfo;
import au.org.ala.delta.rtf.RTFUtils;

public class OutputReportsTest extends IntkeyDatasetTestCase {

    String lineSeparator = System.getProperties().getProperty("line.separator");

    @Test
    public void testOutputCharacters() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        File tempFile = File.createTempFile("OutputReportsTest", null);
        tempFile.deleteOnExit();

        new FileOutputDirective().parseAndProcess(context, tempFile.getAbsolutePath());
        new OutputCharactersDirective().parseAndProcess(context, "habit");

        String fileContents = FileUtils.readFileToString(tempFile);

        assertEquals("OUTPUT CHARACTERS 2-5 13", fileContents.trim());
    }

    @Test
    public void testOutputCharactersWithComment() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        File tempFile = File.createTempFile("OutputReportsTest", null);
        tempFile.deleteOnExit();

        new FileOutputDirective().parseAndProcess(context, tempFile.getAbsolutePath());
        new OutputCommentDirective().parseAndProcess(context, "comment output characters");
        new OutputCharactersDirective().parseAndProcess(context, "habit");

        String fileContents = FileUtils.readFileToString(tempFile);

        assertEquals("comment output characters" + lineSeparator + "2-5 13", fileContents.trim());
    }

    @Test
    public void testOutputTaxa() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        File tempFile = File.createTempFile("OutputReportsTest", null);
        tempFile.deleteOnExit();

        new FileOutputDirective().parseAndProcess(context, tempFile.getAbsolutePath());
        new OutputTaxaDirective().parseAndProcess(context, "cereals");

        String fileContents = FileUtils.readFileToString(tempFile);

        assertEquals("OUTPUT TAXA 7-8 10-11 14", fileContents.trim());
    }

    @Test
    public void testOutputTaxaWithComment() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        File tempFile = File.createTempFile("OutputReportsTest", null);
        tempFile.deleteOnExit();

        new FileOutputDirective().parseAndProcess(context, tempFile.getAbsolutePath());
        new OutputCommentDirective().parseAndProcess(context, "comment output taxa");
        new OutputTaxaDirective().parseAndProcess(context, "cereals");

        String fileContents = FileUtils.readFileToString(tempFile);

        assertEquals("comment output taxa" + lineSeparator + "7-8 10-11 14", fileContents.trim());
    }

    @Test
    public void testOutputDifferences() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        File tempFile = File.createTempFile("OutputReportsTest", null);
        tempFile.deleteOnExit();

        new FileOutputDirective().parseAndProcess(context, tempFile.getAbsolutePath());
        new OutputDifferencesDirective().parseAndProcess(context, "all all");

        String fileContents = FileUtils.readFileToString(tempFile);
        fileContents = fileContents.trim();

        assertEquals("OUTPUT DIFFERENCES" + lineSeparator + "1-5 7-9 11-13 15-16 18-20 25-28 30-31 34-35 37-41 44-54 56-82 84-87", fileContents);
    }

    @Test
    public void testOutputDifferencesWithComment() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        File tempFile = File.createTempFile("OutputReportsTest", null);
        tempFile.deleteOnExit();

        new FileOutputDirective().parseAndProcess(context, tempFile.getAbsolutePath());
        new OutputCommentDirective().parseAndProcess(context, "comment output differences");
        new OutputDifferencesDirective().parseAndProcess(context, "all all");

        String fileContents = FileUtils.readFileToString(tempFile);
        fileContents = fileContents.trim();

        assertEquals("comment output differences" + lineSeparator + "1-5 7-9 11-13 15-16 18-20 25-28 30-31 34-35 37-41 44-54 56-82 84-87", fileContents);
    }

    @Test
    public void testOutputSimilarities() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        File tempFile = File.createTempFile("OutputReportsTest", null);
        tempFile.deleteOnExit();

        new FileOutputDirective().parseAndProcess(context, tempFile.getAbsolutePath());
        new OutputSimilaritiesDirective().parseAndProcess(context, "all all");

        String fileContents = FileUtils.readFileToString(tempFile);
        fileContents = fileContents.trim();

        assertEquals("OUTPUT SIMILARITIES" + lineSeparator + "6 10 14 17 21-24 29 32-33 36 42-43 55 83", fileContents);
    }

    @Test
    public void testOutputSimilaritiesWithComment() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        File tempFile = File.createTempFile("OutputReportsTest", null);
        tempFile.deleteOnExit();

        new FileOutputDirective().parseAndProcess(context, tempFile.getAbsolutePath());
        new OutputCommentDirective().parseAndProcess(context, "comment output similarities");
        new OutputSimilaritiesDirective().parseAndProcess(context, "all all");

        String fileContents = FileUtils.readFileToString(tempFile);
        fileContents = fileContents.trim();

        assertEquals("comment output similarities" + lineSeparator + "6 10 14 17 21-24 29 32-33 36 42-43 55 83", fileContents);
    }

    // TODO rework the below into regression tests. They only fail because I
    // have been unable to replicate
    // the odd decimal place handling used by the legacy intkey.

    // @Test
    // public void testOutputSummaryAllTaxa() throws Exception {
    // IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
    //
    // File tempFile = File.createTempFile("OutputReportsTest", null);
    // tempFile.deleteOnExit();
    //
    // new FileOutputDirective().parseAndProcess(context,
    // tempFile.getAbsolutePath());
    // new OutputSummaryDirective().parseAndProcess(context, "all all");
    //
    // String reportOutput = FileUtils.readFileToString(tempFile);
    // System.out.println(reportOutput);
    // reportOutput = reportOutput.trim();
    // reportOutput = reportOutput.replaceAll("\r", "");
    // reportOutput = reportOutput.replaceAll("\n", " ");
    //
    // URL expectedOutputFileUrl =
    // getClass().getResource("/outputsummary/sample1.txt");
    // File expectedOutputFile = new File(expectedOutputFileUrl.toURI());
    //
    // String expectedOutput = FileUtils.readFileToString(expectedOutputFile);
    // expectedOutput = expectedOutput.trim();
    // expectedOutput = expectedOutput.replaceAll("\r", "");
    // expectedOutput = expectedOutput.replaceAll("\n", " ");
    //
    // assertEquals(expectedOutput, reportOutput);
    // }
    //
    // @Test
    // public void testOutputSummaryAllTaxa2() throws Exception {
    // IntkeyContext context =
    // loadDataset("/dataset/controlling_characters_simple/intkey.ink");
    //
    // File tempFile = File.createTempFile("OutputReportsTest", null);
    // tempFile.deleteOnExit();
    //
    // new FileOutputDirective().parseAndProcess(context,
    // tempFile.getAbsolutePath());
    // new OutputSummaryDirective().parseAndProcess(context, "all all");
    //
    // String reportOutput = FileUtils.readFileToString(tempFile);
    // System.out.println(reportOutput);
    // reportOutput = reportOutput.trim();
    // reportOutput = reportOutput.replaceAll("\r", "");
    // reportOutput = reportOutput.replaceAll("\n", " ");
    //
    // URL expectedOutputFileUrl =
    // getClass().getResource("/outputsummary/controlling_characters_simple1.txt");
    // File expectedOutputFile = new File(expectedOutputFileUrl.toURI());
    //
    // String expectedOutput = FileUtils.readFileToString(expectedOutputFile);
    // expectedOutput = expectedOutput.trim();
    // expectedOutput = expectedOutput.replaceAll("\r", "");
    // expectedOutput = expectedOutput.replaceAll("\n", " ");
    //
    // assertEquals(expectedOutput, reportOutput);
    // }
    //
    // @Test
    // public void testOutputSummarySingleTaxon() throws Exception {
    // IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
    //
    // File tempFile = File.createTempFile("OutputReportsTest", null);
    // tempFile.deleteOnExit();
    //
    // new FileOutputDirective().parseAndProcess(context,
    // tempFile.getAbsolutePath());
    // new OutputSummaryDirective().parseAndProcess(context, "1 all");
    //
    // String reportOutput = FileUtils.readFileToString(tempFile);
    // System.out.println(reportOutput);
    // reportOutput = reportOutput.trim();
    // reportOutput = reportOutput.replaceAll("\r", "");
    // reportOutput = reportOutput.replaceAll("\n", " ");
    //
    // URL expectedOutputFileUrl =
    // getClass().getResource("/outputsummary/sample2.txt");
    // File expectedOutputFile = new File(expectedOutputFileUrl.toURI());
    //
    // String expectedOutput = FileUtils.readFileToString(expectedOutputFile);
    // expectedOutput = expectedOutput.trim();
    // expectedOutput = expectedOutput.replaceAll("\r", "");
    // expectedOutput = expectedOutput.replaceAll("\n", " ");
    //
    // assertEquals(expectedOutput, reportOutput);
    // }
    //
    // @Test
    // public void testOutputSummarySingleTaxon2() throws Exception {
    // IntkeyContext context =
    // loadDataset("/dataset/controlling_characters_simple/intkey.ink");
    //
    // File tempFile = File.createTempFile("OutputReportsTest", null);
    // tempFile.deleteOnExit();
    //
    // new FileOutputDirective().parseAndProcess(context,
    // tempFile.getAbsolutePath());
    // new OutputSummaryDirective().parseAndProcess(context, "1 all");
    //
    // String reportOutput = FileUtils.readFileToString(tempFile);
    // System.out.println(reportOutput);
    // reportOutput = reportOutput.trim();
    // reportOutput = reportOutput.replaceAll("\r", "");
    // reportOutput = reportOutput.replaceAll("\n", " ");
    //
    // URL expectedOutputFileUrl =
    // getClass().getResource("/outputsummary/controlling_characters_simple2.txt");
    // File expectedOutputFile = new File(expectedOutputFileUrl.toURI());
    //
    // String expectedOutput = FileUtils.readFileToString(expectedOutputFile);
    // expectedOutput = expectedOutput.trim();
    // expectedOutput = expectedOutput.replaceAll("\r", "");
    // expectedOutput = expectedOutput.replaceAll("\n", " ");
    //
    // assertEquals(expectedOutput, reportOutput);
    // }

    // public void testOutputDescribe() throws Exception {
    // IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
    //
    // WrappedIntkeyDataset wrapped = new
    // WrappedIntkeyDataset(context.getDataset());
    //
    // DataSetTranslatorFactory factory = new DataSetTranslatorFactory();
    // DeltaContext deltaContext = new DeltaContext(wrapped);
    // deltaContext.setTranslateType(TranslateType.Delta);
    //
    // PrintFile pf = new PrintFile(new StringBuilder());
    //
    // DataSetTranslator translator = factory.createTranslator(deltaContext,
    // pf);
    // translator.translateItems();
    //
    // System.out.println(pf);
    // }

    private class WrappedIntkeyDataset implements MutableDeltaDataSet {

        private IntkeyDataset _intkeyDataset;

        public WrappedIntkeyDataset(IntkeyDataset intkeyDataset) {
            _intkeyDataset = intkeyDataset;
        }

        @Override
        public String getName() {
            return _intkeyDataset.getHeading();
        }

        @Override
        public void setName(String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Item getItem(int number) {
            return _intkeyDataset.getItem(number);
        }

        @Override
        public String getAttributeAsString(int itemNumber, int characterNumber) {
            return _intkeyDataset.getAttribute(itemNumber, characterNumber).getValueAsString();
        }

        @Override
        public Character getCharacter(int number) {
            return _intkeyDataset.getCharacter(number);
        }

        @Override
        public int getNumberOfCharacters() {
            return _intkeyDataset.getNumberOfCharacters();
        }

        @Override
        public int getMaximumNumberOfItems() {
            return _intkeyDataset.getNumberOfTaxa();
        }

        @Override
        public boolean isModified() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void close() {
            // do nothing
        }

        @Override
        public Character addCharacter(CharacterType type) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Character addCharacter(int characterNumber, CharacterType type) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteCharacter(Character character) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void moveCharacter(Character character, int newCharacterNumber) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Item addItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Item addItem(int itemNumber) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Item addVariantItem(int parentItemNumber, int itemNumber) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Attribute addAttribute(int itemNumber, int characterNumber) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Attribute getAttribute(int itemNumber, int characterNumber) {
            return _intkeyDataset.getAttribute(itemNumber, characterNumber);
        }

        @Override
        public void deleteItem(Item item) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void moveItem(Item item, int newItemNumber) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteState(MultiStateCharacter character, int stateNumber) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Item> getUncodedItems(Character character) {
            throw new NotImplementedException();
        }

        @Override
        public List<Item> getItemsWithMultipleStatesCoded(MultiStateCharacter character) {
            throw new NotImplementedException();
        }

        @Override
        public Character changeCharacterType(Character character, CharacterType newType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean canChangeCharacterType(Character character, CharacterType newType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<CharacterDependency> getAllCharacterDependencies() {
            throw new NotImplementedException();
        }

        @Override
        public CharacterDependency addCharacterDependency(MultiStateCharacter owningCharacter, Set<Integer> states, Set<Integer> dependentCharacters) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteCharacterDependency(CharacterDependency characterDependency) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ImageSettings getImageSettings() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setImageSettings(ImageSettings imageSettings) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Item itemForDescription(String description) {
            String strippedDescription = RTFUtils.stripFormatting(description);
            return _intkeyDataset.getTaxonByName(strippedDescription);
        }

        @Override
        public ControllingInfo checkApplicability(Character character, Item item) {
            throw new NotImplementedException();
        }

        @Override
        public boolean isUncoded(Item item, Character character) {
            throw new NotImplementedException();
        }

        @Override
        public List<Item> getItemsAsList() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<Character> getCharactersAsList() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<Attribute> getAllAttributesForCharacter(int characterNumber) {
            // TODO Auto-generated method stub
            return null;
        }

    }
}

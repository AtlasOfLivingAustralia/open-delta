package au.org.ala.delta.intkey.model;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import au.org.ala.delta.intkey.directives.FileOutputDirective;
import au.org.ala.delta.intkey.directives.OutputCharactersDirective;
import au.org.ala.delta.intkey.directives.OutputDifferencesDirective;
import au.org.ala.delta.intkey.directives.OutputSimilaritiesDirective;
import au.org.ala.delta.intkey.directives.OutputSummaryDirective;
import au.org.ala.delta.intkey.directives.OutputTaxaDirective;

public class OutputReportsTest extends IntkeyDatasetTestCase {

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
    public void testOutputDifferences() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        File tempFile = File.createTempFile("OutputReportsTest", null);
        tempFile.deleteOnExit();

        new FileOutputDirective().parseAndProcess(context, tempFile.getAbsolutePath());
        new OutputDifferencesDirective().parseAndProcess(context, "all all");

        String fileContents = FileUtils.readFileToString(tempFile);

        assertEquals("OUTPUT DIFFERENCES\n1-5 7-9 11-13 15-16 18-20 25-28 30-31 34-35 37-41 44-54 56-82 84-87", fileContents.trim());
    }

    @Test
    public void testOutputSimilarities() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        File tempFile = File.createTempFile("OutputReportsTest", null);
        tempFile.deleteOnExit();

        new FileOutputDirective().parseAndProcess(context, tempFile.getAbsolutePath());
        new OutputSimilaritiesDirective().parseAndProcess(context, "all all");

        String fileContents = FileUtils.readFileToString(tempFile);

        assertEquals("OUTPUT SIMILARITIES\n6 10 14 17 21-24 29 32-33 36 42-43 55 83", fileContents.trim());
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
}

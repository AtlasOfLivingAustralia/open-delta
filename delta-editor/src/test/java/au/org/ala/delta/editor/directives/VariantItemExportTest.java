package au.org.ala.delta.editor.directives;

import au.org.ala.delta.editor.model.EditorDataModel;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;
import au.org.ala.delta.editor.slotfile.model.SlotFileRepository;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

/**
 * Tests that data sets with variant items are exported correctly.
 */
public class VariantItemExportTest extends AbstractImportExportTest {


    private String eol = System.getProperty("line.separator");
    private String SPECS =
            "*SHOW ~ Dataset specifications."+eol+eol+
                    "*DATA BUFFER SIZE 4000"+eol+eol+
                    "*NUMBER OF CHARACTERS 1"+eol+eol+
                    "*MAXIMUM NUMBER OF STATES 1"+eol+eol+
                    "*MAXIMUM NUMBER OF ITEMS 3"+eol+eol+
                    "*CHARACTER TYPES"+eol+eol+
                    "*NUMBERS OF STATES 1,1"+eol+eol+
                    "*IMPLICIT VALUES"+eol+eol+
                    "*DEPENDENT CHARACTERS"+eol+eol+
                    "*MANDATORY CHARACTERS"+eol;

    private String CHARS =
            "*SHOW ~ Character list."+eol+eol+
                    "*CHARACTER LIST"+eol+eol+
                    "#1. Character 1/"+eol+
                    "       1. State 1/"+eol+eol;

    private String ITEMS =
            "*SHOW ~ Item descriptions" + eol +
                    eol +
                    "*ITEM DESCRIPTIONS" + eol + eol + eol +
                    "# Item 1/" + eol +
                    "1<comment>,1" + eol + eol +

                    "#+ Item 2/" + eol +
                    "1<comment 2>" + eol + eol +

                    "# Item 3/" + eol +
                    "1,1"+ eol + eol;


    protected void createDataSet() throws Exception {
        SlotFileRepository repository = new SlotFileRepository();
        _dataSet = (SlotFileDataSet)repository.newDataSet();

        DirectiveFilesInitialiser initialiser = new DirectiveFilesInitialiser(null, new EditorDataModel(_dataSet));
        String[] fileNames = {"specs", "chars", "items"};
        DirectiveFileInfo[] files = new DirectiveFileInfo[fileNames.length];
        for (int i=0; i<fileNames.length; i++) {
            files[i] = new DirectiveFileInfo(fileNames[i]);
        }
        initialiser.buildSpecialDirFiles(Arrays.asList(files));


        MultiStateCharacter character = (MultiStateCharacter)_dataSet.addCharacter(CharacterType.UnorderedMultiState);
        character.setDescription("Character 1");
        character.addState(1);
        character.setState(1, "State 1");

        _dataSet.addItem().setDescription("Item 1");
        Item variant = _dataSet.addItem();
        variant.setDescription("Item 2");
        variant.setVariant(true);
        _dataSet.addItem().setDescription("Item 3");

        _dataSet.addAttribute(1, 1).setValueFromString("<comment>1");
        _dataSet.addAttribute(2, 1).setValueFromString("<comment 2>");
        _dataSet.addAttribute(3, 1).setValueFromString("1");
    }

    /**
     * Tests that a data set containing a variant Item can be exported correctly.
     */
    @Test
    public void testVariantItemExport() throws Exception {
        ExportController exporter = new ExportController(_helper);

        String[] fileNames = {"specs", "chars", "items"};
        DirectiveFileInfo[] files = new DirectiveFileInfo[fileNames.length];
        for (int i=0; i<fileNames.length; i++) {
            files[i] = new DirectiveFileInfo(_dataSet.getDirectiveFile(fileNames[i]));
        }
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        exporter.new DoExportTask(tempDir, Arrays.asList(files), true).doInBackground();

        String[] expected = {SPECS, CHARS, ITEMS};
        for (int i=0; i<fileNames.length; i++) {
            File actualFile = new File(tempDir, fileNames[i]);
            assertTrue(actualFile.exists());
            String actualFileContents = FileUtils.readFileToString(actualFile);
            assertEquals(expected[i], actualFileContents);
        }

    }

}

package au.org.ala.delta.editor.slotfile;

import java.io.File;
import java.io.IOException;

import au.org.ala.delta.DeltaTestCase;
import au.org.ala.delta.editor.DeltaFileReader;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.rtf.RTFUtils;
import au.org.ala.delta.util.CodeTimer;

public class ReaderTests extends DeltaTestCase {

	private String[] _files = new String[] { "Ponerini.dlt", "vide.dlt", "cflora.dlt", "sample.dlt", "newsample.dlt", "Grevillea.dlt" };

	public void testBasicRead() throws IOException {

		for (String filename : _files) {
			File f = copyURLToFile(String.format("/%s", filename));
			CodeTimer t = new CodeTimer("Reading " + filename);
			DeltaFileReader.readDeltaFile(f.getAbsolutePath(), null);
			t.stop(true);
		}
	}

	public void testDeepRead() throws IOException {

		for (String filename : _files) {
			File f = copyURLToFile(String.format("/%s", filename));

			CodeTimer t = new CodeTimer("Deep Reading " + filename);
			DeltaDataSet ds = DeltaFileReader.readDeltaFile(f.getAbsolutePath(), null);
			deepRead(ds);
			t.stop(true);
		}
	}

	private void deepRead(DeltaDataSet ds) {
		// Chars...
		System.out.println("Processing " + ds.getNumberOfCharacters() + " characters");
		for (int i = 1; i <= ds.getNumberOfCharacters(); ++i) {
			au.org.ala.delta.model.Character ch = ds.getCharacter(i);

			ch.getDescription();
			ch.getNotes();
			ch.isExclusive();
			ch.isMandatory();

			switch (ch.getCharacterType()) {
			case UnorderedMultiState:
			case OrderedMultiState:
				MultiStateCharacter msc = (MultiStateCharacter) ch;
				msc.getStates();
				break;
			case IntegerNumeric:
				IntegerCharacter intch = (IntegerCharacter) ch;
				break;
			case RealNumeric:
				RealCharacter rch = (RealCharacter) ch;
				break;
			default:

			}
		}

		System.out.println("Processing " + ds.getMaximumNumberOfItems() + " Items");
		for (int i = 1; i <= ds.getMaximumNumberOfItems(); ++i) {
			Item item = ds.getItem(i);

			for (int j = 1; j <= ds.getNumberOfCharacters(); ++j) {
				au.org.ala.delta.model.Character ch = ds.getCharacter(j);
				au.org.ala.delta.model.Attribute a = item.getAttribute(ch);
				String strValue = a.getValue();
				if (ch.getCharacterType() == CharacterType.Text) {
					RTFUtils.stripFormatting(strValue);
				}
			}
		}

	}

//	public void testBasicRead2() throws IOException {
//		CodeTimer t = new CodeTimer("readDeltaFile");
//		DeltaDataSet ds = DeltaFileReader.readDeltaFile("c:/zz/grasses_big.dlt", null);
//		deepRead(ds);
//		t.stop(true);
//	}

}

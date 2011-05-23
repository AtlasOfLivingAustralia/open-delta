package au.org.ala.delta.editor.slotfile;

import java.io.File;
import java.io.IOException;

import au.org.ala.delta.DeltaTestCase;
import au.org.ala.delta.editor.DeltaFileReader;
import au.org.ala.delta.model.DeltaDataSet;
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

//	public void testBasicRead2() throws IOException {
//		CodeTimer t = new CodeTimer("readDeltaFile");
//		DeltaDataSet ds = DeltaFileReader.readDeltaFile("c:/zz/grasses_big.dlt", null);
//		deepRead(ds);
//		t.stop(true);
//	}

}

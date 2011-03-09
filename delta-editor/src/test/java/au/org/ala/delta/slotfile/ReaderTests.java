package au.org.ala.delta.slotfile;

import java.io.File;
import java.io.IOException;

import au.org.ala.delta.DeltaFileReader;
import au.org.ala.delta.DeltaTestCase;
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
	
	
//	public void testFullRead() throws IOException {
//		// File f = copyURLToFile("/sample.dlt");
//		File f = copyURLToFile("/Ponerini.dlt");
//		CodeTimer t = new CodeTimer("readDeltaFile");
//		DeltaFileReader.readDeltaFileFully(f.getAbsolutePath());
//		t.stop(true);		
//	}
	
//	public void testFullRead2() throws IOException {
//		CodeTimer t = new CodeTimer("readDeltaFile");
//		DeltaFileReader.readDeltaFileFully("c:/zz/grasses_big.dlt", null);
//		t.stop(true);
//	}
	

//	public void testBasicRead2() throws IOException {
//		CodeTimer t = new CodeTimer("readDeltaFile");
//		DeltaFileReader.readDeltaFile("c:/zz/grasses_big.dlt", null);
//		t.stop(true);
//	}

	
}

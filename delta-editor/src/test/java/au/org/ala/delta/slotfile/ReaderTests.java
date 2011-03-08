package au.org.ala.delta.slotfile;

import java.io.File;
import java.io.IOException;

import au.org.ala.delta.DeltaFileReader;
import au.org.ala.delta.DeltaTestCase;
import au.org.ala.delta.util.CodeTimer;

public class ReaderTests extends DeltaTestCase {

	public void testBasicRead() throws IOException {
		File f = copyURLToFile("/Ponerini.dlt");
		CodeTimer t = new CodeTimer("readDeltaFile");
		DeltaFileReader.readDeltaFile(f.getAbsolutePath(), null);
		t.stop(true);
	}
	
	public void testFullRead() throws IOException {
		// File f = copyURLToFile("/sample.dlt");
		File f = copyURLToFile("/Ponerini.dlt");
		CodeTimer t = new CodeTimer("readDeltaFile");
		DeltaFileReader.readDeltaFileFully(f.getAbsolutePath());
		t.stop(true);		
	}
	
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

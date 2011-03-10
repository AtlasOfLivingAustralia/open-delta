package au.org.ala.delta.slotfile;

import org.junit.Test;

import au.org.ala.delta.DeltaFileReader;

public class LoadTest {

	@Test
	public void testLoad() {
		// DeltaFileReader.readDeltaFile("C:\\Users\\Chris\\eclipse-workspace\\DELTA\\delta-editor\\sampledata\\Ponerini.dlt", null);
		DeltaFileReader.readDeltaFile("C:/Users/Chris/DELTA resources/grasses_big.dlt", null); 
	}
}

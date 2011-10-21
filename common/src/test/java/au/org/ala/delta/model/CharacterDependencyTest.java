package au.org.ala.delta.model;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.model.impl.ControllingInfo;

public class CharacterDependencyTest extends TestCase {

	private DeltaDataSet _dataSet;
	
	@Before
	public void setUp() throws Exception {
		URL blah = getClass().getResource("/dataset/sample/toint");
		File file = new File(blah.toURI());
		
		_dataSet = DefaultDataSetFactory.load(file);
	}
	
	@Test
	public void testCharacterDependencies() {
		Character character = _dataSet.getCharacter(48);
		Item item = _dataSet.getItem(1);
		ControllingInfo info = _dataSet.checkApplicability(character, item);
		
		assertTrue(info.isInapplicable());
	}
	
}

package au.org.ala.delta.directives;

import org.junit.Before;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.MultiStateCharacter;

import junit.framework.TestCase;

/**
 * Tests the CharacterList class.
 */
public class CharacterListTest extends TestCase {

	
	private DeltaDataSet _dataSet;
	private DeltaContext _context;
	
	
	@Before
	public void setUp() {
		DefaultDataSetFactory factory = new DefaultDataSetFactory();
		_dataSet = factory.createDataSet("test");
		_context = new DeltaContext(_dataSet);
		
	}
	
	public void testCharacterList() throws Exception {
		_context.setNumberOfCharacters(1);
		MultiStateCharacter character = (MultiStateCharacter)_dataSet.addCharacter(CharacterType.OrderedMultiState);
		character.setNumberOfStates(4);
		
		String charDescription = 
			"#1. <adaxial> ligule <form; avoid seedlings>/\n"+
			"    1. an unfringed membrane <may be variously hairy or ciliolate>/\n"+
			"    2. a fringed membrane/\n"+
			"    3. a fringe of hairs/\n"+
			"    4. a rim of minute papillae/\n";

		
		CharacterList characterList = new CharacterList();
		characterList.parseAndProcess(_context, charDescription);
		
		assertEquals(1, _dataSet.getNumberOfCharacters());
		MultiStateCharacter multiStateChar = (MultiStateCharacter)_dataSet.getCharacter(1);
		assertEquals("<adaxial> ligule <form; avoid seedlings>", character.getDescription());
		assertEquals(4, multiStateChar.getNumberOfStates());
		assertEquals("an unfringed membrane <may be variously hairy or ciliolate>", multiStateChar.getState(1));
		assertEquals("a fringed membrane", multiStateChar.getState(2));
		assertEquals("a fringe of hairs", multiStateChar.getState(3));
		assertEquals("a rim of minute papillae", multiStateChar.getState(4));
		
	}
	
}

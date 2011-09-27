package au.org.ala.delta.key;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.DataSetBuilder;
import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.impl.DefaultDataSet;
import au.org.ala.delta.translation.FormatterFactory;
import au.org.ala.delta.translation.key.KeyStateTranslator;

public class KeyStateTranslatorTest extends TestCase {

	private KeyStateTranslator _translator;
	private DeltaContext _context;
	private DeltaDataSet _dataSet;
	
	@Before
	public void setUp() {
		DefaultDataSetFactory factory = new DefaultDataSetFactory();
		_dataSet = (DefaultDataSet)factory.createDataSet("test");
		_context = new DeltaContext(_dataSet);
		_context.setNumberOfCharacters(4);
		DataSetBuilder.buildSimpleDataSet(_dataSet);
		DeltaContext context = new DeltaContext();
		FormatterFactory formatterFactory = new FormatterFactory(context);
		_translator = new KeyStateTranslator(formatterFactory);
	}
	
	@Test
	public void testTranslateUnorderedMultistateCharacter() {
		IdentificationKeyCharacter keyChar = new IdentificationKeyCharacter(_dataSet.getCharacter(1));
		List<Integer> originalStates = new ArrayList<Integer>();
		originalStates.add(2);
		originalStates.add(3);
		
		keyChar.addState(1, originalStates);
		
		originalStates = new ArrayList<Integer>();
		originalStates.add(1);
		keyChar.addState(2, originalStates);
		
		String result = _translator.translateState(keyChar, 1);
		//assertEquals("This is state 2 or 3", result);
		
		result = _translator.translateState(keyChar, 2);
		assertEquals("state 1", result);
		
	}

	@Test
	public void testTranslateOrderedMultistateCharacter() {
		
	}

	@Test
	public void testTranslateIntegerCharacter() {
		
	}
	
	@Test
	public void testTranslateRealCharacter() {
		
	}
}

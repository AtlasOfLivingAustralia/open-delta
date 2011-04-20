package au.org.ala.delta.translation;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.TypeSettingMark;
import au.org.ala.delta.model.TypeSettingMark.MarkPosition;

/**
 * Tests the production of type set natural language.  This test is more of an integration
 * test than a unit test - it relies on the collaboration of several classes to produce the
 * natural language output.
 */
public class TypeSetNaturalLanguageTranslatorTest extends
		NaturalLangaugeTranslatorTest {

	@Before
	public void setUp() throws Exception {
		
		_bytes = new ByteArrayOutputStream();
		PrintStream pout = new PrintStream(_bytes, false, "UTF-8");
		_printer = new Printer(pout, 0);
		_context = new DeltaContext();
	}

	@Override
	protected void initialiseContext(String path) throws Exception {
		super.initialiseContext(path);
		
		_typeSetter = new FormattedTextTypeSetter(createMarks(), _printer);
		_dataSetTranslator = new NaturalLanguageTranslator(_context, _typeSetter, _printer);
	}
	
	protected Map<MarkPosition, TypeSettingMark> createMarks() {
		HashMap<MarkPosition, TypeSettingMark> marks = new HashMap<TypeSettingMark.MarkPosition, TypeSettingMark>();
		
		for (MarkPosition position: MarkPosition.values()) {
			marks.put(position, new TypeSettingMark(position, "mark "+position.getId(), false));
		}
		
		return marks;
	}
	
	public void testSimpleDataSetWithTypesetting() throws Exception {
		initialiseContext(DEFAULT_DATASET_PATH);
		
		_dataSetTranslator.translate();
		checkResult("typeset.txt");
	}
	
	
	
}

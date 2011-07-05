package au.org.ala.delta.editor.directives;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.CharacterTypes;
import au.org.ala.delta.directives.ImplicitValues;
import au.org.ala.delta.directives.MaximumNumberOfItems;
import au.org.ala.delta.directives.MaximumNumberOfStates;
import au.org.ala.delta.directives.NumberOfCharacters;
import au.org.ala.delta.directives.NumbersOfStates;
import au.org.ala.delta.directives.OmitCharacterNumbers;
import au.org.ala.delta.directives.OmitInapplicables;
import au.org.ala.delta.directives.OmitInnerComments;
import au.org.ala.delta.directives.ReplaceAngleBrackets;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.editor.slotfile.DeltaNumber;
import au.org.ala.delta.editor.slotfile.VODirFileDesc.Dir;
import au.org.ala.delta.editor.slotfile.VODirFileDesc.DirArgs;
import au.org.ala.delta.editor.slotfile.VODirFileDesc.DirListData;
import au.org.ala.delta.editor.slotfile.VODirFileDescTest;
import au.org.ala.delta.editor.slotfile.directive.ConforDirType;
import au.org.ala.delta.editor.slotfile.directive.IntkeyDirType;


/**
 * Tests the DirectiveArgConverter class.
 */
public class DirectiveArgConverterTest extends VODirFileDescTest {
	
	private DirectiveArgConverter _converter;
	private DeltaContext _context;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		_converter = new DirectiveArgConverter(_vop);
		_context = new DeltaContext();
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testInternalDirectives() throws Exception {
		Object[] directives = new Object[] {
				new CharacterTypes(), "1,TE 2,IN 3,RN 4,UM 5,OM", ConforDirType.CHARACTER_TYPES,
				new MaximumNumberOfItems(), "100", ConforDirType.MAXIMUM_NUMBER_OF_ITEMS,
				new MaximumNumberOfStates(),"3", ConforDirType.MAXIMUM_NUMBER_OF_STATES,
				new NumberOfCharacters(), "5", ConforDirType.NUMBER_OF_CHARACTERS,
				new NumbersOfStates(), "4,2 5,3", ConforDirType.NUMBERS_OF_STATES,
				new ImplicitValues(), "5,1:3", ConforDirType.IMPLICIT_VALUES
		};
		
		
		for (int i=0; i<directives.length; i+=3) {
			AbstractDirective<DeltaContext> directive = (AbstractDirective<DeltaContext>)directives[i];
			
			System.out.println("Checking: "+directive.getClass());
			directive.parseAndProcess(_context, (String)directives[i+1]);
			Dir dir = _converter.fromDirective(directive);
			
			assertEquals(directives[i+2], dir.dirType);
			assertEquals(0, dir.args.size());
		}
		
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testNoArgDirectives() throws Exception {
		Object[] directives = new Object[] {
				new ReplaceAngleBrackets(), "", ConforDirType.REPLACE_ANGLE_BRACKETS,
				new OmitCharacterNumbers(), "", ConforDirType.OMIT_CHARACTER_NUMBERS,
				new OmitInnerComments(),"", ConforDirType.OMIT_INNER_COMMENTS,
				new OmitInapplicables(), "", ConforDirType.OMIT_INAPPLICABLES
		};
		
		
		for (int i=0; i<directives.length; i+=3) {
			AbstractDirective<DeltaContext> directive = (AbstractDirective<DeltaContext>)directives[i];
			
			System.out.println("Checking: "+directive.getClass());
			directive.parseAndProcess(_context, (String)directives[i+1]);
			Dir dir = _converter.fromDirective(directive);
			
			assertEquals(directives[i+2], dir.dirType);
			assertEquals(0, dir.args.size());
		}
	}
	
	
	@Test
	public void testTextArgConversion() {
		Dir dir = new Dir();
		dir.setDirType(ConforDirType.HEADING);
		dir.resizeArgs(1);
		dir.args.get(0).text = "This is a heading";
		 
		DirectiveArguments args = _converter.convertArgs(dir, DirectiveArgType.DIRARG_TEXT);
		assertEquals("This is a heading", args.getFirstArgumentText());
		
	}
	
	@Test
	public void testCharArgConversion() {
		int id = _vop.getDeltaMaster().uniIdFromCharNo(3);		
		Dir dir = createDirWithId(id, ConforDirType.CHARACTER_FOR_TAXON_IMAGES);
		 
		DirectiveArguments args = _converter.convertArgs(dir, DirectiveArgType.DIRARG_CHAR);
		assertEquals(3, args.getFirstArgumentIdAsInt());
		
	}
	
	private Dir createDirWithId(int id, int dirType) { 
		Dir dir = new Dir();
		dir.setDirType(dirType);
		dir.resizeArgs(1);
		dir.args.get(0).setId(id);
		return dir;
	}
	
	@Test 
	public void testItemArgConversion() {
	
		int id = _vop.getDeltaMaster().uniIdFromItemNo(2);
		Dir dir = createDirWithId(id, ConforDirType.STOP_AFTER_ITEM);
		DirectiveArguments args = _converter.convertArgs(dir, DirectiveArgType.DIRARG_ITEM);
		assertEquals(2, args.getFirstArgumentIdAsInt());
	}
	
	@Test
	public void testItemRealListConversion() {
		int id = _vop.getDeltaMaster().uniIdFromItemNo(2);
		Dir dir = createDirWithId(id, ConforDirType.ITEM_WEIGHTS);
		
		float[] weights = new float[]{1f, 2.2f, 3.3f, 4.4f};
		for (int i=0; i<weights.length; i++) {
			DirListData data = new DirListData();
			data.setAsDeltaNumber(new DeltaNumber(Float.toString(weights[i])));
			dir.args.get(0).getData().add(data);
		}
		
		DirectiveArguments args = _converter.convertArgs(dir, DirectiveArgType.DIRARG_ITEMREALLIST);
		
		assertEquals(2, args.getFirstArgumentIdAsInt());
		List<BigDecimal> weightsData = args.getDirectiveArguments().get(0).getData();
		assertEquals(weights.length, weightsData.size());
		for (int i=0; i<weights.length; i++) {
			assertEquals(weights[i], weightsData.get(i).floatValue());
		}
		
	}
	
	@Test
	public void testCharacterRealListConversion() {
		
		Dir dir = new Dir();
		dir.setDirType(ConforDirType.CHARACTER_RELIABILITIES);
		dir.resizeArgs(5);
		float[] reliabilities = new float[]{1f, 2.2f, 3.3f, 4.4f, 5.5f};
		for (int i=1; i<=5; i++) {
			int id = _vop.getDeltaMaster().uniIdFromCharNo(i);
			dir.args.get(i-1).setId(id);
			dir.args.get(i-1).setValue(Float.toString(reliabilities[i-1]));
		}
		
		DirectiveArguments args = _converter.convertArgs(dir, DirectiveArgType.DIRARG_CHARREALLIST);
		
		assertEquals(5, args.size());
		for (int i=0; i<5; i++) {
			assertEquals(i+1, args.get(i).getId());
			assertEquals(reliabilities[i], args.get(i).getValue().floatValue());
		}
		
	}
	
	@Test
	public void testDefineCharactersConversion() {
		Dir dir = createDirWithId(0, IntkeyDirType.DEFINE_CHARACTERS);
		dir.args.get(0).text = "nomenclature";
		int id = _vop.getDeltaMaster().uniIdFromCharNo(1);
		dir.args.add(new DirArgs(id));
		
		DirectiveArguments args = _converter.convertArgs(dir, DirectiveArgType.DIRARG_CHARREALLIST);
		assertEquals(2, args.size());
		
		assertEquals(0, args.getFirstArgumentIdAsInt());
		assertEquals("nomenclature", args.getFirstArgumentText());
		assertEquals(1, args.get(1).getId());
	}
	
	@Test
	public void testEmphasizeCharactersConverstion() {
		
		int id = _vop.getDeltaMaster().uniIdFromItemNo(2);
		Dir dir = createDirWithId(id, ConforDirType.EMPHASIZE_CHARACTERS);
		
		List<DirListData> charList = dir.args.get(0).getData();
		
		int[] charNos = new int[] {1,3};
		for (int charNo : charNos) {
			id = _vop.getDeltaMaster().uniIdFromCharNo(charNo);
			DirListData data = new DirListData();
			data.setIntNumb(id);
			charList.add(data);
		}
		
		DirectiveArguments args = _converter.convertArgs(dir, DirectiveArgType.DIRARG_ITEMCHARLIST);
		assertEquals(1, args.size());
		
		assertEquals(2, args.getFirstArgumentIdAsInt());
		List<Integer> charNoList = args.get(0).getDataList();
		assertEquals(2, charNoList.size());
		assertEquals(1, charNoList.get(0).intValue());
		assertEquals(3, charNoList.get(1).intValue());
	}
}

package au.org.ala.delta.editor.directives;

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
import au.org.ala.delta.editor.slotfile.VODirFileDesc.Dir;
import au.org.ala.delta.editor.slotfile.VODirFileDescTest;
import au.org.ala.delta.editor.slotfile.directive.ConforDirType;


/**
 * Tests the DirectiveArgConverter class.
 */
public class DirectiveArgConverterTest extends VODirFileDescTest {
	
	private DirectiveArgConverter _converter;
	private DeltaContext _context;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		_converter = new DirectiveArgConverter();
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
			directive.process(_context, (String)directives[i+1]);
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
			directive.process(_context, (String)directives[i+1]);
			Dir dir = _converter.fromDirective(directive);
			
			assertEquals(directives[i+2], dir.dirType);
			assertEquals(0, dir.args.size());
		}
	}
	
}

package au.org.ala.delta.directives;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import au.org.ala.delta.DeltaContext;


public class LinkCharactersTest extends TestCase {

	
	public void testLinkCharactersWithValidInput() throws Exception {
		LinkCharacters directive = new LinkCharacters();
		DeltaContext context = new DeltaContext();
		
		String data = "*LINK CHARACTERS 1-3 6:10-11 7-9";
		
		directive.parseAndProcess(context, data);
		
	
		Set<Integer> set1 = new HashSet<Integer>();
		set1.add(1);
		set1.add(2);
		set1.add(3);
		
		Set<Integer> set2 = new HashSet<Integer>();
		set2.add(6);
		set2.add(10);
		set2.add(11);
		
		Set<Integer> set3 = new HashSet<Integer>();
		set3.add(7);
		set3.add(8);
		set3.add(9);
		
		
		assertEquals(set1, context.getLinkedCharacters(1));
		assertEquals(set1, context.getLinkedCharacters(2));
		assertEquals(set1, context.getLinkedCharacters(3));
		assertNull(context.getLinkedCharacters(4));
		assertNull(context.getLinkedCharacters(5));
		
		assertEquals(set2, context.getLinkedCharacters(6));
		assertEquals(set3, context.getLinkedCharacters(7));
		assertEquals(set3, context.getLinkedCharacters(8));
		assertEquals(set3, context.getLinkedCharacters(9));
		assertEquals(set2, context.getLinkedCharacters(10));
		assertEquals(set2, context.getLinkedCharacters(11));
		
	}
	
}

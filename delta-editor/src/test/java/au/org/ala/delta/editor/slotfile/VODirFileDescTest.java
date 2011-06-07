package au.org.ala.delta.editor.slotfile;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.DeltaTestCase;
import au.org.ala.delta.editor.slotfile.VODirFileDesc.Dir;

/**
 * Tests the VODirFileDesc class.
 */
public class VODirFileDescTest extends DeltaTestCase {

	/** Holds the data set we obtain the data from to back our test*/
	protected DeltaVOP _vop;
	
	@Before
	public void setUp() throws Exception {
		File f = copyURLToFile("/dataset/simple.DLT");
			
		_vop = new DeltaVOP(f.getAbsolutePath(), false);
	}

	@After
	public void tearDown() throws Exception {
		if (_vop != null) {
			_vop.close();
		}
		super.tearDown();
	}	
	
	@Test
	/**
	 * The Specs file looks like:
	 * *SHOW ~ Grass Genera - specifications.
     *
     * *NUMBER OF CHARACTERS 5
     * *MAXIMUM NUMBER OF STATES 3
     * *MAXIMUM NUMBER OF ITEMS 6
     * *CHARACTER TYPES 1,TE 2,IN 3,RN 4,UM 5,OM 
     * *NUMBERS OF STATES 4,2 5,3
     * *IMPLICIT VALUES 5,1:3
	 */
	public void testRead() {
		
		VODirFileDesc specs = getDirFileDesc("Z:\\simple data set\\specs");
		
		assertEquals(7, specs.getNDirectives());
		
		List<Dir> directives = specs.readAllDirectives();
		
		Dir directive = directives.get(0);
		assertEquals(Arrays.asList(new String[]{"SHOW", "", "", ""}), getDirName(directive, specs));
		assertEquals("~ Grass Genera - specifications.", directive.args.get(0).text);
		
		
		// These are all "internal" directives so don't have args as their data is
		// a part of the data set.
		directive = directives.get(1);
		assertEquals(Arrays.asList(new String[]{"NUMBER", "OF", "CHARACTERS", ""}), getDirName(directive, specs));
		assertEquals(0, directive.args.size());
		
		directive = directives.get(2);
		assertEquals(Arrays.asList(new String[]{"MAXIMUM", "NUMBER", "OF", "STATES"}), getDirName(directive, specs));
		assertEquals(0, directive.args.size());
		
		directive = directives.get(3);
		assertEquals(Arrays.asList(new String[]{"MAXIMUM", "NUMBER", "OF", "ITEMS"}), getDirName(directive, specs));
		assertEquals(0, directive.args.size());
		
		directive = directives.get(4);
		assertEquals(Arrays.asList(new String[]{"CHARACTER", "TYPES", "", ""}), getDirName(directive, specs));
		assertEquals(0, directive.args.size());
		
		directive = directives.get(5);
		assertEquals(Arrays.asList(new String[]{"NUMBERS", "OF", "STATES", ""}), getDirName(directive, specs));
		assertEquals(0, directive.args.size());
		
		directive = directives.get(6);
		assertEquals(Arrays.asList(new String[]{"IMPLICIT", "VALUES", "", ""}), getDirName(directive, specs));
		assertEquals(0, directive.args.size());
		
	}
	
	protected List<String> getDirName(Dir directive, VODirFileDesc fileDesc) {
		List<Directive> allDirectives = fileDesc.getDirArray();
		return Arrays.asList(allDirectives.get(directive.getDirType()).getName());
	}
	
	protected VODirFileDesc getDirFileDesc(int dirFileNum) {
		int id = _vop.getDeltaMaster().uniIdFromDirFileNo(dirFileNum);
		return (VODirFileDesc)_vop.getDescFromId(id);
	}
	
	protected VODirFileDesc getDirFileDesc(String fileName) {
		for (int i=1; i<=_vop.getDeltaMaster().getNDirFiles(); i++) {
			VODirFileDesc dirFile = getDirFileDesc(i);
			System.out.println(dirFile.getFileName());
			if (fileName.equalsIgnoreCase(dirFile.getFileName())) {
				return dirFile;
			}
		}
		throw new RuntimeException("No directive file exists with name: "+fileName);
	}
	
}

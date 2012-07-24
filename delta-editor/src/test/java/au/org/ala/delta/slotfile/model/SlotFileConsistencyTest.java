package au.org.ala.delta.slotfile.model;

import java.io.File;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;
import au.org.ala.delta.editor.slotfile.model.SlotFileRepository;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.UnorderedMultiStateCharacter;

public class SlotFileConsistencyTest extends TestCase {
	
	@Test
	public void testSlotFile1() throws DirectiveException {
		
		SlotFileRepository repo = new SlotFileRepository();
		MutableDeltaDataSet dataset = repo.newDataSet();
		for (int i = 0; i < 100; i += 2) {
			System.out.println(i);
			au.org.ala.delta.model.Character ch1 = addUnorderedCharacter(dataset, "character " + i, "state1_" + i, "state2_" + i, "state3_" + i );
			au.org.ala.delta.model.Character ch2 = addUnorderedCharacter(dataset, "character " + (i+1), "state1_" + (i+1), "state2_" + (i+1), "state3_" + (i+1));
			dataset.deleteCharacter(ch1);
			Item item1 = addItem(dataset, "Item " + i);
			Item item2 = addItem(dataset, "Item " + (i + 1));
			dataset.deleteItem(item1);					
			Attribute attr = dataset.addAttribute(ch2.getCharacterId(), item2.getItemNumber());
			attr.setValueFromString("1");
			((SlotFileDataSet) dataset).consistencyCheck();
		}
				
		dumpDataset(dataset);
		dataset.close();
				
	}
	
	@Test
	public void testSlotFile2() throws DirectiveException {
		
		SlotFileRepository repo = new SlotFileRepository();
		MutableDeltaDataSet dataset = repo.newDataSet();
		for (int i = 0; i < 100; i++) {
			au.org.ala.delta.model.Character ch1 = addUnorderedCharacter(dataset, "character " + i, "state1_" + i, "state2_" + i, "state3_" + i );
		}
		
		for (int i = 0; i < 100; ++i) {
			Item item1 = addItem(dataset, "Item " + i);			
		}
		
		for (int i = 0; i <= 25; ++i) {
			Character ch = dataset.getCharacter(50);
			dataset.deleteCharacter(ch);
		}
								
		for (int i = 0; i <= 25; ++i) {
			Item item = dataset.getItem(50);
			dataset.deleteItem(item);
		}
		
		for (int i = 0; i < 25; ++i) {
			au.org.ala.delta.model.Character ch1 = addUnorderedCharacter(dataset, "character " + i, "state1_" + i, "state2_" + i, "state3_" + i );			
		}
		
		for (int i = 0; i < 25; ++i) {
			Item item1 = addItem(dataset, "Item " + i);					
		}
				
		dumpDataset(dataset);
		dataset.close();
				
	}

	@Test
	public void testSlotFile3() throws DirectiveException {
		
		SlotFileRepository repo = new SlotFileRepository();
		MutableDeltaDataSet dataset = repo.newDataSet();
		for (int i = 0; i < 100; i++) {
			au.org.ala.delta.model.Character ch1 = addUnorderedCharacter(dataset, "character " + i, "state1_" + i, "state2_" + i, "state3_" + i );
		}
		
		for (int i = 0; i < 100; ++i) {
			Item item1 = addItem(dataset, "Item " + i);
			for (int j = 1; j <= dataset.getNumberOfCharacters(); ++j) {
				Attribute attr = dataset.addAttribute(item1.getItemNumber(), j);
				attr.setValueFromString("1&2");
			}
		}
		
		for (int i = 0; i <= 25; ++i) {
			Character ch = dataset.getCharacter(50);
			dataset.deleteCharacter(ch);
		}
								
		for (int i = 0; i <= 25; ++i) {
			Item item = dataset.getItem(50);
			dataset.deleteItem(item);
		}
		
		for (int i = 0; i < 25; ++i) {
			addUnorderedCharacter(dataset, "character " + i, "state1_" + i, "state2_" + i, "state3_" + i );			
		}
		
		for (int i = 0; i < 25; ++i) {
			addItem(dataset, "Item " + i);					
		}
				
		dumpDataset(dataset);
		dataset.close();
				
	}
	
	@Test
	public void testSlotFile4() throws DirectiveException {
		SlotFileRepository repo = new SlotFileRepository();
		MutableDeltaDataSet dataset = repo.newDataSet();
		for (int i = 0; i < 100; i++) {
			addTextCharacter(dataset, "character " + i);
		}
		
		for (int i = 0; i < 100; i++) {
			addItem(dataset, "Item " + i);
		}
		
		for (int x = 0; x < 10; ++x) {
			
			for (int i = 0; i < 100; i++) {
				Item item = dataset.getItem(dataset.getMaximumNumberOfItems());
				dataset.deleteItem(item);
				addUnorderedCharacter(dataset, "character " + i );
			}
			
			for (int i = 0; i < 100; i++) {
				Character ch = dataset.getCharacter(dataset.getNumberOfCharacters());
				dataset.deleteCharacter(ch);
				addItem(dataset, "Item " + i);
			}
			
			out("Consistency Check 1");
			((SlotFileDataSet) dataset).consistencyCheck();
			
			File f = new File("testdata");
			if (f.exists()) {
				f.delete();
			}
			
			out("Saving dataset");
			repo.saveAsName(dataset, "testdata", null);
			dataset.close();
			out("Reloading dataset");
			dataset = repo.findByName("testdata", null);
			
			out("Consistency Check 2");
			((SlotFileDataSet) dataset).consistencyCheck();
		}
		
		dumpDataset(dataset);
		dataset.close();

	}
	
	private Item addItem(MutableDeltaDataSet dataset, String itemDesc) {
		Item item = dataset.addItem();
		item.setDescription(itemDesc);
		return item;
	}
	
	private au.org.ala.delta.model.Character addTextCharacter(MutableDeltaDataSet dataset, String text) {
		TextCharacter ch = (TextCharacter) dataset.addCharacter(CharacterType.Text);
		ch.setDescription(text);
		return ch;
	}
	
	private UnorderedMultiStateCharacter addUnorderedCharacter(MutableDeltaDataSet dataset, String description, String...states) {
		UnorderedMultiStateCharacter ch = (UnorderedMultiStateCharacter) dataset.addCharacter(CharacterType.UnorderedMultiState);
		ch.setDescription(description);
		int i = 0;
		for (String s : states) {
			ch.addState(++i);
			ch.setState(i, s);
		}
		return ch;
	}
	
	private void dumpDataset(DeltaDataSet dataset) {
		out("Dumping...");
		for (int i = 1; i <= dataset.getNumberOfCharacters(); ++i) {
			Character ch = dataset.getCharacter(i);
			out("Character %d: %s", i, ch.getDescription());
			if (ch instanceof UnorderedMultiStateCharacter) {
				UnorderedMultiStateCharacter uo = (UnorderedMultiStateCharacter) ch;
				out("  States: %s", StringUtils.join(uo.getStates(), ", "));
			}
		}
		
		for (int i = 1; i <= dataset.getMaximumNumberOfItems(); ++i) {
			Item item = dataset.getItem(i);
			out("Item %d: %s", i, item.getDescription());
		}
	}
	
	private static void out(String format, Object ...args) {
		System.out.println(String.format(format, args));
	}
	

}

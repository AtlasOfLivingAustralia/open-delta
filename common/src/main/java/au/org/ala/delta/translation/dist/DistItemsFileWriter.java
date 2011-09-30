package au.org.ala.delta.translation.dist;

import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.dist.WriteOnceDistItemsFile;
import au.org.ala.delta.io.BinaryKeyFile;
import au.org.ala.delta.io.BinaryKeyFileEncoder;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.util.Pair;

/**
 * Writes the key items file using the data in a supplied DeltaContext and
 * associated data set.
 */
public class DistItemsFileWriter {

	public static final int INAPPLICABLE_BIT = 20;
	private WriteOnceDistItemsFile _itemsFile;
	private FilteredDataSet _dataSet;
	private DeltaContext _context;
	private ItemFormatter _itemFormatter;
	private BinaryKeyFileEncoder _encoder;
	
	
	public DistItemsFileWriter(
			DeltaContext context, 
			FilteredDataSet dataSet,
			ItemFormatter itemFormatter,
			WriteOnceDistItemsFile itemsFile) {
		_itemsFile = itemsFile;
		_dataSet = dataSet;
		_context = context;
		_itemFormatter = itemFormatter;
		_encoder = new BinaryKeyFileEncoder();
		
	}
	
	public void writeAll() {
		
		writeItems();
//		writeHeading();
//		writeCharacterMask();
//		writeNumbersOfStates();
//		writeCharacterDependencies();
//		writeCharacterReliabilities();
//		writeTaxonMask();
//		writeItemLengths();
//		writeItemAbundances();
//		
		// Need to write the header last as it is updated as each section 
		// is written.
		_itemsFile.writeHeader();
	}
	
	protected void writeItems() {
		final int BYTES_IN_WORD = 4;
		 Pair<int[], int[]> offsets = calculateAttributeOffsets();
		 int[] wordOffsets = offsets.getFirst();
		 int[] bitOffsets = offsets.getSecond();
		 ByteBuffer work = ByteBuffer.allocate(1000);
		 Iterator<Item> items = _dataSet.unfilteredItems();
		 while (items.hasNext()) {
			 Item item = items.next();
			 String description = _itemFormatter.formatItemDescription(item);
			 
			 
			 Iterator<IdentificationKeyCharacter> chars = _dataSet.unfilteredIdentificationKeyCharacterIterator();
			 while (chars.hasNext()) {
				 IdentificationKeyCharacter keyChar = chars.next();
				 int charNum = keyChar.getCharacterNumber();
				 if (!keyChar.getCharacterType().isText()) {
					 int offset = wordOffsets[keyChar.getCharacterNumber()];
					 if (!(keyChar.getCharacterType() == CharacterType.UnorderedMultiState)) {
						 work.putDouble(offset*BYTES_IN_WORD, -9999.0d);
					 }
					 Attribute attribute = item.getAttribute(keyChar.getCharacter());
					 if (attribute == null || attribute.isUnknown()) {
						 continue;
					 }
					 switch (keyChar.getCharacterType()) {
					 case UnorderedMultiState:
						 encodeUnorderedMultistateAttribute(
								 work, wordOffsets[charNum-1], bitOffsets[charNum-1], 
								 keyChar, (MultiStateAttribute)attribute);
	
						 break;
					 case OrderedMultiState:
						 break;
					 case IntegerNumeric:
					 case RealNumeric:
						 break;
					 }
				 }
				 _itemsFile.writeItem(description, work);
			 }
		    	 
		 }
		 
	}
	
	private void encodeUnorderedMultistateAttribute(ByteBuffer work, int wordOffset, int bitOffset, IdentificationKeyCharacter keyChar, MultiStateAttribute attribute) {
		List<Integer> states = keyChar.getPresentStates(attribute);
		
		int word = work.getInt(wordOffset);
		
		for (int state : states) {
			int bit = bitOffset + state;
			if (bit > 32) {
				work.putInt(wordOffset, word);
				
				wordOffset++;
				word = work.getInt(wordOffset);
				bitOffset -= 32;
				bit = bitOffset + state;
			}
			
			word |= bit << 2;
		}
		work.putInt(wordOffset, word);
		
	}
	
	private Pair<int[], int[]> calculateAttributeOffsets() {
		int wordOffset = 1;
        int bitOffset = 0;
        final int BITS_IN_WORD = 32;
        
        int[] wordOffsets = new int[_dataSet.getNumberOfCharacters()];
        int[] bitOffsets = new int[_dataSet.getNumberOfCharacters()];
        Iterator<IdentificationKeyCharacter> chars = _dataSet.unfilteredIdentificationKeyCharacterIterator();
        while (chars.hasNext()) {
        	IdentificationKeyCharacter character = chars.next();
        	int charNumber = character.getCharacterNumber();
        	bitOffsets[charNumber-1] = 0;
        	if (character.getCharacterType() == CharacterType.UnorderedMultiState) {
        		int numStates = character.getNumberOfStates();
        		wordOffsets[charNumber-1] = wordOffset;
        		bitOffsets[charNumber-1] = bitOffset;
        		bitOffset += numStates;
        		int word = bitOffset / BITS_IN_WORD;
        		wordOffset += word;
        		bitOffset -= word*BITS_IN_WORD;
        	}
        }
        
        if (bitOffset !=0) {
        	wordOffset++;
        }
 
        chars = _dataSet.identificationKeyCharacterIterator();
        while (chars.hasNext()) {
        	IdentificationKeyCharacter character = chars.next();
        	int charNumber = character.getCharacterNumber();
        	if (character.getCharacterType() != CharacterType.UnorderedMultiState && 
        		character.getCharacterType() != CharacterType.Text) {
        		wordOffsets[charNumber-1] = wordOffset;
        		wordOffset++;
        	}
        }
        
        int itemLength = wordOffset-1;
        if (itemLength > BinaryKeyFile.RECORD_LENGTH_INTEGERS) {
        	throw new IllegalArgumentException("Something is too big");
        }

        return new Pair<int[], int[]>(wordOffsets, bitOffsets);
	}
}

package au.org.ala.delta.editor.slotfile.model;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.TextType;
import au.org.ala.delta.editor.slotfile.VOCharBaseDesc;
import au.org.ala.delta.editor.slotfile.VOItemDesc;
import au.org.ala.delta.model.AbstractObservableDataSet;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.image.Image;

/**
 * Implementation of a DELTA DataSet that uses the random access slotfile to read data on demand rather
 * than storing it in memory.
 */
public class SlotFileDataSet extends AbstractObservableDataSet {

	private DeltaVOP _vop;
	private SlotFileDataSetFactory _factory;

	public SlotFileDataSet(DeltaVOP vop, SlotFileDataSetFactory factory) {
		_vop = vop;
		_factory = factory;
	}
	
	public DeltaVOP getVOP() {
		return _vop;
	}
	
	@Override
	protected Item doGetItem(int number) {
		synchronized (_vop) {
			if (number > getMaximumNumberOfItems()) {
				throw new IndexOutOfBoundsException("No such Item ("+number+">"+getMaximumNumberOfItems());
			}
			
			int itemId = _vop.getDeltaMaster().uniIdFromItemNo(number);
			VOItemDesc itemDesc = (VOItemDesc) _vop.getDescFromId(itemId);
			VOItemAdaptor adaptor = new VOItemAdaptor(_vop, itemDesc, number);
			return new Item(adaptor, number);
		}
	}
	
	@Override
	protected Character doGetCharacter(int number) {
		synchronized (_vop) {
			int charId = _vop.getDeltaMaster().uniIdFromCharNo(number);	
			VOCharBaseDesc characterDesc = (VOCharBaseDesc)_vop.getDescFromId(charId);
			return _factory.createCharacter(CharacterTypeConverter.fromCharType(characterDesc.getCharType()), number);
		}
	}

	@Override
	public String getAttributeAsString(int itemNumber, int characterNumber) {
		synchronized (_vop) {
			int itemId = _vop.getDeltaMaster().uniIdFromItemNo(itemNumber);
			VOItemDesc itemDesc = (VOItemDesc) _vop.getDescFromId(itemId);
			
			int charId = _vop.getDeltaMaster().uniIdFromCharNo(characterNumber);			
			return itemDesc.readAttributeAsText(charId, TextType.UTF8, 1);
		}
		
	}
	
	@Override
	public String getName() {
		return _vop.getFilename();
	}
	
	/**
	 * Doesn't do anything - the name is always the filename of the slot file.
	 */
	@Override
	public void setName(String name) {}

	@Override
	public int getNumberOfCharacters() {
		
		synchronized (_vop) {
			if (_vop.getDeltaMaster() == null) {
				return 0;
			}
			return _vop.getDeltaMaster().getNChars();
		}
		
	}

	@Override
	public int getMaximumNumberOfItems() {
		synchronized (_vop) {
			if (_vop.getDeltaMaster() == null) {
				return 0;
			}
			return _vop.getDeltaMaster().getNItems();
		}
	}
	
	/**
	 * Once there are no more observers of this data set, close the underlying VOP object (which
	 * will close files associated with the VOP).
	 */
	@Override
	public void close() {
		if (_observerList.isEmpty()) {
			_vop.close();
		}
	}
	
	@Override
	protected Character doAddCharacter(int characterNumber, CharacterType type) {
		synchronized (_vop) {
			
			return _factory.createCharacter(type, characterNumber);
		}
	}
	
	@Override
	protected Item doAddItem(int itemNumber) {
		synchronized (_vop) {
			Item item = _factory.createItem(itemNumber);
			return item;
		}
	}
	
	@Override
	protected Item doAddVariantItem(int parentItemNumber, int itemNumber) {
		synchronized (_vop) {
			return _factory.createVariantItem(getItem(parentItemNumber), itemNumber);
		}
	}

	@Override
	public boolean isModified() {
		synchronized (_vop) {
			return _vop.isDirty();
		}
	}

	@Override
	public void deleteItem(Item item) {
		synchronized (_vop) {
			int itemNumber = item.getItemNumber();
			int itemId = _vop.getDeltaMaster().uniIdFromItemNo(itemNumber);
			VOItemDesc itemDesc = (VOItemDesc)_vop.getDescFromId(itemId);
			if (itemDesc == null) {
				return;
			}
			
			List<Image> images = item.getImages();
			for (Image image : images) {
				item.deleteImage(image);
			}
			
			// TODO delete from directive files....
		
			if (_vop.getDeltaMaster().removeItem(itemId)) {
				_vop.deleteObject(itemDesc);
			}
			
			fireItemDeleted(item);
		}
	}
	
	@Override
	public void moveItem(Item item, int newItemNumber) {
		synchronized (_vop) {
			_vop.getDeltaMaster().moveItem(item.getItemNumber(), newItemNumber);	
			fireItemMoved(item, newItemNumber);
		}
	}

	@Override
	public void deleteCharacter(Character character) {
		throw new NotImplementedException();
	}

	@Override
	public void moveCharacter(Character character, int newCharacterNumber) {
		throw new NotImplementedException();
	}
}

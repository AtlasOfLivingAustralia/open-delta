package au.org.ala.delta.editor.slotfile.model;

import java.util.List;

import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.ImageType;
import au.org.ala.delta.editor.slotfile.TextType;
import au.org.ala.delta.editor.slotfile.VOAnyDesc;
import au.org.ala.delta.editor.slotfile.VOCharBaseDesc;
import au.org.ala.delta.editor.slotfile.VOImageDesc;
import au.org.ala.delta.editor.slotfile.VOItemDesc;
import au.org.ala.delta.model.AbstractObservableDataSet;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.Item;

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
			return _factory.createItem(number);	
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
			return _factory.createItem(itemNumber);
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
			
			List<Integer> imageIds = itemDesc.readImageList();
			for (int imageId : imageIds) {
				VOImageDesc imageDesc = (VOImageDesc)_vop.getDescFromId(imageId);
				if (imageDesc.getOwnerId() == itemId) {
					deleteImage(imageDesc.getUniId());
				}
			}
			
			// TODO delete from directive files....
		
			if (_vop.getDeltaMaster().removeItem(itemId)) {
				_vop.deleteObject(itemDesc);
			}
			
			fireItemDeleted(item);
		}
	}
	
	protected void deleteImage(int imageId) {
		if (imageId == VOAnyDesc.VOUID_NULL) {
			return;
		}
		
		VOImageDesc imageDesc = (VOImageDesc)_vop.getDescFromId(imageId);
		int imageType = imageDesc.getImageType();
		int ownerId = imageDesc.getOwnerId();
		if (ownerId != VOAnyDesc.VOUID_NULL) {
			
		    if (imageType == ImageType.IMAGE_TAXON) { // Should be owned by a TVOItemDesc
		        
		        VOItemDesc item = (VOItemDesc)_vop.getDescFromId(ownerId);
		        if (item != null) {   
		            item.deleteImage(imageId);
		        }
		    }
		    else if (imageType == ImageType.IMAGE_CHARACTER) { // Should be owned by a TVOCharBaseDesc
		 
		        VOCharBaseDesc charBase = (VOCharBaseDesc)_vop.getDescFromId(ownerId);
		        if (charBase != null) {           
		            charBase.deleteImage(imageId);
		        }
		    }
		}

	    // Finally, delete the descriptor from the VOP
		_vop.deleteObject(imageDesc);
	    // TODO fireImageDeleted();
	}	
}

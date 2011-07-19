package au.org.ala.delta.editor.slotfile.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.editor.slotfile.Attribute;
import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.TextType;
import au.org.ala.delta.editor.slotfile.VOCharBaseDesc;
import au.org.ala.delta.editor.slotfile.VODirFileDesc;
import au.org.ala.delta.editor.slotfile.VOCharBaseDesc.CharTextInfo;
import au.org.ala.delta.editor.slotfile.VODirFileDesc.DirFileFixedData;
import au.org.ala.delta.editor.slotfile.VOCharTextDesc;
import au.org.ala.delta.editor.slotfile.VOControllingDesc;
import au.org.ala.delta.editor.slotfile.VOImageDesc;
import au.org.ala.delta.editor.slotfile.VOImageInfoDesc;
import au.org.ala.delta.editor.slotfile.VOItemDesc;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;
import au.org.ala.delta.model.AbstractObservableDataSet;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.image.OverlayType;

/**
 * Implementation of a DELTA DataSet that uses the SlotFile class to read data on demand 
 * from disk rather than storing it in memory.
 */
public class SlotFileDataSet extends AbstractObservableDataSet {

	private DeltaVOP _vop;

	public SlotFileDataSet(DeltaVOP vop, SlotFileDataSetFactory factory) {
		super(factory);
		_vop = vop;
	}
	
	public DeltaVOP getVOP() {
		return _vop;
	}
	
	private SlotFileDataSetFactory getFactory() {
		return (SlotFileDataSetFactory)_factory;
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
			if (number > getNumberOfCharacters()) {
				throw new IndexOutOfBoundsException("No such Character ("+number+">"+getNumberOfCharacters());
			}
			int charId = _vop.getDeltaMaster().uniIdFromCharNo(number);	
			VOCharBaseDesc characterDesc = (VOCharBaseDesc)_vop.getDescFromId(charId);
			return getFactory().wrapCharacter(characterDesc, number);
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
			
			// Remove any references to the item occurring in the directives "files".
		    int dirFileCount = _vop.getDeltaMaster().getNDirFiles();
			for (int i = 1; i <= dirFileCount; ++i) {
			    VODirFileDesc dirFile = (VODirFileDesc)_vop.getDescFromId(_vop.getDeltaMaster().uniIdFromDirFileNo(i));
			    dirFile.deleteItem(_vop, itemId);
		    }
		
			if (_vop.getDeltaMaster().removeItem(itemId)) {
				_vop.deleteObject(itemDesc);
			}
			
			fireItemDeleted(item);
		}
	}
	
	@Override
	public void moveItem(Item item, int newItemNumber) {
		synchronized (_vop) {
			int oldNumber = item.getItemNumber();
			_vop.getDeltaMaster().moveItem(oldNumber, newItemNumber);	
			fireItemMoved(item, oldNumber);
		}
	}

	/**
	 * Deletes the supplied character from the SlotFile.
	 * Deletion of a character requires a bit of activity.
	 *
	 * First we will need to delete any item attributes which involve
	 * references to this character, probably after first getting some sort
	 * of confirmation from the user that they really wish to proceed.
	 * That confirmation is assumed to be handled elsewhere.
	 *
	 * Second, we must also delete reference to the character occurring in any of the
	 * internal directives "files"
	 *
	 * Then we also need to delete any "controlling attributes" based on this character,
	 * and update those "controlling attributes" which controlled this character.
	 *
	 * And finally we need to delete all the text descriptors "owned" by this character
	 *
	 * When all this is done, then we can finally delete the character itself.
	 * @param character the character to delete.
	 */
	@Override
	public void deleteCharacter(Character character) {
		synchronized (_vop) {
			int characterNumber = character.getCharacterId();
			int characterId = _vop.getDeltaMaster().uniIdFromCharNo(characterNumber);
			VOCharBaseDesc charDesc = (VOCharBaseDesc)_vop.getDescFromId(characterId);
			
			
			List<Integer> itemList = getEncodedItems(charDesc, VOCharBaseDesc.STATEID_NULL);
		    for (int id : itemList) {
		    	VOItemDesc itemDesc = (VOItemDesc)_vop.getDescFromId(id);
		        itemDesc.deleteAttribute(charDesc.getUniId());
		    }

		    // Delete this character from all directives "files"....
		    int dirFileCount = _vop.getDeltaMaster().getNDirFiles();
		    for (int i = 1; i <= dirFileCount; ++i) {
		        VODirFileDesc dirFile = (VODirFileDesc)_vop.getDescFromId(_vop.getDeltaMaster().uniIdFromDirFileNo(i));
		        dirFile.deleteChar(_vop, characterId);
		    }
		      
		    // Delete controlling attributes based on this character...
		    List<Integer> contAttrs = charDesc.readDependentContAttrs();

		    for (int id : contAttrs) {
		        // Do a bit of consistency checking by making sure the controlling attribute had
		        // this character as its "owner".
		        VOControllingDesc aContDesc = (VOControllingDesc)_vop.getDescFromId(id);
		        if (aContDesc != null && aContDesc.getCharId() == id) {
		            deleteControlling(aContDesc.getUniId());
		        }
		        else {
		            throw new IllegalStateException();
		        }
		    }
		    // Update controlling attributes which controlled this character
		    contAttrs = charDesc.readControllingInfo();
		    for (int id : contAttrs) {
		        removeDependency((VOControllingDesc)_vop.getDescFromId(id), charDesc.getUniId());
		    }
		    // Then we need to delete all the text descriptors "owned" by this character
		    List<CharTextInfo> allText = charDesc.readCharTextInfo();
		    for (CharTextInfo info : allText) {
		        VOCharTextDesc charText = (VOCharTextDesc)_vop.getDescFromId(info.charDesc);
		        if (charText.getCharBaseId() == characterId) { // Just being paranoid...
		           _vop.deleteObject(charText);
		        }
		    }

		    // Now delete any associate image descriptors.
		    List<Image> images = character.getImages();
		    for (Image image : images) {
			    character.deleteImage(image);
			}

		    // Next remove the character from the master list
		    _vop.getDeltaMaster().removeCharacter(characterId);
		    fireCharacterDeleted(character);
		}
	}
	
	// Given a character, fills the vector with the IDs of all items for which an
	// attribute has been encoded for the character (if stateId == STATEID_NULL)
	// or for the state within the character.
	// Returns "true" if vector is non-empty.
	private List<Integer> getEncodedItems (VOCharBaseDesc charBase, int stateId) {
	    List<Integer> itemIds = new ArrayList<Integer>();
			 
		int charId = charBase.getUniId();
		for (int i = 1; i <= getMaximumNumberOfItems(); ++i) {
	        int itemUniId = _vop.getDeltaMaster().uniIdFromItemNo(i);
			VOItemDesc item = (VOItemDesc)_vop.getDescFromId(itemUniId);
			if (item.hasAttribute(charId)) {
			    if (stateId != VOCharBaseDesc.STATEID_NULL) {
			        Attribute attr = item.readAttribute(charId);
			        if (!attr.encodesState(charBase, stateId, true)) {
			            continue;
			        }
			     }
			     itemIds.add(itemUniId);
			 }
	    }
		return itemIds;
	}

	@Override
	public void moveCharacter(Character character, int newCharacterNumber) {
		synchronized (_vop) {
			int oldNumber = character.getCharacterId();
			_vop.getDeltaMaster().moveCharacter(oldNumber, newCharacterNumber);	
			character.setCharacterNumber(newCharacterNumber);
			fireCharacterMoved(character, oldNumber);
		}
	}
	
	@Override
	public void deleteCharacterDependency(CharacterDependency characterDependency) {
		VOControllingAdapter controlling = (VOControllingAdapter)characterDependency.getImpl();
		deleteControlling(controlling.getId());
	}
	
	@Override
	public ImageSettings getImageSettings() {
		VOImageInfoDesc imageInfo = _vop.getImageInfo();
		if (imageInfo == null) {
			return null;
		}
		ImageSettings settings = new ImageSettings();
		ImageSettingsHelper.copyToImageSettings(imageInfo, settings);
		return settings;
	}
	
	@Override
	public void setImageSettings(ImageSettings imageSettings) {
		VOImageInfoDesc imageInfo = _vop.getImageInfo();
		ImageSettingsHelper.copyFromImageSettings(imageInfo, imageSettings);
	}
	
	private boolean deleteControlling(int ctlId) {
			 
		// Get a pointer to the controlling attribute's descriptor, for general use.
		VOControllingDesc ctlBase = (VOControllingDesc)_vop.getDescFromId(ctlId);

		// Next, any characters being controlled by this character will need to have
		// their controlling information updated. That is, the dependencies will need to
		// be removed.

		int charId = ctlBase.getCharId();
		List<Integer> ctlVector = ctlBase.readControlledChars();
	    for (int id : ctlVector) {
	        removeDependency(ctlBase, id);
	    }
			
	    if (ctlBase.getNControlled() != 0)  { // Didn't successfully remove all dependencies!!!
		    throw new RuntimeException("Failed to delete all dependencies");
	    }

		// Then remove the attribute from the list of attributes "owned" by it's character
		VOCharBaseDesc charBase = (VOCharBaseDesc)_vop.getDescFromId(charId);
			  
	    charBase.removeDependentContAttr(ctlId);

		// Then remove the controlling attribute from the master list
		if (_vop.getDeltaMaster().removeContAttr(ctlId)) {
			// Finally, delete the descriptor from the VOP
	        _vop.deleteObject(ctlBase);

			 return true;
	    }
		return false;
	}
	
	// Deleting a state is actually a fairly complicated operation.
    // First we should check to be sure the state is not in use, either
    // in an item description or in a "controlling attribute" and allow for
    // user interaction to correct potential problems. For now, we assume this
    // will be handled elsewhere. However, we will look for "controlling attributes"
    // which use this state, and adjust or remove them.

    // This also needs to be extended to delete any reference to the state from
    // item descriptions, from internal directives "files", and from image overlays!!!
    protected void doDeleteState(MultiStateCharacter character, int stateNumber) {
    	
    	VOCharBaseDesc charDesc = ((VOCharacterAdaptor)character.getImpl()).getCharBaseDesc();
    	int stateId = charDesc.uniIdFromStateNo(stateNumber);
    	int charId = charDesc.getUniId();
    	// Find all items that encode this state, and turn the state "off".
        // This should actually be done by the user before reaching this stage, but we
        // do it here anywhere to guarantee consistency. However, at this stage we would
        // have to second-guess the user about whether any leading or trailing comments
        // should also be deleted....
        List<Integer> itemVect = getEncodedItems(charDesc, stateId);
        if (itemVect.size() > 0) {
            for (int id : itemVect) {
            	VOItemDesc itemDesc = (VOItemDesc)_vop.getDescFromId(id);
                deleteStateFromAttribute(itemDesc, charDesc, stateId);
            }
          }

        // Next, make sure that all references to this state are removed from the directives "files"
        // (The only non-internal directive which currently uses state id is the KEY STATES directive...)
        int dirFileCount = _vop.getDeltaMaster().getNDirFiles();
		for (int i = 1; i <= dirFileCount; ++i) {
			VODirFileDesc dirFile = (VODirFileDesc)_vop.getDescFromId(_vop.getDeltaMaster().uniIdFromDirFileNo(i));
            dirFile.deleteState(_vop, charDesc, stateId);
        }

        // Now delete any associated image overlays.
        List<Integer> imageList = charDesc.readImageList();
        for (int id : imageList) {
            VOImageDesc image = (VOImageDesc)_vop.getDescFromId(id);
            if (image.getOwnerId() == charId) {
                List<ImageOverlay> overlays = image.readAllOverlays();
                for (ImageOverlay overlay : overlays) {
                    if (overlay.isType(OverlayType.OLSTATE) && overlay.stateId == stateId) {
                        deleteImageOverlay(image, overlay.getId(), OverlayType.OLSTATE);
                    }
                }
            }
        }     

        // Should now be possible to use vector of controlled attributes,
        // rather than scan thru the whole map....
        // Check how much of this has already been handled by the descriptor....
    	List<Integer> contAttrVector = charDesc.readDependentContAttrs();
    	for (int id : contAttrVector) {
    	  
    	    VOControllingDesc ctlDesc = (VOControllingDesc)getVOP().getDescFromId(id);
    	    if (compare(charId, stateId, ctlDesc)) {
    	        List<Integer> ctlStates = ctlDesc.readStateIds();
    	        ctlStates.remove((Integer)stateId);
 
    	        changeControllingStates(ctlDesc, ctlStates);
    	    }
    	}
    	if (!charDesc.deleteState(stateId, getVOP())) {
    	    throw new RuntimeException("Unable to delete state: "+stateNumber+" from Character: "+character.getDescription());
    	}
    }
    
    private void deleteImageOverlay(VOImageDesc desc, int overlayId, int overlayType) {
    	if (overlayType == OverlayType.OLHOTSPOT) {
    		desc.removeLocation(overlayId);
    	}
    	else {
    		desc.removeOverlay(overlayId);
    	}
    }
    
    private void deleteStateFromAttribute(VOItemDesc itemDesc, VOCharBaseDesc charBaseDesc, int stateId) {
    	
    	if (itemDesc == null || charBaseDesc == null) {
    	    return;
    	}
    	// Use only for multistate characters.
    	int charId = charBaseDesc.getUniId();
    	Attribute attr = itemDesc.readAttribute(charId);
    	if (attr != null) {
    	    attr.deleteState(charBaseDesc, stateId);
    	}
    	itemDesc.writeAttribute(attr);
    }
   
    
	private void changeControllingStates(VOControllingDesc controlling, List<Integer> stateIds) {
    	int attrId = controlling.getUniId();
  
    	if (stateIds.size() == 0) {
    	    deleteControlling(attrId);
    	}

    	int attrNo = getVOP().getDeltaMaster().attrNoFromUniId(attrId);
    	if (attrNo > 0) {
    	    List<Integer> oldStateIds = controlling.readStateIds();
    	    Collections.sort(stateIds);
    	    
    	    if (!stateIds.equals(oldStateIds)) {
    	        controlling.writeStateIds(stateIds);
    	    }
    	}
    }
    
    private void removeDependency(VOControllingDesc controlling, int charId) {
    	removeDependency(controlling, (VOCharBaseDesc)_vop.getDescFromId(charId));
    }
    
    private void removeDependency(VOControllingDesc controlling, VOCharBaseDesc charBase) {
    	int charId = charBase.getUniId();
    	
    	int attrId = controlling.getUniId();
    	 
    	controlling.removeControlledChar(charId);
    	  
    	charBase.removeControllingInfo(attrId);
    }
    
    private boolean compare(int testCharId, int testStateId, VOControllingDesc testDesc) {
    	if (testDesc != null && testDesc.getCharId() == testCharId) {
          List<Integer> allStates = testDesc.readStateIds();
          return allStates.contains(testStateId);
        }
      else
        return false;
    }
    
    
    /**
     * MultiState types can be changed into other MultiState types.
     * MultiState types can only be changed to a non-MultiState type if the only attribute values that
     * have been coded are comments.
     * @param character the Character to be changed.
     * @param newType the type to change the Character to.
     * @return true if the changeCharacterType operation will succeed.
     */
    private boolean canChangeMultiStateCharacterType(MultiStateCharacter character, CharacterType newType) {
    	if (!newType.isMultistate()) {
	    	for (int i=1; i<=getMaximumNumberOfItems(); i++) {
	    		Item item = doGetItem(i);
	    		MultiStateAttribute attribute = (MultiStateAttribute)item.getAttribute(character);
	    		if (attribute != null && attribute.getPresentStates().size() > 0) {
	    			return false;
	    		}
	    	}
    	}
    	return true;
    }
    
    /**
     * Numeric types can be changed into other Numeric types.
     * Numeric types can only be changed to a non-numeric type if the only attribute values that
     * have been coded are comments.
     * @param character the Character to be changed.
     * @param newType the type to change the Character to.
     * @return true if the changeCharacterType operation will succeed.
     */
    private boolean canChangeNumericCharacterType(NumericCharacter<?> character, CharacterType newType) {
    	if (!newType.isNumeric()) {
    		for (int i=1; i<=getMaximumNumberOfItems(); i++) {
	    		Item item = doGetItem(i);
	    		if (item.hasAttribute(character)) {
	    			if (!item.getAttribute(character).isComment()) {
	    				return false;
	    			}
	    		}
	    	}
    	}
    	return true;
    }
    
    @Override
    public boolean canChangeCharacterType(Character character, CharacterType newType) {
    	List<Item> items = getUncodedItems(character);
    	if (items.size() == getMaximumNumberOfItems()) {
    		return true;
    	}
    	
    	if (character.getCharacterType().isMultistate()) {
    		return canChangeMultiStateCharacterType((MultiStateCharacter)character, newType);
    	}
    	else if (character.getCharacterType().isNumeric()) {
    		return canChangeNumericCharacterType((NumericCharacter<?>)character, newType);
    	}
    	
    	return true;
    }
    
    /**
     * The SlotFile VOCharBaseDesc only requires minor modification when changing character
     * type, however a new instance of the model Character class needs to be created of
     * the correct type.
     */
	@Override
	public Character changeCharacterType(Character character, CharacterType newType) {
		if (!canChangeCharacterType(character, newType)) {
			throw new IllegalArgumentException("Cannot change Character type from :"
					+character.getCharacterType()+ " to "+newType);
		}
		VOCharacterAdaptor impl = (VOCharacterAdaptor)character.getImpl();
		if (character.getCharacterType().isMultistate() && !newType.isMultistate()) {
			MultiStateCharacter multiStateChar = (MultiStateCharacter)character;
			while (multiStateChar.getNumberOfStates() > 0) {
				deleteState(multiStateChar, 1);
			}
		}
		impl.setCharacterType(newType);
		VOCharBaseDesc charBaseDesc = impl.getCharBaseDesc();
		Character newCharacter = getFactory().wrapCharacter(charBaseDesc, character.getCharacterId());
		characterChanged(character);
		
		return newCharacter;
	}

    @Override
    public au.org.ala.delta.model.Attribute addAttribute(int itemNumber, int characterNumber) {
        Item item = getItem(itemNumber);
        Character character = getCharacter(characterNumber);
        au.org.ala.delta.model.Attribute attribute = _factory.createAttribute(character, item);
        item.addAttribute(character, attribute);
        return attribute;
    }

	@Override
	public CharacterDependency addCharacterDependency(
			MultiStateCharacter owningCharacter, Set<Integer> states,
			Set<Integer> dependentCharacters) {
		return _factory.createCharacterDependency(owningCharacter, states, dependentCharacters);
	}
	
	
	public DirectiveFile addDirectiveFile(int fileNumber, String fileName, DirectiveType type) {
		VODirFileDesc.DirFileFixedData fixed = new VODirFileDesc.DirFileFixedData();
		VODirFileDesc dirFile = (VODirFileDesc)_vop.insertObject(fixed, DirFileFixedData.SIZE, null, 0, 0);
		
		dirFile.setFileName(fileName);
		int progType = 0;
		switch (type) {
		case CONFOR:
			progType = VODirFileDesc.PROGTYPE_CONFOR;
			break;
		case INTKEY:
			progType = VODirFileDesc.PROGTYPE_INTKEY;
			break;
		case DIST:
			progType = VODirFileDesc.PROGTYPE_DIST;
			break;
		case KEY:
			progType = VODirFileDesc.PROGTYPE_KEY;
			break;
		}
		dirFile.setProgType((short)progType);
		
		_vop.getDeltaMaster().InsertDirFile(dirFile.getUniId(), fileNumber);
		
		return new DirectiveFile(dirFile);
	}
	
	public int getDirectiveFileCount() {
		return _vop.getDeltaMaster().getNDirFiles();
	}
	
	public DirectiveFile getDirectiveFile(int fileNumber) {
		if (fileNumber <= 0 || fileNumber > getDirectiveFileCount()) {
			throw new IllegalArgumentException("No such file: "+fileNumber);
		}
		int id = _vop.getDeltaMaster().uniIdFromDirFileNo(fileNumber);
		VODirFileDesc dirFile = (VODirFileDesc)_vop.getDescFromId(id);
		
		return new DirectiveFile(dirFile);
	}
}

package au.org.ala.delta.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.impl.ControllingInfo;
import au.org.ala.delta.model.impl.ControllingInfo.ControlledStateType;
import au.org.ala.delta.model.observer.CharacterObserver;
import au.org.ala.delta.model.observer.DeltaDataSetChangeEvent;
import au.org.ala.delta.model.observer.DeltaDataSetObserver;
import au.org.ala.delta.model.observer.ItemObserver;
import au.org.ala.delta.rtf.RTFUtils;

/**
 * An implementation of the DeltaDataSet interface designed to support notification of changes to the
 * data set.  
 * Subclasses should implement the "doXXX" methods where they exist instead of overriding the
 * equivalent interface method.
 */
public abstract class AbstractObservableDataSet implements ObservableDeltaDataSet, ItemObserver, CharacterObserver {

	/** Maintains a list of objects interested in being notified of changes to this data set */
	protected List<DeltaDataSetObserver> _observerList = new ArrayList<DeltaDataSetObserver>();

	/** Used to create elements of the data set */
	protected DeltaDataSetFactory _factory;
	
	public AbstractObservableDataSet(DeltaDataSetFactory factory) {
		_factory = factory;
	}
	
	/**
	 * Adds this AbstractObservableDataSet as an observer of the character.  The actual work
	 * of getting the Character needs to be implemented in doGetCharacter.
	 */
	@Override
	public Character getCharacter(int number) {
		
		Character character = doGetCharacter(number);
		if (character != null) {
			character.addCharacterObserver(this);
		}
		return character;
	}
	protected abstract Character doGetCharacter(int number);
	
	/**
	 * Adds this AbstractObservableDataSet as an observer of the item.  The actual work of getting
	 * the Item needs to be done in doGetItem.
	 */
	@Override
	public Item getItem(int number) {
		Item item = doGetItem(number);
		item.addItemObserver(this);
		return item;
	}
	protected abstract Item doGetItem(int number);
	
	
	/**
	 * Convenience method to add the new character using the next available character number.
	 * @param type the type of character to create and add.
	 */
	@Override
	public Character addCharacter(CharacterType type) {
		return addCharacter(getNumberOfCharacters()+1, type);
	}
	
	/**
	 * Handles notification of the new Character.  The actual work of adding the Character needs
	 * to be implemented in doAddCharacter.
	 */
	@Override
	public Character addCharacter(int characterNumber, CharacterType type) {
		Character character = doAddCharacter(characterNumber, type);
		character.addCharacterObserver(this);
		fireCharacterAdded(character);
		return character;
	}
	protected abstract Character doAddCharacter(int characterNumber, CharacterType type);
	
	/**
	 * Convenience method to add the new Item using the next available item number.
	 */
	@Override
	public Item addItem() {
		return addItem(getMaximumNumberOfItems()+1);
	}
	
	/**
	 * Handles notification of the new Item.  The actual work of adding the Item needs
	 * to be implemented in doAddItem.
	 */
	@Override
	public Item addItem(int itemNumber) {
		Item item = doAddItem(itemNumber);
		item.addItemObserver(this);
		fireItemAdded(item);
		return item;
	}
	protected abstract Item doAddItem(int itemNumber);
	
	public Item addVariantItem(int parentItemNumber, int itemNumber) {
		Item item = doAddVariantItem(parentItemNumber, itemNumber);
		item.addItemObserver(this);
		fireItemAdded(item);
		return item;
	}
	protected abstract Item doAddVariantItem(int parentItemNumber, int itemNumber);
	
	@Override
	public Attribute getAttribute(int itemNumber, int characterNumber) {
		return getItem(itemNumber).getAttribute(getCharacter(characterNumber));
	}
	
	@Override
	public void deleteState(MultiStateCharacter character, int stateNumber) {
		doDeleteState(character, stateNumber);
	
		characterChanged(character);
	}
	
	protected abstract void doDeleteState(MultiStateCharacter character, int stateNumber);
	
	@Override
	public List<Item> getUncodedItems(Character character) {
		List<Item> uncodedItems = new ArrayList<Item>();
		if (character.getCharacterType().isMultistate() &&
			((MultiStateCharacter)character).getUncodedImplicitState() > 0) {
			return uncodedItems;
		}
		for (int i=1; i<=getMaximumNumberOfItems(); i++) {
			Item item = getItem(i);
			if (!item.hasAttribute(character) && 
				!character.checkApplicability(item).isInapplicable()) {
				uncodedItems.add(item);
			}
		}
		return uncodedItems;
	}
	
	@Override
	public List<Item> getItemsWithMultipleStatesCoded(MultiStateCharacter character) {
		List<Item> items = new ArrayList<Item>();
		for (int i=1; i<=getMaximumNumberOfItems(); i++) {
			Item item = getItem(i);
			MultiStateAttribute attribute = (MultiStateAttribute)item.getAttribute(character);
			if (attribute == null) {
				continue;
			}
			Set<Integer> states = attribute.getPresentStates();
			if (states.size() > 1 || attribute.isVariable()) {
				items.add(item);
			}
		}
		return items;
	}
	
	
	
	@Override
	public List<CharacterDependency> getAllCharacterDependencies() {
		List<CharacterDependency> characterDependencies = new ArrayList<CharacterDependency>();
		for (int i=1; i<getNumberOfCharacters(); i++) {
			Character character = doGetCharacter(i);
			if (character.getCharacterType().isMultistate()) {
				characterDependencies.addAll(((MultiStateCharacter)character).getDependentCharacters());
			}
		}
		return characterDependencies;
	}
	
	@Override
	public Item itemForDescription(String description) {
		
		String strippedDescription = RTFUtils.stripFormatting(description);
		for (int i=1; i<=getMaximumNumberOfItems(); i++) {
			Item item = getItem(i);
			
			if (strippedDescription.equals(RTFUtils.stripFormatting(item.getDescription()))) {
				return item;
			}
		}
		return null;
	}
	
	@Override
	public ControllingInfo checkApplicability(Character character, Item item) {
		
		return checkApplicability(character, item, character, 0, new ArrayList<Integer>());
	}
	
	private boolean getControlledChars(List<Integer> testedControlling, MultiStateCharacter charBase, List<Integer> contChars, boolean includeIndirect, int baseId) {
		// We maintain a list of "controlling" characters that have been (or, rather, are being)
		// tested. This can prevent the infinite recursion which can otherwise result if "circular" dependencies somehow are formed.

		if (testedControlling.contains(charBase.getCharacterId())) {
			return false;
		} else {
			testedControlling.add(charBase.getCharacterId());
			List<CharacterDependency> contAttrVector = charBase.getDependentCharacters();
			if (contAttrVector.size() > 0) {
				// / Loop though all the controlling attributes "owned" by this character
				for (CharacterDependency iter : contAttrVector) {
					Set<Integer> controlledChars = iter.getDependentCharacterIds();
					contChars.addAll(controlledChars);
				}
				if (contChars.contains(charBase.getCharacterId()) || contChars.contains(baseId)) {
					throw new CircularDependencyException();
				}
				if (includeIndirect) {
					// OK. We now have a list of all characters DIRECTLY controlled
					// by this one. We should add all those INDIRECTLY controlled as well.
					List<Integer> contDirect = new ArrayList<Integer>(contChars);

					for (Integer i : contDirect) {
						List<Integer> contIndirect = new ArrayList<Integer>();
						Character indirCharBase = getCharacter(i);
						if (indirCharBase.getCharacterType().isMultistate()) {
							if (getControlledChars(testedControlling, (MultiStateCharacter)indirCharBase, contIndirect, true, baseId)) {
								if (contIndirect.contains(baseId)) {
									throw new CircularDependencyException();
								}
								contChars.addAll(contIndirect);
							}
						}
					}
				}
			}
			return !contChars.isEmpty();
		}
	}

	protected ControllingInfo checkApplicability(Character baseChar, Item item, Character charBase, int recurseLevel, List<Integer> testedControlledChars) {

		int dependencyCount = getAllCharacterDependencies().size();
		List<CharacterDependency> controlling = charBase.getControllingCharacters();
		int controllingId = 0;
		if (item == null || charBase == null || controlling.isEmpty()) {
			return new ControllingInfo();
		}

		boolean unknownOk = false;
		ControllingInfo maybeInapplicable = null;
		
		if (controlling != null && controlling.size() > 1) {
			// remove the null (if we are recursing) before we sort.
			while (controlling.contains(null)) {
				controlling.remove((Object)null);
			}
			Collections.sort(controlling);
		}

		List<Integer> controllingChars = new ArrayList<Integer>();
		SortedSet<Integer> controllingStates = new TreeSet<Integer>();
		List<Integer> newContStates = new ArrayList<Integer>();
		int testCharId = -1;

		controlling.add(null); // Append dummy value, to ease handling of the last element
		// Loop through all controlling attributes which directly control this character...
		for (CharacterDependency contAttrDesc : controlling) {
			int newContCharId = 0;
			if (contAttrDesc == null) {
				newContCharId = -1;
			} else {
				newContStates = new ArrayList<Integer>(contAttrDesc.getStatesAsList());
				Collections.sort(newContStates);
				newContCharId = contAttrDesc.getControllingCharacterId();
			}

			if (newContCharId == testCharId) {
				// / Build up all relevant controlling attributes under the control of a
				// / single controlling character, merging the state lists as we go....
				controllingStates.addAll(newContStates);
			} else {
				// Do checks when changing controlling state
				if (testCharId != -1) {
					Character testCharBase = getCharacter(testCharId);

					if (!testCharBase.getCharacterType().isMultistate()) {
						throw new RuntimeException("Controlling characters must be multistate!");
					}
					controllingId = testCharId;
					MultiStateCharacter multiStateChar = (MultiStateCharacter)testCharBase;
					// If the controlling character is coded, see whether it makes us inapplicable

					if (item.hasAttribute(testCharBase)) {
						MultiStateAttribute attrib = (MultiStateAttribute)item.getAttribute(multiStateChar);
						Set<Integer> codedStates = attrib.getPresentStates();

						// If controlling character is "variable", we are NOT controlled
						//if ((pseudoValues[0] & VOItemDesc.PSEUDO_VARIABLE) == 0) {
						if (!attrib.isVariable()) {
							if (codedStates.isEmpty()) {
								// If there are no states for the controlling character,
								// but it is explicitly coded with the "unknown" pseudo-value,
								// allow the controlled character to also be unknown.
								if (attrib.isUnknown()) {
									unknownOk = true;
								} else {
									return new ControllingInfo(ControlledStateType.Inapplicable, controllingId);
								}
							} else if (controllingStates.containsAll(codedStates)) {
								return new ControllingInfo(ControlledStateType.Inapplicable, controllingId);
							}
							else {
								for (int state : codedStates) {
									if (controllingStates.contains(state)) {
										maybeInapplicable = new ControllingInfo(ControlledStateType.MaybeInapplicable, controllingId);
									}
								}
							}
						}
					} else if (multiStateChar.getUncodedImplicitState() > 0) {
						// if the controlling character is not encoded, see if there is an implicit value for it
						if (controllingStates.contains(multiStateChar.getUncodedImplicitState())) {
							return new ControllingInfo(ControlledStateType.Inapplicable, controllingId);
						}
					} else {
						return new ControllingInfo(ControlledStateType.InapplicableOrUnknown, controllingId);
						// /// This should probably be handled as a somewhat special case,
						// /// so the user can be pointed in the right direction
					}
				}
				controllingId = -1;
				testCharId = newContCharId;
				if (testCharId != -1) {
					controllingChars.add(testCharId);
				}
				controllingStates.clear();
				controllingStates.addAll(newContStates);
			}
		}

		// Up to this point, nothing has made this character inapplicable.
		// But it is possible that one of the controlling characters has itself
		// been made inapplicable.

		// Is this check really necessary? I suppose it is, but it slows things down...
		for (int j : controllingChars) {

			if (++recurseLevel >= dependencyCount) {
				try {
					List<Integer> contChars = new ArrayList<Integer>();
					if (baseChar.getCharacterType().isMultistate()) {
						getControlledChars(testedControlledChars, (MultiStateCharacter)baseChar, contChars, true, 0);
					}
				} catch (CircularDependencyException ex) {
					return new ControllingInfo(ControlledStateType.Inapplicable, controllingId);
				}
			}
			Character testCharBase = getCharacter(j);
			ControllingInfo info = checkApplicability(baseChar, item, testCharBase, recurseLevel, testedControlledChars);
			if (info.isInapplicable()) {
				return info;
			}
			else if (info.isMaybeInapplicable()) {
				maybeInapplicable = info;
			}
		}
		ControllingInfo result;
		if (maybeInapplicable != null) {
			result = maybeInapplicable;
		}
		else {
			result = unknownOk ? new ControllingInfo(ControlledStateType.InapplicableOrUnknown, controllingId) : new ControllingInfo();

		}
		return result;
	}

	
	/**
	 * Adds an observer interested in receiving notification of changes to this data set.
	 * Duplicate observers are ignored.
	 * @param observer the observer to add.
	 */
	public void addDeltaDataSetObserver(DeltaDataSetObserver observer) {
		if (_observerList.contains(observer)) {
			return;
		}
		_observerList.add(observer);
	}

	/**
	 * Prevents an observer from receiving further notifications of changes to this data set.
	 * @param observer the observer to remove.
	 */
	public void removeDeltaDataSetObserver(DeltaDataSetObserver observer) {
		_observerList.remove(observer);
	}
	
	protected void fireDeltaDataSetEvent(Item item, Character character, DataSetEventDispatcher dispatcher) {
		dispatcher.fireDataSetEvent(item, character);
	}
	protected void fireDeltaDataSetEvent(Item item, Character character, Object extraInformation, DataSetEventDispatcher dispatcher) {
		dispatcher.fireDataSetEvent(item, character, extraInformation);
	}
	protected void fireDeltaDataSetEvent(Image image, DataSetEventDispatcher dispatcher) {
		dispatcher.fireDataSetEvent(new DeltaDataSetChangeEvent(this, image));
	}
	
	protected void fireItemAdded(Item item) {
		fireDeltaDataSetEvent(item, null, new ItemAddedDispatcher());
	}
	
	protected void fireItemDeleted(Item item) {
		fireDeltaDataSetEvent(item, null, new ItemDeletedDispatcher());
	}
	
	protected void fireCharacterAdded(Character character) {
		fireDeltaDataSetEvent(null, character, new CharacterAddedDispatcher());
	}
	
	protected void fireCharacterDeleted(Character character) {
		fireDeltaDataSetEvent(null, character, new CharacterDeletedDispatcher());
	}
	
	protected void fireCharacterMoved(Character character, int oldCharacterNumber) {
		fireDeltaDataSetEvent(null, character, oldCharacterNumber, new CharacterMovedDispatcher());
	}
	
	protected void fireItemMoved(Item item, int oldItemNumber) {
		fireDeltaDataSetEvent(item, null, oldItemNumber, new ItemMovedDispatcher());
	}

	@Override
	public void characterChanged(Character character) {
		fireDeltaDataSetEvent(null, character, new CharacterEditedDispatcher());
	}
	
	@Override
	public void characterTypeChanged(Character oldCharacter, Character newCharacter) {
		fireDeltaDataSetEvent(null, oldCharacter, newCharacter, new CharacterChangedTypeDispatcher());
	}
		
	@Override
	public void characterStateChanged(Character character, int stateNum) {
		fireDeltaDataSetEvent(null, character, stateNum, new CharacterEditedDispatcher());
	}
	
	@Override
	public void imageChanged(Image image) {
		fireDeltaDataSetEvent(image, new ImageEditedDispatcher());
	}

	@Override
	public void itemChanged(Item item, Attribute attribute) {
		fireDeltaDataSetEvent(item, null, new ItemEditedDispatcher());
	}
	
	

	protected abstract class DataSetEventDispatcher {

		public void fireDataSetEvent(Item item, Character character, Object extraInformation) {

			DeltaDataSetChangeEvent dataSetChangeEvent = new DeltaDataSetChangeEvent(AbstractObservableDataSet.this,
					character, item, extraInformation);
			fireDataSetEvent(dataSetChangeEvent);	
		}
		
		public void fireDataSetEvent(Item item, Character character) {
			DeltaDataSetChangeEvent dataSetChangeEvent = new DeltaDataSetChangeEvent(AbstractObservableDataSet.this,
					character, item);
			fireDataSetEvent(dataSetChangeEvent);
		}
		
		protected void fireDataSetEvent(DeltaDataSetChangeEvent event) {
			// process in reverse order to support removal during processing.
			for (int i = _observerList.size() - 1; i >= 0; i--) {
				doFireEvent((DeltaDataSetObserver) _observerList.get(i), event);
			}
		}

		public abstract void doFireEvent(DeltaDataSetObserver observer, DeltaDataSetChangeEvent event);
	}

	protected class ItemAddedDispatcher extends DataSetEventDispatcher {
		@Override
		public void doFireEvent(DeltaDataSetObserver observer, DeltaDataSetChangeEvent event) {
			observer.itemAdded(event);
		}
	}

	protected class ItemEditedDispatcher extends DataSetEventDispatcher {
		@Override
		public void doFireEvent(DeltaDataSetObserver observer, DeltaDataSetChangeEvent event) {
			observer.itemEdited(event);
		}
	}
	protected class ItemMovedDispatcher extends DataSetEventDispatcher {
		@Override
		public void doFireEvent(DeltaDataSetObserver observer, DeltaDataSetChangeEvent event) {
			observer.itemMoved(event);
		}
	}
	
	protected class ItemDeletedDispatcher extends DataSetEventDispatcher {
		@Override
		public void doFireEvent(DeltaDataSetObserver observer, DeltaDataSetChangeEvent event) {
			observer.itemDeleted(event);
		}
	}

	protected class CharacterEditedDispatcher extends DataSetEventDispatcher {
		@Override
		public void doFireEvent(DeltaDataSetObserver observer, DeltaDataSetChangeEvent event) {
			observer.characterEdited(event);
		}
	}
	
	protected class ImageEditedDispatcher extends DataSetEventDispatcher {
		@Override
		public void doFireEvent(DeltaDataSetObserver observer, DeltaDataSetChangeEvent event) {
			observer.imageEdited(event);
		}
	}

	protected class CharacterAddedDispatcher extends DataSetEventDispatcher {
		@Override
		public void doFireEvent(DeltaDataSetObserver observer, DeltaDataSetChangeEvent event) {
			observer.characterAdded(event);
		}
	}
	
	protected class CharacterChangedTypeDispatcher extends DataSetEventDispatcher {
		@Override
		public void doFireEvent(DeltaDataSetObserver observer, DeltaDataSetChangeEvent event) {
			observer.characterTypeChanged(event);			
		}
	}
	
	protected class CharacterDeletedDispatcher extends DataSetEventDispatcher {
		@Override
		public void doFireEvent(DeltaDataSetObserver observer, DeltaDataSetChangeEvent event) {
			observer.characterDeleted(event);
		}
	}
	
	protected class CharacterMovedDispatcher extends DataSetEventDispatcher {
		@Override
		public void doFireEvent(DeltaDataSetObserver observer, DeltaDataSetChangeEvent event) {
			observer.characterMoved(event);
		}
	}
}

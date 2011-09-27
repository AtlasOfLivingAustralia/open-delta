package au.org.ala.delta.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.FloatRange;

/**
 * Handles transformations from the states / values defined in a DeltaDataSet
 * for use in identification keys (For example for use by the intkey 
 * program).
 */
public class IdentificationKeyCharacter {

	public static abstract class KeyState implements Comparable<KeyState>{
		int stateId;
		public abstract boolean isPresent(Number value);
		
		public int getStateNumber() {
			return stateId;
		}

		@Override
		public int compareTo(KeyState o) {
			if (o == null) {
				return 1;
			}
			else {
				if (stateId > o.stateId) {
					return 1;
				}
				else if (stateId == o.stateId) {
					return 0;
				}
				else {
					return -1;
				}
			}
		}
	}
	
	/**
	 * Represents a mapping from a set of states from the original 
	 * multistate character to a single new state defined by the KEY STATES
	 * directive.
	 */
	public static class MultiStateKeyState extends KeyState {
	
		Set<Integer> _originalStates;
		
		public MultiStateKeyState(int id, Collection<Integer> originalStates) {
			stateId = id;
			_originalStates = new HashSet<Integer>(originalStates);
		}
		
		public boolean isPresent(Number orignalState) {
			return _originalStates.contains(orignalState);
		}
		
		public List<Integer> originalStates() {
			List<Integer> states = new ArrayList<Integer>(_originalStates);
			Collections.sort(states);
			return states;
		}
	}
	
	/**
	 * Represents a mapping from a range of values of the original 
	 * real or integer character to a single new state defined by the KEY STATES
	 * directive.
	 */
	public static class NumericKeyState extends KeyState {
		
		FloatRange _stateRange;
		
		public NumericKeyState(int id, FloatRange range) {
			stateId = id;
			_stateRange = range;
		}
		
		public boolean isPresent(Number value) {
			return _stateRange.containsFloat(value);
		}
		
		public FloatRange stateRange() {
			return _stateRange;
		}
	}
	
	private Character _character;
	private List<KeyState> _states;
	private int _filteredCharacterNumber;
	
	public IdentificationKeyCharacter(Character character) {
		if (character == null) {
			throw new IllegalArgumentException("Null character invalid");
		}
		_character = character;
		_filteredCharacterNumber = character.getCharacterId();
		_states = new ArrayList<KeyState>();
	}
	
	public IdentificationKeyCharacter(int characterNumber, Character character) {
		this (character);
		_filteredCharacterNumber = characterNumber;
	}
	
	public void addState(int id, List<Integer> originalStates) {
		int numOriginalStates = ((MultiStateCharacter)_character).getNumberOfStates();
		
		for (int state : originalStates) {
			if (state > numOriginalStates) {
				throw new IllegalArgumentException("Invalid state: "+state+". Character "+
						_character.getCharacterId()+" has only "+numOriginalStates+" states");
			}
		}
		MultiStateKeyState state = new MultiStateKeyState(id, originalStates);
		_states.add(state);
		
	}
	
	public void addState(int id, FloatRange range) {
		NumericKeyState state = new NumericKeyState(id, range);
		_states.add(state);
		Collections.sort(_states);
	}
	
	public int getNumberOfStates() {
		if (_states.size() > 0) {
			return _states.size();
		}
		else {
			if (_character.getCharacterType().isMultistate()) {
				return ((MultiStateCharacter)_character).getNumberOfStates(); 
			}
			else {
				return 0;
			}
		}
	}
	
	public KeyState getKeyState(int stateNum) {
		if (stateNum <= 0 || stateNum > _states.size() ) {
			throw new IllegalArgumentException(stateNum+" must be between 0 and "+_states.size());
		}
		return _states.get(stateNum - 1);
	}
	
	public List<Integer> getPresentStates(MultiStateAttribute attribute) {
		
		List<Integer> states = new ArrayList<Integer>();
		if (attribute.isVariable()) {
			for (int i=1; i<=getNumberOfStates(); i++) {
				states.add(i);
			}
		}
		else {
			Set<Integer> originalStates = attribute.getPresentStates();
			if (_states.size() > 0) {
				for (int originalState : originalStates) {
					for (KeyState state : _states) {
						if (state.isPresent(originalState)) {
							states.add(state.getStateNumber());
						}
					}
				}
			}
			else {
				states.addAll(originalStates);
			}
		}
		return states;
	}

	public Integer getCharacterNumber() {
		return _character.getCharacterId();
	}
	
	public void setFilteredCharacterNumber(int characterNumber) {
		_filteredCharacterNumber = characterNumber;
	}
	
	public int getFilteredCharacterNumber() {
		return _filteredCharacterNumber;
	}
	
	public CharacterType getCharacterType() {
		return _character.getCharacterType();
	}
	
	public Character getCharacter() {
		return _character;
	}
	

	
	public List<KeyState> getStates() {
		return _states;
	}
}

package au.org.ala.delta.model;

import java.util.ArrayList;
import java.util.Collection;
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

	public abstract class KeyState {
		int stateId;
		public abstract boolean isPresent(Number value);
		
		public int getStateNumber() {
			return stateId;
		}
	}
	
	public class MultiStateKeyState extends KeyState {
	
		Set<Integer> _originalStates;
		
		public MultiStateKeyState(int id, Collection<Integer> originalStates) {
			stateId = id;
			_originalStates = new HashSet<Integer>(originalStates);
		}
		
		public boolean isPresent(Number orignalState) {
			return _originalStates.contains(orignalState);
		}
		
	}
	
	public class NumericKeyState extends KeyState {
		
		FloatRange _stateRange;
		
		public NumericKeyState(int id, FloatRange range) {
			stateId = id;
			_stateRange = range;
		}
		
		public boolean isPresent(Number value) {
			return _stateRange.containsFloat(value);
		}
	}
	
	private Character _character;
	private List<KeyState> _states;
	
	public IdentificationKeyCharacter(Character character) {
		_character = character;
		_states = new ArrayList<KeyState>();
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
	}
	
	public int getNumberOfStates() {
		if (_states.size() > 0) {
			return _states.size();
		}
		else {
			return ((MultiStateCharacter)_character).getNumberOfStates();
		}
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
	
	public CharacterType getCharacterType() {
		return _character.getCharacterType();
	}
	
	public Character getCharacter() {
		return _character;
	}
}

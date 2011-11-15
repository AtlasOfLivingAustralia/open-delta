package au.org.ala.delta.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.Range;

/**
 * Handles transformations from the states / values defined in a DeltaDataSet
 * for use in identification keys (For example for use by the intkey 
 * program).
 */
public class IdentificationKeyCharacter {

	public static abstract class KeyState implements Comparable<KeyState>{
		int stateId;
		public abstract boolean isPresent(Number value);
		public abstract boolean isPresent(Range range);
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
		
		public boolean isPresent(Range range) {
			return false;
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
		private BigDecimal _min;
		private BigDecimal _max;
		
		public NumericKeyState(int id, BigDecimal min, BigDecimal max) {
			stateId = id;
			_min = min;
			_max = max;
			_stateRange = new FloatRange(min.floatValue(), max.floatValue());
		}
		
		public boolean isPresent(Number value) {
			return _stateRange.containsFloat(value);
		}
		
		public boolean isPresent(Range range) {
			return _stateRange.overlapsRange(range);
		}
		
		public BigDecimal min() {
			return _min;
		}
		
		public BigDecimal max() {
			return _max;
		}
	}
	
	private Character _character;
	private boolean _useNormalValues;
	private boolean _useMeanValues;
	private List<KeyState> _states;
	private int _filteredCharacterNumber;
	
	public IdentificationKeyCharacter(Character character) {
		this(character, false);
	}
	
	public IdentificationKeyCharacter(Character character, boolean useNormalValues) {
		if (character == null) {
			throw new IllegalArgumentException("Null character invalid");
		}
		_character = character;
		_filteredCharacterNumber = character.getCharacterId();
		_states = new ArrayList<KeyState>();
		_useNormalValues = useNormalValues;
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
	
	public void addState(int id, BigDecimal min, BigDecimal max) {
		NumericKeyState state = new NumericKeyState(id, min, max);
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
		
		Set<Integer> states = new HashSet<Integer>();
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
		List<Integer> statesList = new ArrayList<Integer>(states);
		Collections.sort(statesList);
		return statesList;
	}

	public List<Integer> getPresentStates(NumericAttribute attribute) {
		Set<Integer> states = new HashSet<Integer>();
		if (attribute.isVariable()) {
			for (int i=1; i<=getNumberOfStates(); i++) {
				states.add(i);
			}
		}
		else if (!attribute.isUnknown() && !attribute.isInapplicable()) {
			for (KeyState state : _states) {
				List<NumericRange> values = attribute.getNumericValue();
				for (NumericRange value : values) {
					Range range = null;
					if (_useNormalValues) {
						range = value.getNormalRange();
					}
					else {
						range = value.getFullRange();
					}
					if (state.isPresent(range)) {
						states.add(state.getStateNumber());
					}
				}
			}
			
		}
		return new ArrayList<Integer>(states);
	}
	
	
	public int convertToKeyState(int originalState) {
		if (_states.isEmpty()) {
			return originalState;
		}
		else {
			for (KeyState state : _states) {
				if (state.isPresent(originalState)) {
					return state.stateId;
				}
			}
		}
		return -1;
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

	public List<CharacterDependency> getDependentCharacters() {
		if (!getCharacterType().isMultistate()) {
			throw new IllegalArgumentException("Only multistate characters can have dependent characters");
		}
		List<CharacterDependency> originalDependencies = _character.getDependentCharacters();
		if (_states.size() == 0) {
			return originalDependencies;
		}
		
		// Convert the dependencies to the new states.
		Map<Integer, Set<Integer>> dependencyPerState = new HashMap<Integer, Set<Integer>>();
		for (CharacterDependency dependency : originalDependencies) {
			for (int state : dependency.getStates()) {
				dependencyPerState.put(state, dependency.getDependentCharacterIds());
			}
		}
		
		
		Map<Set<Integer>, CharacterDependency> modifiedDependencies = new HashMap<Set<Integer>, CharacterDependency>(); 
		for (KeyState keyState : _states) {
			MultiStateKeyState msKeyState = (MultiStateKeyState)keyState;
			Set<Integer> dependentChars = new HashSet<Integer>();
			for (int originalState : msKeyState.originalStates()) {
				Set<Integer> tmpDependencies = dependencyPerState.get(originalState);
				if (tmpDependencies != null) {
					dependentChars.addAll(tmpDependencies);
				}
			}
			if (!dependentChars.isEmpty()) {
				if (modifiedDependencies.containsKey(dependentChars)) {
					modifiedDependencies.get(dependentChars).getStates().add(keyState.stateId);
				}
				else {
					modifiedDependencies.put(dependentChars, newDependency(keyState.stateId, dependentChars));
				}
			}
		}
		
		return new ArrayList<CharacterDependency>(modifiedDependencies.values());
	}
	
	private CharacterDependency newDependency(int state, Set<Integer> dependentChars) {
		DefaultDataSetFactory factory = new DefaultDataSetFactory();
		Set<Integer> states = new HashSet<Integer>();
		states.add(state);
		return factory.createCharacterDependency((MultiStateCharacter)_character, states, dependentChars);
	}

	public void setUseNormalValues(boolean b) {
		_useNormalValues = b;	
	}
	
	public void setUseMeanValues(boolean useMeanValues) {
		_useMeanValues = useMeanValues;
	}
}

package au.org.ala.delta.model.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * Default / in memory implementation of the CharacterDependencyData interface.
 */
public class DefaultCharacterDependencyData implements CharacterDependencyData {

	private int _controllingCharacterId;
	private Set<Integer> _dependentCharacterIds;
	private Set<Integer> _states = new HashSet<Integer>();
	private String _description;

	/**
	 * constructor
	 * 
	 * @param controllingCharacterId
	 *            id of the controlling character
	 * @param states
	 *            the ids of the states which when set on the controlling
	 *            character, make the dependent characters <b>inapplicable</b>
	 * @param dependentCharacterIds
	 *            ids of the dependent characters
	 */
	public DefaultCharacterDependencyData(int controllingCharacterId,
			Set<Integer> states, Set<Integer> dependentCharacterIds) {
		_controllingCharacterId = controllingCharacterId;
		_dependentCharacterIds = new HashSet<Integer>(dependentCharacterIds);
		_states = new HashSet<Integer>(states);
	}

	public void addDependentCharacter(au.org.ala.delta.model.Character character) {

	}

	public void removeDependentCharacter(
			au.org.ala.delta.model.Character character) {

	}

	/**
	 * @return the id of the controlling character
	 */
	@Override
	public int getControllingCharacterId() {
		return _controllingCharacterId;
	}

	/**
	 * @return the ids of the dependent characters
	 */
	@Override
	public Set<Integer> getDependentCharacterIds() {
		// return defensive copy
		return new HashSet<Integer>(_dependentCharacterIds);
	}

	/**
	 * @return the states which when set on the controlling character, make the
	 *         dependent characters <b>inapplicable</b>
	 */
	@Override
	public Set<Integer> getStates() {
		// return defensive copy
		return new HashSet<Integer>(_states);
	}
	
	

	@Override
	public void setStates(Set<Integer> states) {
		_states = new HashSet<Integer>(states);
	}

	/**
	 * @return a description of this CharacterDependency.
	 */
	@Override
	public String getDescription() {
		return _description;
	}

	/**
	 * Provides a description of this CharacterDependency.
	 * 
	 * @param description a description of this CharacterDependency.
	 */
	@Override
	public void setDescription(String description) {
		_description = description;
	}

	@Override
	public String toString() {
		String states = StringUtils.join(_states, ", ");
		return String.format("Char. %d controls chars. [%s] for states [%s]",
				_controllingCharacterId, _dependentCharacterIds, states);
	}

}

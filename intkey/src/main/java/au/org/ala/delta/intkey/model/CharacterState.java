package au.org.ala.delta.intkey.model;

import java.util.List;

public class CharacterState {
    private String _description;
    private List<Character> _dependentCharacters;
    
    public CharacterState(String description) {
        this._description = description;
    }

    public String getDescription() {
        return _description;
    }

    void setDescription(String description) {
        this._description = description;
    }

    public List<Character> getDependentCharacters() {
        return _dependentCharacters;
    }

    void setDependentCharacters(List<Character> dependentCharacters) {
        this._dependentCharacters = dependentCharacters;
    }
    
    
}

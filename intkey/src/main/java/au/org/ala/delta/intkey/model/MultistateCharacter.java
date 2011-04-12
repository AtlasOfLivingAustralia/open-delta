package au.org.ala.delta.intkey.model;

import java.util.ArrayList;
import java.util.List;

public class MultistateCharacter extends Character {
    private boolean _ordered;
    private List<CharacterState> _states;
    
    public MultistateCharacter(boolean isOrdered) {
        _ordered = isOrdered;
    }
    
    public List<CharacterState> getStates() {
        //return defensive copy
        return new ArrayList<CharacterState>(_states);
    }
    
    void setStates(List<CharacterState> states) {
        this._states = states;
    }

    public boolean isOrdered() {
        return _ordered;
    }

    void setOrdered(boolean ordered) {
        this._ordered = ordered;
    }
    
    
}

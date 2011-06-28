package au.org.ala.delta.intkey.ui;

import java.util.List;
import java.util.Map;

import au.org.ala.delta.model.Character;

public class BestCharacterListModel extends CharacterListModel {

    private Map<Character, Double> _separatingPowers;
    
    public BestCharacterListModel(List<Character> characters, Map<Character, Double> separatingPowers) {
        super(characters);
        _separatingPowers = separatingPowers;
    }
    
    @Override
    public Object getElementAt(int index) {
        Character ch = _characters.get(index);
        double separatingPower = _separatingPowers.get(ch);
        String charDescription = _formatter.formatCharacterDescription(ch);
        return String.format("%.2f    %s", separatingPower, charDescription);
    }

}

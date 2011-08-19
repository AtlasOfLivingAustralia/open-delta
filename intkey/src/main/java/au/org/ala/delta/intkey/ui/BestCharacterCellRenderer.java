package au.org.ala.delta.intkey.ui;

import java.util.HashMap;
import java.util.Map;

import au.org.ala.delta.model.Character;

public class BestCharacterCellRenderer extends CharacterCellRenderer {
    
    /**
     * 
     */
    private static final long serialVersionUID = 4185197144357517831L;

    private Map<Character, Double> _separatingPowers;
    
    public BestCharacterCellRenderer(Map<Character, Double> separatingPowers) {
        _separatingPowers = new HashMap<Character, Double>(separatingPowers);
    }

    @Override
    protected String getTextForValue(Object value) {
        if (value instanceof Character) {
            Character ch = (Character) value;
            double separatingPower = _separatingPowers.get(ch);
            String charDescription = _formatter.formatCharacterDescription(ch);
            return String.format("%.2f    %s", separatingPower, charDescription);
        } else {
            return value.toString();
        }
    }

}

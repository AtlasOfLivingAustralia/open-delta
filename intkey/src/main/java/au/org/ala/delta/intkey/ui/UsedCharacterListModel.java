package au.org.ala.delta.intkey.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

import au.org.ala.delta.intkey.model.specimen.CharacterValue;

//Used to display the values set for used characters in a JList
//TODO refactor CharacterValue stuff so that this becomes "AttributeListModel"

public class UsedCharacterListModel extends AbstractListModel {

    private List<CharacterValue> _values;

    public UsedCharacterListModel(List<CharacterValue> values) {
        _values = new ArrayList<CharacterValue>(values);
    }

    @Override
    public int getSize() {
        return _values.size();
    }

    @Override
    public Object getElementAt(int index) {
        return _values.get(index).toString();
    }

    public CharacterValue getCharacterValueAt(int index) {
        return _values.get(index);
    }

    public au.org.ala.delta.model.Character getCharacterAt(int index) {
        return _values.get(index).getCharacter();
    }
}

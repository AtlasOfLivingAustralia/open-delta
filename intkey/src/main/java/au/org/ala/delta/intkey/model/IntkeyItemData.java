package au.org.ala.delta.intkey.model;

import java.util.List;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.impl.DefaultItemData;

public class IntkeyItemData extends DefaultItemData {
    
    // These methods are not supported as to get attribute data,
    // Intkey has to read this data off disk using methods provided 
    // in the IntkeyDataset class.

    @Override
    public List<Attribute> getAttributes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Attribute getAttribute(Character character) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addAttribute(Character character, Attribute attribute) {
        throw new UnsupportedOperationException();
    }

}

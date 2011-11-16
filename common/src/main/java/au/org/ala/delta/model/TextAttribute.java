package au.org.ala.delta.model;

import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.model.impl.AttributeData;

public class TextAttribute extends Attribute {

    public TextAttribute(TextCharacter character, AttributeData impl) {
        super(character, impl);
    }
    
    @Override
    public TextCharacter getCharacter() {
        return (TextCharacter) super.getCharacter();
    }

    public String getText() {
        return getValueAsString();
    }
    
    public void setText(String text) throws DirectiveException {
        setValueFromString(text);
        notifyObservers();
    }

}

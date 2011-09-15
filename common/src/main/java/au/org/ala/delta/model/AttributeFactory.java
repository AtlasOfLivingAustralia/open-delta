package au.org.ala.delta.model;

import au.org.ala.delta.model.impl.AttributeData;

public class AttributeFactory {
    public static Attribute newAttribute(Character character, AttributeData impl) {
        if (character instanceof IntegerCharacter) {
            return new IntegerAttribute((IntegerCharacter) character, impl);
        } else if (character instanceof RealCharacter) {
            return new RealAttribute((RealCharacter) character, impl);
        } else if (character instanceof MultiStateCharacter) {
            return new MultiStateAttribute((MultiStateCharacter) character, impl);
        } else if (character instanceof TextCharacter) {
            return new TextAttribute((TextCharacter) character, impl);
        } else if (character instanceof UnknownCharacter) {
        	return new UnknownAttribute((UnknownCharacter) character, impl);
        } else {
            throw new RuntimeException("unrecognized character type");
        }
    }
}

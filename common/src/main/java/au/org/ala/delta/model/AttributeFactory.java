/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 * 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
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

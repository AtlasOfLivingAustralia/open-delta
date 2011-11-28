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

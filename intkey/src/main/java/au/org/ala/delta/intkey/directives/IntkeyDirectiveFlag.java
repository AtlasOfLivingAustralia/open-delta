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
package au.org.ala.delta.intkey.directives;

public class IntkeyDirectiveFlag {

    private char _symbol;
    private String _name;
    private boolean _takesStringValue;

    /**
     * ctor
     * 
     * @param symbol
     *            the character used to identify the flag. E.g. for flag "/X",
     *            the symbol is will be 'X'
     * @param name
     *            the name of the javabean property on an
     *            IntkeyDirectiveInvocation object that corresponds to the flag.
     *            The javabean property will be set with the parsed value of the
     *            flag
     * @param takesStringValue
     *            If true, the flag is used to input a String value. The string
     *            value must be supplied after the flag symbol and an '=' sign,
     *            e.g. "/X=string". If false, the flag is used to input a
     *            boolean value - no characters are required after the character
     *            symbol.
     */
    public IntkeyDirectiveFlag(char symbol, String name, boolean takesStringValue) {
        this._symbol = symbol;
        this._name = name;
        this._takesStringValue = takesStringValue;
    }

    public char getSymbol() {
        return _symbol;
    }

    public String getName() {
        return _name;
    }

    public boolean takesStringValue() {
        return _takesStringValue;
    }
}

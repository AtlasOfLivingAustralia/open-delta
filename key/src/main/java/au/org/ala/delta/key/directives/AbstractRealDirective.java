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
package au.org.ala.delta.key.directives;

import java.text.ParseException;

import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.DirectiveError.Error;
import au.org.ala.delta.key.KeyContext;

public abstract class AbstractRealDirective extends AbstractDirective<KeyContext> {

    private double _value = 0;

    protected AbstractRealDirective(String... controlWords) {
        super(controlWords);
    }

    @Override
    public DirectiveArguments getDirectiveArgs() {

        DirectiveArguments args = new DirectiveArguments();
        args.addDirectiveArgument(_value);
        return args;
    }

    @Override
    public int getArgType() {
        return DirectiveArgType.DIRARG_REAL;
    }

    @Override
    public void parse(KeyContext context, String data) throws ParseException {
        try {
            _value = Double.parseDouble(data.trim());
        } catch (Exception ex) {
            throw DirectiveError.asException(Error.INVALID_REAL_NUMBER, 0, context.getCurrentParsingContext().getCurrentOffset());
        }
    }
    
    @Override
    public void process(KeyContext context, DirectiveArguments directiveArguments) throws Exception {
        processReal(context, _value);
        
    }

    protected abstract void processReal(KeyContext context, double value) throws Exception;

}

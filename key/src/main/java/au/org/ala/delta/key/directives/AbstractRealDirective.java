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

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.ParsingUtils;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.IntegerValidator;
import au.org.ala.delta.directives.validation.DirectiveError.Error;
import au.org.ala.delta.directives.validation.RealValidator;
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
        _value = ParsingUtils.readReal(context.getCurrentParsingContext(), createValidator(context), data.trim());
    }
    
    @Override
    public void process(KeyContext context, DirectiveArguments directiveArguments) throws Exception {
        processReal(context, _value);
        
    }

    protected abstract void processReal(KeyContext context, double value) throws Exception;
    
    /**
     * Subclasses should override this method to create a validator appropriate for the directive type.
     * @param context the current parsing/processing context.
     * @return either an appropriate instance of IntegerValidator or null if validation is not required.
     */
    protected abstract RealValidator createValidator(DeltaContext context);

}

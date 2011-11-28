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
package au.org.ala.delta.directives;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.util.IntegerFunctor;

public abstract class AbstractDirective<C extends AbstractDeltaContext> {
    
    protected String _data;
    protected String _controlWords[];
    
    protected AbstractDirective(String... controlWords) {
        assert controlWords.length <= 4;
        _controlWords = controlWords;
    }
    
    public String getData() {
        return _data;
    }

    public String[] getControlWords() {
        return _controlWords;
    }
    
    public abstract DirectiveArguments getDirectiveArgs();
    
    public abstract int getArgType();
    
    /**
     * The order in which directives appear is important for the correct
     * operation of CONFOR.  Out of order directives will result in an 
     * error during parsing.
     * Directives of order "0" may appear anywhere.
     * @return the default order for a directive, 0.
     */
    public int getOrder() {
    	return 0;
    }

    @Override
    public String toString() {
        return String.format("Directive: %s", StringUtils.join(_controlWords, " "));
    }
    
    public void parseAndProcess(C context, String data) throws Exception {
    	parse(context, data);
    	DirectiveArguments args = getDirectiveArgs();
    	process(context, args);
    }
    
    public abstract void parse(C context, String data) throws ParseException;
    
    public abstract void process(C context, DirectiveArguments directiveArguments) throws Exception;
    
    public String getName() {
        return StringUtils.join(_controlWords, " ").toUpperCase();
    }
    
    private static Pattern RANGE_PATTERN = Pattern.compile("^([-]*\\d+)[-](\\d+)$");
    
    protected IntRange parseRange(String str) {
        Matcher m = RANGE_PATTERN.matcher(str);
        if (m.matches()) {
            int lhs = Integer.parseInt(m.group(1));
            int rhs = Integer.parseInt(m.group(2));
            return new IntRange(lhs, rhs);
        } else {
            return new IntRange(Integer.parseInt(str));
        }
    }
    
    protected void forEach(IntRange range, C context, IntegerFunctor<C> func) {
        for (int i = range.getMinimumInteger(); i <= range.getMaximumInteger(); ++i) {
            if (func != null) {
                func.invoke(context, i);
            }
        }
    }
    
    public boolean canSpecifyTextDelimiter() {
    	return false;
    }
    
}

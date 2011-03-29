package au.org.ala.delta.directives;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.DeltaContext;
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

    @Override
    public String toString() {
        return String.format("Directive: %s", StringUtils.join(_controlWords, " "));
    }
    
    public abstract void process(C context, String data) throws Exception;
    
    public Object getName() {
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
}

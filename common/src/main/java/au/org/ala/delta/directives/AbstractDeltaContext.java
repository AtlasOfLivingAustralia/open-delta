package au.org.ala.delta.directives;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import au.org.ala.delta.directives.validation.DirectiveError;

public abstract class AbstractDeltaContext {
    
    protected Stack<ParsingContext> _parsingContexts = new Stack<ParsingContext>();
    
    private List<DirectiveError> _errors = new ArrayList<DirectiveError>();
    
    public ParsingContext getCurrentParsingContext() {
        if (_parsingContexts.size() > 0) {
            return _parsingContexts.peek();
        }
        return null;
    }

    public ParsingContext newParsingContext() {
        ParsingContext context = new ParsingContext();
        _parsingContexts.push(context);
        return context;
    }

    public ParsingContext endCurrentParsingContext() {
        if (_parsingContexts.size() > 0) {
            return _parsingContexts.pop();
        }
        return null;
    }
    
    public Charset getFileEncoding() {
    	return Charset.forName("Cp1252");
    }
    
    public Charset getOutputFileEncoding() {
    	return Charset.forName("utf-8");
    }

	public void addError(DirectiveError error) {
		_errors.add(error);
	}
	
	public List<DirectiveError> getErrors() {
		return new ArrayList<DirectiveError>(_errors);
	}
	
	public void clearErrors() {
		_errors.clear();
	}

}

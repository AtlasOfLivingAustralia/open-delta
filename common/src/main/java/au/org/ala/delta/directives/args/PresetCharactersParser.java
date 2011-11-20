package au.org.ala.delta.directives.args;

import java.io.Reader;
import java.text.ParseException;

import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.validation.DirectiveError;

public class PresetCharactersParser extends DirectiveArgsParser {

	public PresetCharactersParser(AbstractDeltaContext context, Reader reader) {
		super(context, reader);
	}

	@Override
	public void parse() throws ParseException {
		_args = new DirectiveArguments();
		
		readNext();
		int lastColumn = -1;
		int lastGroup = -1;
		skipWhitespace();
		
		
		while (_currentInt >= 0) {
			int charNum = readInteger();
			expect(',');
			readNext();
			
			int columnNum = readInteger();
			expect(':');
			readNext();
			
			int groupNum = readInteger();
			
            if (columnNum < lastColumn || groupNum <= lastGroup) {
            	throw DirectiveError.asException(DirectiveError.Error.VALUE_OUT_OF_ORDER, _position);
            }
            
			DirectiveArgument<?> arg = _args.addDirectiveArgument(charNum);
			arg.add(columnNum);
			arg.add(groupNum);
			
			lastGroup = groupNum;
            if (columnNum > lastColumn) {
                lastGroup = 0;
            }
            lastColumn = columnNum;
            skipWhitespace();
		}
		
	}
	
	

}

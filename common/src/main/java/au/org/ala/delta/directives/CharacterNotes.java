package au.org.ala.delta.directives;

import java.io.StringReader;
import java.text.ParseException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgsParser;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.IntegerTextListParser;

public class CharacterNotes extends AbstractTextListDirective<Integer> {

	public CharacterNotes() {
		super("character", "notes");
	}
	
	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_INTERNAL;
	}

	@Override
	public void parse(DeltaContext context, String data) throws ParseException {
		DirectiveArgsParser parser = new IntegerTextListParser(context, new StringReader(data));
		parser.parse();
		
		_args = parser.getDirectiveArgs();
		
	}

	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		
	}

}

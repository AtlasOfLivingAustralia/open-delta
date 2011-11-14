package au.org.ala.delta.directives;

import java.io.StringReader;
import java.util.HashSet;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgsParser;
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.IdWithIdListParser;

public class EmphasizeCharacters extends AbstractCustomDirective {
	
	public static final String[] CONTROL_WORDS = {"emphasize", "characters"};
	
	
	public EmphasizeCharacters() {
		super(CONTROL_WORDS);
	}

	@Override
	protected DirectiveArgsParser createParser(DeltaContext context,
			StringReader reader) {
		return new IdWithIdListParser(context, reader);
	}

	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_ITEMCHARLIST;
	}

	@Override
	public void process(DeltaContext context,
			DirectiveArguments directiveArguments) throws Exception {
		
		for (DirectiveArgument<?> arg : directiveArguments.getDirectiveArguments()) {
			if (arg.getId() instanceof Integer) {
				context.emphasizeCharacters((Integer)arg.getId(), new HashSet<Integer>(arg.getDataList()));
			}
			else {
				context.emphasizeCharacters((String)arg.getId(), new HashSet<Integer>(arg.getDataList()));
			}
		}
		
	}
	
	@Override
	public int getOrder() {
		return 4;
	}
}

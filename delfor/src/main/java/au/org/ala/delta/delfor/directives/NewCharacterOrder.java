package au.org.ala.delta.delfor.directives;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.delfor.DelforContext;
import au.org.ala.delta.delfor.format.CharacterReorderer;
import au.org.ala.delta.directives.AbstractRangeListDirective;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.DirectiveException;

/**
 * Processes the NEW CHARACTER ORDER directive.
 */
public class NewCharacterOrder extends AbstractRangeListDirective<DelforContext> {

	public NewCharacterOrder() {
		super("new", "character", "order");
	}
	
	private List<Integer> _newOrder;
	
	@Override
	protected void processNumber(DelforContext context, int number) throws DirectiveException {
		if (_newOrder.contains(number)) {
			// check all character numbers exist.
			if (_newOrder.size() != context.getDataSet().getNumberOfCharacters()) {
				throw DirectiveError.asException(DirectiveError.Error.CHARACTER_ALREADY_SPECIFIED, 0);
			}
		}
		_newOrder.add(number);
	}
	
	
	

	@Override
	public void process(DelforContext context, DirectiveArguments directiveArguments) throws Exception {
		_newOrder = new ArrayList<Integer>();
		
		super.process(context, directiveArguments);
		
		// check all character numbers exist.
		if (_newOrder.size() != context.getDataSet().getNumberOfCharacters()) {
			throw DirectiveError.asException(DirectiveError.Error.MISSING_DATA, 0);
		}
		
		context.addFormattingAction(new CharacterReorderer(_newOrder));
	}

	@Override
	public int getArgType() {
	     return DirectiveArgType.DIRARG_CHARLIST;
	}

	@Override
	public int getOrder() {
		return 4;
	}
}

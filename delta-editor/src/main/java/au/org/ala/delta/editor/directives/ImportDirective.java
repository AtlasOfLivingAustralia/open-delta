package au.org.ala.delta.editor.directives;

import java.text.ParseException;

import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.args.DirectiveArgsParser;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.DirectiveArgType;

/**
 * Parses and imports all directives not of type DirectiveType.DIRARG_INTERNAL.
 */
public class ImportDirective extends AbstractDirective<ImportContext>{

	private Directive _directive;
	private DirectiveArguments _args;

	public ImportDirective(Directive directive) {
		super(directive.getName());
		_directive = directive;
	}

	@Override
	public DirectiveArguments getDirectiveArgs() {
		return _args;
	}

	@Override
	public int getArgType() {
		return _directive.getArgType();
	}

	@Override
	public void parse(ImportContext context, String data) throws ParseException {
		
		DirectiveArgsParser parser = DirectiveArgParserFactory.parserFor(_directive, context, data);
		
		if (_directive.getArgType() != DirectiveArgType.DIRARG_NONE) {
			parser.parse(); 
			_args = parser.getDirectiveArgs();
		}
		
		Logger.debug("Importer parsed directive: "+_directive.joinNameComponents()+" Arg type: "+_directive.getArgType());
	}

	/**
	 * The DirectiveFileImporter will never invoke this method.
	 */
	@Override
	public void process(ImportContext context,
			DirectiveArguments directiveArguments) throws Exception {
		throw new UnsupportedOperationException();
	}
}
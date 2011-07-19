package au.org.ala.delta.editor.directives;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.DirectiveParser;
import au.org.ala.delta.directives.args.DirectiveArgsParser;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.DirectiveArgType;
import au.org.ala.delta.editor.slotfile.DirectiveInstance;


/**
 * The DirectiveFileImporter is responsible for importing each of the 
 * directives in a single file into a DeltaDataSet.
 */
public class DirectiveFileImporter extends DirectiveParser<ImportContext> {

	private DirectiveImportHandler _handler;
	private Directive[] _directives; 
	
	public DirectiveFileImporter(DirectiveImportHandler handler, Directive[] directives) {
		_handler = handler;
		_directives = directives;
		registerDirectives(directives);
		registerObserver(handler);
	}
	
	
	@Override
	protected void handleUnrecognizedDirective(ImportContext context, List<String> controlWords) {
		_handler.handleUnrecognizedDirective(context, controlWords);
	}

	@Override
	protected void handleDirectiveProcessingException(
			ImportContext context, AbstractDirective<ImportContext> d, Exception ex) {
		ex.printStackTrace();
		_handler.handleDirectiveProcessingException(context, d, ex);
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
    protected void doProcess(ImportContext context, AbstractDirective d, String dd)
			throws ParseException, Exception {
		
		Directive directive = typeOf(d);
		
    	d.parse(context, dd);
    	DirectiveInstance instance = new DirectiveInstance(directive, d.getDirectiveArgs());
		context.getDirectiveFile().add(instance);
		
		if (d.getArgType() == DirectiveArgType.DIRARG_INTERNAL) {
			d.process(context, d.getDirectiveArgs());
		}
	}


	private void registerDirectives(Directive[] directives) {
		Directive directive = null;
    	try {
    		
	    	for (int i=0; i<directives.length; i++) {
	    		directive = directives[i];
	    		if (directive.getArgType() == DirectiveArgType.DIRARG_INTERNAL) {
	    			Class<? extends AbstractDirective<?>> dirClass = directive.getImplementationClass();
	    			registerDirective(dirClass.newInstance());
	    		}
	    		else {
	    			registerDirective(new ImportDirective(directive));
	    		}
	    	}
    	}
    	catch (Exception e) {
    		throw new RuntimeException("Failed to find directive for: "+directive.joinNameComponents(), e);
    	}
	}
	
	public Directive typeOf(AbstractDirective<?> directive) {
		String[] directiveName = directive.getControlWords();
		for (Directive dir : _directives) {
			if (directiveName.length == dir.getName().length) {
				
				boolean match = true;
				for (int i=0; i<directiveName.length; i++) {
					if (!directiveName[i].toUpperCase().equals(dir.getName()[i].toUpperCase())) {
						match = false;
					}
				}
				if (match) {
					return dir;
				}
			}
		}
		throw new RuntimeException("Cannot find a directive matching: "+Arrays.asList(directiveName));
	}
    
    class ImportDirective extends AbstractDirective<ImportContext>{

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
			
			System.out.println("Directive: "+_directive.joinNameComponents()+" Arg type: "+_directive.getArgType());
		}

		@Override
		public void process(ImportContext context,
				DirectiveArguments directiveArguments) throws Exception {
			throw new UnsupportedOperationException();
		}
    }
}

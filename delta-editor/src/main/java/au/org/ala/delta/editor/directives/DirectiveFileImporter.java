package au.org.ala.delta.editor.directives;

import java.io.StringReader;
import java.text.ParseException;
import java.util.List;

import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.DirectiveParser;
import au.org.ala.delta.directives.args.DirectiveArgsParser;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.IdListParser;
import au.org.ala.delta.directives.args.IdValueListParser;
import au.org.ala.delta.directives.args.IdWithIdListParser;
import au.org.ala.delta.directives.args.IntegerIdArgParser;
import au.org.ala.delta.directives.args.IntegerTextListParser;
import au.org.ala.delta.directives.args.NumericArgParser;
import au.org.ala.delta.directives.args.StringTextListParser;
import au.org.ala.delta.directives.args.TextArgParser;
import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.DirectiveArgType;
import au.org.ala.delta.editor.slotfile.DirectiveInstance;
import au.org.ala.delta.editor.slotfile.directive.ConforDirType;

public class DirectiveFileImporter extends DirectiveParser<ImportContext> {

	private DirectiveImportHandler _handler;
	
	public DirectiveFileImporter(DirectiveImportHandler handler) {
		_handler = handler;
		registerDirectives();
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
		
		Directive directive = ConforDirType.typeOf(d);
		
    	d.parse(context, dd);
    	
    	DirectiveInstance instance = new DirectiveInstance(directive, d.getDirectiveArgs());
		
		context.getDirectiveFile().add(instance);
		
		if (d.getArgType() == DirectiveArgType.DIRARG_INTERNAL) {
			d.process(context, d.getDirectiveArgs());
		}
	}

    
    private void registerDirectives() { 
    	Directive directive = null;
    	try {
	    	for (int i=0; i<ConforDirType.ConforDirArray.length; i++) {
	    		directive = ConforDirType.ConforDirArray[i];
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
			
			DirectiveArgsParser parser = null;
			StringReader reader = new StringReader(data);
			switch (_directive.getArgType()) {
			
			case DirectiveArgType.DIRARG_COMMENT:
			case DirectiveArgType.DIRARG_TRANSLATION:
			case DirectiveArgType.DIRARG_TEXT:
			case DirectiveArgType.DIRARG_FILE:
				parser = new TextArgParser(context, reader);
				break;
			case DirectiveArgType.DIRARG_INTEGER:
			case DirectiveArgType.DIRARG_REAL:
				parser = new NumericArgParser(context, reader);
				break;
			case DirectiveArgType.DIRARG_CHAR:
			case DirectiveArgType.DIRARG_ITEM:
				parser = new IntegerIdArgParser(context, reader);
				break;
			case DirectiveArgType.DIRARG_CHARLIST:
			case DirectiveArgType.DIRARG_ITEMLIST:
				parser = new IdListParser(context, reader);
				break;
			case DirectiveArgType.DIRARG_TEXTLIST:
			case DirectiveArgType.DIRARG_CHARTEXTLIST:
				parser = new IntegerTextListParser(context, reader);
				break;
			case DirectiveArgType.DIRARG_CHARINTEGERLIST:
			case DirectiveArgType.DIRARG_CHARREALLIST:
			case DirectiveArgType.DIRARG_ITEMREALLIST:
				parser = new IdValueListParser(context, reader);
				break;
			case DirectiveArgType.DIRARG_ITEMTEXTLIST:
			case DirectiveArgType.DIRARG_ITEMFILELIST:
				parser = new StringTextListParser(context, reader);
				break;
			case DirectiveArgType.DIRARG_ITEMCHARLIST:
				parser = new IdWithIdListParser(context, reader);
				break;
			}
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

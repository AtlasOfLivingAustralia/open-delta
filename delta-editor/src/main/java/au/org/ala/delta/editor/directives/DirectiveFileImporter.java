package au.org.ala.delta.editor.directives;

import java.text.ParseException;
import java.util.List;

import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.DirectiveParser;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.DirectiveArgType;
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
		
    	d.parse(context, dd);
		
		context.getDirectiveFile().add(d);
		
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
    	
    	public ImportDirective(Directive directive) {
    		super(directive.getName());
    		_directive = directive;
    	}
    
		@Override
		public DirectiveArguments getDirectiveArgs() {
			return null;
		}

		@Override
		public int getArgType() {
			return _directive.getArgType();
		}

		@Override
		public void parse(ImportContext context, String data) throws ParseException {
			
			switch (_directive.getArgType()) {
			
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

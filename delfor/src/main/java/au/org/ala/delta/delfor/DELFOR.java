package au.org.ala.delta.delfor;

import java.io.File;

import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.DirectiveParserObserver;
import au.org.ala.delta.directives.ParsingContext;
import au.org.ala.delta.dist.DIST;
import au.org.ala.delta.editor.slotfile.model.SlotFileRepository;
import au.org.ala.delta.model.AbstractObservableDataSet;

public class DELFOR implements DirectiveParserObserver {

	private DelforContext _context;
	
	/**
	 * @param args specifies the name of the input file to use.
	 */
	public static void main(String[] args) throws Exception {

	    
		StringBuilder credits = new StringBuilder("DELFOR version 2.15 (Java)");
		credits.append("\n\nJava edition ported by the Atlas of Living Australia, 2010.\n");
		
		
		System.out.println(credits);
		
		File f = handleArgs(args);
		if (!f.exists()) {
			Logger.log("File %s does not exist!", f.getName());
			return;
		}
		
		new DIST(f);
	}
	
	private static File handleArgs(String[] args) throws Exception {
		String fileName;
		if (args.length == 0) {
			fileName = "dist";
		}
		else {
			fileName = args[0];
		}
		
		return new File(fileName);
	}
	
	public DELFOR(File input) throws Exception {
		
		SlotFileRepository dataSetRepository = new SlotFileRepository();
		AbstractObservableDataSet dataSet = (AbstractObservableDataSet) dataSetRepository.newDataSet();

		_context = new DelforContext(dataSet);
		
		DelforDirectiveFileParser parser = DelforDirectiveFileParser.createInstance();
		parser.registerObserver(this);
		parser.parse(input, _context);
	}
	

	public File getOutputFile() {
		return _context.getOutputFileSelector().getOutputFileAsFile();
	}
	
	public File getListingFile() {
		return null;
	}
	
	public File getErrorFile() {
		return null;
	}

	@Override
	public void preProcess(AbstractDirective<? extends AbstractDeltaContext> directive, String data) {}
	
	@Override
	public void handleDirectiveProcessingException(AbstractDeltaContext context, AbstractDirective<? extends AbstractDeltaContext> d, Exception ex) {
		 ParsingContext pc = context.getCurrentParsingContext();
	        if (pc.getFile() != null) {
	            Logger.error(String.format("Exception occured trying to process directive: %s (%s %d:%d)", d.getName(), pc.getFile().getName(),
	                    pc.getCurrentDirectiveStartLine(), pc.getCurrentDirectiveStartOffset()));
	            Logger.error(ex);
	        } else {
	            Logger.error(String.format("Exception occured trying to process directive: %s (%d:%d)", d.getName(), pc.getCurrentDirectiveStartLine(),
	                    pc.getCurrentDirectiveStartOffset()));
	            Logger.error(ex);
	        }
	        
		
	}

	public void postProcess(AbstractDirective<? extends AbstractDeltaContext> directive) {}

	public void finishedProcessing() {}
}

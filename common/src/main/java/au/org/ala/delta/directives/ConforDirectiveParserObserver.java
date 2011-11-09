package au.org.ala.delta.directives;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.io.OutputFileManager;
import au.org.ala.delta.model.image.ImageType;
import au.org.ala.delta.translation.DataSetTranslator;
import au.org.ala.delta.translation.DataSetTranslatorFactory;
import au.org.ala.delta.util.DataSetHelper;
import au.org.ala.delta.util.Utils;

/**
 * Takes action at certain points in the directive parsing lifecycle, for
 * example after the CHARACTER LIST or ITEM DESCRIPTIONS directives have been
 * parsed a translation action may be initiated.
 */
public class ConforDirectiveParserObserver implements DirectiveParserObserver {

	private DeltaContext _context;
	private DataSetTranslatorFactory _factory;
	private DataSetHelper _helper;
	private int _ListFilenameSize = 15;


	private int _totalErrors;
	private boolean _fatalErrorEncountered;

	public ConforDirectiveParserObserver(DeltaContext context) {
		_context = context;
		_context.setDirectiveParserObserver(this);
		_factory = new DataSetTranslatorFactory();
		_helper = new DataSetHelper(context.getDataSet());
	}

	@Override
	public void preProcess(AbstractDirective<? extends AbstractDeltaContext> directive, String data) {

		outputToListingFile(directive, data);
		
		if (isCharacterList(directive) || isItemDescriptions(directive)) {
			checkForFatalError();
		} 
	}

	protected void outputToListingFile(AbstractDirective<? extends AbstractDeltaContext> directive, String data) {
		if (isCharacterDirective(directive) && !_context.isCharacterListingEnabled()) {

			_context.getOutputFileSelector().listMessage(formatWithFileName(directive.getName()));
		}
		else if (isItemDirective(directive) && !_context.isItemListingEnabled()) {
			_context.getOutputFileSelector().listMessage(formatWithFileName(directive.getName()));
		}
		else {
			ParsingContext context = _context.getCurrentParsingContext();
			int pos = 0;
			String prefix = "*"+directive.getName()+ " ";
			for (int i = (int)context.getCurrentDirectiveStartLine(); i<context.getCurrentLine(); i++) {
				int nextPos = data.indexOf('\n', pos);
				if (nextPos < 0) {
					nextPos = data.length();
				}
				String line = data.substring(pos, nextPos);
				line = formatWithFileName(prefix + line,context.getFile().getAbsolutePath(), i);
				_context.getOutputFileSelector().listMessage(line);
				pos = nextPos+1;
				prefix = "";
			}
			
		}
	}
	
	private boolean isCharacterList(AbstractDirective<? extends AbstractDeltaContext> directive) {
		return directive instanceof CharacterList || directive instanceof KeyCharacterList;
	}
	
	private boolean isItemDescriptions(AbstractDirective<? extends AbstractDeltaContext> directive) {
		return directive instanceof ItemDescriptions;
	}
	
	/**
	 * @return true if the directive is CHARACTER LIST, CHARACTER NOTES, 
	 * CHARACTER IMAGES.  Used to control listing output.
	 */
	private boolean isCharacterDirective(AbstractDirective<? extends AbstractDeltaContext> directive) {
		return isCharacterList(directive) || 
			directive instanceof CharacterNotes ||
		    directive instanceof CharacterImages;
 	}
	
	/**
	 * @return true if the directive is ITEM DESCRIPTIONS, TAXON IMAGES.  
	 * Used to control listing output.
	 */
	private boolean isItemDirective(AbstractDirective<? extends AbstractDeltaContext> directive) {
		return isItemDescriptions(directive) || directive instanceof TaxonImages;
	}

	@Override
	public void postProcess(AbstractDirective<? extends AbstractDeltaContext> directive) {

		handleErrors();

		if (isCharacterList(directive)) {
			postProcessCharacters();
		} else if (isItemDescriptions(directive)) {
			postProcessItems();
		}
	}

	@Override
	public void finishedProcessing() {
		OutputFileManager fileManager = _context.getOutputFileSelector();
		if (_totalErrors > 0) {
			fileManager.message("");
			fileManager.message("****** Number of errors = "+_totalErrors);
			fileManager.message("****** Abnormal Termination.");
		}
		else {
			fileManager.message("Normal termination.");
		}
		fileManager.message("");
		
		listOutputFiles();
	}
	
	private void listOutputFiles() {
		OutputFileManager fileManager = _context.getOutputFileSelector();
		fileManager.message("Output files - ");
		for (String fileName : fileManager.getOutputFileNames()) {
			fileManager.message("   "+fileName);
		}
		
	}

	private void postProcessCharacters() {
		DataSetTranslator translator = _factory.createTranslator(_context);
		translator.translateCharacters();
	}

	private void postProcessItems() {
		_helper.addItemImages(_context.getImages(ImageType.IMAGE_TAXON));

		DataSetTranslator translator = _factory.createTranslator(_context);
		translator.translateItems();
	}

	@Override
	public void handleDirectiveProcessingException(AbstractDeltaContext context,
			AbstractDirective<? extends AbstractDeltaContext> directive, Exception ex) throws DirectiveException {
		_totalErrors ++;
		ParsingContext pc = context.getCurrentParsingContext();

		if (ex instanceof DirectiveException) {
			OutputFileManager fileManager = ((DeltaContext)context).getOutputFileSelector();

			int offset = ((DirectiveException) ex).getErrorOffset();

			// Write the directive out for context.
			try {
				String text = currentDirective(pc, offset);
				text = formatWithFileName(text, pc.getFile().getAbsolutePath(), pc.getCurrentDirectiveStartLine());
				fileManager.errorMessage(text);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			StringBuilder errorLocation = new StringBuilder();
			for (int i = 0; i < offset + pc.getDirectiveEndOffset() + 14; i++) {
				errorLocation.append(' ');
			}

			errorLocation.append("^");
			fileManager.message(errorLocation.toString());
			fileManager.message("****** " + ex.getMessage());
			
			if (((DirectiveException) ex).isFatal()) {
				throw (DirectiveException)ex;
			}

		} else {
			if (pc.getFile() != null) {
				Logger.error(String.format("Exception occured trying to process directive: %s (%s %d:%d)",
						directive.getName(), pc.getFile().getName(), pc.getCurrentDirectiveStartLine(),
						pc.getCurrentDirectiveStartOffset()));
				Logger.error(ex);
			} else {
				Logger.error(String.format("Exception occured trying to process directive: %s (%d:%d)",
						directive.getName(), pc.getCurrentDirectiveStartLine(), pc.getCurrentDirectiveStartOffset()));
				Logger.error(ex);
			}
		}

	}

	public String currentDirective(ParsingContext pc, int errorOffset) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(pc.getFile())));
		String line = null;
		int i = 1;
		line = reader.readLine();
		while (line != null && i < pc.getCurrentDirectiveStartLine()-1) {
			line = reader.readLine();
			i++;
			
			System.out.println("Line: " + i + ": " + line);
		}

		String directiveLine = reader.readLine();

		return directiveLine;

	}

	private void handleErrors() {
		List<DirectiveError> errors = _context.getErrors();

		OutputFileManager manager = _context.getOutputFileSelector();
		for (DirectiveError error : errors) {
			manager.listMessage(error.getMessage());
			if (error.isFatal()) {
				_totalErrors++;
				_fatalErrorEncountered = true;
			}
		}

		_context.clearErrors();
	}

	private void checkForFatalError() {
		if (_fatalErrorEncountered) {
			throw new RuntimeException("It's all over!");
		}
	}
	
	private String formatWithFileName(String text, String fileName, long lineNumber) {
		
		String filename = Utils.fixedWidth(String.format("%s,%d", fileName, lineNumber), _ListFilenameSize);
		
		return String.format("%s %s", filename, text);
	}
	
	private String formatWithFileName(String text) {
		ParsingContext context = _context.getCurrentParsingContext();
		return formatWithFileName(text, context.getFile().getAbsolutePath(), context.getCurrentDirectiveStartLine());
	}
}

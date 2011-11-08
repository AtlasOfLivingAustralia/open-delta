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

/**
 * Takes action at certain points in the directive parsing lifecycle, for
 * example after the CHARACTER LIST or ITEM DESCRIPTIONS directives have been
 * parsed a translation action may be initiated.
 */
public class ConforDirectiveParserObserver implements DirectiveParserObserver {

	private DeltaContext _context;
	private DataSetTranslatorFactory _factory;
	private DataSetHelper _helper;

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

		if (directive.getControlWords().equals(CharacterList.CONTROL_WORDS)
				|| directive.getControlWords().equals(KeyCharacterList.CONTROL_WORDS)
				|| directive.getControlWords().equals(ItemDescriptions.CONTROL_WORDS)) {
			checkForFatalError();
		}

		_context.getOutputFileSelector().listMessage(directive.getName() + " " + data);
	}

	@Override
	public void postProcess(AbstractDirective<? extends AbstractDeltaContext> directive) {

		handleErrors();

		if (directive.getControlWords().equals(CharacterList.CONTROL_WORDS)
				|| directive.getControlWords().equals(KeyCharacterList.CONTROL_WORDS)) {
			postProcessCharacters();
		} else if (directive.getControlWords().equals(ItemDescriptions.CONTROL_WORDS)) {
			postProcessItems();
		}
	}

	@Override
	public void finishedProcessing() {
		// processPrintActions();
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
			AbstractDirective<? extends AbstractDeltaContext> directive, Exception ex) {
		ParsingContext pc = context.getCurrentParsingContext();

		if (ex instanceof DirectiveException) {
			OutputFileManager fileManager = ((DeltaContext)context).getOutputFileSelector();

			int offset = ((DirectiveException) ex).getErrorOffset();

			// Write the directive out for context.
			try {
				fileManager.ErrorMessage(currentDirective(pc, offset));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			StringBuilder errorLocation = new StringBuilder();
			for (int i = 0; i < offset + pc.getDirectiveEndOffset() - 2; i++) {
				errorLocation.append(' ');
			}

			errorLocation.append("^");
			fileManager.ErrorMessage(errorLocation.toString());
			fileManager.ErrorMessage("****** " + ex.getMessage());
			fileManager.listMessage("****** " + ex.getMessage());

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
		while (line != null && i < pc.getCurrentDirectiveStartLine()) {
			i++;
			line = reader.readLine();

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
}

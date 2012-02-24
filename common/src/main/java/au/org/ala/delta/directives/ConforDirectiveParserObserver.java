/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.directives;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.directives.validation.IncompatibleDirectivesValidator;
import au.org.ala.delta.directives.validation.ItemDescriptionsValidator;
import au.org.ala.delta.io.OutputFileManager;
import au.org.ala.delta.model.image.ImageType;
import au.org.ala.delta.translation.DataSetTranslator;
import au.org.ala.delta.translation.DataSetTranslatorFactory;
import au.org.ala.delta.util.DataSetHelper;
import au.org.ala.delta.util.Utils;

/**
 * Takes action at certain points in the directive parsing lifecycle, for example after the CHARACTER LIST or ITEM DESCRIPTIONS directives have been parsed a translation action may be initiated.
 */
public class ConforDirectiveParserObserver implements DirectiveParserObserver {

	private DeltaContext _context;
	private DataSetTranslatorFactory _factory;
	private DataSetHelper _helper;
	private int _ListFilenameSize = 15;
	private IncompatibleDirectivesValidator _validator;

	private int _totalErrors;
	private int _totalWarnings;
	private boolean _fatalErrorEncountered;

	public ConforDirectiveParserObserver(DeltaContext context) {
		_context = context;
		_context.setDirectiveParserObserver(this);
		_factory = new DataSetTranslatorFactory();
		_helper = new DataSetHelper(context.getDataSet());
		_validator = new IncompatibleDirectivesValidator();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void preProcess(AbstractDirective<? extends AbstractDeltaContext> directive, String data) throws DirectiveException {

		_validator.validate((AbstractDirective<DeltaContext>) directive);
		if (isCharacterList(directive) || isItemDescriptions(directive)) {
			checkForFatalError();
		}
	}

	protected void outputToListingFile(AbstractDirective<? extends AbstractDeltaContext> directive) {
		if (isCharacterDirective(directive) && !_context.isCharacterListingEnabled()) {

			_context.getOutputFileSelector().listMessage(formatWithFileName(directive.getName()));
		} else if (isItemDirective(directive) && !_context.isItemListingEnabled()) {
			_context.getOutputFileSelector().listMessage(formatWithFileName(directive.getName()));
		} else {
			ParsingContext context = _context.getCurrentParsingContext();
			int pos = 0;
			String directiveText = context.getCurrentDirectiveText();
			for (int i = (int) context.getCurrentDirectiveStartLine(); i < context.getCurrentLine(); i++) {
				int nextPos = directiveText.indexOf('\n', pos);
				if (nextPos < 0) {
					nextPos = directiveText.length();
				}
				String line = directiveText.substring(pos, nextPos);
				String filename = "<nofile>";
				if (context.getFile() != null) {
					filename = context.getFile().getAbsolutePath();
				}
				line = formatWithFileName(line, filename, i);
				_context.getOutputFileSelector().listMessage(line);
				pos = nextPos + 1;
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
	 * @return true if the directive is CHARACTER LIST, CHARACTER NOTES, CHARACTER IMAGES. Used to control listing output.
	 */
	private boolean isCharacterDirective(AbstractDirective<? extends AbstractDeltaContext> directive) {
		return isCharacterList(directive) || directive instanceof CharacterNotes || directive instanceof CharacterImages;
	}

	/**
	 * @return true if the directive is ITEM DESCRIPTIONS, TAXON IMAGES. Used to control listing output.
	 */
	private boolean isItemDirective(AbstractDirective<? extends AbstractDeltaContext> directive) {
		return isItemDescriptions(directive) || directive instanceof TaxonImages;
	}

	@Override
	public void postProcess(AbstractDirective<? extends AbstractDeltaContext> directive) throws DirectiveException {

		outputToListingFile(directive);

		handleErrors();
		try {
			if (isCharacterList(directive)) {
				postProcessCharacters();
			} else if (isItemDescriptions(directive)) {
				postProcessItems();
			}
		} catch (DirectiveException e) {
			try {
				handleDirectiveProcessingException(_context, directive, e);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	@Override
	public void finishedProcessing() {
		OutputFileManager fileManager = _context.getOutputFileSelector();
		if (_totalErrors > 0) {
			fileManager.message("");
			fileManager.message("****** Number of errors = " + _totalErrors);
			fileManager.message("****** Abnormal Termination.");
		} else {
			fileManager.message("Normal termination.");
		}
		fileManager.message("");

		listOutputFiles();
	}

	private void listOutputFiles() {
		OutputFileManager fileManager = _context.getOutputFileSelector();
		fileManager.message("Output files - ");
		for (File fileName : fileManager.getOutputFiles()) {
			fileManager.message("   " + fileName.getName());
		}

	}

	private void postProcessCharacters() throws DirectiveException {
		DataSetTranslator translator = _factory.createTranslator(_context);
		translator.translateCharacters();
	}

	private void postProcessItems() throws DirectiveException {

		validateItemDescriptions();
		_helper.addItemImages(_context.getImages(ImageType.IMAGE_TAXON));

		DataSetTranslator translator = _factory.createTranslator(_context);
		translator.translateItems();
	}

	private void validateItemDescriptions() {
		ItemDescriptionsValidator validator = new ItemDescriptionsValidator();

		validator.validate(_context, AddCharacters.CONTROL_WORDS, _context.addCharacterDescriptions());
		validator.validate(_context, EmphasizeCharacters.CONTROL_WORDS, _context.emphasizedCharacterDescriptions());
		validator.validate(_context, ItemHeadings.CONTROL_WORDS, _context.itemHeadingDescriptions());
		validator.validate(_context, ItemOutputFiles.CONTROL_WORDS, _context.itemOutputFilesDescriptions());
		validator.validate(_context, IndexHeadings.CONTROL_WORDS, _context.indexHeadingsDescriptions());
		validator.validateImages(_context, TaxonImages.CONTROL_WORDS, _context.getImages(ImageType.IMAGE_TAXON).iterator());
	}

	@Override
	public void handleDirectiveProcessingException(AbstractDeltaContext context, AbstractDirective<? extends AbstractDeltaContext> directive, Exception ex) throws DirectiveException {
		_totalErrors++;
		ParsingContext pc = context.getCurrentParsingContext();

		if (ex instanceof DirectiveException) {

			writeError(context, ((DirectiveException) ex).getError(), pc);
			throw (DirectiveException) ex;

		} else {
			if (pc.getFile() != null) {
				Logger.error(String.format("Exception occured trying to process directive: %s (%s %d:%d)", directive.getName(), pc.getFile().getName(), pc.getCurrentDirectiveStartLine(),
						pc.getCurrentDirectiveStartOffset()));
				Logger.error(ex);
			} else {
				Logger.error(String.format("Exception occured trying to process directive: %s (%d:%d)", directive.getName(), pc.getCurrentDirectiveStartLine(), pc.getCurrentDirectiveStartOffset()));
				Logger.error(ex);
			}
		}

	}

	protected void writeError(AbstractDeltaContext context, DirectiveError error, ParsingContext pc) throws DirectiveException {
		OutputFileManager fileManager = ((DeltaContext) context).getOutputFileSelector();

		int offset = (int) error.getPosition();

		String directiveText = pc.getCurrentDirectiveText();
		int dataStart = (int) pc.getDirectiveEndOffset();

		offset += dataStart;

		int newLineAfterError = directiveText.indexOf('\n', offset);
		if (newLineAfterError < 0) {
			newLineAfterError = directiveText.length();
		}

		String[] lines = directiveText.substring(0, newLineAfterError).split("\n");

		int lineNum = (int) pc.getCurrentDirectiveStartLine();
		
		for (String line : lines) {

			String filename = "<no file>";
			if (pc.getFile() != null) {
				filename = pc.getFile().getAbsolutePath();
			}
			
			int lineLength = line.length();
			
			line = formatWithFileName(line, filename, lineNum);
			fileManager.message(line);
			
			if (offset >= 0 && offset < lineLength) {
				// Error marker should go below this line
				StringBuilder errorLocation = new StringBuilder();
				for (int i = 0; i < offset + 16; i++) {
					errorLocation.append(' ');
				}

				errorLocation.append("^");
				fileManager.message(errorLocation.toString());				
			} 
			
			offset -= lineLength + 1; // + 1 for the line endings 
			
			lineNum++;
		}

		fileManager.message("****** " + error.getMessage());
	}

	private void handleErrors() throws DirectiveException {
		List<DirectiveError> errors = _context.getErrors();
		ParsingContext pc = _context.getCurrentParsingContext();
		OutputFileManager manager = _context.getOutputFileSelector();
		for (DirectiveError error : errors) {
			writeError(_context, error, pc);
			if (error.isError()) {
				_totalErrors++;
				if (error.isFatal()) {
					_fatalErrorEncountered = true;
					throw error.asException();
				}
			} else if (error.isWarning()) {
				_totalWarnings++;
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
		String filename = "<no file>";
		if (context.getFile() != null) {
			filename = context.getFile().getAbsolutePath();
		}
		return formatWithFileName(text, filename, context.getCurrentDirectiveStartLine());
	}
}

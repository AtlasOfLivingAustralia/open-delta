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

import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.DirectiveException;

/**
 * A directive file is a text file containing one or more directives. Directives
 * start with an '*' followed by up to four alphanumeric components of the
 * directive name delimited by a space,followed by the data (if any) of the
 * directive, and is terminated either by the beginning of a new directive, or
 * the end of the file.
 * 
 * @author baird
 * 
 */
public class ConforDirectiveFileParser extends DirectiveParser<DeltaContext> {

    //Private constructor, must use factory method to get an instance
    private ConforDirectiveFileParser() {
        
    }
    
    public static ConforDirectiveFileParser createInstance() {
        ConforDirectiveFileParser instance = new ConforDirectiveFileParser();
        
        instance.registerDirective(new AbsoluteError());
        instance.registerDirective(new AddCharacters());
        instance.registerDirective(new AlternateComma());
        instance.registerDirective(new ApplicableCharacters());
        
        instance.registerDirective(new CharacterForOutputFiles());
        instance.registerDirective(new CharacterForTaxonImages());
        instance.registerDirective(new CharacterForTaxonNames());   
        instance.registerDirective(new CharacterHeadings());
        instance.registerDirective(new CharacterImages());
        instance.registerDirective(new CharacterKeywordImages());
        instance.registerDirective(new CharacterList());
        instance.registerDirective(new CharacterNotes());
        instance.registerDirective(new CharacterReliabilities());
        instance.registerDirective(new CharactersForSynonymy());
        instance.registerDirective(new CharacterTypes());
        instance.registerDirective(new CharacterWeights());
        instance.registerDirective(new ChineseFormat());
        instance.registerDirective(new Comment());
        
        instance.registerDirective(new DataBufferSize());
        instance.registerDirective(new DataListing());        
        instance.registerDirective(new DependentCharacters());
        instance.registerDirective(new DisableDeltaOutput());
        instance.registerDirective(new DistOutputFile());
        
        instance.registerDirective(new EmphasizeCharacters());
        instance.registerDirective(new EmphasizeFeatures());
        instance.registerDirective(new ErrorFile());
        instance.registerDirective(new ExcludeCharacters());
        instance.registerDirective(new ExcludeItems());
        
        instance.registerDirective(new FormattingMarks());
        
        instance.registerDirective(new Heading());
        
        instance.registerDirective(new ImageDirectory());
        instance.registerDirective(new ImplicitValues());
        instance.registerDirective(new InapplicableCharacters());
        instance.registerDirective(new IncludeCharacters());
        instance.registerDirective(new IncludeItems());     
        instance.registerDirective(new IndexHeadings());
        instance.registerDirective(new IndexOutputFile());
        instance.registerDirective(new IndexText());
        instance.registerDirective(new InputDeltaFile());
        instance.registerDirective(new InputFile());
        instance.registerDirective(new InsertImplicitValues());
        instance.registerDirective(new InsertRedundantVariantAttributes());       
        instance.registerDirective(new IntkeyOutputFile());
        instance.registerDirective(new ItemAbundances());
        instance.registerDirective(new ItemDescriptions());
        instance.registerDirective(new ItemHeadings());
        instance.registerDirective(new ItemOutputFiles());
        instance.registerDirective(new ItemSubHeadings());
        instance.registerDirective(new ItemWeights());
        
        instance.registerDirective(new KeyCharacterList()); 
        instance.registerDirective(new KeyOutputFile());   
        instance.registerDirective(new KeyStates());   
        
        instance.registerDirective(new LinkCharacters());
        instance.registerDirective(new ListingFile());
        instance.registerDirective(new ListHeading());

        instance.registerDirective(new MandatoryCharacters());
        instance.registerDirective(new MaximumNumberOfItems());
        instance.registerDirective(new MaximumNumberOfStates());
      
        instance.registerDirective(new NewFilesAtItems());
        instance.registerDirective(new NewParagraphAtCharacters());
        instance.registerDirective(new NonautomaticControllingCharacters());
        instance.registerDirective(new NoDataListing());
        instance.registerDirective(new NumberOfCharacters());
        instance.registerDirective(new NumbersOfStates());
        instance.registerDirective(new NumberStatesFromZero());
        
        instance.registerDirective(new OmitCharacterNumbers());
        instance.registerDirective(new OmitFinalComma());
        instance.registerDirective(new OmitInapplicables());
        instance.registerDirective(new OmitInnerComments());
        instance.registerDirective(new OmitLowerForCharacters());
        instance.registerDirective(new OmitOrForCharacters());
        instance.registerDirective(new OmitPeriodForCharacters());
        instance.registerDirective(new OmitRedundantVariantAttributes());
        instance.registerDirective(new OmitSpaceBeforeUnits());
        instance.registerDirective(new OmitTypeSettingMarks());
        instance.registerDirective(new OutputDirectory());
        instance.registerDirective(new OutputFile());
        instance.registerDirective(new OutputFormatHtml());
        instance.registerDirective(new OutputParameters());
        instance.registerDirective(new OutputWidth());       
        instance.registerDirective(new OverlayFonts());
        
        instance.registerDirective(new PercentError());
        instance.registerDirective(new PrintCharacterList());  
        instance.registerDirective(new PrintComment());  
        instance.registerDirective(new PrintFile());
        instance.registerDirective(new PrintHeading()); 
        instance.registerDirective(new PrintItemDescriptions()); 
        instance.registerDirective(new PrintItemNames());
        instance.registerDirective(new PrintSummary());
        instance.registerDirective(new PrintUncodedCharacters());
        instance.registerDirective(new PrintWidth());

        instance.registerDirective(new RegistrationHeading());
        instance.registerDirective(new RegistrationSubHeading());
        instance.registerDirective(new ReplaceAngleBrackets());
        instance.registerDirective(new ReplaceSemicolonByComma()); 
        
        instance.registerDirective(new Show());
        instance.registerDirective(new StartupImages());
        instance.registerDirective(new SubjectForOutputFiles());
        
        instance.registerDirective(new TaxonImages());
        instance.registerDirective(new TaxonKeywordImages());
        instance.registerDirective(new TaxonLinks());
        instance.registerDirective(new TranslateImplicitValues());
        instance.registerDirective(new TranslateInto());
        instance.registerDirective(new TranslateUncodedCharacters());
        instance.registerDirective(new TreatIntegerAsReal());
        instance.registerDirective(new TreatVariableAsUnknown());
        instance.registerDirective(new TypeSettingMarks());
        
        instance.registerDirective(new UseControllingCharactersFirst());
        instance.registerDirective(new UseLastValueCoded());
        instance.registerDirective(new UseNormalValues());
        instance.registerDirective(new Vocabulary());
        
        
        return instance;
    }
    
    /** Tracks the order of the most recently processed directive. */
    private int _currentOrder;

    @Override
    protected void handleUnrecognizedDirective(DeltaContext context, List<String> controlWords) {
        ParsingContext pc = context.getCurrentParsingContext();
        if (pc.getFile() != null) {
            Logger.log("Unrecognized Directive: %s at offset %s %d:%d", StringUtils.join(controlWords, " "), pc.getFile().getName(), pc.getCurrentDirectiveStartLine(),
                    pc.getCurrentDirectiveStartOffset());
        } else {
            Logger.log("Unrecognized Directive: %s at offset %d:%d", StringUtils.join(controlWords, " "), pc.getCurrentDirectiveStartLine(), pc.getCurrentDirectiveStartOffset());
        }
    }

	@Override
	protected void executeDirective(AbstractDirective<DeltaContext> directive, String data, DeltaContext context)
			throws DirectiveException {
		
		int order = directive.getOrder();
		// Directives with order 0 can appear anywhere
		if (order > 0 && order < _currentOrder) {
			throw DirectiveError.asException(DirectiveError.Error.DIRECTIVE_OUT_OF_ORDER, 0);
		}
		_currentOrder = order;
		super.executeDirective(directive, data, context);
	}
    
    
    
}

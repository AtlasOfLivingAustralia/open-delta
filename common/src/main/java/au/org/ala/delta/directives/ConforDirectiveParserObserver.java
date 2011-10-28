package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaContext.PrintActionType;
import au.org.ala.delta.model.image.ImageType;
import au.org.ala.delta.translation.DataSetTranslator;
import au.org.ala.delta.translation.DataSetTranslatorFactory;
import au.org.ala.delta.translation.print.PrintAction;
import au.org.ala.delta.util.DataSetHelper;

/**
 * Takes action at certain points in the directive parsing lifecycle, for
 * example after the CHARACTER LIST or ITEM DESCRIPTIONS directives have
 * been parsed a translation action may be initiated.
 */
public class ConforDirectiveParserObserver implements DirectiveParserObserver {

    private DeltaContext _context; 
    private DataSetTranslatorFactory _factory;
    private DataSetHelper _helper;
    
    public ConforDirectiveParserObserver(DeltaContext context) {
        _context = context;
        _context.setDirectiveParserObserver(this);
        _factory = new DataSetTranslatorFactory();
        _helper = new DataSetHelper(context.getDataSet());
    }
    
    @Override
    public void preProcess(AbstractDirective<? extends AbstractDeltaContext> directive, String data) {
        _context.ListMessage(directive.getName() + " " +data);
    }

    @Override
    public void postProcess(AbstractDirective<? extends AbstractDeltaContext> directive) {
        if (directive.getControlWords().equals(CharacterList.CONTROL_WORDS) ||
        	directive.getControlWords().equals(KeyCharacterList.CONTROL_WORDS)) {
        	postProcessCharacters();
        }
        else if (directive.getControlWords().equals(ItemDescriptions.CONTROL_WORDS)) {
        	postProcessItems();
        }
    }
    
    @Override
    public void finishedProcessing() {
    	//processPrintActions();
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
	
//	private void processPrintActions() {
//		for (PrintActionType actionType : _context.getPrintActions()) {
//			PrintAction action = _factory.createPrintAction(_context, actionType);
//			action.print();
//		}
//	}
	
	
}

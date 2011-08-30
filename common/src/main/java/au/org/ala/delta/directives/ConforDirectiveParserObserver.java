package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.translation.DataSetTranslator;
import au.org.ala.delta.translation.DataSetTranslatorFactory;

/**
 * Takes action at certain points in the directive parsing lifecycle, for
 * example after the CHARACTER LIST or ITEM DESCRIPTIONS directives have
 * been parsed a translation action may be initiated.
 */
public class ConforDirectiveParserObserver implements DirectiveParserObserver {

    private DeltaContext _context; 
    private DataSetTranslatorFactory _factory;
    
    public ConforDirectiveParserObserver(DeltaContext context) {
        _context = context;
        _context.setDirectiveParserObserver(this);
        _factory = new DataSetTranslatorFactory();
    }
    
    @Override
    public void preProcess(AbstractDirective<? extends AbstractDeltaContext> directive, String data) {
        _context.ListMessage(directive.getName() + " " +data);
    }

    @Override
    public void postProcess(AbstractDirective<? extends AbstractDeltaContext> directive) {
        if (directive.getControlWords().equals(CharacterList.CONTROL_WORDS)) {
        	DataSetTranslator translator = _factory.createTranslator(_context);
    		translator.translateCharacters();
        }
        else if (directive.getControlWords().equals(ItemDescriptions.CONTROL_WORDS)) {
        	DataSetTranslator translator = _factory.createTranslator(_context);
    		translator.translateItems();
        }
    }
}

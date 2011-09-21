package au.org.ala.delta.intkey.directives.invocation;

import java.util.List;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.rtf.RTFBuilder;

public class DescribeDirectiveInvocation implements IntkeyDirectiveInvocation {

    private List<Item> _taxa;
    private List<Character> _characters;

    public void setTaxa(List<Item> taxa) {
        this._taxa = taxa;
    }

    public void setCharacters(List<Character> characters) {
        this._characters = characters;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        ItemFormatter taxonFormatter = new ItemFormatter(false, false, AngleBracketHandlingMode.REMOVE, false, false);
        CharacterFormatter characterFormatter = new CharacterFormatter(false, false, AngleBracketHandlingMode.REMOVE_SURROUNDING_REPLACE_INNER, false);
        AttributeFormatter attributeFormatter = new AttributeFormatter(false, false, false);

        RTFBuilder builder = new RTFBuilder();
        builder.startDocument();

        for (Item taxon : _taxa) {
            builder.appendText(taxonFormatter.formatItemDescription(taxon));
            builder.increaseIndent();
            
            for (Character ch: _characters) {
                Attribute attr = context.getDataset().getAttribute(taxon.getItemNumber(), ch.getCharacterId());
                
                if (!attr.isInapplicable() && !attr.isUnknown()) {
                    String characterDescription = characterFormatter.formatCharacterDescription(ch);
                    String attributeDescription = attributeFormatter.formatAttribute(attr);
                    builder.appendText(String.format("%s %s", characterDescription, attributeDescription));                    
                }

            }
            
            builder.decreaseIndent();
        }
        
        builder.endDocument();

        context.getUI().displayRTFReport(builder.toString(), "Describe");

        return true;
    }

}

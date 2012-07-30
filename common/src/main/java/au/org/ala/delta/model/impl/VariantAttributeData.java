package au.org.ala.delta.model.impl;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.attribute.DefaultParsedAttribute;
import au.org.ala.delta.model.attribute.ParsedAttribute;
import org.apache.commons.lang.StringUtils;

/**
 * Overrides DefaultAttributeData to return the data of the master Item in the case that this Attribute has
 * no coded value.
 */
public class VariantAttributeData extends DefaultAttributeData {

    private Item _masterItem;

    public VariantAttributeData(Item master, Character character) {
        super(character);
        _masterItem = master;
    }

    @Override
    public String getValueAsString() {
        String attributeValue = super.getValueAsString();
        if (StringUtils.isEmpty(attributeValue)) {
            Attribute masterAttribute = _masterItem.getAttribute(_character);
            if (masterAttribute == null) {
                return "";
            }
            return masterAttribute.getValueAsString();
        }
        return attributeValue;
    }

    @Override
    protected DefaultParsedAttribute defaultParsedAttribute() {
        if (StringUtils.isEmpty(super.getValueAsString())) {
            Attribute masterAttribute = _masterItem.getAttribute(_character);
            if (masterAttribute != null) {
                return (DefaultParsedAttribute)masterAttribute.parsedAttribute();
            }
        }
        return (DefaultParsedAttribute)_parsedAttribute;
    }

    @Override
    public ParsedAttribute parsedAttribute() {
        if (isInherited()) {
            Attribute masterAttribute = _masterItem.getAttribute(_character);
            if (masterAttribute != null) {

                return _masterItem.getAttribute(_character).parsedAttribute();
            }
        }
        return _parsedAttribute;
    }

    @Override
    public boolean isInherited() {
        String attributeValue = super.getValueAsString();
        return StringUtils.isEmpty(attributeValue);
    }

}

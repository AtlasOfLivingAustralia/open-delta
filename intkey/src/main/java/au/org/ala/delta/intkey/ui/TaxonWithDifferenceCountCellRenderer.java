package au.org.ala.delta.intkey.ui;

import java.util.HashMap;
import java.util.Map;

import au.org.ala.delta.model.Item;

public class TaxonWithDifferenceCountCellRenderer extends TaxonCellRenderer {

    /**
     * 
     */
    private static final long serialVersionUID = -1387340931499472805L;
    
    private Map<Item, Integer> _differenceCounts;

    public TaxonWithDifferenceCountCellRenderer(Map<Item, Integer> differenceCounts) {
        _differenceCounts = new HashMap<Item, Integer>();
        if (differenceCounts != null) {
            _differenceCounts.putAll(differenceCounts);
        }
    }

    @Override
    protected String getTextForValue(Object value) {
        if (value instanceof Item) {
            Item taxon = (Item) value;
            int differenceCount = 0;

            if (_differenceCounts != null) {
                if (_differenceCounts.containsKey(taxon)) {
                    differenceCount = _differenceCounts.get(taxon);
                }
            }

            return String.format("(%s) %s", differenceCount, _formatter.formatItemDescription(taxon));
        } else {
            return value.toString();
        }
    }

}

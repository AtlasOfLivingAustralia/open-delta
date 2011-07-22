package au.org.ala.delta.intkey.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;

import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.ItemFormatter;

public class TaxonWithDifferenceCountListModel extends TaxonListModel {

    private Map<Item, Integer> _differenceCounts;

    public TaxonWithDifferenceCountListModel(List<Item> items, Map<Item, Integer> differenceCounts) {
        super(items);

        _differenceCounts = differenceCounts;

        if (_differenceCounts != null) {
            // Sort taxa by number of differences, then by
            // taxon number
            Collections.sort(_items, new Comparator<Item>() {

                @Override
                public int compare(Item t1, Item t2) {
                    int diffT1 = _differenceCounts.get(t1);
                    int diffT2 = _differenceCounts.get(t2);

                    if (diffT1 == diffT2) {
                        return t1.compareTo(t2);
                    } else {
                        return Integer.valueOf(diffT1).compareTo(Integer.valueOf(diffT2));
                    }
                }
            });
        }
    }

    @Override
    public Object getElementAt(int index) {
        Item taxon = _items.get(index);
        
        int differenceCount = 0;
        
        if (_differenceCounts != null) {
            differenceCount = _differenceCounts.get(taxon);
        }
        
        return String.format("(%s) %s", differenceCount, _formatter.formatItemDescription(taxon));
    }

}

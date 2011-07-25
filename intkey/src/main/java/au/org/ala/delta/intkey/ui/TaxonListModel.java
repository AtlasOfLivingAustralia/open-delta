package au.org.ala.delta.intkey.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.ItemFormatter;

public class TaxonListModel extends AbstractListModel {

    protected List<Item> _items;
    protected ItemFormatter _formatter;

    public TaxonListModel(List<Item> items) {
        _items = new ArrayList<Item>(items);
        Collections.sort(_items);
        _formatter = new ItemFormatter(false, true, false, false, true, false);
    }

    @Override
    public int getSize() {
        return _items.size();
    }

    @Override
    public Object getElementAt(int index) {
        return _formatter.formatItemDescription(_items.get(index));
    }
    
    public Item getTaxonAt(int index) {
        return _items.get(index);
    }
}

package au.org.ala.delta.intkey.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.ItemFormatter;

public class TaxonListModel extends AbstractListModel {

    List<Item> _items;
    ItemFormatter _formatter;

    public TaxonListModel(List<Item> items) {
        _items = new ArrayList<Item>(items);
        _formatter = new ItemFormatter(false, true, false, true, false);
    }

    @Override
    public int getSize() {
        return _items.size();
    }

    @Override
    public Object getElementAt(int index) {
        return _formatter.formatItemDescription(_items.get(index));
    }
    
    public Item getItemAt(int index) {
        return _items.get(index);
    }
}

package au.org.ala.delta.intkey.ui;

import java.awt.Color;
import java.awt.Component;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ColoringListCellRenderer extends JLabel implements ListCellRenderer {

    /**
     * 
     */
    private static final long serialVersionUID = 8458733723316953454L;
    
    private Set<Integer> _indicesToColor;

    public ColoringListCellRenderer() {
        setOpaque(true);
        _indicesToColor = new HashSet<Integer>();
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        String s = value.toString();
        setText(s);
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            if (_indicesToColor.contains(index)) {
                setForeground(Color.BLUE);
            } else {
                setForeground(list.getForeground());
            }
        }
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);
        return this;
    }

    public void setIndicesToColor(Set<Integer> indicesToColor) {
        _indicesToColor = new HashSet<Integer>(indicesToColor);
    }

}

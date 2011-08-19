package au.org.ala.delta.intkey.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public abstract class ColoringListCellRenderer extends JLabel implements ListCellRenderer {

    /**
     * 
     */
    private static final long serialVersionUID = 8458733723316953454L;
    
    public ColoringListCellRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        String s = getTextForValue(value);
        
        setText(s);
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            if (isValueColored(value)) {
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

    protected abstract String getTextForValue(Object value);
    protected abstract boolean isValueColored(Object value);
}

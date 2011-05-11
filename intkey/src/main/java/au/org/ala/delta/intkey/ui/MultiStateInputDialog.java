package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;

public class MultiStateInputDialog extends CharacterValueInputDialog {
    
    List<Integer> _inputData;

    private JList _list;
    private JScrollPane _scrollPane;

    public MultiStateInputDialog(Frame owner, MultiStateCharacter ch) {
        super(owner, ch);
        setTitle("Select state or states");
        setSize(new Dimension(500, 300));
        
        _scrollPane = new JScrollPane();
        _pnlMain.add(_scrollPane, BorderLayout.CENTER);
        
        _list = new JList();
        _scrollPane.setViewportView(_list);
        
        
        DefaultListModel listModel = new DefaultListModel();
        for (int i=0; i < ch.getNumberOfStates(); i++) {
            listModel.addElement(_formatter.formatState(ch, i + 1));
        }
        
        _list.setModel(listModel);
    }

    @Override
    void handleBtnOKClicked() {
        int[] selectedIndicies = _list.getSelectedIndices();
        _inputData = new ArrayList<Integer>();
        for (int i: selectedIndicies) {
            _inputData.add(i + 1);
        }
        setVisible(false);
    }
    
    public List<Integer> getInputData() {
        return _inputData;
    }
}

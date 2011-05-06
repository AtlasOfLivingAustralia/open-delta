package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import au.org.ala.delta.model.MultiStateCharacter;

public class MultiStateInputDialog extends CharacterValueInputDialog {
    
    MultiStateCharacter _char;
    List<Integer> _inputData;

    private JList _list;
    private JScrollPane _scrollPane;

    public MultiStateInputDialog(Frame owner, MultiStateCharacter ch) {
        super(owner);
        setTitle("Select state or states");
        setSize(new Dimension(500, 300));
        
        JLabel lblSelectValue = new JLabel();
        lblSelectValue.setBorder(new EmptyBorder(0, 0, 5, 0));
        _pnlMain.add(lblSelectValue, BorderLayout.NORTH);
        
        _scrollPane = new JScrollPane();
        _pnlMain.add(_scrollPane, BorderLayout.CENTER);
        
        _list = new JList();
        _scrollPane.setViewportView(_list);
        
        _char = ch;
        lblSelectValue.setText(_char.getDescription());
        _list.setModel(new CharacterStatesListModel(_char));
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
    
    class CharacterStatesListModel extends AbstractListModel {

        private MultiStateCharacter _char;
        
        public CharacterStatesListModel(MultiStateCharacter ch) {
            _char = ch;
        }
        
        @Override
        public int getSize() {
            // TODO Auto-generated method stub
            return _char.getNumberOfStates();
        }

        @Override
        public Object getElementAt(int index) {
            return _char.getState(index + 1);
        }
    }
}

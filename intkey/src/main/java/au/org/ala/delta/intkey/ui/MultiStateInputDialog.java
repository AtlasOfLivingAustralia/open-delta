package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import au.org.ala.delta.model.MultiStateCharacter;

public class MultiStateInputDialog extends CharacterValueInputDialog {

    List<Integer> _inputData;

    private JList _list;
    private JScrollPane _scrollPane;

    public MultiStateInputDialog(Frame owner, MultiStateCharacter ch) {
        super(owner, ch);
        setTitle("Select state or states");
        setSize(new Dimension(600, 350));

        _scrollPane = new JScrollPane();
        _pnlMain.add(_scrollPane, BorderLayout.CENTER);

        _list = new JList();
        _scrollPane.setViewportView(_list);

        DefaultListModel listModel = new DefaultListModel();
        for (int i = 0; i < ch.getNumberOfStates(); i++) {
            listModel.addElement(_formatter.formatState(ch, i + 1));
        }

        _list.setModel(listModel);
        
        _inputData = new ArrayList<Integer>();
    }

    @Override
    void handleBtnOKClicked() {
        int[] selectedIndicies = _list.getSelectedIndices();

        // Show confirmation dialog if all of the states have been
        // selected.
        if (selectedIndicies.length == _list.getModel().getSize()) {
            int dlgSelection = JOptionPane.showConfirmDialog(this, "You have selected all of the character states. Is that what you want?", "Confirm selection", JOptionPane.YES_NO_OPTION);
            if (dlgSelection == JOptionPane.NO_OPTION) {
                return;
            }
        }

        for (int i : selectedIndicies) {
            _inputData.add(i + 1);
        }
        
        setVisible(false);
    }

    public List<Integer> getInputData() {
        return _inputData;
    }

    @Override
    void handleBtnCancelClicked() {
        this.setVisible(false);
    }
}

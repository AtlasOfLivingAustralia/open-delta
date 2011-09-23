package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.image.ImageSettings;

public class MultiStateInputDialog extends CharacterValueInputDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -7594452342030275494L;

    Set<Integer> _inputData;

    private JList _list;
    private JScrollPane _scrollPane;

    @Resource
    String title;
    
    @Resource
    String selectionConfirmationMessage;
    
    @Resource
    String selectionConfirmationTitle;    

    public MultiStateInputDialog(Frame owner, MultiStateCharacter ch, ImageSettings imageSettings, boolean displayNumbering) {
        super(owner, ch, imageSettings, displayNumbering);
        
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(MultiStateInputDialog.class);
        resourceMap.injectFields(this);
        
        setTitle(title);
        setPreferredSize(new Dimension(600, 350));

        _scrollPane = new JScrollPane();
        _pnlMain.add(_scrollPane, BorderLayout.CENTER);

        _list = new JList();
        _scrollPane.setViewportView(_list);

        DefaultListModel listModel = new DefaultListModel();
        for (int i = 0; i < ch.getNumberOfStates(); i++) {
            listModel.addElement(_formatter.formatState(ch, i + 1));
        }

        _list.setModel(listModel);
        
        _inputData = new HashSet<Integer>();

    }

    @Override
    void handleBtnOKClicked() {
        int[] selectedIndicies = _list.getSelectedIndices();

        // Show confirmation dialog if all of the states have been
        // selected.
        if (selectedIndicies.length == _list.getModel().getSize()) {
            int dlgSelection = JOptionPane.showConfirmDialog(this, selectionConfirmationMessage, selectionConfirmationTitle, JOptionPane.YES_NO_OPTION);
            if (dlgSelection == JOptionPane.NO_OPTION) {
                return;
            }
        }

        for (int i : selectedIndicies) {
            _inputData.add(i + 1);
        }
        
        setVisible(false);
    }

    @Override
    void handleBtnCancelClicked() {
        this.setVisible(false);
    }
    

    @Override
    void handleBtnImagesClicked() {
        CharacterImageInputDialog dlg = new CharacterImageInputDialog(this,  (MultiStateCharacter) _ch, _imageSettings, true);
        dlg.setVisible(true);
        
        Set<Integer> selectedStates = dlg.getSelectedStates();
        if (selectedStates != null) {
            _inputData.addAll(selectedStates);
            this.setVisible(false);
        }
        
    }
    
    public Set<Integer> getInputData() {
        return _inputData;
    }

}

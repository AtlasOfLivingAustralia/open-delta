package au.org.ala.delta.intkey.ui;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import au.org.ala.delta.model.TextCharacter;

public class TextInputDialog extends CharacterValueInputDialog {
    private JPanel _pnlTxtFld;
    private JTextField _txtInput;
    private TextCharacter _char;
    private List<String> _inputData;
    public TextInputDialog(Frame owner, TextCharacter ch) {
        super(owner);
        setTitle("Enter text");
        _pnlMain.setLayout(new BorderLayout(0, 0));
        
        _pnlTxtFld = new JPanel();
        _pnlMain.add(_pnlTxtFld, BorderLayout.CENTER);
        _pnlTxtFld.setLayout(new BorderLayout(0, 0));
        
        _txtInput = new JTextField();
        _pnlTxtFld.add(_txtInput, BorderLayout.NORTH);
        _txtInput.setColumns(10);
        
        JLabel lblEnterTextThere = new JLabel();
        _pnlMain.add(lblEnterTextThere, BorderLayout.NORTH);
        lblEnterTextThere.setBorder(new EmptyBorder(0, 0, 5, 0));
        
        _char = ch;
        lblEnterTextThere.setText(_char.getDescription());
        _inputData = null;
    }
    
    @Override
    void handleBtnOKClicked() {
        String data = _txtInput.getText();
        _inputData = new ArrayList<String>(Arrays.asList(data.split("/")));
        setVisible(false);
    }
    
    public List<String> getInputData() {
        return _inputData;
    }
    
    

}

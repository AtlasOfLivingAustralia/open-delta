package au.org.ala.delta.intkey.ui;

import java.awt.Frame;

import javax.swing.JOptionPane;

import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.intkey.directives.ParsingUtils;
import au.org.ala.delta.model.IntegerCharacter;

public class IntegerInputDialog extends NumberInputDialog {

    private IntRange _inputData;

    public IntegerInputDialog(Frame owner, IntegerCharacter ch) {
        super(owner, ch);
        _inputData = null;
    }

    @Override
    void handleBtnOKClicked() {
        String inputTxt = _txtInput.getText();
        if (inputTxt.length() > 0) {
            try {
                _inputData = ParsingUtils.parseIntegerCharacterValue(inputTxt);
                this.setVisible(false);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Integer value(s) required", "Invalid input", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            this.setVisible(false);
        }
    }

    public IntRange getInputData() {
        return _inputData;
    }

    @Override
    void handleBtnCancelClicked() {
        this.setVisible(false);
    }

}

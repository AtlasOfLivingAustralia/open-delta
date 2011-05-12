package au.org.ala.delta.intkey.ui;

import java.awt.Frame;

import javax.swing.JOptionPane;

import org.apache.commons.lang.math.FloatRange;

import au.org.ala.delta.intkey.directives.ParsingUtils;
import au.org.ala.delta.model.RealCharacter;

public class RealInputDialog extends NumberInputDialog {

    private FloatRange _inputData;

    public RealInputDialog(Frame owner, RealCharacter ch) {
        super(owner, ch);
        _inputData = null;
    }

    @Override
    void handleBtnOKClicked() {
        String inputTxt = _txtInput.getText();
        if (inputTxt.length() > 0) {
            try {
                _inputData = ParsingUtils.parseRealCharacterValue(inputTxt);
                setVisible(false);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Real value(s) required", "Invalid input", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            setVisible(false);
        }
    }

    public FloatRange getInputData() {
        return _inputData;
    }

    @Override
    void handleBtnCancelClicked() {
        this.setVisible(false);
    }

}

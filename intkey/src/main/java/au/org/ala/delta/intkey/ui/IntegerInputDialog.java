package au.org.ala.delta.intkey.ui;

import java.awt.Frame;

import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.intkey.directives.ParsingUtils;
import au.org.ala.delta.model.IntegerCharacter;

public class IntegerInputDialog extends NumberInputDialog {

    private IntRange _inputData;
    
    public IntegerInputDialog(Frame owner, IntegerCharacter ch) {
        super(owner, ch);
    }

    @Override
    void handleBtnOKClicked() {
        String inputTxt = _txtInput.getText();
        _inputData = ParsingUtils.parseIntegerCharacterValue(inputTxt);
        setVisible(false);
    }
    
    public IntRange getInputData() {
        return _inputData;
    }

}

package au.org.ala.delta.intkey.ui;

import java.awt.Frame;

import org.apache.commons.lang.math.FloatRange;

import au.org.ala.delta.intkey.directives.ParsingUtils;
import au.org.ala.delta.model.RealCharacter;


public class RealInputDialog extends NumberInputDialog {

    private FloatRange _inputData;
    
    public RealInputDialog(Frame owner, RealCharacter ch) {
        super(owner, ch);
    }

    @Override
    void handleBtnOKClicked() {
        String inputTxt = _txtInput.getText();
        _inputData = ParsingUtils.parseRealCharacterValue(inputTxt);
        setVisible(false);
    }
    
    public FloatRange getInputData() {
        return _inputData;
    }

}

package au.org.ala.delta.intkey.ui;

import java.awt.Frame;

import javax.swing.JOptionPane;

import org.apache.commons.lang.math.FloatRange;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.intkey.directives.ParsingUtils;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.image.ImageSettings;

public class RealInputDialog extends NumberInputDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -3565062911844305863L;

    private FloatRange _inputData;

    @Resource
    String title;

    @Resource
    String validationErrorMessage;

    @Resource
    String validationErrorTitle;

    public RealInputDialog(Frame owner, RealCharacter ch, ImageSettings imageSettings) {
        super(owner, ch, imageSettings);

        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(RealInputDialog.class);
        resourceMap.injectFields(this);

        setTitle(title);

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
                JOptionPane.showMessageDialog(this, validationErrorMessage, validationErrorTitle, JOptionPane.ERROR_MESSAGE);
            }
        } else {
            setVisible(false);
        }
    }

    @Override
    void handleBtnCancelClicked() {
        this.setVisible(false);
    }

    @Override
    void handleBtnImagesClicked() {
        CharacterImageInputDialog dlg = new CharacterImageInputDialog(this, _ch, _imageSettings);
        dlg.setVisible(true);
        
        try {
            FloatRange rangeFromImageDialog = dlg.getInputRealValues();
            if (rangeFromImageDialog != null) {
                _inputData = rangeFromImageDialog;
                this.setVisible(false);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, validationErrorMessage, validationErrorTitle, JOptionPane.ERROR_MESSAGE);
        }
    }

    public FloatRange getInputData() {
        return _inputData;
    }

}

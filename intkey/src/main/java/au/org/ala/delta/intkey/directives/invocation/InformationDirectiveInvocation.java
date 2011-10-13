package au.org.ala.delta.intkey.directives.invocation;

import java.util.List;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Item;

public class InformationDirectiveInvocation extends IntkeyDirectiveInvocation {

    private String _imagesAutoDisplayText;
    private String _otherItemsAutoDisplayText;
    private boolean _closePromptAfterAutoDisplay;

    private List<Item> _taxa;

    public void setTaxa(List<Item> taxa) {
        this._taxa = taxa;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        context.getUI().displayTaxonInformation(_taxa, _imagesAutoDisplayText, _otherItemsAutoDisplayText, _closePromptAfterAutoDisplay);
        return false;
    }

    public void setImagesAutoDisplayText(String imagesAutoDisplayText) {
        this._imagesAutoDisplayText = imagesAutoDisplayText;
    }

    public void setOtherItemsAutoDisplayText(String otherItemsAutoDisplayText) {
        this._otherItemsAutoDisplayText = otherItemsAutoDisplayText;
    }

    public void setClosePromptAfterAutoDisplay(boolean closePromptAfterAutoDisplay) {
        this._closePromptAfterAutoDisplay = closePromptAfterAutoDisplay;
    }
}

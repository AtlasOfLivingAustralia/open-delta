package au.org.ala.delta.intkey.directives.invocation;

import java.text.MessageFormat;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;

public class DisplayCharacterOrderSeparateDirectiveInvocation extends IntkeyDirectiveInvocation {

    private Item _taxonToSeparate;
    private ItemFormatter _formatter;

    public DisplayCharacterOrderSeparateDirectiveInvocation(Item taxonToSeparate) {
        _taxonToSeparate = taxonToSeparate;
        _formatter = new ItemFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, true, false, true);
    }

    @Override
    public boolean execute(IntkeyContext context) {
        if (!context.getAvailableTaxa().contains(_taxonToSeparate)) {
            context.getUI().displayErrorMessage(
                    MessageFormat.format(UIUtils.getResourceString("DisplayCharacterOrderSeparate.TaxonNoLongerInContentionMsg"), _formatter.formatItemDescription(_taxonToSeparate)));
            return false;
        }

        context.setCharacterOrderSeparate(_taxonToSeparate.getItemNumber());
        return true;
    }

    @Override
    public String toString() {
        return String.format("DISPLAY CHARACTERORDER SEPARATE %s", _taxonToSeparate.getItemNumber());
    }
}

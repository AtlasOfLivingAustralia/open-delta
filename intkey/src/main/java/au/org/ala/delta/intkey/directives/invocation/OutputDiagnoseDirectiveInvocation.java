package au.org.ala.delta.intkey.directives.invocation;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.ReportUtils;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.Specimen;
import au.org.ala.delta.util.Utils;


public class OutputDiagnoseDirectiveInvocation extends AbstractDiagnoseDirectiveInvocation {
    
    private IntkeyContext _context;

    @Override
    public String doRunInBackground(IntkeyContext context) throws IntkeyDirectiveInvocationException {
        _context = context;
        if (_context.getLastOutputLineWasComment()) {
            _context.setLastOutputLineWasComment(false);
        } else {
            //TODO - need to include arguments to command here
            _context.appendTextToOutputFile("OUTPUT DIAGNOSE");
        }
        
        doDiagnose(context, UIUtils.getResourceString("OutputDiagnoseDirective.Progress.Generating"));
        
        return null;
    }
    
    @Override
    protected void handleProcessingDone(IntkeyContext context, String result) {
        // do nothing - output file is updated by doRunInBackground
    }
    
    @Override
    protected void handleStartProcessingTaxon(Item taxon) {
        // do nothing
    }

    @Override
    protected void handleCharacterUsed(Attribute attr) {
        // do nothing
    }

    @Override
    protected void handleDiagLevelAttained(int diagLevel) {
        // do nothing
    }

    @Override
    protected void handleDiagLevelNotAttained(int diagLevel) {
        // do nothing
    }

    @Override
    protected void handleEndProcessingTaxon(Item taxon, boolean diagLevelNotAttained, Specimen specimen, List<Item> remainingTaxa) {
        List<au.org.ala.delta.model.Character> usedCharacters = specimen.getUsedCharacters();
        
        //used characters are returned in the order in which they were used. sort them into the order in which they appear
        //in the dataset
        Collections.sort(usedCharacters);
        
        _context.appendTextToOutputFile(MessageFormat.format("#{0}. {1}", taxon.getItemNumber(), Utils.formatIntegersAsListOfRanges(ReportUtils.characterListToIntegerList(usedCharacters))));
    }

}

package au.org.ala.delta.intkey.directives.invocation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.intkey.model.DiagType;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.rtf.RTFBuilder;
import au.org.ala.delta.rtf.RTFUtils;
import au.org.ala.delta.util.Utils;

public class StatusSetDirectiveInvocation extends IntkeyDirectiveInvocation {

    @Override
    public boolean execute(IntkeyContext context) throws IntkeyDirectiveInvocationException {
        RTFBuilder builder = new RTFBuilder();
        builder.startDocument();

        builder.setTextColor(Color.BLUE);
        builder.appendText(UIUtils.getResourceString("Status.Set.title"));
        builder.setTextColor(Color.BLACK);
        builder.appendText(UIUtils.getResourceString("Status.Set.heading"));

        String autoToleranceSetting = context.isAutoTolerance() ? UIUtils.getResourceString("Status.Set.onValue") : UIUtils.getResourceString("Status.Set.offValue");
        int stopBestSetting = context.getStopBest();
        double rbaseSetting = context.getRBase();
        int toleranceSetting = context.getTolerance();
        double varywt = context.getVaryWeight();
        // TODO need to implement set demonstration
        String demonstrationSetting = "TODO";
        String imagePaths = RTFUtils.escapeRTF(StringUtils.join(context.getImageSettings().getResourcePathLocations(), ";"));
        String infoPaths = RTFUtils.escapeRTF(StringUtils.join(context.getInfoSettings().getResourcePathLocations(), ";"));

        builder.appendText(UIUtils.getResourceString("Status.Set.line1", autoToleranceSetting, stopBestSetting, rbaseSetting, toleranceSetting, varywt, demonstrationSetting, imagePaths, infoPaths));

        StringBuilder matchValueBuilder = new StringBuilder();
        if (context.getMatchInapplicables()) {
            matchValueBuilder.append(UIUtils.getResourceString("Status.Set.inapplicablesMatchValue"));
            matchValueBuilder.append(" ");
        }

        if (context.getMatchUnknowns()) {
            matchValueBuilder.append(UIUtils.getResourceString("Status.Set.unknownsMatchValue"));
            matchValueBuilder.append(" ");
        }

        switch (context.getMatchType()) {
        case OVERLAP:
            matchValueBuilder.append(UIUtils.getResourceString("Status.Set.overlapMatchValue"));
            break;
        case SUBSET:
            matchValueBuilder.append(UIUtils.getResourceString("Status.Set.subsetMatchValue"));
            break;
        case EXACT:
            matchValueBuilder.append(UIUtils.getResourceString("Status.Set.exactMatchValue"));
            break;
        default:
            throw new IllegalArgumentException("Unrecognized match type");
        }

        builder.appendText(UIUtils.getResourceString("Status.Set.matchSettings", matchValueBuilder.toString()));

        int diagLevel = context.getDiagLevel();
        String diagTypeString;
        if (context.getDiagType() == DiagType.SPECIMENS) {
            diagTypeString = UIUtils.getResourceString("Status.Set.specimensDiagType");
        } else {
            diagTypeString = UIUtils.getResourceString("Status.Set.taxaDiagType");
        }
        
        builder.appendText(UIUtils.getResourceString("Status.Set.diagSettings", diagLevel, diagTypeString));

        Set<Character> exactCharacters = context.getExactCharacters();
        List<Integer> exactCharacterNumbers = new ArrayList<Integer>();
        for (Character ch : exactCharacters) {
            exactCharacterNumbers.add(ch.getCharacterId());
        }
        Collections.sort(exactCharacterNumbers);

        String exactCharacterNumbersAsString;
        if (exactCharacterNumbers.isEmpty()) {
            exactCharacterNumbersAsString = UIUtils.getResourceString("Status.Set.emptyCharacterSet");
        } else {
            exactCharacterNumbersAsString = Utils.formatIntegersAsListOfRanges(exactCharacterNumbers);
        }

        builder.appendText(UIUtils.getResourceString("Status.Set.exactCharacters", exactCharacterNumbersAsString));

        List<Integer> fixedCharacterNumbers = context.getFixedCharactersList();
        Collections.sort(fixedCharacterNumbers);

        String fixedCharacterNumbersAsString;
        if (fixedCharacterNumbers.isEmpty()) {
            fixedCharacterNumbersAsString = UIUtils.getResourceString("Status.Set.emptyCharacterSet");
        } else {
            fixedCharacterNumbersAsString = Utils.formatIntegersAsListOfRanges(fixedCharacterNumbers);
        }

        builder.appendText(UIUtils.getResourceString("Status.Set.fixedCharacters", fixedCharacterNumbersAsString));

        builder.endDocument();

        context.getUI().displayRTFReport(builder.toString(), UIUtils.getResourceString("Status.title"));

        return true;
    }
    
    private String buildReliabilitiesString(List<Character> characters) {
        StringBuilder builder = new StringBuilder();
        
        int startRange;
        int currentReliabilityValue;
        
        for (int i=0; i < characters.size(); i++) {
            
        }
        
        return builder.toString();
    }

}

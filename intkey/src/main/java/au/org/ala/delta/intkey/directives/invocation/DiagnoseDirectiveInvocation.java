/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 * 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.intkey.directives.invocation;

import java.awt.Color;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.Specimen;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.rtf.RTFBuilder;

public class DiagnoseDirectiveInvocation extends AbstractDiagnoseDirectiveInvocation {

    private ItemFormatter _itemFormatter;
    private ItemFormatter _noNumberingRTFCommentsItemFormatter;
    private ItemFormatter _noCommentsItemFormatter;
    
    private CharacterFormatter _characterFormatter;
    private AttributeFormatter _attributeFormatter;
    private RTFBuilder _builder = new RTFBuilder();


    @Override
    public String doRunInBackground(IntkeyContext context) throws IntkeyDirectiveInvocationException {
        _itemFormatter = new ItemFormatter(context.displayNumbering(), CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REMOVE, false, false, true);
        _noNumberingRTFCommentsItemFormatter = new ItemFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, true, false, true);
        _noCommentsItemFormatter = new ItemFormatter(context.displayNumbering(), CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, false, false, true);
        
        _characterFormatter = new CharacterFormatter(context.displayNumbering(), CommentStrippingMode.RETAIN_SURROUNDING_STRIP_INNER, AngleBracketHandlingMode.REMOVE, false, true);
        _attributeFormatter = new AttributeFormatter(context.displayNumbering(), false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, true, context.getDataset()
                .getOrWord());
        _builder = new RTFBuilder();
        _builder.startDocument();

        doDiagnose(context, UIUtils.getResourceString("DiagnoseDirective.Progress.Generating"));

        _builder.endDocument();
        
        return _builder.toString();
    }
    
    @Override
    protected void handleProcessingDone(IntkeyContext context, String result) {
        context.getUI().displayRTFReport(result, UIUtils.getResourceString("DiagnoseDirective.ReportTitle"));
    }

    @Override
    protected void handleCharacterUsed(Attribute attr) {
        _builder.appendText(String.format("%s %s", _characterFormatter.formatCharacterDescription(attr.getCharacter()), _attributeFormatter.formatAttribute(attr)));
    }

    @Override
    protected void handleStartProcessingTaxon(Item taxon) {
        _builder.appendText(_itemFormatter.formatItemDescription(taxon));
        _builder.increaseIndent();
    }
    
    @Override
    protected void handleDiagLevelAttained(int diagLevel) {
        _builder.setTextColor(Color.RED);
        _builder.appendText(UIUtils.getResourceString("DiagnoseDirective.DiagnosticLevelAttained", diagLevel));
        _builder.setTextColor(Color.BLACK);
    }

    @Override
    protected void handleDiagLevelNotAttained(int diagLevel) {
        _builder.setTextColor(Color.RED);
        _builder.appendText(UIUtils.getResourceString("DiagnoseDirective.DiagnosticLevelNotAttained", diagLevel));
        _builder.setTextColor(Color.BLACK);
    }

    @Override
    protected void handleEndProcessingTaxon(Item taxon, boolean diagLevelNotAttained, Specimen specimen, List<Item> remainingTaxa) {
        if (diagLevelNotAttained) {
            _builder.setTextColor(Color.RED);
            _builder.appendText(UIUtils.getResourceString("DiagnoseDirective.DiagnoseIncomplete", _noNumberingRTFCommentsItemFormatter.formatItemDescription(taxon)));
            _builder.setTextColor(Color.BLACK);
        }

        _builder.decreaseIndent();
        
        // Output number of differences for each remaining taxon if the desired diagLevel was not reached.
        if (diagLevelNotAttained) {
            _builder.setTextColor(Color.RED);
            if (remainingTaxa.size() == 1) {
                _builder.appendText(UIUtils.getResourceString("OneTaxonRemains.log"));
            } else {
                _builder.appendText(UIUtils.getResourceString("MultipleTaxaRemain.log", remainingTaxa.size()));
            }
            
            _builder.setTextColor(Color.BLACK);
            Map<Item, Set<Character>> taxonDifferences = specimen.getTaxonDifferences();
            for (Item remainingTaxon: remainingTaxa) {
                int numDifferences = taxonDifferences.get(remainingTaxon).size();
                _builder.appendText(MessageFormat.format("({0}) {1}", numDifferences, _noCommentsItemFormatter.formatItemDescription(remainingTaxon)));
            }
        }
        
    }

}

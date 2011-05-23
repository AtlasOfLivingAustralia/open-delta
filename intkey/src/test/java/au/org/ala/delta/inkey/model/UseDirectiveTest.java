package au.org.ala.delta.inkey.model;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.lang.math.FloatRange;
import org.junit.Test;

import au.org.ala.delta.intkey.directives.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.model.specimen.MultiStateValue;
import au.org.ala.delta.intkey.model.specimen.RealValue;
import au.org.ala.delta.intkey.model.specimen.Specimen;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.UnorderedMultiStateCharacter;

public class UseDirectiveTest extends TestCase {
    
    @Test
    public void testControllingCharactersSet() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/controlling_characters_simple/intkey.ink");
        IntkeyContext context = new IntkeyContext(null);
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        
        IntkeyDataset ds = context.getDataset();
        
        UnorderedMultiStateCharacter charSeedPresence = (UnorderedMultiStateCharacter) ds.getCharacter(2);
        UnorderedMultiStateCharacter charSeedInShell = (UnorderedMultiStateCharacter) ds.getCharacter(3);
        RealCharacter charAvgThickness = (RealCharacter) ds.getCharacter(4);
        
        IntkeyDirectiveInvocation invoc = new UseDirective().doProcess(context, "4,1");
        context.executeDirective(invoc);
        
        Specimen specimen = context.getSpecimen();
        
        MultiStateValue charSeedPresenceValue = (MultiStateValue) specimen.getValueForCharacter(charSeedPresence);
        assertEquals(1, charSeedPresenceValue.getStateValues().size());
        assertEquals(1, (int) charSeedPresenceValue.getStateValues().get(0));
        
        MultiStateValue charSeedInShellValue = (MultiStateValue) specimen.getValueForCharacter(charSeedInShell);
        assertEquals(1, charSeedInShellValue.getStateValues().size());
        assertEquals(1, (int) charSeedInShellValue.getStateValues().get(0));        
        
        RealValue charAvgThicknessValue = (RealValue) specimen.getValueForCharacter(charAvgThickness);
        assertEquals(new FloatRange(1.0, 1.0), charAvgThicknessValue.getRange());
    }
    
    @Test
    public void testDependentCharactersRemoved() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/controlling_characters_simple/intkey.ink");
        IntkeyContext context = new IntkeyContext(null);
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        
        IntkeyDataset ds = context.getDataset();
        
        UnorderedMultiStateCharacter charSeedPresence = (UnorderedMultiStateCharacter) ds.getCharacter(2);
        UnorderedMultiStateCharacter charSeedInShell = (UnorderedMultiStateCharacter) ds.getCharacter(3);
        RealCharacter charAvgThickness = (RealCharacter) ds.getCharacter(4);
        
        IntkeyDirectiveInvocation invoc = new UseDirective().doProcess(context, "4,1");
        context.executeDirective(invoc);
        
        IntkeyDirectiveInvocation invoc2 = new UseDirective().doProcess(context, "/M 2,2");
        context.executeDirective(invoc2);
        
        Specimen specimen = context.getSpecimen();
        
        MultiStateValue charSeedPresenceValue = (MultiStateValue) specimen.getValueForCharacter(charSeedPresence);
        assertEquals(1, charSeedPresenceValue.getStateValues().size());
        assertEquals(2, (int) charSeedPresenceValue.getStateValues().get(0));
        
        assertFalse(specimen.hasValueFor(charSeedInShell));
        assertFalse(specimen.hasValueFor(charAvgThickness));
    }
}

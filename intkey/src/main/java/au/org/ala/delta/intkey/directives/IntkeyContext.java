package au.org.ala.delta.intkey.directives;

import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.intkey.Intkey;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.model.IntkeyDatasetFileBuilder;

/**
 * Controller? Handles input and updates UI, model accordingly.
 * @author Chris
 *
 */
public class IntkeyContext extends AbstractDeltaContext {
    
    //dataset
    //other settings
    
    //set of commands that have been run
    //other stuff
    
    private File _taxaFile;
    private File _charactersFile;
    
    private IntkeyDataset _dataset;
    private File _datasetInitFile;
    
    private Intkey _appUI;
    
    public IntkeyContext(Intkey appUI) {
        _appUI = appUI;
    }
    
    public void setFileCharacters(String fileName) {
        System.out.println("Setting characters file to: " + fileName);
        _charactersFile = new File(_datasetInitFile.getParentFile(), fileName);
        
        if (_dataset == null && _taxaFile != null) {
            createNewDataSet();
        } else {
            _dataset = null;
            _charactersFile = null;
        }
    }
    
    public void setFileTaxa(String fileName) {
        System.out.println("Setting taxa file to: " + fileName);
        _taxaFile = new File(_datasetInitFile.getParentFile(), fileName);
    }
    
    private void createNewDataSet() {
        _dataset = new IntkeyDatasetFileBuilder().readDataSet(_charactersFile, _taxaFile);
        _appUI.handleNewDataSet(_dataset);
    }
    
    public void newDataSetFile(String fileName) {
        System.out.println("Reading in new Data Set file from: " + fileName);
        
        IntkeyDirectiveParser parser = IntkeyDirectiveParser.createInstance();
        
        try {
            _datasetInitFile = new File(fileName);
            parser.parse(new File(fileName), this);
        } catch (IOException ex) {
            Logger.log(ex.getMessage());
        }
    }
    
    public void executeFunctor(IntkeyDirectiveInvocation invoc) {
        invoc.execute(this);
    }
    
    public JFrame getMainFrame() {
        return _appUI.getMainFrame();
    }
    
}

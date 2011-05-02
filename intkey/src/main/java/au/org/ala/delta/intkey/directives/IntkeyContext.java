package au.org.ala.delta.intkey.directives;

import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.intkey.Intkey;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.model.IntkeyDatasetFileBuilder;

/**
 * Controller? Handles input and updates UI, model accordingly.
 * 
 * @author Chris
 * 
 */
public class IntkeyContext extends AbstractDeltaContext {

    // dataset
    // other settings

    // set of commands that have been run
    // other stuff

    private File _taxaFile;
    private File _charactersFile;

    private IntkeyDataset _dataset;
    private File _datasetInitFile;

    private Intkey _appUI;

    public IntkeyContext(Intkey appUI) {
        _appUI = appUI;
    }

    public void setFileCharacters(String fileName) {
        Logger.log("Setting characters file to: %s", fileName);

        if (_datasetInitFile != null) {
            _charactersFile = new File(_datasetInitFile.getParentFile(), fileName);
        } else {
            _charactersFile = new File(fileName);
        }

        if (!_charactersFile.exists()) {
            String absoluteFileName = _charactersFile.getAbsolutePath();
            _charactersFile = null;
            throw new IllegalArgumentException(String.format("Characters file '%s' could not be found", absoluteFileName));
        }

        if (_dataset == null && _taxaFile != null) {
            createNewDataSet();
        } else {
            _dataset = null;
            _taxaFile = null;
        }
    }

    public void setFileTaxa(String fileName) {
        Logger.log("Setting taxa file to: %s", fileName);

        if (_datasetInitFile != null) {
            _taxaFile = new File(_datasetInitFile.getParentFile(), fileName);
        } else {
            _taxaFile = new File(fileName);
        }
        
        if (!_taxaFile.exists()) {
            String absoluteFileName = _taxaFile.getAbsolutePath();
            _taxaFile = null;
            throw new IllegalArgumentException(String.format("Taxa file '%s' could not be found", absoluteFileName));
        }

        if (_dataset == null && _charactersFile != null) {
            createNewDataSet();
        } else {
            _dataset = null;
            _charactersFile = null;
        }
    }

    private void createNewDataSet() {
        _dataset = new IntkeyDatasetFileBuilder().readDataSet(_charactersFile, _taxaFile);
        _appUI.handleNewDataSet(_dataset);
    }

    public void newDataSetFile(String fileName) {
        Logger.log("Reading in new Data Set file from: %s", fileName);

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

    public IntkeyDataset getDataset() {
        return _dataset;
    }

    public void useCharacters() {
        Logger.log("Using characters");
    }

}

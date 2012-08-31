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
package au.org.ala.delta.intkey.model;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.util.Pair;

import junit.framework.TestCase;

/**
 * A unit test that requires an intkey dataset to be loaded
 * 
 * @author ChrisF
 * 
 */
public abstract class IntkeyDatasetTestCase extends TestCase {

    /**
     * Called by individual test methods to initialize an IntkeyContext and load
     * the specified dataset
     * 
     * @param resourcePathToDataset
     *            A resource path to the dataset to be loaded
     * @return An initialized IntkeyContext with the specified dataset loaded.
     * @throws Exception
     */
    public IntkeyContext loadDataset(String resourcePathToDataset) throws Exception {
        URL initFileUrl = getClass().getResource(resourcePathToDataset);
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        context.newDataSetFile(initFileUrl);

        return context;
    }

    public void loadNewDatasetInExistingContext(String resourcePathToDataset, IntkeyContext context) throws Exception {
        URL initFileUrl = getClass().getResource(resourcePathToDataset);
        context.newDataSetFile(initFileUrl);
    }
    
    public static Pair<List<Integer>, List<Integer>> getCharacterAndTaxonNumbersForBest(IntkeyContext context) {
        List<Integer> characterNumbers = new ArrayList<Integer>();
        List<Integer> taxonNumbers = new ArrayList<Integer>();

        List<Character> availableCharacters = context.getAvailableCharacters();
        availableCharacters.removeAll(context.getDataset().getCharactersToIgnoreForBest());

        for (Character ch : availableCharacters) {
            characterNumbers.add(ch.getCharacterId());
        }

        for (Item taxon : context.getAvailableTaxa()) {
            taxonNumbers.add(taxon.getItemNumber());
        }

        return new Pair<List<Integer>, List<Integer>>(characterNumbers, taxonNumbers);
    }
}

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

import javax.swing.SwingWorker;

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
        SwingWorker<?, ?> worker = context.newDataSetFile(new File(initFileUrl.toURI()));

        // The dataset is loaded on a separate thread so we need to wait until
        // it is loaded.
        worker.get();

        return context;
    }

    public void loadNewDatasetInExistingContext(String resourcePathToDataset, IntkeyContext context) throws Exception {
        URL initFileUrl = getClass().getResource(resourcePathToDataset);
        SwingWorker<?, ?> worker = context.newDataSetFile(new File(initFileUrl.toURI()));

        // The dataset is loaded on a separate thread so we need to wait until
        // it is loaded.
        worker.get();
    }
}

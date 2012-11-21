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

import java.io.File;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.ui.UIUtils;

public class FileInputDirectiveInvocation extends LongRunningIntkeyDirectiveInvocation<Void> {

    private File _file;

    public void setFile(File file) {
        this._file = file;
    }

    @Override
    protected Void doRunInBackground(IntkeyContext context) throws IntkeyDirectiveInvocationException {
        progress(UIUtils.getResourceString("FileInputDirective.Progress"));
        context.processInputFile(_file);
        
        return null;
    }

    @Override
    protected void handleProcessingDone(IntkeyContext context, Void result) {
        // Update the UI as the input file may have included operations that have modified the UI which will have been ignored given that this
        // task is done in the background and all UI updates are ignored from background tasks.
        context.getUI().handleUpdateAll();
    }

}

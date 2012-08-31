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

import au.org.ala.delta.intkey.model.IntkeyContext;

public abstract class LongRunningIntkeyDirectiveInvocation<T> implements IntkeyDirectiveInvocation {
    
    protected DirectiveInvocationProgressHandler _progressHandler;

    private String _stringRepresentation;
    
    @Override
    public void setStringRepresentation(String stringRepresentation) {
        this._stringRepresentation = stringRepresentation;
    }
    
    @Override
    public String toString() {
        return _stringRepresentation;
    }

    /**
     * Calling execute on this class causes the entirety if the work to be done of the calling thread. 
     */
    @Override
    public final boolean execute(IntkeyContext context) throws IntkeyDirectiveInvocationException {
        T result = runInBackground(context, null);
        done(context, result);
        
        return true;
    }
    
    public T runInBackground(IntkeyContext context, DirectiveInvocationProgressHandler progressHandler) throws IntkeyDirectiveInvocationException {
        _progressHandler = progressHandler;
        return doRunInBackground(context);
    }
    
    protected abstract T doRunInBackground(IntkeyContext context) throws IntkeyDirectiveInvocationException;
    
    public void done(IntkeyContext context, Object result) {
        handleProcessingDone(context, (T) result); 
    }
    
    protected abstract void handleProcessingDone(IntkeyContext context, T result);
    
    protected void progress(String progressMessage) {
        if (_progressHandler != null) {
            _progressHandler.progress(progressMessage);
        }
    }

}

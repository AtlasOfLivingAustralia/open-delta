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
package au.org.ala.delta.ui;

import java.lang.reflect.Method;
import java.util.ResourceBundle;

import javax.swing.Action;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.utils.AppHelper;
import org.jdesktop.application.utils.OSXAdapter;
import org.jdesktop.application.utils.PlatformType;

public abstract class DeltaSingleFrameApplication extends SingleFrameApplication {

	public static void setupMacSystemProperties(Class<? extends Application> applicationClass) {
		if (isMac()) {
		
			// Unfortunately setting the application about name in a SAF lifecycle method is too late
			// which means we can't use the SAF resource injection.
			String packageName = applicationClass.getPackage().getName();
			String bundleName = packageName+".resources."+applicationClass.getSimpleName();
	
			ResourceBundle bundle = ResourceBundle.getBundle(bundleName);
			String name = bundle.getString("Application.shortName");
			
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", name);
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("apple.awt.fileDialogForDirectories", "true");
			
		}
		
	}
	
	/**
	 * Returns true if this application is running on a MAC.
	 * 
	 */
	public static boolean isMac() {
		return PlatformType.OS_X.equals(AppHelper.getPlatform());
	}
	
	/** Action to invoke to display the about box */
	protected Action _showAboutAction;
	
	public void showAboutBox() {
		_showAboutAction.actionPerformed(null);
	}
	
	/**
	 * Configures the MAC application menu item (About <application>) to invoke the supplied 
	 * Action.
	 * @param aboutBoxAction the Action to invoke from the MAC about menu.
	 */
	protected void configureMacAboutBox(Action aboutBoxAction) {
		_showAboutAction = aboutBoxAction;
		 try {
			 Method showAboutBox = DeltaSingleFrameApplication.class.getDeclaredMethod("showAboutBox", null);
			 OSXAdapter.setAboutHandler(this, showAboutBox);
		 }
		 catch (Exception e) {
			 e.printStackTrace();
		 }
	}
	
	/**
     * Overrides shutdown to prevent NullPointerExceptions being output in Ubuntu.
     * (Frames are opened maximised by default so the bounds listener never gets the 
     * normal frame bounds which causes a null pointer when trying to save those bounds).
     */
    @Override
    protected void shutdown() {
        try {
        	super.shutdown();
        }
        catch (Exception e) {
        	
        }
    }

	
}

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
	
}

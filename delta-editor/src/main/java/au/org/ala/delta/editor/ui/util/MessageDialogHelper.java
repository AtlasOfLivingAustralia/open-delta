package au.org.ala.delta.editor.ui.util;

import java.awt.Window;

import javax.swing.JOptionPane;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

/**
 * Helper class for displaying message dialogs using messages from resource bundles.
 */
public class MessageDialogHelper {

	private ResourceMap _messages;
	private Window _dialogParent;
	
	public MessageDialogHelper() {
		
		SingleFrameApplication application = (SingleFrameApplication)Application.getInstance();
		_messages = application.getContext().getResourceMap();
		_dialogParent = application.getMainFrame();
	}
	
	public boolean confirmDeleteImage() {
		
		String title = _messages.getString("deleteImage.title");
		String message = _messages.getString("deleteImage.message");
		int result = au.org.ala.delta.ui.MessageDialogHelper.showConfirmDialog(_dialogParent, title, message, message.length());
		return (result == JOptionPane.YES_OPTION);
	}
}

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
	
	public boolean confirmDeleteItem(String itemDescription) {
		String title = _messages.getString("deleteItem.title");
		String message = _messages.getString("deleteItem.message", itemDescription);
		int result = au.org.ala.delta.ui.MessageDialogHelper.showConfirmDialog(_dialogParent, title, message, 50);
		return result == JOptionPane.OK_OPTION;
	}
	
	public void errorLoadingImage(String fileName) {
		String title = _messages.getString("errorLoadingImage.title");
		String message = _messages.getString("errorLoadingImage.message", fileName);
		JOptionPane.showMessageDialog(_dialogParent, message, title, JOptionPane.ERROR_MESSAGE);
	}

	public void errorPlayingSound(String soundFile) {
		String title = _messages.getString("errorPlayingSound.title");
		String message = _messages.getString("errorPlayingSound.message", soundFile);
		JOptionPane.showMessageDialog(_dialogParent, message, title, JOptionPane.ERROR_MESSAGE);
	}

	public void showUncodedMandatoryItemsWarning() {
		String title = _messages.getString("uncodedMandatoryItems.title");
		String message = _messages.getString("uncodedMandatoryItems.message");
		JOptionPane.showMessageDialog(_dialogParent, message, title, JOptionPane.WARNING_MESSAGE);
	}
}

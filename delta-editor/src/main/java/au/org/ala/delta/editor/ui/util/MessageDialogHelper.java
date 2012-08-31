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
package au.org.ala.delta.editor.ui.util;

import java.awt.Window;

import javax.swing.JOptionPane;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.editor.ui.validator.ValidationResult;

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
	
	public boolean confirmDeleteCharacter(String characterDescription) {
		String title = _messages.getString("deleteCharacter.title");
		String message = _messages.getString("deleteCharacter.message", characterDescription);
		int result = au.org.ala.delta.ui.MessageDialogHelper.showConfirmDialog(_dialogParent, title, message, 50);
		return result == JOptionPane.OK_OPTION;
	}
	
	public boolean confirmDeleteState(String stateDescription) {
		String title = _messages.getString("deleteState.title");
		String message = _messages.getString("deleteState.message", stateDescription);
		int result = au.org.ala.delta.ui.MessageDialogHelper.showConfirmDialog(_dialogParent, title, message, 50);
		return result == JOptionPane.OK_OPTION;
	}
	

	public boolean confirmDeleteCharacterDependency(String dependencyDescription) {
		String title = _messages.getString("deleteDependency.title");
		String message = _messages.getString("deleteDependency.message", dependencyDescription);
		int result = au.org.ala.delta.ui.MessageDialogHelper.showConfirmDialog(_dialogParent, title, message, 50);
		return result == JOptionPane.OK_OPTION;
	}
	
	public boolean confirmDeleteDirectiveFile(String fileName) {
		String title = _messages.getString("deleteDirectiveFile.title");
		String message = _messages.getString("deleteDirectiveFile.message", fileName);
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

	public void cannotMakeCharacterExclusive() {
		String title = _messages.getString("cannotMakeCharacterExclusive.title");
		String message = _messages.getString("cannotMakeCharacterExclusive.message");
		JOptionPane.showMessageDialog(_dialogParent, message, title, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Displays an error or warning dialog (using JOptionPane) containing the result of a validation.
	 * The title and message of the dialog are derived from the message key.  
	 * @param result the validation result to display.
	 */
	public void displayValidationResult(ValidationResult result) {
		
		String title = _messages.getString(result.getMessageKey()+".title");
		String message = result.getMessage();
		
		int type = result.isError() ? JOptionPane.ERROR_MESSAGE : JOptionPane.WARNING_MESSAGE;
		JOptionPane.showMessageDialog(_dialogParent, message, title, type);
		
	}

	public void errorRunningDirectiveFile(String fileName) {
		String title = _messages.getString("errorRunningDirectiveFile.title");
		String message = _messages.getString("errorRunningDirectiveFile.message", fileName);
		JOptionPane.showMessageDialog(_dialogParent, message, title, JOptionPane.ERROR_MESSAGE);

	}

	public String promptForDirectiveFileName() {
		String title = _messages.getString("directiveFileNamePrompt.title");
		String message = _messages.getString("directiveFileNamePrompt.message");
		return JOptionPane.showInputDialog(_dialogParent, message, title, JOptionPane.PLAIN_MESSAGE);

	}

	public int promtForSaveBeforeClosing() {
		String title = _messages.getString("saveBeforeClosing.title");
		String message = _messages.getString("saveBeforeClosing.message");
		return JOptionPane.showConfirmDialog(_dialogParent, message, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) ;
	}
	
	public int confirmNotOnImagePath() {
		String title = _messages.getString("confirmNotOnImagePath.title");
		String message = _messages.getString("confirmNotOnImagePath.message");
		return JOptionPane.showConfirmDialog(_dialogParent, message, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) ;
	}
	
	public int confirmDuplicateFileName() {
		String title = _messages.getString("confirmDuplicateFilename.title");
		String message = _messages.getString("confirmDuplicateFilename.message");
		return JOptionPane.showConfirmDialog(_dialogParent, message, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) ;
	}

	public boolean confirmDeleteOverlay() {
		String title = _messages.getString("deleteOverlay.title");
		String message = _messages.getString("deleteOverlay.message");
		int result = au.org.ala.delta.ui.MessageDialogHelper.showConfirmDialog(_dialogParent, title, message, message.length());
		return (result == JOptionPane.YES_OPTION);

	}
	
	public String promptForControllingAttributeLabel(String defaultLabel) {
		String title = _messages.getString("controllingAttributeLabelPrompt.title");
		String message = _messages.getString("controllingAttributeLabelPrompt.message");
		return (String)JOptionPane.showInputDialog(_dialogParent, message, title, JOptionPane.PLAIN_MESSAGE, null, null, defaultLabel);

	}

	public void displayCircularDependencyError() {
		String title = _messages.getString("circularDependencyError.title");
		String message = _messages.getString("circularDependencyError.message");
		JOptionPane.showMessageDialog(_dialogParent, message, title, JOptionPane.ERROR_MESSAGE);
	}

	public boolean confirmExport() {
		String title = _messages.getString("confirmExport.title");
		String message = _messages.getString("confirmExport.message");
		int result = au.org.ala.delta.ui.MessageDialogHelper.showConfirmDialog(_dialogParent, title, message, 50);
		return result == JOptionPane.OK_OPTION;
	}
	
}

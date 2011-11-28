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

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.KeyStroke;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import au.org.ala.delta.ui.util.UIUtils;

public class BaseDialog extends JDialog {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /** Constant for the dialog state OK. */
    public static final int OK = 1;
    /** Constant for the dialog state CANCEL. */
    public static final int CANCEL = 0;

    /** The dialog state. */
    private int state = CANCEL;

    /**
     * Gets the dialog state.
     *
     * @return The dialog state.
     */
    public int getState() {
        return state;
    }

    /**
     * Constructs a BaseDialog instance with specific attributes.
     *
     * @throws java.awt.HeadlessException
     */
    public BaseDialog() throws UnsupportedOperationException {
        super();
    }

    /**
     * Constructs a BaseDialog instance with specific attributes.
     *
     * @param frame
     *            The parent frame.
     * @throws java.awt.HeadlessException
     */
    public BaseDialog(Frame frame) throws UnsupportedOperationException {
        super(frame);
    }

    /**
     * Constructs a BaseDialog instance with specific attributes.
     *
     * @param frame
     *            The parent frame.
     * @param modal
     *            The modal flag.
     * @throws java.awt.HeadlessException
     */
    public BaseDialog(Frame frame, boolean modal) throws UnsupportedOperationException {
        super(frame, modal);
    }

    /**
     * Constructs a BaseDialog instance with specific attributes.
     *
     * @param frame
     *            The parent frame.
     * @param title
     *            The dialog title.
     * @throws java.awt.HeadlessException
     *             if GraphicsEnvironment.isHeadless() returns true.
     */
    public BaseDialog(Frame frame, String title) throws UnsupportedOperationException {
        super(frame, title);
    }

    /**
     * Constructs a BaseDialog instance with specific attributes.
     *
     * @param frame
     *            The parent frame.
     * @param title
     *            The dialog title.
     * @param modal
     *            The modal flag.
     * @throws java.awt.HeadlessException
     */
    public BaseDialog(Frame frame, String title, boolean modal) throws UnsupportedOperationException {
        super(frame, title, modal);
    }

    /**
     * Constructs a BaseDialog instance with specific attributes.
     *
     * @param parent
     *            The parent dialog.
     * @throws java.awt.HeadlessException
     */
    public BaseDialog(Dialog parent) throws UnsupportedOperationException {
        super(parent);
    }

    /**
     * Constructs a BaseDialog instance with specific attributes.
     *
     * @param parent
     *            The parent dialog.
     * @param modal
     *            The modal flag.
     * @throws java.awt.HeadlessException
     */
    public BaseDialog(Dialog parent, boolean modal) throws UnsupportedOperationException {
        super(parent, modal);
    }

    /**
     * Constructs a BaseDialog instance with specific attributes.
     *
     * @param parent
     *            The parent dialog.
     * @param title
     *            The dialog title.
     * @throws java.awt.HeadlessException
     */
    public BaseDialog(Dialog parent, String title) throws UnsupportedOperationException {
        super(parent, title);
    }

    /**
     * Constructs a BaseDialog instance with specific attributes.
     *
     * @param parent
     *            The parent dialog.
     * @param title
     *            The dialog title.
     * @param modal
     *            The modal flag.
     * @throws java.awt.HeadlessException
     */
    public BaseDialog(Dialog parent, String title, boolean modal) throws UnsupportedOperationException {
        super(parent, title, modal);
    }

    /**
     * Registers standard key bindings.
     */
    protected void registerStandardKeyBindings() {
        // defining key bindings
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");

        ActionMap actionMap = this.getRootPane().getActionMap();
        actionMap.put("enter", new AbstractAction() {

            private static final long serialVersionUID = 1L;

            /**
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        actionMap.put("escape", new AbstractAction() {

            private static final long serialVersionUID = 1L;

            /**
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
    }

    /**
     * Processes the action event, raised by the OK button.
     *
     * @param evt
     *            The raised action event.
     */
    private void okButtonActionPerformed(ActionEvent evt) {
        this.state = OK;
        closeDialog(null);
    }

    /**
     * Processes the action event, raised by the Cancel button.
     *
     * @param evt
     *            The raised action event.
     */
    private void cancelButtonActionPerformed(ActionEvent evt) {
        this.state = CANCEL;
        closeDialog(null);
    }

    /**
     * Closes the dialog
     *
     * @param evt
     *            The window event.
     */
    protected void closeDialog(WindowEvent evt) {
        setVisible(false);
        dispose();
    }

    /**
     * Centers the dialog over its parent component.
     */
    protected void centerDialog() {
        // centering dialog over text area
        Container parent = getParent();

        Dimension parentSize = parent.getSize();
        Dimension dialogSize = this.getSize();
        Point parentLocn = parent.getLocationOnScreen();

        int locnX = parentLocn.x + (parentSize.width - dialogSize.width) / 2;
        int locnY = parentLocn.y + (parentSize.height - dialogSize.height) / 2;

        setLocation(locnX, locnY);
    }

    protected void hookInternalFrame(JComponent owner) {
		JInternalFrame internalFrame = UIUtils.getParentInternalFrame(owner);
		if (internalFrame != null) {
			internalFrame.addInternalFrameListener(new InternalFrameAdapter() {
				@Override
				public void internalFrameClosing(InternalFrameEvent e) {
					if (isVisible()) {
						setVisible(false);
					}
				}
			});
		}
    }
    
}

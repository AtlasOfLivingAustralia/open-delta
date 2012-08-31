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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.model.SearchDirection;
import au.org.ala.delta.ui.util.UIUtils;

public class SearchDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private ButtonGroup buttonGroup;
	private JCheckBox chckbxMatchCase;
	private JCheckBox chckbxWrapSearch;
	private JRadioButton rdbtnForwards;
	private JRadioButton rdbtnBackwards;
	private SearchController _controller;

	/**
	 * Create the dialog.
	 */
	public SearchDialog(SearchController controller) {
		super(UIUtils.getParentFrame(controller.getOwningComponent()));
		hookInternalFrame(controller.getOwningComponent());
		_controller = controller;
		UIUtils.centerDialog(this, controller.getOwningComponent().getParent());
		setTitle(controller.getTitle());
		setName(_controller.getTitle());
		setBounds(100, 100, 366, 229);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		SingleFrameApplication application = (SingleFrameApplication) Application.getInstance();
		ResourceMap messages = application.getContext().getResourceMap();

		JLabel lblFind = new JLabel(messages.getString("searchDialog.lblFind"));
		lblFind.setMinimumSize(new Dimension(30, 0));

		textField = new JTextField();
		textField.setColumns(10);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, messages.getString("searchDialog.groupDirection"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		buttonGroup = new ButtonGroup();

		rdbtnForwards = new JRadioButton(messages.getString("searchDialog.directionForwards"));
		rdbtnForwards.setSelected(true);
		buttonGroup.add(rdbtnForwards);

		rdbtnBackwards = new JRadioButton(messages.getString("searchDialog.directionBackwards"));
		buttonGroup.add(rdbtnBackwards);
		contentPanel.setLayout(new MigLayout("", "[growprio 0,grow,left][grow][grow]", "[20px][21px,grow][grow]"));
		contentPanel.add(lblFind, "cell 0 0,alignx left,aligny top");
		contentPanel.add(textField, "cell 1 0 2 1,growx,aligny top");
		
		final JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, messages.getString("searchDialog.optionsPanelTitle"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPanel.add(panel_1, "cell 0 1 2 1,grow");
				panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		
				chckbxMatchCase = new JCheckBox(messages.getString("searchDialog.lblMatchCase"));
				panel_1.add(chckbxMatchCase);
				
						chckbxWrapSearch = new JCheckBox(messages.getString("searchDialog.lblWrapSearch"));
						panel_1.add(chckbxWrapSearch);
						chckbxWrapSearch.setSelected(true);
		contentPanel.add(panel, "cell 2 1,grow");
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(rdbtnForwards);
		panel.add(rdbtnBackwards);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton findButton = new JButton(messages.getString("searchDialog.btnFindNext"));
				findButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						findNext();
					}
				});

				buttonPane.add(findButton);
				getRootPane().setDefaultButton(findButton);
			}
			{
				JButton cancelButton = new JButton(messages.getString("searchDialog.btnCancel"));
				buttonPane.add(cancelButton);
				cancelButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
			}
		}
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

	@Override
	protected JRootPane createRootPane() {
		JRootPane rootPane = super.createRootPane();
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);		
		rootPane.registerKeyboardAction(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		}, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		return rootPane;
	}

	public SearchController getSearchController() {
		return _controller;
	}

	public void findNext() {
		if (!StringUtils.isEmpty(textField.getText())) {
			SearchDirection direction = rdbtnForwards.isSelected() ? SearchDirection.Forward : SearchDirection.Backward;
			SearchOptions options = new SearchOptions(textField.getText(), direction, chckbxMatchCase.isSelected(), chckbxWrapSearch.isSelected());
			_controller.findNext(options);
		}

	}

	public void setFindText(String findText) {
		textField.setText(findText);
	}
}

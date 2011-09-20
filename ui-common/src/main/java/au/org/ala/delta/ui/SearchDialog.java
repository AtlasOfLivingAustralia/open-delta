package au.org.ala.delta.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
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
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

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
		setBounds(100, 100, 364, 200);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		SingleFrameApplication application = (SingleFrameApplication) Application.getInstance();
		ResourceMap messages = application.getContext().getResourceMap();

		JLabel lblFind = new JLabel(messages.getString("searchDialog.lblFind"));

		textField = new JTextField();
		textField.setColumns(10);

		chckbxMatchCase = new JCheckBox(messages.getString("searchDialog.lblMatchCase"));

		chckbxWrapSearch = new JCheckBox(messages.getString("searchDialog.lblWrapSearch"));
		chckbxWrapSearch.setSelected(true);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, messages.getString("searchDialog.groupDirection"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblFind)
					.addGap(18)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(textField, GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(chckbxMatchCase)
								.addComponent(chckbxWrapSearch))
							.addGap(26)
							.addComponent(panel, GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblFind)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(chckbxMatchCase)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(chckbxWrapSearch))
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE))
					.addContainerGap())
		);

		buttonGroup = new ButtonGroup();

		rdbtnForwards = new JRadioButton(messages.getString("searchDialog.directionForwards"));
		rdbtnForwards.setSelected(true);
		buttonGroup.add(rdbtnForwards);

		rdbtnBackwards = new JRadioButton(messages.getString("searchDialog.directionBackwards"));
		buttonGroup.add(rdbtnBackwards);

		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.TRAILING).addGroup(
				gl_panel.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								gl_panel.createParallelGroup(Alignment.TRAILING).addComponent(rdbtnBackwards, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
										.addComponent(rdbtnForwards, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)).addContainerGap()));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_panel.createSequentialGroup().addComponent(rdbtnForwards).addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(rdbtnBackwards)));
		panel.setLayout(gl_panel);
		contentPanel.setLayout(gl_contentPanel);
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
}

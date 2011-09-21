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
import javax.swing.BoxLayout;
import java.awt.Component;

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
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		SingleFrameApplication application = (SingleFrameApplication) Application.getInstance();
		ResourceMap messages = application.getContext().getResourceMap();

		buttonGroup = new ButtonGroup();
		contentPanel.setLayout(new BorderLayout(0, 0));
				
				JPanel panel_1 = new JPanel();
				contentPanel.add(panel_1, BorderLayout.NORTH);
				panel_1.setLayout(new BorderLayout(0, 0));
		
				JLabel lblFind = new JLabel(messages.getString("searchDialog.lblFind"));
				panel_1.add(lblFind, BorderLayout.WEST);
		
				textField = new JTextField();
				panel_1.add(textField, BorderLayout.CENTER);
				textField.setColumns(10);
		
		JPanel panel_2 = new JPanel();
		contentPanel.add(panel_2, BorderLayout.CENTER);
				panel_2.setLayout(new BorderLayout(0, 0));
		
				JPanel panel = new JPanel();
				panel_2.add(panel, BorderLayout.EAST);
				panel.setBorder(new TitledBorder(null, messages.getString("searchDialog.groupDirection"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
				
						rdbtnForwards = new JRadioButton(messages.getString("searchDialog.directionForwards"));
						rdbtnForwards.setSelected(true);
						buttonGroup.add(rdbtnForwards);
						
								rdbtnBackwards = new JRadioButton(messages.getString("searchDialog.directionBackwards"));
								buttonGroup.add(rdbtnBackwards);
										panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
										panel.add(rdbtnForwards);
										panel.add(rdbtnBackwards);
										
										JPanel panel_3 = new JPanel();
										panel_3.setBorder(new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
										panel_2.add(panel_3, BorderLayout.WEST);
												panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.Y_AXIS));
										
												chckbxMatchCase = new JCheckBox(messages.getString("searchDialog.lblMatchCase"));
												panel_3.add(chckbxMatchCase);
												
														chckbxWrapSearch = new JCheckBox(messages.getString("searchDialog.lblWrapSearch"));
														panel_3.add(chckbxWrapSearch);
														chckbxWrapSearch.setSelected(true);
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

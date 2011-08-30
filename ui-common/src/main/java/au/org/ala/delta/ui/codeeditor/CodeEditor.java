package au.org.ala.delta.ui.codeeditor;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.ui.codeeditor.action.FindKeyAction;
import au.org.ala.delta.ui.codeeditor.action.GotoLineKeyAction;
import au.org.ala.delta.ui.codeeditor.action.ToggleLineNumbersAction;
import au.org.ala.delta.ui.codeeditor.action.ToggleShowWhitespaceAction;

public class CodeEditor extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private CodeTextArea _editor;
	private JLabel _status;
	private JToolBar _toolbar;
	private ResourceMap _resources;
	
	public CodeEditor() {
		init("text/plain");
	}
	
	public CodeEditor(String mimeType) {
		init(mimeType);
	}
	
	private void init(String mimeType) {
		this.setLayout(new BorderLayout());
		
		_resources = Application.getInstance().getContext().getResourceManager().getResourceMap();
		
		_status = new JLabel();
		_status.setPreferredSize(new Dimension(100,23));
		_editor = new CodeTextArea(mimeType);
		_toolbar = new JToolBar();
		
		addToggleToolbarButton(new ToggleShowWhitespaceAction(_editor), "show_whitespace", false);
		addToggleToolbarButton(new ToggleLineNumbersAction(_editor), "show_linenumbers", true);
		addToolbarButton(new FindKeyAction(_editor), "editor_find");
		addToolbarButton(new GotoLineKeyAction(_editor), "editor_gotoline");
		
		_editor.addCaretPositionListener(new CaretPositionListener() {
            public void positionUpdate(CaretPositionEvent evt) {
                statusMessage(String.format("Line: %d of %d Column: %d", (evt.getRow() + 1), _editor.getLineCount(), (evt.getColumn() + 1)));
            }
          });		

		add(_toolbar, BorderLayout.NORTH);
		add(new JScrollPane(_editor), BorderLayout.CENTER);
		add(_status, BorderLayout.SOUTH);
	}
	
	/**
	 * Allows a client of the CodeEditor to add custom functions to the
	 * toolbar (for example, "save").
	 * @param action the action that will occur when the button is pressed.
	 * @param name the name to use when configuring the button properties
	 * from the resource bundle.
	 */	
	public JButton addToolbarButton(Action action, String name) {
		JButton btn = new JButton(action);
		decorateToolbarButton(btn, action, name);
		_toolbar.add(btn);
		return btn;
	}
	
	/**
	 * Allows a client of the CodeEditor to add custom functions to the
	 * toolbar (for example, "save"). This function adds a toggle button
	 * @param action the action that will occur when the button is pressed.
	 * @param name the name to use when configuring the button properties
	 * from the resource bundle.
	 */	
	public JToggleButton addToggleToolbarButton(Action action, String name, boolean initiallySelected) {
		JToggleButton btn = new JToggleButton(action);
		decorateToolbarButton(btn, action, name);
		btn.setSelected(initiallySelected);		
		_toolbar.add(btn);
		return btn;
	}
					
	private void decorateToolbarButton(AbstractButton button, Action action, String name) {
		
		String keyPrefix = name + ".Action.";
		
		String iconKey = keyPrefix + "icon";
		if (_resources.keySet().contains(keyPrefix+"icon")) {
			ImageIcon icon = _resources.getImageIcon(iconKey);
			action.putValue(Action.SMALL_ICON, icon);
		}
		
		action.putValue(Action.SHORT_DESCRIPTION, _resources.getString(keyPrefix+"shortDescription"));
		button.setAction(action);
		button.setFocusable(false);
		button.setHideActionText(true);
	}

	private void statusMessage(String message) {
		_status.setText(message);
	}
	
	public CodeTextArea getTextArea() {
		return _editor;
	}

	public void setText(String text) {
		_editor.setText(text);
	}

}

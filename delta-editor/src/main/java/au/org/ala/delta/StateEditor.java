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
package au.org.ala.delta;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.StringWriter;

import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import au.org.ala.delta.gui.RtfEditor;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.StateValue;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.rtf.MyRTFEditorKit;

public class StateEditor extends JPanel {

	private static final long serialVersionUID = 1L;

	private DeltaContext _context;
	private RtfEditor _textPane;
	private JToggleButton _btnBold;
	private JList _list;

	private Character _character;
	private Item _item;
	
	/** Tracks whether the attribute has been modified since it was displayed */
	private boolean _modified;

	public StateEditor(DeltaContext context) {
		_context = context;
		setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(200, 150));
		JSplitPane split = new JSplitPane();

		_textPane = new RtfEditor();
		_list = new JList();

		split.setLeftComponent(_textPane);
		split.setRightComponent(_list);

		split.setDividerLocation(300);
		split.setResizeWeight(0.5);

		JToolBar toolbar = _textPane.buildAndInstallToolbar();
		add(toolbar, BorderLayout.NORTH);

		_textPane.getDocument().addDocumentListener(new EditListener());
		_textPane.setInputVerifier(new EditCommitter());

		add(split, BorderLayout.CENTER);
	}

	public void bind(Character ch, Item item) {
		
		_character = ch;
		_item = item;
		if (ch != null && item != null) {
			final StateValue sv = _context.getMatrix().getValue(_character.getCharacterId(), _item.getItemId());
			if (sv != null) {
				String str = sv.getValue();
				if (!str.startsWith("{\\rtf1")) {
					str = String.format("{\\rtf1\\ansi\\ansicpg1252 %s }", sv.getValue());
				}

				_textPane.setText(str);
			} else {
				_textPane.setText("");
			}

			if (ch instanceof MultiStateCharacter) {
				MultiStateCharacter mc = (MultiStateCharacter) ch;
				_list.setModel(new StateListModel(mc.getStates()));
				_list.setCellRenderer(new StateRenderer());
			} else {
				_list.setModel(new DefaultListModel());
				_list.setCellRenderer(new DefaultListCellRenderer());
			}
		}
		_modified = false;
	}
	
	class EditListener implements DocumentListener {

		@Override
		public void insertUpdate(DocumentEvent e) {
			_modified = true;
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			_modified = true;
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			_modified = true;
		}
	}
	
	
	class EditCommitter extends InputVerifier {

		@Override
		public boolean shouldYieldFocus(JComponent input) {
			boolean valid = verify(input);
			
			if (valid) {
				Document doc = ((JTextPane)input).getDocument();
				MyRTFEditorKit kit = (MyRTFEditorKit) _textPane.getEditorKit();				
				ByteOutputStream bos = new ByteOutputStream();
				try {
					kit.writeBody(bos, doc);
					String rtf = new String(bos.getBytes()).trim();
					if (_character instanceof TextCharacter) {
						rtf = "<"+rtf+">";
					}
					else {
						// bit dodgy the RTF isn't quite how we want.  strip off leading/trailing control chars.
						if (rtf.startsWith("\\")) {
							rtf = rtf.substring(rtf.indexOf(' ')+1);
						}
						int lastCommentIndex = rtf.lastIndexOf(">");
						String bitAfterLastComment = rtf;
						if (lastCommentIndex > 0) {
							bitAfterLastComment = rtf.substring(lastCommentIndex);
						}
						
						int controlCharAfterComment = bitAfterLastComment.indexOf("\\");
						if (controlCharAfterComment > 0) {
							rtf = rtf.substring(0, controlCharAfterComment);
						}
					}
					_item.getAttribute(_character).setValue(rtf);
					_modified = false;
				} catch (Exception ex) {
					throw new RuntimeException(ex);					
				}
				
				// TODO I've bypassed the application model step here which will prevent notification of
				// other components looking at the same data set.
				//_context.setAttribute(_item, _character, ((JTextPane)input).getText());
			}
			
			return valid;
		}

		/**
		 * Edits are committed on focus lost events - hence we validate at this point.
		 * TODO a failed validate prevents focus transferal but it doesn't prevent a selection
		 * on the table or list from updating the text in the document!
		 * @see java.awt.event.FocusAdapter#focusLost(java.awt.event.FocusEvent)
		 */
		@Override
		public boolean verify(JComponent component) {
			if (_modified) {
				
				return true;
			}
			return false;
		}
	}

}


class StateRenderer extends DefaultListCellRenderer {

	private JCheckBox stateRenderer = new JCheckBox();
	/* (non-Javadoc)
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		stateRenderer.setBackground(getBackground());
		stateRenderer.setForeground(getForeground());
		stateRenderer.setText(value.toString());
		return stateRenderer;
	}
	
}

class StateListModel extends AbstractListModel {

	private static final long serialVersionUID = 1L;

	private String[] _states;

	public StateListModel(String[] states) {
		_states = states;
	}

	@Override
	public int getSize() {
		return _states.length;
	}

	@Override
	public Object getElementAt(int index) {
		return String.format("%d. %s", index + 1, _states[index]);
	}

}

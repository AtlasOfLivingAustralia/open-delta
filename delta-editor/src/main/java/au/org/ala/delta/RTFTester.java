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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;

import au.org.ala.delta.gui.rtf.SimpleRtfEditorKit;

public class RTFTester extends JFrame {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {

		RTFTester instance = new RTFTester();
		instance.setVisible(true);
	}

	private JTextPane _textPane;
	private JToggleButton _btnBold;

	public RTFTester() {
		super("RTF Tester");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(new Dimension(800, 600));

		this.getContentPane().setLayout(new BorderLayout());
		_textPane = new JTextPane();

		JToolBar toolbar = new JToolBar();
		this.getContentPane().add(toolbar, BorderLayout.NORTH);

		_btnBold = decorateToolbarAction(new StyledEditorKit.BoldAction(), "B");
		toolbar.add(_btnBold);
		toolbar.add(decorateToolbarAction(new SuperscriptAction(), "S"));
		toolbar.add(decorateToolbarAction(new SubscriptAction(), "s"));

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.err.println(getRTF());
			}
		});

		toolbar.add(btnSave);

		this.getContentPane().add(_textPane, BorderLayout.CENTER);

		_textPane.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						StyledEditorKit kit = (StyledEditorKit) _textPane.getEditorKit();
						MutableAttributeSet attr = kit.getInputAttributes();
						_btnBold.setSelected(StyleConstants.isBold(attr));
					}
				});

			}
		});
	}

	public String getRTF() {

		StyledDocument doc = (StyledDocument) _textPane.getDocument();
		SimpleRtfEditorKit kit = new SimpleRtfEditorKit();

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			kit.write(out, doc, doc.getStartPosition().getOffset(), doc.getLength());
			return new String(out.toByteArray());
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private JToggleButton decorateToolbarAction(Action action, String name) {
		JToggleButton b = new JToggleButton(action);
		b.setFocusable(false);
		action.putValue(Action.NAME, name);
		return b;
	}

	public void setupStyles() {
		StyledDocument doc = _textPane.getStyledDocument();
		Style style = doc.addStyle("Bold", null);
		StyleConstants.setBold(style, true);

	}

	class SubscriptAction extends StyledEditorKit.StyledTextAction {

		public SubscriptAction() {
			super(StyleConstants.Subscript.toString());
		}

		public void actionPerformed(ActionEvent ae) {
			if (_textPane != null) {
				StyledEditorKit kit = getStyledEditorKit(_textPane);
				MutableAttributeSet attr = kit.getInputAttributes();
				boolean subscript = (StyleConstants.isSubscript(attr)) ? false : true;
				SimpleAttributeSet sas = new SimpleAttributeSet();
				StyleConstants.setSubscript(sas, subscript);
				setCharacterAttributes(_textPane, sas, false);
			}
		}
	}

	class SuperscriptAction extends StyledEditorKit.StyledTextAction {

		public SuperscriptAction() {
			super(StyleConstants.Superscript.toString());
		}

		public void actionPerformed(ActionEvent ae) {
			if (_textPane != null) {
				StyledEditorKit kit = getStyledEditorKit(_textPane);
				MutableAttributeSet attr = kit.getInputAttributes();
				boolean superscript = (StyleConstants.isSuperscript(attr)) ? false : true;
				SimpleAttributeSet sas = new SimpleAttributeSet();
				StyleConstants.setSuperscript(sas, superscript);
				setCharacterAttributes(_textPane, sas, false);
			}
		}
	}

}

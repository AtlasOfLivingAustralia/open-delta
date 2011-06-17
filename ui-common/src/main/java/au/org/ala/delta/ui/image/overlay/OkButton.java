package au.org.ala.delta.ui.image.overlay;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class OkButton extends JButton implements ActionListener {
	private static final long serialVersionUID = 7019370330547978789L;

	public OkButton(String text) {
		super(text);
		
		addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//getParent().close();
	}
	
	
}

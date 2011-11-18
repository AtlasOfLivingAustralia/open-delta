package au.org.ala.delta.intkey.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.jdesktop.application.Application;

import au.org.ala.delta.intkey.Intkey;

public class IntKeyDialogController {

	private static List<IntkeyDialog> DIALOGS = new ArrayList<IntkeyDialog>();

	public static boolean registerDialog(final IntkeyDialog dialog) {
		synchronized (DIALOGS) {
			if (!DIALOGS.contains(dialog)) {
				DIALOGS.add(dialog);

				dialog.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosed(WindowEvent e) {
						unregisterDialog(dialog);
					}

					@Override
					public void windowActivated(WindowEvent e) {
						if (!DIALOGS.contains(dialog)) {
							DIALOGS.add(dialog);
						}
					}

				});

				return true;
			}
		}
		return false;
	}

	public static boolean unregisterDialog(final IntkeyDialog dialog) {
		synchronized (DIALOGS) {
			if (DIALOGS.contains(dialog)) {
				DIALOGS.remove(dialog);
				return true;
			}
		}
		return false;
	}

	private static IntkeyDialog[] getVisibleWindows() {
		List<IntkeyDialog> temp = new ArrayList<IntkeyDialog>();
		for (IntkeyDialog dlg : DIALOGS) {
			if (dlg.isVisible()) {
				temp.add(dlg);
				dlg.toFront();
			}
		}
		return temp.toArray(new IntkeyDialog[] {});
	}

	public static void cascadeWindows() {
		if (Application.getInstance() instanceof Intkey) {
			Intkey intkey = (Intkey) Application.getInstance();
			IntkeyDialog[] arr = getVisibleWindows();
			ArrayUtils.reverse(arr);
			au.org.ala.delta.ui.util.UIUtils.cascade(arr, intkey.getClientBounds(), 24);
		}
	}

	public static void tileWindows() {
		if (Application.getInstance() instanceof Intkey) {
			Intkey intkey = (Intkey) Application.getInstance();
			IntkeyDialog[] arr = getVisibleWindows();
			au.org.ala.delta.ui.util.UIUtils.tileWindows(arr, intkey.getClientBounds(), false);
		}
	}

	public static void closeWindows() {
		IntkeyDialog[] arr = getVisibleWindows();
		for (IntkeyDialog dlg : arr) {
			dlg.setVisible(false);
		}
	}

}

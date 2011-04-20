package au.org.ala.delta.editor.ui.util;

import javax.swing.ActionMap;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * Helper class that will assemble a menu from a list of Strings.
 */
public class MenuBuilder {

	/**
	 * Builds and attaches menu items to the supplied menu.  The menu items will be built based on
	 * the strings in the supplied actionNames.  
	 * If the name is a "-" a menu separator will be added.
	 * Otherwise for each string in the actionNames list, a new menu item will be created and 
	 * configured with an action from the action map identified by the name.
	 * @param menu the menu to build.
	 * @param actionNames the names of actions, in the order they should appear on the menu.
	 * @param actionMap contains the actions to be attached to the menu.  There must be an action 
	 * for each name in the actionNames parameter.
	 */
	public static void buildMenu(JMenu menu, String[] actionNames, ActionMap actionMap) {
		for (String action : actionNames) {
			addMenuItem(menu, action, actionMap);
		}
	}
	
	/**
	 * Creates and adds a menu item to the supplied menu with an action identified by the supplied actionName.
	 * 
	 * @param menu
	 *            the menu to add the new item to.
	 * @param actionName
	 *            the name of the action, or "-" to add a separator.
	 */
	private static void addMenuItem(JMenu menu, String actionName, ActionMap actionMap) {
		if ("-".equals(actionName)) {
			menu.addSeparator();
		} else {
			JMenuItem menuItem = new JMenuItem();
			menuItem.setAction(actionMap.get(actionName));
			menu.add(menuItem);
		}
	}

}

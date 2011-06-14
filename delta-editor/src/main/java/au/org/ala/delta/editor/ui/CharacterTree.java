package au.org.ala.delta.editor.ui;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * The CharacterTree extends JTree to provide 1-click cell editing and to implement the
 * ReorderableList interface to help our Drag and Drop implementation.
 */
public class CharacterTree extends JTree implements ReorderableList {

	private static final long serialVersionUID = 1462521823171738637L;
	private boolean _doubleProcessingMouseEvent = false;
	
	
	public CharacterTree() {
		setToggleClickCount(0);
		addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				if (_doubleProcessingMouseEvent) {
					return;
				}
				if (!isEditing()) {
					int selectedRow = getClosestRowForLocation(e.getX(), e.getY());
					
					if ((selectedRow >= 0) && (e.getClickCount() == 2) && SwingUtilities.isLeftMouseButton(e)) {
						Action action = getActionMap().get("SelectionAction");
						if (action != null) {
							action.actionPerformed(new ActionEvent(this, -1, ""));
						}
					}
				}
			}
		});
	}
	
	/**
	 * This is a done to initiate a cell edit from a single click with
	 * drag and drop enabled.
	 */
	protected void processMouseEvent(MouseEvent e) {
		
		super.processMouseEvent(e);
		_doubleProcessingMouseEvent = false;
		TreePath selectedPath = getSelectionPath();
		if (selectedPath != null) {
			Object lastComponent = selectedPath.getLastPathComponent();
			if (lastComponent instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)lastComponent;
				
				if (node.isLeaf()) {
					int row = getRowForPath(selectedPath);
					Rectangle bounds = getRowBounds(row);
					int pos = e.getX()-bounds.x;
					if (pos >= 0 && pos < 20) {						
						_doubleProcessingMouseEvent = true;
						super.processMouseEvent(e);
					}
				}
			}
		}
	}
	@Override
	public int getSelectedIndex() {
		TreePath selectedPath = getSelectionPath();
		
		return pathToCharacterNumber(selectedPath)-1;
	}
	@Override
	public void setSelectedIndex(int index) {
		Object[] nodes = new Object[2];
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)getModel().getRoot();
		nodes[0] = root;
		nodes[1] = root.getChildAt(index);
		TreePath path = new TreePath(nodes);
		
		setSelectionPath(path);
		
	}
	@Override
	public int getDropLocationIndex(javax.swing.TransferHandler.DropLocation dropLocation) {
		
		if (dropLocation == null) {
			return -1;
		}
		return dropLocationToCharacterNumber((DropLocation)dropLocation)-1;
	}
	
	@Override
	public void setSelectionAction(Action action) {
		getActionMap().put("SelectionAction", action);		
	}
	
	/**
	 * Turns a DropLocation into the character number of the character at the location.
	 * @param location the current drop location.
	 * @return the character number representing the drop position.
	 */
	private int dropLocationToCharacterNumber(DropLocation location) {
		TreePath path = location.getPath();
		Object[] nodes = path.getPath();
		if (nodes.length == 2) {
			return pathToCharacterNumber(path);
		}
		else {
			return location.getChildIndex()+1;
		}
	}
	
	/**
	 * Returns the character number of the Character at the supplied TreePath.
	 * @param location the current drop location.
	 * @return the character number representing the drop position.
	 */
	private int pathToCharacterNumber(TreePath path) {
		if (path != null) {
			Object[] nodes = path.getPath();
			if (nodes[1] instanceof CharacterTreeNode) {
				return ((CharacterTreeNode)nodes[1]).getCharacter().getCharacterId();
			}
		}
		return -1;
	}
	
	
}

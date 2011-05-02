package au.org.ala.delta.editor.ui.dnd;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.TooManyListenersException;

import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 * The DropIndicationTable is able to provide an indication of the of the drop location of a
 * row or column being dragged, despite the drag and drop operation occurring on a different
 * table instance.  In the DELTA case, the drag and drop operation occurs on the row header but
 * is simulated on the main content table to provide a better visual indication of the drop 
 * location to the user.
 */
public class DropIndicationTable extends JTable implements DropTargetListener {

	private static final long serialVersionUID = -3467217705935460965L;
	
	/** True during the time this table paints a drop indication */
	private boolean _fakingDrop = false;
	
	/** The row the drop will occur at */
	private int _dropRow = -1;
	
	/** The index of the drop row the last time we painted a drop location */
	public int _lastPainted = -1;
	
	/** The JTable that the drag and drop operation is actually occurring on */
	private JTable _dropEventSource;
	
	/**
	 * Creates a new DropIndicationTable with the supplied model using the supplied JTable as
	 * the source of drop events to respond to.
	 * @param model the model for this table.
	 * @param dropEventSource this table will react to drop target events from this table.
	 */
	public DropIndicationTable(TableModel model, JTable dropEventSource) {
		super(model);
		_dropEventSource = dropEventSource;
		
		try {
			_dropEventSource.getDropTarget().addDropTargetListener(this);
		} catch (TooManyListenersException e) {
			throw new RuntimeException("Unable to install drop target listener on "+dropEventSource, e);
		}
	}
	
	/**
	 * Overrides paintComponent to paint the drop location indication if necessary.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		// Draw drop lines.
		if (_fakingDrop) {
			
			Rectangle rect = getCellRect(_dropRow, 0, true);
			if (g.getClip().intersects(rect.x, rect.y-2, getWidth(), 3)) {
				g.fillRect(rect.x, rect.y-2, getWidth(), 3);
				_lastPainted = _dropRow;
			}
		}
	}
	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		_fakingDrop = true;
		updateDropIndication();
	}
	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		updateDropIndication();
	}
	
	private void updateDropIndication() {
		JTable.DropLocation dl = _dropEventSource.getDropLocation();
		_dropRow = dl.getRow();
		paintImmediately(getDropIndicationBounds());
	}
	
	/**
	 * Works out the bounds in which our customized painting should occur.
	 * @return a rectangle containing the clip bounds for our paint.
	 */
	private Rectangle getDropIndicationBounds() {
		
		Rectangle previousRec = getCellRect(_lastPainted, 0, true);
		Rectangle rect = getCellRect(_dropRow, 0, true);
		
		int minY = Math.min(previousRec.y, rect.y);
		int maxY = Math.max(previousRec.y, rect.y);
		
		int height = maxY-minY+3;
		
		return new Rectangle(rect.x, minY-2, getWidth(), height); 
	}
	
	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {}
	
	@Override
	public void dragExit(DropTargetEvent dte) {
		finishDrop();
	}
	
	@Override
	public void drop(DropTargetDropEvent dtde) {
		finishDrop();
	}
	
	private void finishDrop() {
		_dropRow = -1;
		_lastPainted = -1;
		_fakingDrop = false;
		repaint();
	}
}

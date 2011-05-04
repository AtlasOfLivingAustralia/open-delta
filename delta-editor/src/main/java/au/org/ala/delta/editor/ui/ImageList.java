package au.org.ala.delta.editor.ui;

import java.awt.Component;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import au.org.ala.delta.model.image.Image;

public class ImageList extends SelectionList {
 
	private static final long serialVersionUID = 3022295073432330518L;

	public ImageList() {
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setCellRenderer(new ImageListRenderer());
	}
	
	public void setImages(List<Image> images) {
		setModel(new ImageListModel(images));
		
		if (images != null && images.size() > 0) {
			setSelectedIndex(0);
		}	
	}
	
	class ImageListModel extends AbstractListModel {

		private static final long serialVersionUID = 8138974771050262084L;
		private List<Image> _images;
		
		public ImageListModel(List<Image> images) {
			_images = images;
		}
		@Override
		public int getSize() {
			if (_images == null) {
				return 0;
			}
			return _images.size();
		}

		@Override
		public Object getElementAt(int index) {
			return _images.get(index);
		}
		
	}
	
	class ImageListRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = -5158573248085323517L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			String fileName = ((Image)value).getFileName();
			return super.getListCellRendererComponent(list, fileName, index, isSelected,
					cellHasFocus);
		}
		
	}
}

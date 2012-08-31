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
package au.org.ala.delta.editor.ui;

import java.awt.Component;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import au.org.ala.delta.model.image.Image;

/**
 * A JList that displays a list of Image objects.
 */
public class ImageList extends SelectionList {
 
	private static final long serialVersionUID = 3022295073432330518L;

	public ImageList() {
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setCellRenderer(new ImageListRenderer());
	}
	
	/**
	 * Sets the images that will be displayed in the list.
	 * @param images the images to display.
	 */
	public void setImages(List<Image> images) {
		setModel(new ImageListModel(images));
		
		if (images != null && images.size() > 0) {
			setSelectedIndex(0);
		}	
	}
	
	/**
	 * A ListModel that works with Image objects.
	 */
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
	
	/**
	 * Renders an Image as it's file name.
	 */
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

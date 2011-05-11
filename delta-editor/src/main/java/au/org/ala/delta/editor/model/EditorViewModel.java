package au.org.ala.delta.editor.model;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;

public interface EditorViewModel extends DeltaDataSet {

	public abstract void setSelectedItem(Item selectedItem);

	public abstract void setSelectedCharacter(Character selectedCharacter);

	public abstract Item getSelectedItem();

	public abstract Character getSelectedCharacter();

	public abstract void deleteItem(Item item);

	public abstract String getName();

	public abstract String getShortName();

	public abstract void setName(String name);

	public abstract void close();

}
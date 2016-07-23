package me.opl.apps.modelcreator2.model;

import me.opl.apps.modelcreator2.model.property.Property;

public interface Selectable {
	public String[] getPropertyNames();

	public Property getPropertyByName(String propertyName);

	public void onSelected();

	public void onUnselected();
}

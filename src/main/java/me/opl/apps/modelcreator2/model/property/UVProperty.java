package me.opl.apps.modelcreator2.model.property;

import me.opl.apps.modelcreator2.model.UV;

public class UVProperty extends Property {
	private UV value;

	public UVProperty() {
		this(new UV());
	}

	public UVProperty(UV initialValue) {
		this.value = initialValue;
	}

	@Override
	public UV getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		if (value == null) throw new IllegalArgumentException("Given value is null");
		if (!(value instanceof UV)) throw new IllegalArgumentException("Given value is not a UV");

		this.value = (UV) value;
	}
}

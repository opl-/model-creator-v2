package me.opl.apps.modelcreator2.model.property;

public class StringProperty extends Property {
	private String value;

	public StringProperty() {
		this("");
	}

	public StringProperty(String initialValue) {
		this.value = initialValue;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		if (value == null) throw new IllegalArgumentException("Given value is null");
		if (!(value instanceof String)) throw new IllegalArgumentException("Given value is not a String");

		this.value = (String) value;
	}
}

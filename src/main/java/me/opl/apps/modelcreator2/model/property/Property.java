package me.opl.apps.modelcreator2.model.property;

public abstract class Property {
	private String displayName;

	/**
	 * Get display name of this property.
	 *
	 * @return Display name
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Get the value of this property.
	 *
	 * @return Value of this property
	 */
	public abstract Object getValue();

	/**
	 * Set the value of this property.
	 */
	public abstract void setValue(Object value);
}

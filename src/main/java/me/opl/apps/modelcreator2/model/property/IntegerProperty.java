package me.opl.apps.modelcreator2.model.property;

public class IntegerProperty extends Property {
	private int value;

	private boolean hasBounds;
	private int minimalValue;
	private int maximalValue;

	/**
	 * Creates a new IntegerProperty with initial value of 0 and no minimal nor
	 * maximal value.
	 */
	public IntegerProperty() {
		this(0);
	}

	/**
	 * Creates a new IntegerProperty with given initial value and no minimal nor
	 * maximal value.
	 *
	 * @param initialValue Initial value of this property
	 */
	public IntegerProperty(int initialValue) {
		this.value = initialValue;

		this.hasBounds = false;
	}

	/**
	 * Creates a new IntegerProperty with initial value of 0 and bounds.
	 *
	 * @param minimalValue Minimal value of this property
	 * @param maximalValue Maximal value of this property
	 */
	public IntegerProperty(int minimalValue, int maximalValue) {
		this(0, minimalValue, maximalValue);
	}

	/**
	 * Creates a new IntegerProperty with given initial value and bounds.
	 *
	 * @param initialValue Initial value of this property
	 * @param minimalValue Minimal value of this property
	 * @param maximalValue Maximal value of this property
	 * @throws IllegalArgumentException If maximum value is bigger than minimal value
	 */
	public IntegerProperty(int initialValue, int minimalValue, int maximalValue) throws IllegalArgumentException {
		if (maximalValue < minimalValue) throw new IllegalArgumentException("Maximum value is bigger than minimal value");

		this.value = initialValue < minimalValue ? minimalValue : initialValue > maximalValue ? maximalValue : initialValue;

		this.minimalValue = minimalValue;
		this.maximalValue = maximalValue;

		this.hasBounds = true;
	}

	/**
	 * Get value of this property.
	 *
	 * @return Current value of this property
	 */
	@Override
	public Integer getValue() {
		return value;
	}

	/**
	 * Get minimal value of this property.
	 *
	 * @return Minimal value of this property
	 */
	public int getMinimalValue() {
		return minimalValue;
	}

	/**
	 * Set minimal value of this property.
	 */
	public void setMinimalValue(int minimalValue) {
		this.minimalValue = minimalValue;
	}

	/**
	 * Get value of this property.
	 *
	 * @return Current value of this property
	 */
	public int getMaximalValue() {
		return maximalValue;
	}

	/**
	 * Set maximal value of this property.
	 *
	 * @param maximalValue New maximum value
	 */
	public void setMaximalValue(int maximalValue) {
		this.maximalValue = maximalValue;
	}

	/**
	 * Sets the current value of this property to the given one.
	 *
	 * @param value New value for this property
	 * @throws IllegalArgumentException If the given value is null or not an
	 * Integer
	 * @throws NumberFormatException If the given value is out of bounds
	 */
	@Override
	public void setValue(Object value) {
		if (value == null) throw new IllegalArgumentException("Given value is null");
		if (!(value instanceof Integer)) throw new IllegalArgumentException("Given value is not an Integer");

		if (hasBounds && ((Integer) value < minimalValue || (Integer) value > maximalValue)) throw new NumberFormatException("Given value is out of bounds (" + value + ")");

		this.value = (Integer) value;
	}
}

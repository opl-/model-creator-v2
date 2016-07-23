package me.opl.apps.modelcreator2.model.property;

public class DoubleProperty extends Property {
	private double value;

	private boolean hasBounds;
	private double minimalValue;
	private double maximalValue;

	public DoubleProperty() {
		this(0d);
	}

	public DoubleProperty(double initialValue) {
		this.value = initialValue;

		this.hasBounds = false;
	}

	public DoubleProperty(double minimalValue, double maximalValue) {
		this(0d, minimalValue, maximalValue);
	}

	public DoubleProperty(double initialValue, double minimalValue, double maximalValue) throws IllegalArgumentException {
		if (maximalValue < minimalValue) throw new IllegalArgumentException("Maximum value is bigger than minimal value");

		this.value = initialValue < minimalValue ? minimalValue : initialValue > maximalValue ? maximalValue : initialValue;

		this.minimalValue = minimalValue;
		this.maximalValue = maximalValue;

		this.hasBounds = true;
	}

	@Override
	public Double getValue() {
		return value;
	}

	public double getMinimalValue() {
		return minimalValue;
	}

	public void setMinimalValue(double minimalValue) {
		this.minimalValue = minimalValue;
	}

	public double getMaximalValue() {
		return maximalValue;
	}

	public void setMaximalValue(double maximalValue) {
		this.maximalValue = maximalValue;
	}

	@Override
	public void setValue(Object value) {
		if (value == null) throw new IllegalArgumentException("Given value is null");
		if (!(value instanceof Double)) throw new IllegalArgumentException("Given value is not a Double");

		if (hasBounds && ((Double) value < minimalValue || (Double) value > maximalValue)) throw new NumberFormatException("Given value is out of bounds (" + value + ")");

		this.value = (Double) value;
	}
}

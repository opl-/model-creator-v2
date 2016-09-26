package me.opl.apps.modelcreator2.model;

public abstract class RotatableElement extends Element {
	private Axis rotationAxis;
	private Position rotationOrigin;
	private float rotationAngle;

	public RotatableElement() {
		this.rotationAxis = null;
		this.rotationOrigin = null;
		this.rotationAngle = 0f;
	}

	public RotatableElement(Axis rotationAxis, Position rotationOrigin, float rotationAngle) {
		this.rotationAxis = rotationAxis;
		this.rotationOrigin = rotationOrigin.clone();
		this.rotationAngle = rotationAngle;
	}

	/**
	 * Get the rotation axis.
	 *
	 * @return Rotation axis
	 */
	public Axis getRotationAxis() {
		return rotationAxis;
	}

	/**
	 * Set the rotation axis of this element and it's fragments.
	 *
	 * @param rotationAxis Rotation Axis
	 */
	public void setRotationAxis(Axis rotationAxis) {
		this.rotationAxis = rotationAxis;
	}

	/**
	 * Get the Position instance holding the rotation origin.
	 *
	 * @return Rotation origin
	 */
	public Position getRotationOrigin() {
		return rotationOrigin;
	}

	/**
	 * Get the rotation angle of this element and it's fragments.
	 *
	 * @return Rotation angle
	 */
	public float getRotationAngle() {
		return rotationAngle;
	}

	/**
	 * Set the rotation angle of this element and it's fragments.
	 *
	 * @param rotationAngle New rotation angle.
	 */
	public void setRotationAngle(float rotationAngle) {
		this.rotationAngle = rotationAngle;
	}

	@Override
	public abstract RotatableFragment[] getFragments();
}

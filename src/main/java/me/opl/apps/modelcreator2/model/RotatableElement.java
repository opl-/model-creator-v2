package me.opl.apps.modelcreator2.model;

public abstract class RotatableElement extends Element {
	private Rotation rotation;
	private Position rotationOrigin;
	private boolean rotationRescale = false;

	/**
	 * Just like {@link Element}, but additionally stores rotation data allowing
	 * the element to be rotated. The rotation is used by the child
	 * {@link Fragment}s.
	 *
	 * Uses (0,0,0) as rotation, (8,8,8) as origin.
	 */
	public RotatableElement() {
		this.rotation = new Rotation(0, 0, 0);
		this.rotationOrigin = new Position(8, 8, 8);
	}

	/**
	 * Just like {@link Element}, but additionally stores rotation data allowing
	 * the element to be rotated. The rotation is used by the child
	 * {@link Fragment}s.
	 *
	 * @param rotation Rotation values
	 * @param rotationOrigin Point to rotate around
	 */
	public RotatableElement(Rotation rotation, Position rotationOrigin) {
		this.rotation = rotation.clone();
		this.rotationOrigin = rotationOrigin.clone();
	}

	/**
	 * Get the rotation of this element and its fragments.
	 *
	 * @return Current rotation
	 */
	public Rotation getRotation() {
		return rotation.clone();
	}

	/**
	 * Set the rotation of this element and its fragments.
	 *
	 * @param newRotation New rotation value
	 */
	public void setRotation(Rotation newRotation) {
		rotation.set(newRotation);
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
	 * Set the rotation origin of this element and it's fragments. This modifies
	 * the origin Position object.
	 *
	 * @param newRotationOrigin New rotation origin
	 */
	public void setRotationOrigin(Position newRotationOrigin) {
		rotationOrigin.set(newRotationOrigin);
	}

	/**
	 * TODO: find out how to apply rescaling and add it to docs
	 *
	 * @return `true` if element should be rescaled, `false` otherwise
	 */
	public boolean getRotationRescale() {
		return rotationRescale;
	}

	/**
	 * @param rotationRescale `true` if element should be rescaled, `false`
	 * otherwise
	 * @see RotatableElement#getRotationRescale()
	 */
	public void setRotationRescale(boolean rotationRescale) {
		this.rotationRescale = rotationRescale;
	}

	@Override
	public abstract RotatableFragment[] getFragments();
}

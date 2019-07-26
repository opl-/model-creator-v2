package me.opl.apps.modelcreator2.model;

// TODO: add flipping textures
public class FaceData {
	private Fragment fragment;

	private boolean visible = true;

	// TODO: setting to simulate solid blocks around
	private Face cullFace;
	private Texture texture;
	private UV uv;
	private int textureRotation;
	private int tintIndex = -1;

	/**
	 * Creates a new invisible face.
	 *
	 * @param fragment Fragment instance this face belongs to
	 */
	public FaceData(Fragment fragment) {
		this.fragment = fragment;
		this.visible = false;
	}

	/**
	 * Creates a new visible face with passed values.
	 *
	 * @param fragment Fragment instance this face belongs to
	 * @param cullFace Cull face of this face or {@code null} if none
	 * @param uv UV of this face or {@code null} for automatic
	 * @param texture Texture of this face or {@code null} if none
	 */
	public FaceData(Fragment fragment, Face cullFace, UV uv, Texture texture) {
		this.fragment = fragment;
		this.cullFace = cullFace;
		this.uv = uv;
		this.texture = texture;
	}

	public Fragment getFragment() {
		return fragment;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @return This face's texture or {@code null} if none
	 */
	public Texture getTexture() {
		return texture;
	}

	/**
	 * @param texture New texture for this face or {@code null} for none
	 */
	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	/**
	 * @return This face's cull face or {@code null} if none
	 */
	public Face getCullFace() {
		return cullFace;
	}

	/**
	 * @param texture New cull face for this face or {@code null} for none
	 */
	public void setCullFace(Face cullFace) {
		this.cullFace = cullFace;
	}

	/**
	 * @return This face's user defined UV mapping or {@code null} if none set
	 * (automatic)
	 */
	public UV getUV() {
		return uv;
	}

	/**
	 * @param newUV New UV for this face or {@code null} to unset
	 */
	public void setUV(UV newUV) {
		if (newUV == null) uv = null;
		else if (uv == null) uv = newUV.clone();
		else uv.setUV(newUV);
	}

	// TODO: is the rotation CW or CCW? mention it in the docs
	/**
	 * Returns the texture rotation.
	 *
	 * @return Texture rotation; one of {@code [0, 90, 180, 270]}
	 */
	public int getTextureRotation() {
		return textureRotation;
	}

	/**
	 * Sets the texture rotation.
	 *
	 * @param textureRotation New texture rotation. Has to be one of
	 * {@code [0, 90, 180, 270]}.
	 */
	public void setTextureRotation(int textureRotation) {
		if (textureRotation != 0 && textureRotation != 90 && textureRotation != 180 && textureRotation != 270) {
			throw new IllegalArgumentException("Invalid textureRotation: " + textureRotation);
		}

		this.textureRotation = textureRotation;
	}
}

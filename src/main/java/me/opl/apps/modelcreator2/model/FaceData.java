package me.opl.apps.modelcreator2.model;

public class FaceData {
	private Face cullFace;
	private UV uv;
	private Texture texture;

	public FaceData(Face cullFace, UV uv, Texture texture) {
		this.cullFace = cullFace;
		this.uv = uv;
		this.texture = texture;
	}

	/**
	 * Returns this face's texture or `null` if one isn't set.
	 * 
	 * @return This face's texture or `null`
	 */
	public Texture getTexture() {
		return texture;
	}
}

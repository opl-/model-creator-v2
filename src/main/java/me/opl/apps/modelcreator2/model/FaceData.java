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
}

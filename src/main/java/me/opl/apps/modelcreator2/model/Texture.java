package me.opl.apps.modelcreator2.model;

// TODO: implement texture references (note, much later: split this into 2 classes?)
public class Texture {
	private String name;
	private ResourceLocation resourceLocation;
	private String textureReference;

	public Texture(String name, ResourceLocation resourceLocation) {
		this.name = name;
		this.resourceLocation = resourceLocation;
	}

	public Texture(String name, String textureReference) {
		this.name = name;
		this.textureReference = textureReference;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean isReference() {
		return textureReference != null;
	}

	public ResourceLocation getResourceLocation() {
		return resourceLocation;
	}

	public String getTextureReference() {
		return textureReference;
	}

	@Override
	public String toString() {
		return "Texture[name=" + getName() + "," + (isReference() ? "ref=" + getTextureReference() : "loc=" + getResourceLocation()) + "]";
	}
}

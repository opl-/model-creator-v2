package me.opl.apps.modelcreator2.viewport;

import java.util.ArrayList;
import java.util.List;

import me.opl.apps.modelcreator2.model.Texture;

public class ResourceManager {
	private List<ShaderProgramResource> shaderPrograms;
	private List<TextureResource> textures;

	public ResourceManager() {
		shaderPrograms = new ArrayList<ShaderProgramResource>();
	}
}

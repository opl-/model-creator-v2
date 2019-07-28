package me.opl.apps.modelcreator2.viewport;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jogamp.opengl.GL3;

import me.opl.apps.modelcreator2.model.ResourceLocation;
import me.opl.apps.modelcreator2.model.ResourcePack;
import me.opl.apps.modelcreator2.model.ZipResourcePack;
import me.opl.apps.modelcreator2.viewport.resource.FramebufferResource;
import me.opl.apps.modelcreator2.viewport.resource.FramebufferResource.FramebufferRenderer;
import me.opl.apps.modelcreator2.viewport.resource.MissingTextureResource;
import me.opl.apps.modelcreator2.viewport.resource.Resource;
import me.opl.apps.modelcreator2.viewport.resource.ShaderProgramResource;
import me.opl.apps.modelcreator2.viewport.resource.SimpleTextureResource;
import me.opl.apps.modelcreator2.viewport.resource.TextureResource;

public class ResourceManager {
	private Map<String, ShaderProgramResource> shaderPrograms = new HashMap<>();
	private Map<ResourceLocation, TextureResource> textures = new HashMap<>();
	private List<FramebufferResource> framebuffers = new ArrayList<>();

	private List<ResourcePack> resourcePacks = new ArrayList<>();

	private List<Resource> updateList = new ArrayList<>();
	private List<Resource> destroyList = new ArrayList<>();

	private MissingTextureResource missingTexture;

	// TODO: all of this
	/**
	 * Manages all program resources.
	 *
	 * Loads all {@code Resource}s and maps {@code ResourceLocation}s to their
	 * Resources.
	 */
	public ResourceManager() {
		// XXX: uhh... no. move the string to settings and adding to the list to initializer or something
		// resourcePacks.add(new ZipResourcePack(new File("C:\\Users\\opl\\AppData\\Roaming\\.minecraft\\versions\\1.12.2\\1.12.2.jar")));
		// resourcePacks.add(new ZipResourcePack(new File("C:\\Users\\opl\\Desktop\\mymodels\\reinhardt\\assets.zip")));
		// resourcePacks.add(new ZipResourcePack(new File("E:\\model-creator-2\\1.12.2.jar")));
		resourcePacks.add(new ZipResourcePack(new File("/home/opl/MultiMC/libraries/com/mojang/minecraft/1.13/minecraft-1.13-client.jar")));

		missingTexture = new MissingTextureResource();
		addToUpdateList(missingTexture);
		textures.put(new ResourceLocation("missingno"), missingTexture);
	}

	public void addToUpdateList(Resource resource) {
		if (updateList.contains(resource)) return;

		updateList.add(resource);
	}

	public List<Resource> getUpdateList() {
		return updateList;
	}

	public void processUpdateList(GL3 gl) {
		if (updateList.size() == 0) return;

		for (Resource r : updateList) {
			if (!r.isInitialized()) r.prepare(gl);
			if (!r.isReady()) r.update(gl);
		}

		updateList.clear();
	}

	public void addToDestroyList(Resource resource) {
		if (destroyList.contains(resource)) return;

		destroyList.add(resource);
	}

	public List<Resource> getDestroyList() {
		return destroyList;
	}

	public void processDestroyList(GL3 gl) {
		for (Resource r : destroyList) r.destroy(gl);

		destroyList.clear();
	}

	public InputStream getResourceInputStream(String resourcePath) {
		for (ResourcePack rp : resourcePacks) {
			InputStream is = rp.getResourceStream(resourcePath);

			if (is != null) return is;
		}

		return null;
	}

	/**
	 * Loads a shader or returns an already loaded instance of it if a shader
	 * with the given name has already been loaded.
	 *
	 * @param programName Name of the shader program
	 * @param shaderTypes Types of shaders to load
	 * @return New or existing ShaderProgramResource
	 */
	public ShaderProgramResource loadShaderProgram(String programName, int[] shaderTypes) {
		// TODO: make this only load?
		ShaderProgramResource shaderProgram = getShaderProgram(programName);

		if (shaderProgram != null) return shaderProgram;

		shaderProgram = new ShaderProgramResource(programName, shaderTypes);

		shaderPrograms.put(programName, shaderProgram);
		addToUpdateList(shaderProgram);

		return shaderProgram;
	}

	public ShaderProgramResource getShaderProgram(String programName) {
		return shaderPrograms.get(programName);
	}

	public FramebufferResource createFramebuffer(FramebufferRenderer renderer, int width, int height) {
		FramebufferResource framebuffer = new FramebufferResource(renderer, width, height);

		framebuffers.add(framebuffer);
		addToUpdateList(framebuffer);

		return framebuffer;
	}

	public TextureResource loadTexture(ResourceLocation location) {
		if (getTexture(location) != null) return getTexture(location);

		// TODO: add loading animated textures by loading associated .mcmeta
		TextureResource texture = new SimpleTextureResource(this, location);

		textures.put(location, texture);
		addToUpdateList(texture);

		return texture;
	}

	/**
	 * Returns the texture at given location or {@code null} if no valid texture
	 * at given location.
	 *
	 * @param location Location of the texture
	 * @return Texture at location
	 */
	public TextureResource getTexture(ResourceLocation location) {
		return textures.get(location);
	}

	/**
	 * Returns the texture at given location or the missing texture if no
	 * valid texture at given location.
	 *
	 * @param location Location of the texture
	 * @return Texture at location or the missing texture
	 */
	public TextureResource getTextureOrMissing(ResourceLocation location) {
		TextureResource texture = textures.get(location);

		return texture == null ? missingTexture : texture;
	}
}

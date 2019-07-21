package me.opl.apps.modelcreator2.viewport.resource;

import java.io.IOException;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import me.opl.apps.modelcreator2.model.ResourceLocation;
import me.opl.apps.modelcreator2.viewport.ResourceManager;

public class SimpleTextureResource implements TextureResource {
	private ResourceManager resourceManager;
	private ResourceLocation location;

	private TextureData textureData;

	private int textureID = -1;

	// TODO: add automatic texture reloading (listener itself will have to be handled by the resource pack resource, because of things like zip resource packs)
	// TODO: add a setting to disable automatic texture reloading
	// TODO: this will have to support in program texture editing at some point - how will that work?
	public SimpleTextureResource(ResourceManager resourceManager, ResourceLocation location) {
		this.resourceManager = resourceManager;
		this.location = location;
	}

	@Override
	public boolean isInitialized() {
		return textureID != -1;
	}

	@Override
	public void prepare(GL3 gl) {
		try {
			textureData = TextureIO.newTextureData(gl.getGLProfile(), resourceManager.getResourceInputStream(location), false, "png");
		} catch (IOException e) {
			e.printStackTrace();
		}

		int[] ib = new int[1];
		gl.glGenTextures(1, ib, 0);
		textureID = ib[0];

		gl.glBindTexture(GL3.GL_TEXTURE_2D, textureID);

		gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);

		gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_NEAREST);
		gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_NEAREST);

		gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, textureData.getInternalFormat(), textureData.getWidth(), textureData.getHeight(), 0, textureData.getPixelFormat(), textureData.getPixelType(), textureData.getBuffer());

		gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);
	}

	@Override
	public boolean isReady() {
		return textureID != -1;
	}

	@Override
	public void bind(GL3 gl) {
		gl.glBindTexture(GL3.GL_TEXTURE_2D, textureID);
	}

	@Override
	public void unbind(GL3 gl) {
		gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);
	}

	@Override
	public void destroy(GL3 gl) {
		if (textureData != null) {
			textureData.flush();
			textureData = null;
		}

		if (textureID != -1) {
			gl.glDeleteTextures(1, new int[] {textureID}, 0);
			textureID = -1;
		}
	}

	@Override
	public int getWidth() {
		return textureData.getWidth();
	}

	@Override
	public int getHeight() {
		return textureData.getHeight();
	}

	@Override public void update(GL3 gl) {}
}

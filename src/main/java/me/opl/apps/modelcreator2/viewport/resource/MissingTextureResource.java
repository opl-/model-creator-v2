package me.opl.apps.modelcreator2.viewport.resource;

import java.nio.ByteBuffer;

import com.jogamp.opengl.GL3;

import me.opl.apps.modelcreator2.util.GLHelper;

public class MissingTextureResource implements TextureResource {
	private int textureID = -1;

	@Override
	public boolean isInitialized() {
		return textureID != -1;
	}

	@Override
	public void prepare(GL3 gl) {
		int[] ib = new int[1];
		gl.glGenTextures(1, ib, 0);
		textureID = ib[0];

		gl.glBindTexture(GL3.GL_TEXTURE_2D, textureID);

		gl.glTexStorage2D(GL3.GL_TEXTURE_2D, 1, GL3.GL_RGBA8, 2, 2);

		gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_REPEAT);
		gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_T, GL3.GL_REPEAT);

		gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_NEAREST);
		gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_NEAREST);

		ByteBuffer buffer = GLHelper.createByteBuffer(new byte[] {
			(byte) 0xff, 0, (byte) 0xff, (byte) 0xff,
			0, 0, 0, (byte) 0xff,
			0, 0, 0, (byte) 0xff,
			(byte) 0xff, 0x00, (byte) 0xff, (byte) 0xff
		});

		gl.glTexSubImage2D(GL3.GL_TEXTURE_2D, 0, 0, 0, 2, 2, GL3.GL_RGBA, GL3.GL_UNSIGNED_BYTE, buffer);

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
		if (textureID != -1) {
			gl.glDeleteTextures(1, new int[] {textureID}, 0);
			textureID = -1;
		}
	}

	@Override
	public int getWidth() {
		return 2;
	}

	@Override
	public int getHeight() {
		return 2;
	}

	@Override public void update(GL3 gl) {}
}

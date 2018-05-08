package me.opl.apps.modelcreator2.viewport.resource;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;

import com.jogamp.opengl.GL3;

import me.opl.apps.modelcreator2.util.GLHelper;

// TODO: switching textures of a single FBO is apparently faster than rebinding the FBO (http://www.songho.ca/opengl/gl_fbo.html)
public class FramebufferResource implements Resource {
	private FramebufferRenderer renderer;

	private int fbo = -1;
	private int colorTex = -1;
	private int depthTex = -1;

	private boolean resized = true;
	private int width;
	private int height;

	private ByteBuffer buffer;
	private BufferedImage image;

	public FramebufferResource(FramebufferRenderer renderer, int width, int height) {
		this.renderer = renderer;

		this.width = width;
		this.height = height;
	}

	public FramebufferRenderer getRenderer() {
		return renderer;
	}

	public void setRenderer(FramebufferRenderer framebufferRenderer) {
		this.renderer = framebufferRenderer;
	}

	@Override
	public boolean isInitialized() {
		return fbo != -1;
	}

	@Override
	public void prepare(GL3 gl) {
		if (isInitialized()) return;

		int[] ib = new int[1];

		gl.glGenFramebuffers(1, ib, 0);
		fbo = ib[0];
	}

	@Override
	public boolean isReady() {
		return fbo != -1 && !resized;
	}

	@Override
	public void update(GL3 gl) {
		if (!resized) return;

		buffer = GLHelper.createByteBuffer(width * height * 4);

		if (image != null) image.flush();
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		gl.glBindFramebuffer(GL3.GL_FRAMEBUFFER, fbo);

		recreateTextures(gl);

		gl.glBindFramebuffer(GL3.GL_FRAMEBUFFER, 0);
	}

	@Override
	public void bind(GL3 gl) {
		gl.glBindFramebuffer(GL3.GL_FRAMEBUFFER, fbo);

		gl.glViewport(0, 0, width, height);
	}

	@Override
	public void unbind(GL3 gl) {
		gl.glBindFramebuffer(GL3.GL_FRAMEBUFFER, 0);
	}

	// TODO: most likely plenty of room for performance improvements
	public void saveImage(GL3 gl) {
		buffer.clear();

		gl.glReadBuffer(GL3.GL_COLOR_ATTACHMENT0);
		gl.glReadPixels(0, 0, width, height, GL3.GL_RGBA, GL3.GL_UNSIGNED_BYTE, buffer);

		buffer.clear();

		int[] data = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

		for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) {
			byte r = buffer.get();
			byte g = buffer.get();
			byte b = buffer.get();
			byte a = buffer.get();

			data[(height - y - 1) * width + x] = ((a << 24) & 0xFF000000) | ((r << 16) & 0xFF0000) | ((g << 8) & 0xFF00) | (b & 0xFF);
		}
	}

	public BufferedImage getImage() {
		return image;
	}

	private void recreateTextures(GL3 gl) {
		if (colorTex != -1) gl.glDeleteTextures(1, new int[] {colorTex}, 0);
		if (depthTex != -1) gl.glDeleteRenderbuffers(1, new int[] {depthTex}, 0);

		int[] ib = new int[1];

		gl.glGenTextures(1, ib, 0);
		colorTex = ib[0];
		gl.glGenRenderbuffers(1, ib, 0);
		depthTex = ib[0];

		gl.glBindTexture(GL3.GL_TEXTURE_2D, colorTex);

		gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_NEAREST);
		gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_NEAREST);
		gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);

		gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGBA8, width, height, 0, GL3.GL_RGBA, GL3.GL_UNSIGNED_BYTE, null);

		gl.glFramebufferTexture2D(GL3.GL_FRAMEBUFFER, GL3.GL_COLOR_ATTACHMENT0, GL3.GL_TEXTURE_2D, colorTex, 0);

		gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);

		gl.glBindRenderbuffer(GL3.GL_RENDERBUFFER, depthTex);
		gl.glRenderbufferStorage(GL3.GL_RENDERBUFFER, GL3.GL_DEPTH_COMPONENT, width, height);

		gl.glFramebufferRenderbuffer(GL3.GL_FRAMEBUFFER, GL3.GL_DEPTH_ATTACHMENT, GL3.GL_RENDERBUFFER, depthTex);

		gl.glBindRenderbuffer(GL3.GL_RENDERBUFFER, 0);

		checkErrors(gl);

		resized = false;
	}

	private void checkErrors(GL3 gl) {
		// TODO: this function is pretty expensive (https://hacks.mozilla.org/2014/01/webgl-deferred-shading/) - disable outside of testing
		int error = gl.glCheckFramebufferStatus(GL3.GL_FRAMEBUFFER);

		if (error != GL3.GL_FRAMEBUFFER_COMPLETE) throw new IllegalStateException("Framebuffer err: " + error);
	}

	public void setSize(int width, int height) {
		if (this.width == width && this.height == height) return;

		if (width < 1 || height < 1) throw new IllegalArgumentException("Invalid size given (" + width + "x" + height + ")");

		this.width = width;
		this.height = height;

		resized = true;
	}

	@Override
	public void destroy(GL3 gl) {
		if (colorTex != -1) {
			gl.glDeleteTextures(2, new int[] {colorTex}, 0);
			colorTex = -1;
		}

		if (depthTex != -1) {
			gl.glDeleteRenderbuffers(2, new int[] {depthTex}, 0);
			depthTex = -1;
		}

		if (fbo != -1) {
			gl.glDeleteFramebuffers(1, new int[] {fbo}, 0);
			fbo = -1;
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public static interface FramebufferRenderer {
		public void render(GL3 gl, FramebufferResource framebuffer);
	}
}

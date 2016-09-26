package me.opl.apps.modelcreator2.viewport;

import javax.media.opengl.GL4;

// TODO
public class TextureResource implements Resource {
	@Override
	public int glID() {
		return 0;
	}

	@Override
	public void prepare(GL4 gl) {}

	@Override
	public void bind(GL4 gl) {}

	@Override
	public void unbind(GL4 gl) {}

	@Override
	public void destroy(GL4 gl) {}
}

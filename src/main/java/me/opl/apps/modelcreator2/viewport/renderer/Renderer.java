package me.opl.apps.modelcreator2.viewport.renderer;

import javax.media.opengl.GL4;

public interface Renderer {
	public void prepare(GL4 gl);

	public void update(GL4 gl);

	public void render(GL4 gl);

	public void destroy(GL4 gl);
}

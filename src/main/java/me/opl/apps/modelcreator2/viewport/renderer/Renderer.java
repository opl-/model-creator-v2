package me.opl.apps.modelcreator2.viewport.renderer;

import com.jogamp.opengl.GL3;

public interface Renderer {
	public boolean isInitialized();

	public void prepare(GL3 gl);

	public boolean isReady();

	public void update(GL3 gl);

	public void render(GL3 gl);

	public void destroy(GL3 gl);
}

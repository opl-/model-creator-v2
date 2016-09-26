package me.opl.apps.modelcreator2.viewport;

import javax.media.opengl.GL4;

public interface Resource {
	public int glID();

	public void prepare(GL4 gl);

	public void bind(GL4 gl);

	public void unbind(GL4 gl);

	public void destroy(GL4 gl);
}

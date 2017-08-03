package me.opl.apps.modelcreator2.viewport.renderer;

import com.jogamp.opengl.GL3;

public interface ToolRenderer {
	public boolean isInitialized();

	public void prepare(GL3 gl);

	public boolean isReady();

	public void update(GL3 gl);

	/**
	 * Called right before the model is rendered. By default the model shader is
	 * bound. The renderer can change the shader.
	 *
	 * @param gl The GL instance used for this render
	 */
	public void renderBeforeModel(GL3 gl);

	/**
	 * Called right after the model is rendered. By default the model shader is
	 * bound. The renderer can change the shader.
	 *
	 * @param gl The GL instance used for this render
	 */
	public void renderAfterModel(GL3 gl);

	/**
	 * Called after the model is rendered. By default the line shader is bound.
	 * The renderer can change the shader.
	 *
	 * @param gl The GL instance used for this render
	 */
	public void renderLines(GL3 gl);

	public void destroy(GL3 gl);
}

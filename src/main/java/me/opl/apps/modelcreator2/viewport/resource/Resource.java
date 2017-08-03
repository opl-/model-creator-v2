package me.opl.apps.modelcreator2.viewport.resource;

import com.jogamp.opengl.GL3;

public interface Resource {
	/**
	 * @return {@code true} if {@link Resource#prepare(GL3)} has been called,
	 * {@code false} otherwise
	 */
	public boolean isInitialized();

	/**
	 * Called when the resource is first created or after {@link
	 * Resource#destroy(GL3)} has been called. The resource can allocate the
	 * resources it will need and set their initial values here.
	 *
	 * @param gl An instance of the GL pipeline
	 */
	public void prepare(GL3 gl);

	/**
	 * Called before a render cycle to ensure that the resource is ready to be
	 * rendered. If this method returns {@code false}, {@link
	 * Resource#update(GL3)} will be called in order for the resource to update
	 * all of its assets.
	 *
	 * @return {@code false} if {@link Resource#update(GL3)} should be called
	 * before rendering, {@code true} otherwise
	 */
	public boolean isReady();

	/**
	 * Called before rendering if {@link Resource#isReady()} returns {@code
	 * false}. After this method is called, the resource should be ready to be
	 * rendered and {@link Resource#isReady()} should return {@code true}.
	 *
	 * @param gl An instance of the GL pipeline
	 */
	public void update(GL3 gl);

	/**
	 * Called to bind the resource. Results of calling this method when the
	 * resource isn't initialized or ready are undefined.
	 *
	 * @param gl An instance of the GL pipeline
	 */
	public void bind(GL3 gl);

	/**
	 * Called to unbind the resource.
	 *
	 * @param gl An instance of the GL pipeline
	 */
	public void unbind(GL3 gl);

	/**
	 * Called to destroy the resource. This method should free all data used by
	 * this resource. Afterwards, calling the {@link Resource#isInitialized()}
	 * method should return {@code false} and the resource should not be usable
	 * until the {@link Resource#prepare(GL3)} method is called again. Does
	 * nothing if the resource has not been initialized.
	 *
	 * @param gl An instance of the GL pipeline
	 */
	public void destroy(GL3 gl);
}

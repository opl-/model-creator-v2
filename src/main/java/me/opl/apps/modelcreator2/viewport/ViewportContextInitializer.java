package me.opl.apps.modelcreator2.viewport;

import com.jogamp.opengl.GL3;

import me.opl.apps.modelcreator2.viewport.RenderManager.ContextInitializer;
import me.opl.apps.modelcreator2.viewport.renderer.BoundaryRenderer;
import me.opl.apps.modelcreator2.viewport.renderer.CompassOverlayRenderer;

public class ViewportContextInitializer implements ContextInitializer {
	@Override
	public void init(GL3 gl, RenderManager renderManager) {
		gl.glEnable(GL3.GL_DEPTH_TEST);
		gl.glDepthFunc(GL3.GL_LEQUAL);

		gl.glBlendFunc(GL3.GL_SRC_ALPHA, GL3.GL_ONE_MINUS_SRC_ALPHA);

		gl.glClearColor(0.35f, 0.35f, 0.35f, 1f);

		gl.glCullFace(GL3.GL_FRONT);
		gl.glEnable(GL3.GL_CULL_FACE);

		renderManager.getResourceManager().loadShaderProgram("model", new int[] {GL3.GL_VERTEX_SHADER, GL3.GL_FRAGMENT_SHADER});
		renderManager.getResourceManager().loadShaderProgram("line", new int[] {GL3.GL_VERTEX_SHADER, GL3.GL_GEOMETRY_SHADER, GL3.GL_FRAGMENT_SHADER});

		renderManager.registerRenderer(new BoundaryRenderer());
		renderManager.registerRenderer(new CompassOverlayRenderer());
	}
}

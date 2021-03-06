package me.opl.apps.modelcreator2.viewport;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.FloatUtil;

import me.opl.apps.modelcreator2.ModelCreator;
import me.opl.apps.modelcreator2.model.Axis;
import me.opl.apps.modelcreator2.model.Element;
import me.opl.apps.modelcreator2.model.Fragment;
import me.opl.apps.modelcreator2.model.MinecraftModel;
import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.util.GLHelper;
import me.opl.apps.modelcreator2.util.RotationHelper;
import me.opl.apps.modelcreator2.viewport.ViewportFramebufferRenderer.CameraMode.View;
import me.opl.apps.modelcreator2.viewport.renderer.BoundaryRenderer;
import me.opl.apps.modelcreator2.viewport.renderer.CompassOverlayRenderer;
import me.opl.apps.modelcreator2.viewport.renderer.Renderer;
import me.opl.apps.modelcreator2.viewport.renderer.RotationHandleRenderer;
import me.opl.apps.modelcreator2.viewport.renderer.ToolRenderer;
import me.opl.apps.modelcreator2.viewport.resource.FramebufferResource;
import me.opl.apps.modelcreator2.viewport.resource.FramebufferResource.FramebufferRenderer;
import me.opl.apps.modelcreator2.viewport.resource.ShaderProgramResource;

public class ViewportFramebufferRenderer implements FramebufferRenderer {
	private ModelCreator modelCreator;
	private RenderManager renderManager;

	private ShaderProgramResource modelShader;
	private ShaderProgramResource lineShader;

	private CameraMode cameraMode;
	private RenderMode renderMode;

	private Position cameraTarget;
	private Position cameraPosition;

	private int width = 0;
	private int height = 0;
	private float[] viewProjectionMatrix = new float[32];
	private float[] viewProjectionMatrixUnproject = new float[32];

	private BoundaryRenderer boundaryRenderer;
	private CompassOverlayRenderer compassOverlayRenderer;

	private MinecraftModel model;

	private RotationHandleRenderer xHandleRenderer;

	private long startTime;

	public ViewportFramebufferRenderer(ModelCreator modelCreator) {
		this(modelCreator, CameraMode.PERSPECTIVE_FREE, RenderMode.TEXTURED);
	}

	public ViewportFramebufferRenderer(ModelCreator modelCreator, CameraMode cameraMode, RenderMode renderMode) {
		this.modelCreator = modelCreator;
		this.renderManager = modelCreator.getRenderManager();

		this.cameraMode = cameraMode;
		this.renderMode = renderMode;

		modelShader = renderManager.getResourceManager().getShaderProgram("model");
		lineShader = renderManager.getResourceManager().getShaderProgram("line");

		cameraTarget = new Position(8f, 8f, 8f);
		cameraPosition = new Position(100f, 80f, -42f).subtract(cameraTarget).normalize().multiply(50f).add(cameraTarget);

		boundaryRenderer = renderManager.getRenderer(BoundaryRenderer.class);
		compassOverlayRenderer = renderManager.getRenderer(CompassOverlayRenderer.class);

		xHandleRenderer = new RotationHandleRenderer(Axis.X, new Position(8, 8, 8), 16f);

		startTime = System.currentTimeMillis();
	}

	@Override
	public void render(GL3 gl, FramebufferResource framebuffer) {
		// XXX
		if (!xHandleRenderer.isReady()) xHandleRenderer.prepare(gl);

		gl.glClear(GL3.GL_DEPTH_BUFFER_BIT | GL3.GL_COLOR_BUFFER_BIT);

		width = framebuffer.getWidth();
		height = framebuffer.getHeight();

		FloatUtil.makeLookAt(viewProjectionMatrix, 0, (!cameraMode.hasDirection() ? cameraPosition : cameraTarget.clone().subtract(cameraMode.getDirection().multiply(cameraTarget.distance(cameraPosition)))).toArray(), 0, cameraTarget.toArray(), 0, new float[] {0, 1, 0}, 0, new float[16]);
		System.arraycopy(viewProjectionMatrix, 0, viewProjectionMatrixUnproject, 0, 16);

		if (cameraMode.getView() == View.PERSPECTIVE) {
			FloatUtil.makePerspective(viewProjectionMatrix, 16, true, 60f * RotationHelper.TO_RADIANS, (float) width / (float) height, 0.2f, 1000f);
			FloatUtil.makePerspective(viewProjectionMatrixUnproject, 16, true, 60f * RotationHelper.TO_RADIANS, (float) width / (float) height, 1f, 1000f);
		} else if (cameraMode.getView() == View.ORTHO) {
			float dist = cameraTarget.distance(cameraPosition) * 1000f;
			float ar = (float) width  / (float) height;

			float up = dist / (float) height / 2f * (1f / ar);
			float left = dist / (float) width / 2f * ar;

			FloatUtil.makeOrtho(viewProjectionMatrix, 16, true, -left, left, -up, up, 1f, 1000f);
			System.arraycopy(viewProjectionMatrix, 16, viewProjectionMatrixUnproject, 16, 16);
		}

		float[] projectionView = new float[16];
		FloatUtil.multMatrix(viewProjectionMatrix, 16, viewProjectionMatrix, 0, projectionView, 0);

		ToolRenderer toolRenderer = model == null ? null : renderManager.getToolRenderer(modelCreator.getToolManager(model.getModelWithElements()).getActiveTool());

		if (toolRenderer != null) {
			if (!toolRenderer.isInitialized()) toolRenderer.prepare(gl);
			if (!toolRenderer.isReady()) toolRenderer.update(gl);
		}

		modelShader.bind(gl);

		int modelUniform = modelShader.getUniformLocation(gl, "modelMatrix");
		gl.glUniformMatrix4fv(modelUniform, 1, false, GLHelper.IDENTITY_MATRIX, 0);

		int projectionUniform = modelShader.getUniformLocation(gl, "viewProjectionMatrix");
		gl.glUniformMatrix4fv(projectionUniform, 1, false, projectionView, 0);

		int timeUniform = modelShader.getUniformLocation(gl, "time");
		gl.glUniform1f(timeUniform, (System.currentTimeMillis() - startTime) / 1000f);

		int frameUniform = modelShader.getUniformLocation(gl, "frame");
		gl.glUniform1f(frameUniform, renderManager.getPlaybackState().getCurrentFrame());

		int texturesUniform = modelShader.getUniformLocation(gl, "textures");
		gl.glUniform1iv(texturesUniform, 16, new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}, 0);

		if (toolRenderer != null) toolRenderer.renderBeforeModel(gl);

		modelShader.bind(gl);

		// solid stuff

		if (model != null) {
			for (Element e : model.getElements()) for (Fragment f : e.getFragments()) {
				Renderer renderer = renderManager.getFragmentRenderer(model, f);
				if (!renderer.isInitialized()) renderer.prepare(gl);
				if (!renderer.isReady()) renderer.update(gl);

				renderer.render(gl);
			}
		}

		if (toolRenderer != null) toolRenderer.renderAfterModel(gl);


		lineShader.bind(gl);

		modelUniform = lineShader.getUniformLocation(gl, "modelMatrix");
		gl.glUniformMatrix4fv(modelUniform, 1, false, GLHelper.IDENTITY_MATRIX, 0);

		projectionUniform = lineShader.getUniformLocation(gl, "viewProjectionMatrix");
		gl.glUniformMatrix4fv(projectionUniform, 1, false, projectionView, 0);

		int cameraDirectionUniform = lineShader.getUniformLocation(gl, "cameraDirection");
		gl.glUniform3fv(cameraDirectionUniform, 1, getCameraDirection().toArray(), 0);

		int cameraPositionUniform = lineShader.getUniformLocation(gl, "cameraPosition");
		gl.glUniform3fv(cameraPositionUniform, 1, getCameraPosition().toArray(), 0);

		int constantWidthUniform = lineShader.getUniformLocation(gl, "constantWidth");
		gl.glUniform1i(constantWidthUniform, 1);

		// TODO: switch to multisampling?
		gl.glEnable(GL3.GL_POLYGON_SMOOTH);

		if (!getRenderMode().isPlainRender()) {
			boundaryRenderer.render(gl);
			if (toolRenderer != null) toolRenderer.renderLines(gl);
			gl.glDisable(GL3.GL_DEPTH_TEST);
			compassOverlayRenderer.render(gl);
			gl.glEnable(GL3.GL_DEPTH_TEST);
		}

		gl.glDisable(GL3.GL_POLYGON_SMOOTH);
	}

	public CameraMode getCameraMode() {
		return cameraMode;
	}

	public void setCameraMode(CameraMode cameraMode) {
		if (cameraMode == null) throw new IllegalArgumentException("cameraMode is null");
		this.cameraMode = cameraMode;
		// XXX
//		repaint(); // TODO: fire event?
	}

	public RenderMode getRenderMode() {
		return renderMode;
	}

	public void setRenderMode(RenderMode renderMode) {
		if (renderMode == null) throw new IllegalArgumentException("renderMode is null");
		this.renderMode = renderMode;
		// XXX
//		repaint(); // TODO: fire event?
	}

	/**
	 * @return The instance of Position storing the camera's position
	 */
	public Position getCameraPosition() {
		return cameraPosition;
	}

	/**
	 * @return The instance of Position storing the camera's target
	 */
	public Position getCameraTarget() {
		return cameraTarget;
	}

	/**
	 * Returns a Position object with the camera's direction.
	 *
	 * @return Direction of the camera
	 */
	public Position getCameraDirection() {
		return cameraMode.hasDirection() ? cameraMode.getDirection() : cameraTarget.clone().subtract(cameraPosition).normalize();
	}

	/**
	 * Returns the view projection matrices used on last render.
	 *
	 * @return Look-at matrix at 0-15, frustrum/orthogonal matrix at 16-31
	 */
	public float[] getViewProjectionMatrix() {
		return viewProjectionMatrix;
	}

	/**
	 * Returns the view projection matrices used on last render. The projection
	 * matrix returned by this method has the near clip plane set to 1, because
	 * values smaller than 1 break unprojecting.
	 *
	 * @return Look-at matrix at 0-15, frustrum/orthogonal matrix at 16-31
	 */
	public float[] getViewProjectionMatrixUnproject() {
		return viewProjectionMatrixUnproject;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setDisplayedModel(MinecraftModel model) {
		this.model = model;
	}

	public MinecraftModel getDisplayedModel() {
		return model;
	}

	/**
	 * Shorthand for {@link MinecraftModel#getModelWithElements()}.
	 *
	 * @return Model which holds the elements or {@code null} if none
	 */
	public MinecraftModel getEditedModel() {
		return model == null ? null : model.getModelWithElements();
	}

	public static enum CameraMode {
		PERSPECTIVE_FREE("Perspective Free", View.PERSPECTIVE, null),
		ORTHO_FREE("Ortho Free", View.ORTHO, null),
		ORTHO_NORTH("Ortho North", View.ORTHO, new Position(0f, 0f, 1f)),
		ORTHO_SOUTH("Ortho South", View.ORTHO, new Position(0f, 0f, -1f)),
		ORTHO_EAST("Ortho East", View.ORTHO, new Position(-1f, 0f, 0f)),
		ORTHO_WEST("Ortho West", View.ORTHO, new Position(1f, 0f, 0f)),
		ORTHO_UP("Ortho Up", View.ORTHO, new Position(0f, -1f, 0.0005f).normalize()),
		ORTHO_DOWN("Ortho Down", View.ORTHO, new Position(0f, 1f, 0.0005f).normalize());

		public static enum View {
			PERSPECTIVE,
			ORTHO
		}

		private String displayName;
		private View view;
		private Position direction;

		private CameraMode(String displayName, View view, Position direction) {
			this.displayName = displayName;
			this.view = view;
			this.direction = direction;
		}

		public String getDisplayName() {
			return displayName;
		}

		public View getView() {
			return view;
		}

		public Position getDirection() {
			return direction == null ? null : direction.clone();
		}

		public boolean hasDirection() {
			return direction != null;
		}
	}

	public static enum RenderMode {
		TEXTURED("Textured", false),
		MODEL("Model", true);

		private String displayName;
		private boolean plainRender;

		private RenderMode(String displayName, boolean plainRender) {
			this.displayName = displayName;
			this.plainRender = plainRender;
		}

		public String getDisplayName() {
			return displayName;
		}

		public boolean isPlainRender() {
			return plainRender;
		}
	}
}

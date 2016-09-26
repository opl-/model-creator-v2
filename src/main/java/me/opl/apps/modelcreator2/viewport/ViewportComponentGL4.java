package me.opl.apps.modelcreator2.viewport;

import java.awt.Color;
import java.awt.Graphics;

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.math.FloatUtil;

import me.opl.apps.modelcreator2.MCVariableProvider;
import me.opl.apps.modelcreator2.ModelWindow;
import me.opl.apps.modelcreator2.model.Axis;
import me.opl.apps.modelcreator2.model.BaseModel;
import me.opl.apps.modelcreator2.model.BlockModel;
import me.opl.apps.modelcreator2.model.CuboidElement;
import me.opl.apps.modelcreator2.model.Element;
import me.opl.apps.modelcreator2.model.Fragment;
import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.viewport.ViewportComponentGL4.CameraMode.View;
import me.opl.apps.modelcreator2.viewport.renderer.BoundaryRenderer;
import me.opl.apps.modelcreator2.viewport.renderer.CompassOverlayRenderer;
import me.opl.apps.modelcreator2.viewport.renderer.RotationHandleRenderer;
import me.opl.libs.tablib.VariableProvider;

public class ViewportComponentGL4 extends GLJPanel implements GLEventListener {
	private static final long serialVersionUID = -8904162480695586692L;

	private ViewportController controller;

	private GLU glu;

	private CameraMode cameraMode;
	private RenderMode renderMode;

	private Position cameraTarget;
	private Position cameraPosition;

	private BoundaryRenderer boundaryRenderer;
	private RotationHandleRenderer xRotationHandleRenderer;
	private RotationHandleRenderer yRotationHandleRenderer;
	private RotationHandleRenderer zRotationHandleRenderer;
	private CompassOverlayRenderer compassOverlayRenderer;

	private float[] viewProjectionMatrix;
	private int[] viewportBuffer;

	private ShaderProgramResource modelShader;
	private ShaderProgramResource lineShader;

	/*private int vao;
	private int vbo;
	private int ebo;*/

	public ViewportComponentGL4(VariableProvider vp) {
		this(vp, CameraMode.PERSPECTIVE_FREE, RenderMode.TEXTURED);
	}

	public ViewportComponentGL4(VariableProvider vp, CameraMode cameraMode, RenderMode renderMode) {
		addGLEventListener(this);

		this.cameraMode = cameraMode;
		this.renderMode = renderMode;

		glu = new GLU();

		cameraTarget = new Position(8f, 8f, 8f);
		cameraPosition = new Position(100f, 80f, -42f).subtract(cameraTarget).normalize().multiply(100f).add(cameraTarget);

		boundaryRenderer = new BoundaryRenderer();
		xRotationHandleRenderer = new RotationHandleRenderer(Axis.X, new Position(8f, 4f, 8f), new Position(8f, 4f, 8f).distance(16f, 4f, 16f) + 1f);
		yRotationHandleRenderer = new RotationHandleRenderer(Axis.Y, new Position(8f, 4f, 8f), new Position(8f, 4f, 8f).distance(16f, 4f, 16f) + 1f);
		zRotationHandleRenderer = new RotationHandleRenderer(Axis.Z, new Position(8f, 4f, 8f), new Position(8f, 4f, 8f).distance(16f, 4f, 16f) + 1f);
		compassOverlayRenderer = new CompassOverlayRenderer(this);

		viewProjectionMatrix = new float[32];
		viewportBuffer = new int[4];

		// XXX: debug
		controller = new ViewportController((ModelWindow) vp.getObject(MCVariableProvider.KEY_MODEL_WINDOW), this, new GLU());
		BaseModel model = new BlockModel(null);
		model.addElement(new CuboidElement());
		controller.setModel(model);

		addMouseListener(controller);
		addMouseMotionListener(controller);
		addMouseWheelListener(controller);

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					// XXX: debug

					repaint();

					try {Thread.sleep(50l);}
					catch (Exception e) {e.printStackTrace();}
				}
			}
		}).start();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		String cameraModeButtonText = cameraMode.getDisplayName() + " \u25be";
		g.setColor(Color.BLACK);
		g.drawString(cameraModeButtonText, 4, 13);
		g.setColor(Color.WHITE);
		g.drawString(cameraModeButtonText, 3, 12);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL4 gl = drawable.getGL().getGL4();

		gl.glClear(GL4.GL_DEPTH_BUFFER_BIT | GL4.GL_COLOR_BUFFER_BIT);


		FloatUtil.makeLookAt(viewProjectionMatrix, 0, (!cameraMode.hasDirection() ? cameraPosition : cameraTarget.clone().subtract(cameraMode.getDirection().multiply(cameraTarget.distance(cameraPosition)))).toArray(), 0, cameraTarget.toArray(), 0, new float[] {0, 1, 0}, 0, new float[16]);

		if (cameraMode.getView() == View.PERSPECTIVE) {
			FloatUtil.makePerspective(viewProjectionMatrix, 16, true, 45f, (float) getWidth() / (float) getHeight(), 1f, 1000f);
		} else if (cameraMode.getView() == View.ORTHO) {
			float dist = cameraTarget.distance(cameraPosition) * 1000f;
			float ar = (float) getWidth() / (float) getHeight();

			float up = dist / (float) getHeight() / 2f * (1f / ar);
			float left = dist / (float) getWidth() / 2f * ar;

			FloatUtil.makeOrtho(viewProjectionMatrix, 16, true, -left, left, -up, up, 1f, 1000f);
		}

		float[] projectionView = new float[16];
		FloatUtil.multMatrix(viewProjectionMatrix, 16, viewProjectionMatrix, 0, projectionView, 0);

		modelShader.bind(gl);

		int projectionUniform = gl.glGetUniformLocation(modelShader.glID(), "viewProjection");
		gl.glUniformMatrix4fv(projectionUniform, 1, false, projectionView, 0);

		lineShader.bind(gl);

		projectionUniform = gl.glGetUniformLocation(lineShader.glID(), "viewProjection");
		gl.glUniformMatrix4fv(projectionUniform, 1, false, projectionView, 0);

		int cameraDirectionUniform = gl.glGetUniformLocation(lineShader.glID(), "cameraDirection");
		gl.glUniform3fv(cameraDirectionUniform, 1, getCameraDirection().toArray(), 0);

		int cameraPositionUniform = gl.glGetUniformLocation(lineShader.glID(), "cameraPosition");
		gl.glUniform3fv(cameraPositionUniform, 1, getCameraPosition().toArray(), 0);

		int constantWidthUniform = gl.glGetUniformLocation(lineShader.glID(), "constantWidth");
		gl.glUniform1i(constantWidthUniform, 1);


		modelShader.bind(gl);

		gl.glEnable(GL4.GL_BLEND);

		if (controller.getModel() != null) {
			for (Element e : controller.getModel().getElements()) {
				for (Fragment f : e.getFragments()) {
					f.getRenderer().render(gl);
				}
			}
		}

		gl.glDisable(GL4.GL_BLEND);


		lineShader.bind(gl);

		gl.glEnable(GL4.GL_POLYGON_SMOOTH);

		/*xRotationHandleRenderer.render(gl);
		yRotationHandleRenderer.render(gl);
		zRotationHandleRenderer.render(gl);*/
		boundaryRenderer.render(gl);
		compassOverlayRenderer.render(gl);

		gl.glDisable(GL4.GL_POLYGON_SMOOTH);


		int err = gl.glGetError();
		if (err != 0) System.out.println("DREEEEEW " + err); // XXX

		/*
		// TODO: setup?

		/*if (clickLocation != null) {
			double[] pos = new double[3];
			glu.gluUnProject(clickLocation.getX(), getHeight() - clickLocation.getY(), buffer[32], buffer, 0, buffer, 16, viewport.array(), 0, pos, 0);
			Position rayPoint = new Position(pos[0], pos[1], pos[2]);

			gl.glPointSize(2f);
			gl.glBegin(GL2.GL_POINTS);
			gl.glVertex3dv(rayPoint.toArray(), 0);
			gl.glEnd();

			test(gl, cameraPosition, rayPoint);
		}*//*

		// Minecraft position visualizer
		// TODO: add a toggle for it
		gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		gl.glOrtho(30 - getWidth(), 30, -getHeight(), 30, -1000d, 1000d);

		Position dir = getCameraDirection();
		glu.gluLookAt(-dir.getX(), -dir.getY(), -dir.getZ(), 0d, 0d, 0d, 0, 1, 0);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glPushMatrix();

		gl.glLineWidth(2f);
		gl.glColor3d(1d, 0d, 0d);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3d( 0d, 0d, 0d);
		gl.glVertex3d(20d, 0d, 0d);
		gl.glEnd();

		gl.glColor3d(0d, 1d, 0d);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3d(0d,  0d, 0d);
		gl.glVertex3d(0d, 20d, 0d);
		gl.glEnd();

		gl.glColor3d(0d, 0d, 1d);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3d(0d, 0d,  0d);
		gl.glVertex3d(0d, 0d, 20d);
		gl.glEnd();

		gl.glPopMatrix();

		// XXX: temp - testing ray - polygon intersections

		/*Position[] plane = new Position[] {new Position(10d, 10d, 10d), new Position(18d, 10d, 10d), new Position(10d, 18d, 18d)};
		Position[] ray = new Position[] {new Position(15d, 15d, 0d), new Position(0d, 0d, 1d)};

		Position i = IntersectionHelper.rayPlaneIntersection(ray, plane);
		boolean hit = IntersectionHelper.rayQuadIntersection(ray, plane);

		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);

		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3dv(plane[0].toArray(), 0);
		gl.glVertex3dv(plane[1].toArray(), 0);
		gl.glVertex3d(plane[1].getX(), plane[2].getY(), plane[2].getZ());
		gl.glVertex3dv(plane[2].toArray(), 0);
		gl.glEnd();

		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

		gl.glColor3d(1d, 0d, i == null ? 0d : 1d);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3dv(ray[0].toArray(), 0);
		gl.glVertex3dv(ray[1].clone().multiply(10000d, 10000d, 10000d).add(ray[0]).toArray(), 0);
		gl.glEnd();

		if (i != null) {
			gl.glColor3f(hit ? 0d : 1d, hit ? 1d : 0d, 0d);
			gl.glPointSize(5f);
			gl.glBegin(GL2.GL_POINTS);
			gl.glVertex3dv(i.toArray(), 0);
			gl.glEnd();
		}*/
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {}

	@Override
	public void init(GLAutoDrawable drawable) {
		try {
			GL4 gl = drawable.getGL().getGL4();

			gl.glEnable(GL4.GL_DEPTH_TEST);
			gl.glDepthFunc(GL4.GL_LEQUAL);

			gl.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);

			// TODO: make configurable
			gl.glClearColor(0.35f, 0.35f, 0.35f, 1f);
			//gl.glClearColor(0.5f, 0.5f, 1f, 1f);


			modelShader = new ShaderProgramResource("model-test", new int[] {GL4.GL_VERTEX_SHADER, GL4.GL_FRAGMENT_SHADER});
			modelShader.prepare(gl);

			lineShader = new ShaderProgramResource("line", new int[] {GL4.GL_VERTEX_SHADER, GL4.GL_GEOMETRY_SHADER, GL4.GL_FRAGMENT_SHADER});
			lineShader.prepare(gl);


			/*vao = GLHelper.genVertexArray(gl);
			gl.glBindVertexArray(vao);

			vbo = GLHelper.genBuffer(gl);
			ebo = GLHelper.genBuffer(gl);

			ByteBuffer vertices = ByteBuffer.allocate(25 * 3);
			vertices.order(ByteOrder.nativeOrder());

			vertices.putFloat(0f);
			vertices.putFloat(0f);
			vertices.putFloat(0f);
			vertices.put((byte) 32);
			vertices.putFloat(1f);
			vertices.putFloat(1f);
			vertices.putFloat(1f);

			vertices.putFloat(16f);
			vertices.putFloat(0f);
			vertices.putFloat(0f);
			vertices.put((byte) 32);
			vertices.putFloat(1f);
			vertices.putFloat(1f);
			vertices.putFloat(1f);

			vertices.putFloat(16f);
			vertices.putFloat(0f);
			vertices.putFloat(16f);
			vertices.put((byte) 32);
			vertices.putFloat(1f);
			vertices.putFloat(1f);
			vertices.putFloat(1f);

			ByteBuffer indices = ByteBuffer.allocate(GLHelper.INTEGER_SIZE * 3);
			indices.order(ByteOrder.nativeOrder());

			indices.putInt(0);
			indices.putInt(1);
			indices.putInt(2);

			vertices.clear();
			indices.clear();

			gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo);
			gl.glBufferData(GL4.GL_ARRAY_BUFFER, vertices.capacity(), vertices, GL4.GL_STATIC_DRAW);

			gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, ebo);
			gl.glBufferData(GL4.GL_ELEMENT_ARRAY_BUFFER, indices.capacity(), indices, GL4.GL_STATIC_DRAW);

			gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, GLHelper.FLOAT_SIZE * 6 + 1, 0);
			gl.glEnableVertexAttribArray(0);
			gl.glVertexAttribIPointer(1, 1, GL4.GL_BYTE, GLHelper.FLOAT_SIZE * 6 + 1, GLHelper.FLOAT_SIZE * 3);
			gl.glEnableVertexAttribArray(1);
			gl.glVertexAttribPointer(2, 3, GL4.GL_FLOAT, false, GLHelper.FLOAT_SIZE * 6 + 1, GLHelper.FLOAT_SIZE * 3 + 1);
			gl.glEnableVertexAttribArray(2);

			gl.glBindVertexArray(0);

			indices.clear();
			String a = "";
			for (int i = 0; i < indices.capacity(); i++) a += (indices.get() + "     ").substring(0, 5) + ((i + 1) % ((4 - 2) * 3 * GLHelper.INTEGER_SIZE) == 0 ? "\n" : ",");
			System.out.println("indices=" + a);

			vertices.clear();
			a = "";
			for (int i = 0; i < vertices.capacity(); i++) a += (vertices.get() + "     ").substring(0, 5) + ((i + 1) % 25 == 0 ? "\n" : ",");
			System.out.println("vertices=" + a);*/

			boundaryRenderer.prepare(gl);
			xRotationHandleRenderer.prepare(gl);
			yRotationHandleRenderer.prepare(gl);
			zRotationHandleRenderer.prepare(gl);
			compassOverlayRenderer.prepare(gl);

			if (controller.getModel() != null) {
				for (Element e : controller.getModel().getElements()) {
					for (Fragment f : e.getFragments()) f.getRenderer().prepare(gl);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL4 gl = drawable.getGL().getGL4();

		viewportBuffer[0] = 0;
		viewportBuffer[1] = 0;
		viewportBuffer[2] = width;
		viewportBuffer[3] = height;

		gl.glViewport(0, 0, width, height);
	}

	public CameraMode getCameraMode() {
		return cameraMode;
	}

	public void setCameraMode(CameraMode cameraMode) {
		if (cameraMode == null) throw new IllegalArgumentException("cameraMode is null");
		this.cameraMode = cameraMode;
		repaint(); // TODO: fire event?
	}

	public RenderMode getRenderMode() {
		return renderMode;
	}

	public void setRenderMode(RenderMode renderMode) {
		if (renderMode == null) throw new IllegalArgumentException("renderMode is null");
		this.renderMode = renderMode;
		repaint(); // TODO: fire event?
	}

	/**
	 * Returns a Position object storing camera's position.
	 *
	 * @return Position position of the camera
	 */
	public Position getCameraPosition() {
		return cameraPosition;
	}

	public Position getCameraTarget() {
		return cameraTarget;
	}

	public Position getCameraDirection() {
		return cameraMode.hasDirection() ? cameraMode.getDirection() : cameraTarget.clone().subtract(cameraPosition).normalize();
	}

	public float[] getModelviewProjectionMatrix() {
		return viewProjectionMatrix;
	}

	public int[] getViewportMatrix() {
		return viewportBuffer;
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
		MODEL("Model"),
		TEXTURED("Textured"),
		WIREFRAME("Wireframe");

		private String displayName;

		private RenderMode(String displayName) {
			this.displayName = displayName;
		}

		public String getDisplayName() {
			return displayName;
		}
	}
}

package me.opl.apps.modelcreator2.viewport;

import java.awt.Color;
import java.awt.Graphics;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;

import me.opl.apps.modelcreator2.ModelWindow;
import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.util.GLHelper;
import me.opl.apps.modelcreator2.util.IntersectionHelper;
import me.opl.libs.tablib.VariableProvider;

public class ViewportComponentGL2 extends GLJPanel implements GLEventListener {
	private static final long serialVersionUID = -8904162480695586692L;

	private GLU glu;

	private ViewportController controller;

	private CameraMode cameraMode;
	private RenderMode renderMode;

	private Position cameraTarget;
	private Position cameraPosition;

	private float fov = 45f;

	private float[] matrixBuffer;
	private int[] viewportBuffer;

	public ViewportComponentGL2(VariableProvider vp) {
		this(vp, CameraMode.PERSPECTIVE_FREE, RenderMode.TEXTURED);
	}

	public ViewportComponentGL2(VariableProvider vp, CameraMode cameraMode, RenderMode renderMode) {
		addGLEventListener(this);

		this.cameraMode = cameraMode;
		this.renderMode = renderMode;

		cameraTarget = new Position(8f, 8f, 8f);
		cameraPosition = new Position(100f, 80f, -42f).subtract(cameraTarget).normalize().multiply(100f).add(cameraTarget);

		matrixBuffer = new float[32];
		viewportBuffer = new int[4];

		glu = new GLU();

		//controller = new ModelViewController((ModelWindow) vp.getObject(ModelWindow.VP_MODEL_WINDOW), this, glu);
		addMouseListener(controller);
		addMouseMotionListener(controller);
		addMouseWheelListener(controller);

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					repaint();

					try {Thread.sleep(40l);}
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
		GL2 gl = drawable.getGL().getGL2();

		gl.glClear(GL2.GL_DEPTH_BUFFER_BIT | GL2.GL_COLOR_BUFFER_BIT);

		// Position camera
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		if (cameraMode.getView() == CameraMode.View.PERSPECTIVE) {
			glu.gluPerspective(fov, (float) getWidth() / (float) getHeight(), 1, 1000);
		} else if (cameraMode.getView() == CameraMode.View.ORTHO) {
			float ar = (float) getWidth() / (float) getHeight();
			float w = fov * ar;
			float h = w / ar;
		    gl.glOrtho(-w, w, -h, h, cameraMode.hasDirection() ? -1000f : 1f, 1000f);
		}

		if (cameraMode.getDirection() != null) {
			Position dir = cameraMode.getDirection();
			glu.gluLookAt(cameraTarget.getX(), cameraTarget.getY(), cameraTarget.getZ(), cameraTarget.getX() + dir.getX(), cameraTarget.getY() + dir.getY(), cameraTarget.getZ() + dir.getZ(), 0, 1, 0);
		} else {
			glu.gluLookAt(cameraPosition.getX(), cameraPosition.getY(), cameraPosition.getZ(), cameraTarget.getX(), cameraTarget.getY(), cameraTarget.getZ(), 0, 1, 0);
		}

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		// Getting matrix values for click detection
		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, matrixBuffer, 0);
		gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX, matrixBuffer, 16);
		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewportBuffer, 0);
		System.out.println(GLHelper.arrayToString(viewportBuffer));

		// 16x16x16 model outline
		gl.glLineWidth(1f);
		gl.glColor4f(1f, 1f, 1f, 0.2f);
		gl.glEnable(GL2.GL_BLEND);

		gl.glBegin(GL2.GL_LINES); gl.glVertex3d( 0d,  0d,  0d); gl.glVertex3d(16d,  0d,  0d); gl.glEnd();
		gl.glBegin(GL2.GL_LINES); gl.glVertex3d( 0d,  0d,  0d); gl.glVertex3d( 0d, 16d,  0d); gl.glEnd();
		gl.glBegin(GL2.GL_LINES); gl.glVertex3d( 0d,  0d,  0d); gl.glVertex3d( 0d,  0d, 16d); gl.glEnd();
		gl.glBegin(GL2.GL_LINES); gl.glVertex3d(16d, 16d, 16d); gl.glVertex3d( 0d, 16d, 16d); gl.glEnd();
		gl.glBegin(GL2.GL_LINES); gl.glVertex3d(16d, 16d, 16d); gl.glVertex3d(16d,  0d, 16d); gl.glEnd();
		gl.glBegin(GL2.GL_LINES); gl.glVertex3d(16d, 16d, 16d); gl.glVertex3d(16d, 16d,  0d); gl.glEnd();
		gl.glBegin(GL2.GL_LINES); gl.glVertex3d(16d,  0d,  0d); gl.glVertex3d(16d, 16d,  0d); gl.glEnd();
		gl.glBegin(GL2.GL_LINES); gl.glVertex3d(16d,  0d,  0d); gl.glVertex3d(16d,  0d, 16d); gl.glEnd();
		gl.glBegin(GL2.GL_LINES); gl.glVertex3d( 0d, 16d,  0d); gl.glVertex3d( 0d, 16d, 16d); gl.glEnd();
		gl.glBegin(GL2.GL_LINES); gl.glVertex3d( 0d, 16d,  0d); gl.glVertex3d(16d, 16d,  0d); gl.glEnd();
		gl.glBegin(GL2.GL_LINES); gl.glVertex3d( 0d,  0d, 16d); gl.glVertex3d(16d,  0d, 16d); gl.glEnd();
		gl.glBegin(GL2.GL_LINES); gl.glVertex3d( 0d,  0d, 16d); gl.glVertex3d( 0d, 16d, 16d); gl.glEnd();

		// North marker
		gl.glColor4d(1d, 1d, 1d, 0.8d);

		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3d(6.5d, 0d, -2d);
		gl.glVertex3d(6.5d, 0d, -8d);
		gl.glEnd();

		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3d(9.5d, 0d, -2d);
		gl.glVertex3d(9.5d, 0d, -8d);
		gl.glEnd();

		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3d(9.5d, 0d, -2d);
		gl.glVertex3d(6.5d, 0d, -8d);
		gl.glEnd();

		gl.glDisable(GL2.GL_BLEND);

		/*if (clickLocation != null) {
			IntBuffer viewport = IntBuffer.allocate(4);
			gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport);

			double[] buffer = new double[33];
			gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, buffer, 0);
			gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, buffer, 16);
			DoubleBuffer winZBuffer = DoubleBuffer.allocate(1);
			gl.glReadPixels((int) clickLocation.getX(), (int) clickLocation.getY(), 1, 1, GL2.GL_DEPTH_COMPONENT, GL2.GL_DOUBLE, winZBuffer);
			buffer[32] = winZBuffer.get(0);

			double[] pos = new double[3];
			glu.gluUnProject(clickLocation.getX(), getHeight() - clickLocation.getY(), buffer[32], buffer, 0, buffer, 16, viewport.array(), 0, pos, 0);
			Position rayPoint = new Position(pos[0], pos[1], pos[2]);

			gl.glPointSize(2f);
			gl.glBegin(GL2.GL_POINTS);
			gl.glVertex3dv(rayPoint.toArray(), 0);
			gl.glEnd();

			test(gl, cameraPosition, rayPoint);
		}*/

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

	private void test(GL2 gl, Position rayStart, Position rayPoint) {
		boolean hits = IntersectionHelper.rayQuadIntersection(rayStart, rayPoint, new Position(0,16,16), new Position(0,16,0), new Position(16,16,16));
		gl.glEnable(GL2.GL_BLEND);
		gl.glBegin(GL2.GL_LINES);
		gl.glColor4d(hits?0:1, hits?1:0, 0, .3d);
		gl.glVertex3fv(rayStart.toArray(), 0);
		gl.glColor4d(1, 1, 0, 0);
		gl.glVertex3fv(rayPoint.toArray(), 0);
		gl.glEnd();
		gl.glColor4d(1d, hits? 0d : 1d, 1d, 1d);
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3fv(new Position(0,16,0).toArray(), 0);
		gl.glVertex3fv(new Position(16,16,0).toArray(), 0);
		gl.glVertex3fv(new Position(16,16,16).toArray(), 0);
		gl.glVertex3fv(new Position(0,16,0).clone().add(new Position(16,16,16)).subtract(new Position(16,16,0)).toArray(), 0);
		gl.glEnd();
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
		gl.glDisable(GL2.GL_BLEND);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL);

		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		gl.glClearColor(0.5f, 0.5f, 1f, 1f);

		gl.glShadeModel(GL2.GL_SMOOTH);

		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();

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

	public Position getCameraPosition() {
		return cameraPosition;
	}

	public Position getCameraTarget() {
		return cameraTarget;
	}

	public Position getCameraDirection() {
		return cameraMode.hasDirection() ? cameraMode.getDirection() : cameraTarget.clone().subtract(cameraPosition).normalize();
	}

	public float[] getMatrixData() {
		return matrixBuffer;
	}

	public int[] getViewportData() {
		return viewportBuffer;
	}

	public static enum CameraMode {
		PERSPECTIVE_FREE("Perspective Free", View.PERSPECTIVE, null),
		ORTHO_FREE("Ortho Free", View.ORTHO, null),
		ORTHO_NORTH("Ortho North", View.ORTHO, new Position(0f, 0f, 1f)),
		ORTHO_SOUTH("Ortho South", View.ORTHO, new Position(0f, 0f, -1f)),
		ORTHO_EAST("Ortho East", View.ORTHO, new Position(-1f, 0f, 0f)),
		ORTHO_WEST("Ortho West", View.ORTHO, new Position(1f, 0f, 0f)),
		ORTHO_UP("Ortho Up", View.ORTHO, new Position(0f, -1f, -0.0005f).normalize()),
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

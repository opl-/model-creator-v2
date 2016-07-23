package me.opl.apps.modelcreator2.panel;

import javax.media.opengl.GL2;
import javax.swing.JPanel;

import me.opl.apps.modelcreator2.ModelWindow;
import me.opl.apps.modelcreator2.panel.modelview.ModelViewComponent;
import me.opl.libs.tablib.AbstractPanel;
import me.opl.libs.tablib.VariableProvider;

public class ModelViewPanel extends AbstractPanel {
	private ModelViewComponent mvc;

	public ModelViewPanel(VariableProvider vp) {
		super(vp);

		mvc = new ModelViewComponent(vp);
	}

	@Override
	public JPanel getPanel() {
		return mvc;
	}

	@Override
	public String getTitle() {
		return "Model View";
	}

	private void drawCube(GL2 gl, double x1, double y1, double z1, double x2, double y2, double z2) {
		// XXX: unused
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3d(x1, y1, z1);
		gl.glVertex3d(x1, y1, z2);
		gl.glVertex3d(x2, y1, z2);
		gl.glVertex3d(x2, y1, z1);
		gl.glEnd();
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3d(x1, y1, z1);
		gl.glVertex3d(x1, y1, z2);
		gl.glVertex3d(x1, y2, z2);
		gl.glVertex3d(x1, y2, z1);
		gl.glEnd();
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3d(x2, y1, z1);
		gl.glVertex3d(x2, y1, z2);
		gl.glVertex3d(x2, y2, z2);
		gl.glVertex3d(x2, y2, z1);
		gl.glEnd();
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3d(x1, y2, z1);
		gl.glVertex3d(x1, y2, z2);
		gl.glVertex3d(x2, y2, z2);
		gl.glVertex3d(x2, y2, z1);
		gl.glEnd();
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3d(x1, y2, z1);
		gl.glVertex3d(x2, y2, z1);
		gl.glVertex3d(x2, y1, z1);
		gl.glVertex3d(x1, y1, z1);
		gl.glEnd();
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3d(x1, y2, z2);
		gl.glVertex3d(x2, y2, z2);
		gl.glVertex3d(x2, y1, z2);
		gl.glVertex3d(x1, y1, z2);
		gl.glEnd();
	}
}

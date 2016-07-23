package me.opl.apps.modelcreator2.util;

import javax.media.opengl.glu.GLU;

import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.panel.modelview.ModelViewComponent;
import me.opl.apps.modelcreator2.panel.modelview.ModelViewComponent.CameraMode.View;

public class SelectionHelper {
	public static Position[] rayFromClick(GLU glu, ModelViewComponent view, int x, int y) {
		double[] pos = new double[3];

		glu.gluUnProject(x, view.getHeight() - y, 0, view.getMatrixData(), 0, view.getMatrixData(), 16, view.getViewportData(), 0, pos, 0);
		Position rayPoint = new Position(pos[0], pos[1], pos[2]);

		Position rayStart = null;
		if (view.getCameraMode().getView() == View.PERSPECTIVE) {
			rayStart = view.getCameraPosition();
		} else if (view.getCameraMode().getView() == View.ORTHO) {
			glu.gluUnProject(x, view.getHeight() - y, 1, view.getMatrixData(), 0, view.getMatrixData(), 16, view.getViewportData(), 0, pos, 0);
			rayStart = new Position(pos[0], pos[1], pos[2]);
		}

		return new Position[] {rayStart, rayPoint};
	}
}

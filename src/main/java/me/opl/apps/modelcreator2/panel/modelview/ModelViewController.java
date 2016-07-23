package me.opl.apps.modelcreator2.panel.modelview;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.font.FontRenderContext;

import javax.media.opengl.glu.GLU;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import me.opl.apps.modelcreator2.ModelWindow;
import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.panel.modelview.ModelViewComponent.CameraMode;
import me.opl.apps.modelcreator2.tool.ToolManager;
import me.opl.apps.modelcreator2.util.RotationHelper;

public class ModelViewController implements MouseListener, MouseMotionListener, MouseWheelListener {
	private static final Font FONT = new JLabel().getFont();
	private static final FontRenderContext FONT_RENDER_CONTEXT = new FontRenderContext(FONT.getTransform(), true, true);

	private ModelViewComponent view;

	private GLU glu;

	private Point lastMousePosition;

	private ToolManager toolManager;

	public ModelViewController(ModelWindow mw, ModelViewComponent component, GLU glu) {
		this.view = component;
		this.glu = glu;

		toolManager = new ToolManager(mw);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		lastMousePosition = new Point(e.getX(), e.getY());

		if (e.getButton() == MouseEvent.BUTTON1) {
			if (e.getX() > 0 && e.getY() > 0 && e.getY() < 17 && e.getX() < FONT.getStringBounds(view.getCameraMode().getDisplayName() + " \u25be", FONT_RENDER_CONTEXT).getWidth() + 4) {
				JPopupMenu popup = new JPopupMenu();
				for (final CameraMode cm : CameraMode.values()) {
					JMenuItem item = new JMenuItem(cm.getDisplayName());
					item.addActionListener(new ActionListener() {
						@Override public void actionPerformed(ActionEvent e) { view.setCameraMode(cm); }
					});
					popup.add(item);
				}
				popup.show(view, 0, 17);
			} else {
				toolManager.onMousePressed(e);
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if ((e.getModifiersEx() & (MouseEvent.BUTTON3_DOWN_MASK | MouseEvent.SHIFT_DOWN_MASK)) == (MouseEvent.BUTTON3_DOWN_MASK | MouseEvent.SHIFT_DOWN_MASK) || (e.getModifiersEx() & (MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.ALT_DOWN_MASK | MouseEvent.SHIFT_DOWN_MASK)) == (MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.ALT_DOWN_MASK | MouseEvent.SHIFT_DOWN_MASK)) {
			moveCamera(e.getX() - lastMousePosition.getX(), e.getY() - lastMousePosition.getY());

			lastMousePosition = new Point(e.getX(), e.getY());
		} else if (((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0) || (e.getModifiersEx() & (MouseEvent.ALT_DOWN_MASK | MouseEvent.BUTTON1_DOWN_MASK)) == (MouseEvent.ALT_DOWN_MASK | MouseEvent.BUTTON1_DOWN_MASK)) {
			double xChange = (double) (e.getX() - lastMousePosition.getX());
			double yChange = (e.getY() - lastMousePosition.getY());

			if (!view.getCameraMode().hasDirection()) {
				if (xChange != 0) view.getCameraPosition().set(RotationHelper.rotateY(view.getCameraPosition(), xChange * Math.PI / 540d, view.getCameraTarget())); // / 180d / 3d // XXX: rotation sensitivity
				if (yChange != 0) {
					Position cameraPosRelativeToTarget = view.getCameraPosition().clone().subtract(view.getCameraTarget());
					double angleToKnownAxis = Math.atan2(-cameraPosRelativeToTarget.getZ(), cameraPosRelativeToTarget.getX());
					cameraPosRelativeToTarget.set(RotationHelper.rotateY(cameraPosRelativeToTarget, angleToKnownAxis));
	
					double rotationFromY = yChange * Math.PI / 540d; // / 180d / 3d // XXX: rotation sensitivity
					double currentZRotation = Math.atan2(cameraPosRelativeToTarget.getY(), cameraPosRelativeToTarget.getX());
					if (currentZRotation - rotationFromY < Math.PI / -2d + 0.01d) rotationFromY = Math.PI / 2d - 0.01d + currentZRotation;
					else if (currentZRotation - rotationFromY > Math.PI / 2d - 0.01d) rotationFromY = Math.PI / -2d + 0.01d + currentZRotation;
	
					cameraPosRelativeToTarget.set(RotationHelper.rotateZ(cameraPosRelativeToTarget, rotationFromY));
					cameraPosRelativeToTarget.set(RotationHelper.rotateY(cameraPosRelativeToTarget, -angleToKnownAxis));
					view.getCameraPosition().set(cameraPosRelativeToTarget.add(view.getCameraTarget()));
				}
			} else {
				moveCamera(xChange, yChange);
			}

			lastMousePosition = new Point(e.getX(), e.getY());
		} else {
			toolManager.onMouseDragged(e);
		}
	}

	private void moveCamera(double xChange, double yChange) {
		Position cameraDirection = view.getCameraDirection();
		double angleToKnownAxis = Math.atan2(cameraDirection.getX(), cameraDirection.getZ());
		cameraDirection.set(RotationHelper.rotateY(cameraDirection, angleToKnownAxis));
		cameraDirection.set(RotationHelper.rotateX(cameraDirection, Math.PI / 2d));

		Position up = RotationHelper.rotateY(cameraDirection, -angleToKnownAxis);
		Position right = up.cross(view.getCameraDirection());

		up.multiply(yChange / 3d);
		right.multiply(xChange / 3d);

		up.add(right);

		view.getCameraPosition().add(up);
		view.getCameraTarget().add(up);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		Position cameraDirection = view.getCameraDirection();
		cameraDirection.multiply(-3d * e.getWheelRotation());

		if (e.isShiftDown()) {
			view.getCameraPosition().add(cameraDirection);
			view.getCameraTarget().add(cameraDirection);
		} else {
			if (view.getCameraPosition().distance(view.getCameraTarget()) + 3d * e.getWheelRotation() > 0d) {
				view.getCameraPosition().add(cameraDirection);
			}
		}
	}

	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseMoved(MouseEvent e) {}
}

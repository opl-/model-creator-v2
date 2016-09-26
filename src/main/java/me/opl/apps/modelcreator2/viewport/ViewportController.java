package me.opl.apps.modelcreator2.viewport;

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
import me.opl.apps.modelcreator2.model.BaseModel;
import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.tool.ToolManager;
import me.opl.apps.modelcreator2.util.RotationHelper;
import me.opl.apps.modelcreator2.viewport.ViewportComponentGL4.CameraMode;

public class ViewportController implements MouseListener, MouseMotionListener, MouseWheelListener {
	private static final Font FONT = new JLabel().getFont();
	private static final FontRenderContext FONT_RENDER_CONTEXT = new FontRenderContext(FONT.getTransform(), true, true);

	private BaseModel model;

	private ViewportComponentGL4 view;

	private Point lastMousePosition;

	private ToolManager toolManager; // XXX: make global or per window (probably global)

	public ViewportController(ModelWindow mw, ViewportComponentGL4 component, GLU glu) {
		this.view = component;

		toolManager = new ToolManager(mw);
	}

	public BaseModel getModel() {
		return model;
	}

	public void setModel(BaseModel model) {
		this.model = model;
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
			moveCamera((float) (e.getX() - lastMousePosition.getX()), (float) (e.getY() - lastMousePosition.getY()));

			lastMousePosition = new Point(e.getX(), e.getY());
		} else if (((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0) || (e.getModifiersEx() & (MouseEvent.ALT_DOWN_MASK | MouseEvent.BUTTON1_DOWN_MASK)) == (MouseEvent.ALT_DOWN_MASK | MouseEvent.BUTTON1_DOWN_MASK)) {
			float xChange = (float) (e.getX() - lastMousePosition.getX());
			float yChange = (float) (e.getY() - lastMousePosition.getY());

			if (!view.getCameraMode().hasDirection()) {
				if (xChange != 0) view.getCameraPosition().set(RotationHelper.rotateY(view.getCameraPosition(), (float) (xChange * Math.PI / 540d), view.getCameraTarget())); // / 180d / 3d // XXX: rotation sensitivity
				if (yChange != 0) {
					Position cameraPosRelativeToTarget = view.getCameraPosition().clone().subtract(view.getCameraTarget());
					float angleToKnownAxis = (float) (Math.atan2(-cameraPosRelativeToTarget.getZ(), cameraPosRelativeToTarget.getX()));
					cameraPosRelativeToTarget.set(RotationHelper.rotateY(cameraPosRelativeToTarget, angleToKnownAxis));
	
					float rotationFromY = (float) (yChange * Math.PI / 540d); // / 180d / 3d // XXX: rotation sensitivity
					float currentZRotation = (float) (Math.atan2(cameraPosRelativeToTarget.getY(), cameraPosRelativeToTarget.getX()));

					if (currentZRotation - rotationFromY < Math.PI / -2f + 0.01f) rotationFromY = (float) (Math.PI / 2d - 0.01d + currentZRotation);
					else if (currentZRotation - rotationFromY > Math.PI / 2f - 0.01f) rotationFromY = (float) (Math.PI / -2d + 0.01d + currentZRotation);
	
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

	private void moveCamera(float xChange, float yChange) {
		Position cameraDirection = view.getCameraDirection();
		float angleToKnownAxis = (float) (Math.atan2(cameraDirection.getX(), cameraDirection.getZ()));
		cameraDirection.set(RotationHelper.rotateY(cameraDirection, angleToKnownAxis));
		cameraDirection.set(RotationHelper.rotateX(cameraDirection, (float) (Math.PI / 2d)));

		Position up = RotationHelper.rotateY(cameraDirection, -angleToKnownAxis);
		Position right = up.cross(view.getCameraDirection());

		up.multiply(yChange / 3f);
		right.multiply(xChange / 3f);

		up.add(right);

		view.getCameraPosition().add(up);
		view.getCameraTarget().add(up);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		/*Position cameraDirection = view.getCameraDirection();
		cameraDirection.multiply(-3f * e.getWheelRotation());

		if (e.isShiftDown()) {
			view.getCameraPosition().add(cameraDirection);
			view.getCameraTarget().add(cameraDirection);
		} else {
			System.out.println(view.getCameraPosition().distance(view.getCameraTarget()) + ",pos=" + view.getCameraPosition() + ",tar=" + view.getCameraTarget());
			if (view.getCameraPosition().distance(view.getCameraTarget()) + 3f * e.getWheelRotation() > 0f) {
				view.getCameraPosition().add(cameraDirection);
			}
		}*/

		if (e.isShiftDown()) {
			Position cameraDirection = view.getCameraDirection().multiply(e.getWheelRotation() > 0 ? -1 : 1);

			view.getCameraPosition().add(cameraDirection);
			view.getCameraTarget().add(cameraDirection);
		} else {
			if (view.getCameraPosition().distance(view.getCameraTarget()) + 3f * e.getWheelRotation() > 0) {
				view.getCameraPosition().add(view.getCameraPosition().clone().subtract(view.getCameraTarget()).normalize().multiply(3f * e.getWheelRotation()));
			}
		}
	}

	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseMoved(MouseEvent e) {}
}

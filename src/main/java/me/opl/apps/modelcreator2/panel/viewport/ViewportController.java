package me.opl.apps.modelcreator2.panel.viewport;

import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.font.FontRenderContext;

import javax.swing.JLabel;

import me.opl.apps.modelcreator2.ModelCreator;
import me.opl.apps.modelcreator2.model.BaseModel;
import me.opl.apps.modelcreator2.tool.PointerToolEvent;
import me.opl.apps.modelcreator2.tool.PointerToolEvent.PointerEventType;
import me.opl.apps.modelcreator2.tool.Tool;
import me.opl.apps.modelcreator2.tool.ToolManager;
import me.opl.apps.modelcreator2.tool.tool.ElementCreateTool;
import me.opl.apps.modelcreator2.tool.tool.MoveTool;
import me.opl.apps.modelcreator2.tool.tool.RotateTool;
import me.opl.apps.modelcreator2.viewport.ViewportFramebufferRenderer;

public class ViewportController implements MouseListener, MouseMotionListener, MouseWheelListener {
	private static final Font FONT = new JLabel().getFont();
	private static final FontRenderContext FONT_RENDER_CONTEXT = new FontRenderContext(FONT.getTransform(), true, true);

	private ModelCreator modelCreator;

	private ViewportJPanel view;
	private ViewportFramebufferRenderer vfr;

	private boolean[] dragState = new boolean[16];
	private int lastMouseX;
	private int lastMouseY;
	private int lastPressCount;

	public ViewportController(ModelCreator modelCreator, ViewportJPanel component, ViewportFramebufferRenderer vfr) {
		this.modelCreator = modelCreator;

		this.view = component;
		this.vfr = vfr;

		view.addMouseListener(this);
		view.addMouseMotionListener(this);
		view.addMouseWheelListener(this);
	}

	public BaseModel getModel() {
		return view.getViewportFramebufferRenderer().getDisplayedModel();
	}

	public void setModel(BaseModel model) {
		view.getViewportFramebufferRenderer().setDisplayedModel(model);
	}

	private int getButtonStatesFromEvent(MouseEvent e) {
		return (e.getModifiersEx() >> 10 & 0x07) | (e.getModifiersEx() >> 11 & ~0x07);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		for (int i = 1; i < Math.min(16, MouseInfo.getNumberOfButtons()); i++) if (!dragState[i - 1] && (e.getModifiersEx() & MouseEvent.getMaskForButton(i)) != 0) {
			dragState[i - 1] = true;
			modelCreator.getToolManager(vfr.getEditedModel()).firePointerEvent(new PointerToolEvent(view.getViewportFramebufferRenderer(), PointerEventType.DRAG_START, i, getButtonStatesFromEvent(e), lastPressCount, e.getX(), e.getY(), lastMouseX, lastMouseY, e.isControlDown(), e.isShiftDown(), e.isAltDown()));
		}

		modelCreator.getToolManager(vfr.getEditedModel()).firePointerEvent(new PointerToolEvent(view.getViewportFramebufferRenderer(), PointerEventType.DRAG, e.getButton(), getButtonStatesFromEvent(e), lastPressCount, e.getX(), e.getY(), lastMouseX, lastMouseY, e.isControlDown(), e.isShiftDown(), e.isAltDown()));

		lastMouseX = e.getX();
		lastMouseY = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		modelCreator.getToolManager(vfr.getEditedModel()).firePointerEvent(new PointerToolEvent(view.getViewportFramebufferRenderer(), PointerEventType.HOVER, e.getButton(), getButtonStatesFromEvent(e), e.getClickCount(), e.getX(), e.getY(), lastMouseX, lastMouseY, e.isControlDown(), e.isShiftDown(), e.isAltDown()));

		lastMouseX = e.getX();
		lastMouseY = e.getY();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getX() < 20 && e.getY() < 20) {
			ToolManager tm = modelCreator.getToolManager(getModel().getModelWithElements());
			Class<? extends Tool> activeTool = tm.getActiveTool().getClass();

			if (activeTool == ElementCreateTool.class) tm.setActiveTool(RotateTool.class);
			else if (activeTool == RotateTool.class) tm.setActiveTool(MoveTool.class);
			else if (activeTool == MoveTool.class) tm.setActiveTool(ElementCreateTool.class);
		} else {
			System.out.println("click   " + Integer.toBinaryString(0x80000000 | e.getModifiersEx()) + " " + e.getClickCount());
			modelCreator.getToolManager(vfr.getEditedModel()).firePointerEvent(new PointerToolEvent(view.getViewportFramebufferRenderer(), PointerEventType.CLICK, e.getButton(), getButtonStatesFromEvent(e), e.getClickCount(), e.getX(), e.getY(), lastMouseX, lastMouseY, e.isControlDown(), e.isShiftDown(), e.isAltDown()));
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println("press   " + Integer.toBinaryString(0x80000000 | e.getModifiersEx()));
		modelCreator.getToolManager(vfr.getEditedModel()).firePointerEvent(new PointerToolEvent(view.getViewportFramebufferRenderer(), PointerEventType.DOWN, e.getButton(), getButtonStatesFromEvent(e), e.getClickCount(), e.getX(), e.getY(), lastMouseX, lastMouseY, e.isControlDown(), e.isShiftDown(), e.isAltDown()));

		lastPressCount = e.getClickCount();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		System.out.println("release " + Integer.toBinaryString(0x80000000 | e.getModifiersEx()) + " " + e.getButton());

		if (e.getButton() > 0 && e.getButton() <= 16 && dragState[e.getButton() - 1]) {
			modelCreator.getToolManager(vfr.getEditedModel()).firePointerEvent(new PointerToolEvent(view.getViewportFramebufferRenderer(), PointerEventType.DRAG_END, e.getButton(), getButtonStatesFromEvent(e), e.getClickCount(), e.getX(), e.getY(), lastMouseX, lastMouseY, e.isControlDown(), e.isShiftDown(), e.isAltDown()));
			dragState[e.getButton() - 1] = false;
		}

		modelCreator.getToolManager(vfr.getEditedModel()).firePointerEvent(new PointerToolEvent(view.getViewportFramebufferRenderer(), PointerEventType.UP, e.getButton(), getButtonStatesFromEvent(e), e.getClickCount(), e.getX(), e.getY(), lastMouseX, lastMouseY, e.isControlDown(), e.isShiftDown(), e.isAltDown()));
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		System.out.println("scroll  " + Integer.toBinaryString(0x80000000 | e.getModifiersEx()));
		modelCreator.getToolManager(vfr.getEditedModel()).firePointerEvent(new PointerToolEvent(view.getViewportFramebufferRenderer(), PointerEventType.SCROLL, MouseEvent.BUTTON2, getButtonStatesFromEvent(e), e.getClickCount(), e.getX(), e.getY(), lastMouseX, lastMouseY, (float) e.getPreciseWheelRotation(), e.isControlDown(), e.isShiftDown(), e.isAltDown()));
	}

	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}

	/*@Override
	public void mousePressed(MouseEvent e) {
		modelCreator.getToolManager().firePointerEvent(new PointerToolEvent(view.getViewportFramebufferRenderer(), PointerEventType.DOWN, e.getButton(), getButtonStatesFromEvent(e), e.getX(), e.getY(), lastMouseX, lastMouseY, e.isControlDown(), e.isShiftDown()));

		lastMousePosition = new Point(e.getX(), e.getY());

		if (e.getButton() == MouseEvent.BUTTON1) {
			if (e.getX() > 0 && e.getY() > 0 && e.getY() < 17 && e.getX() < FONT.getStringBounds(view.getViewportFramebufferRenderer().getCameraMode().getDisplayName() + " \u25be", FONT_RENDER_CONTEXT).getWidth() + 4) {
				JPopupMenu popup = new JPopupMenu();

				for (final CameraMode cm : CameraMode.values()) {
					JCheckBoxMenuItem item = new JCheckBoxMenuItem(cm.getDisplayName(), cm == view.getViewportFramebufferRenderer().getCameraMode());

					item.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							view.getViewportFramebufferRenderer().setCameraMode(cm);
						}
					});

					popup.add(item);
				}

				popup.addSeparator();

				for (final RenderMode rm : RenderMode.values()) {
					JCheckBoxMenuItem item = new JCheckBoxMenuItem(rm.getDisplayName(), rm == view.getViewportFramebufferRenderer().getRenderMode());

					item.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							view.getViewportFramebufferRenderer().setRenderMode(rm);
						}
					});

					popup.add(item);
				}

				popup.addSeparator();

				JMenu modelList = new JMenu("Model");
				for (final BaseModel m : modelCreator.getModels()) {
					JCheckBoxMenuItem item = new JCheckBoxMenuItem(m.getName(), m == getModel());

					item.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							setModel(m);
						}
					});

					modelList.add(item);
				}

				popup.add(modelList);

				popup.show(view, 0, 17);
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		modelCreator.getToolManager().firePointerEvent(new PointerToolEvent(view.getViewportFramebufferRenderer(), PointerEventType.DRAG, e.getButton(), getButtonStatesFromEvent(e), e.getX(), e.getY(), lastMouseX, lastMouseY, e.isControlDown(), e.isShiftDown()));

		// TODO: modify to use world coordinates

		if ((e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) == MouseEvent.BUTTON2_DOWN_MASK || (e.getModifiersEx() & (MouseEvent.BUTTON3_DOWN_MASK | MouseEvent.SHIFT_DOWN_MASK)) == (MouseEvent.BUTTON3_DOWN_MASK | MouseEvent.SHIFT_DOWN_MASK) || (e.getModifiersEx() & (MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.ALT_DOWN_MASK | MouseEvent.SHIFT_DOWN_MASK)) == (MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.ALT_DOWN_MASK | MouseEvent.SHIFT_DOWN_MASK)) {
			moveCamera((float) (e.getX() - lastMousePosition.getX()), (float) (e.getY() - lastMousePosition.getY()));

			lastMousePosition = new Point(e.getX(), e.getY());
		} else if (((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0) || (e.getModifiersEx() & (MouseEvent.ALT_DOWN_MASK | MouseEvent.BUTTON1_DOWN_MASK)) == (MouseEvent.ALT_DOWN_MASK | MouseEvent.BUTTON1_DOWN_MASK)) {
			float xChange = (float) (e.getX() - lastMousePosition.getX());
			float yChange = (float) (e.getY() - lastMousePosition.getY());

			if (!view.getViewportFramebufferRenderer().getCameraMode().hasDirection()) {
				if (xChange != 0) view.getViewportFramebufferRenderer().getCameraPosition().set(RotationHelper.rotateY(view.getViewportFramebufferRenderer().getCameraPosition(), (float) (xChange * Math.PI / 540d * RotationHelper.TO_DEGREES), view.getViewportFramebufferRenderer().getCameraTarget())); // / 180d / 3d // XXX: [settings] rotation sensitivity
				if (yChange != 0) {
					Position cameraPosRelativeToTarget = view.getViewportFramebufferRenderer().getCameraPosition().clone().subtract(view.getViewportFramebufferRenderer().getCameraTarget());
					float angleToKnownAxis = (float) (Math.atan2(-cameraPosRelativeToTarget.getZ(), cameraPosRelativeToTarget.getX()));
					cameraPosRelativeToTarget.set(RotationHelper.rotateY(cameraPosRelativeToTarget, angleToKnownAxis * RotationHelper.TO_DEGREES));
	
					float rotationFromY = (float) (-yChange * Math.PI / 540d); // / 180d / 3d // XXX: [settings] rotation sensitivity
					float currentZRotation = (float) (Math.atan2(cameraPosRelativeToTarget.getY(), cameraPosRelativeToTarget.getX()));

					if (currentZRotation - rotationFromY < Math.PI / -2f + 0.01f) rotationFromY = (float) (Math.PI / 2d - 0.01d + currentZRotation);
					else if (currentZRotation - rotationFromY > Math.PI / 2f - 0.01f) rotationFromY = (float) (Math.PI / -2d + 0.01d + currentZRotation);
	
					cameraPosRelativeToTarget.set(RotationHelper.rotateZ(cameraPosRelativeToTarget, rotationFromY * RotationHelper.TO_DEGREES));
					cameraPosRelativeToTarget.set(RotationHelper.rotateY(cameraPosRelativeToTarget, -angleToKnownAxis * RotationHelper.TO_DEGREES));
					view.getViewportFramebufferRenderer().getCameraPosition().set(cameraPosRelativeToTarget.add(view.getViewportFramebufferRenderer().getCameraTarget()));
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
		Position cameraDirection = view.getViewportFramebufferRenderer().getCameraDirection();

		float angleToKnownAxis = (float) (Math.atan2(cameraDirection.getX(), cameraDirection.getZ()) * RotationHelper.TO_DEGREES);

		cameraDirection.set(RotationHelper.rotateY(cameraDirection, angleToKnownAxis));
		cameraDirection.set(RotationHelper.rotateX(cameraDirection, 90f));

		Position up = RotationHelper.rotateY(cameraDirection, -angleToKnownAxis);
		Position right = up.cross(view.getViewportFramebufferRenderer().getCameraDirection());

		up.multiply(yChange / 3f);
		right.multiply(xChange / 3f);

		up.add(right);

		view.getViewportFramebufferRenderer().getCameraPosition().add(up);
		view.getViewportFramebufferRenderer().getCameraTarget().add(up);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.isShiftDown()) {
			Position cameraDirection = view.getViewportFramebufferRenderer().getCameraDirection().multiply(e.getWheelRotation() > 0 ? -1 : 1);

			view.getViewportFramebufferRenderer().getCameraPosition().add(cameraDirection);
			view.getViewportFramebufferRenderer().getCameraTarget().add(cameraDirection);
		} else {
			if (view.getViewportFramebufferRenderer().getCameraPosition().distance(view.getViewportFramebufferRenderer().getCameraTarget()) + 3f * e.getWheelRotation() > 0) {
				view.getViewportFramebufferRenderer().getCameraPosition().add(view.getViewportFramebufferRenderer().getCameraPosition().clone().subtract(view.getViewportFramebufferRenderer().getCameraTarget()).normalize().multiply(3f * e.getWheelRotation()));
			}
		}
	}

	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseMoved(MouseEvent e) {}*/
}

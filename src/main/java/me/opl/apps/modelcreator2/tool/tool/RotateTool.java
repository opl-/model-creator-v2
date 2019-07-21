package me.opl.apps.modelcreator2.tool.tool;

import me.opl.apps.modelcreator2.model.Axis;
import me.opl.apps.modelcreator2.model.BaseModel;
import me.opl.apps.modelcreator2.model.Element;
import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.model.RotatableElement;
import me.opl.apps.modelcreator2.model.Rotation;
import me.opl.apps.modelcreator2.tool.PointerToolEvent;
import me.opl.apps.modelcreator2.tool.PointerToolEvent.PointerEventType;
import me.opl.apps.modelcreator2.util.RayHelper;
import me.opl.apps.modelcreator2.util.RotationHelper;
import me.opl.apps.modelcreator2.viewport.RenderManager;
import me.opl.apps.modelcreator2.viewport.renderer.RotateToolRenderer;
import me.opl.apps.modelcreator2.viewport.renderer.ToolRenderer;

public class RotateTool extends SelectionTool {
	private static final float ROTATION_225 = 22.5f * RotationHelper.TO_RADIANS;
	private static final float ROTATION_90 = 90f * RotationHelper.TO_RADIANS;

	private BaseModel model;

	private Position actionCenter = new Position(8, 8, 8);
	private Axis rotationAxis;
	private Rotation rotationOrigin = new Rotation();
	private Rotation appliedChange = new Rotation();

	private boolean rotating;

	public RotateTool(BaseModel model) {
		this.model = model;
	}

	@Override
	public void onPointerEvent(PointerToolEvent event) {
		if (event.getType() == PointerEventType.HOVER) {
			Axis axis = null;
			Position intersection = null;
			float closestDist = Float.MAX_VALUE;

			for (Axis a : Axis.values()) {
				Position pos = RayHelper.rayPlaneIntersection(event.getRay(), actionCenter, a.getVector());
				if (pos == null) continue;

				float distFromCenter = pos.distance(actionCenter);
				if (distFromCenter > 4.6f && distFromCenter < 5.4f) {
					float distFromCamera = event.getRay().start().distance(pos);

					if (distFromCamera < closestDist) {
						closestDist = distFromCamera;
						intersection = pos;
						axis = a;
					}
				}
			}

			if (axis != rotationAxis) {
				rotationAxis = axis;
				triggerUpdate();

				if (rotationAxis != null) rotationOrigin = getRotation(rotationAxis, intersection);
			}
		} else if (event.getType() == PointerEventType.DRAG_START && event.testHeld(PointerToolEvent.BUTTON_LEFT, 0)) {
			if (rotationAxis != null) {
				rotating = true;
				appliedChange.setr(0, 0, 0);
				return;
			}
		} else if (event.getType() == PointerEventType.DRAG_END && !event.testHeld(PointerToolEvent.BUTTON_LEFT, 0)) {
			if (rotating) {
				rotating = false;
				rotationAxis = null;
				triggerUpdate();
				return;
			}
		} else if (rotating && event.getType() == PointerEventType.DRAG && event.testHeld(PointerToolEvent.BUTTON_LEFT, 0)) {
			Position intersection = RayHelper.rayPlaneIntersection(event.getRay(), actionCenter, rotationAxis.getVector());
			if (intersection == null) return;

			Rotation change = rotationOrigin.clone().subtract(getRotation(rotationAxis, intersection));
			RotationHelper.snapRotationToStep(change, ROTATION_225);
			change.subtract(appliedChange);
			if (change.equalsr(0, 0, 0)) return;

			System.out.println("change " + change.getXd() + " " + change.getYd() + " " + change.getZd());

			for (Element e : model.getSelectedElements()) {
				if (!(e instanceof RotatableElement)) continue;
				RotatableElement re = (RotatableElement) e;

				System.out.println(RotationHelper.rotate(change.clone(), re.getRotation()) + " appliedChange=" + appliedChange + " oldRotation=" + re.getRotation());
				re.setRotation(re.getRotation().add(change));
				System.out.println("newRotation=" + re.getRotation() + " change=" + change);
			}

			appliedChange.add(change);

			return;
		}

		super.onPointerEvent(event);
	}

	private Rotation getRotation(Axis axis, Position intersection) {
		Position relativePos = intersection.clone().subtract(actionCenter);

		if (axis == Axis.X) {
			return new Rotation((float) Math.atan2(relativePos.getZ(), relativePos.getY()), 0, 0);
		} else if (axis == Axis.Y) {
			return new Rotation(0, (float) Math.atan2(relativePos.getX(), relativePos.getZ()), 0);
		} else if (axis == Axis.Z) {
			return new Rotation(0, 0, (float) Math.atan2(relativePos.getY(), relativePos.getX()));
		}

		return null;
	}

	public Axis getRotationAxis() {
		return rotationAxis;
	}

	@Override
	public ToolRenderer createRenderer(RenderManager renderManager) {
		return new RotateToolRenderer(this);
	}
}

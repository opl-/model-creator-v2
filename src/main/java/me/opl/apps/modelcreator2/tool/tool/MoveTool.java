package me.opl.apps.modelcreator2.tool.tool;

import me.opl.apps.modelcreator2.event.ElementsResizedEvent;
import me.opl.apps.modelcreator2.event.EventHandler;
import me.opl.apps.modelcreator2.event.EventListener;
import me.opl.apps.modelcreator2.event.SelectionChangedEvent;
import me.opl.apps.modelcreator2.model.Axis;
import me.opl.apps.modelcreator2.model.BaseModel;
import me.opl.apps.modelcreator2.model.Element;
import me.opl.apps.modelcreator2.model.Face;
import me.opl.apps.modelcreator2.model.FaceData;
import me.opl.apps.modelcreator2.model.Grid;
import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.model.Ray;
import me.opl.apps.modelcreator2.model.RotatableElement;
import me.opl.apps.modelcreator2.model.Rotation;
import me.opl.apps.modelcreator2.tool.PointerToolEvent;
import me.opl.apps.modelcreator2.tool.PointerToolEvent.PointerEventType;
import me.opl.apps.modelcreator2.util.RayHelper;
import me.opl.apps.modelcreator2.util.RotationHelper;
import me.opl.apps.modelcreator2.viewport.RenderManager;
import me.opl.apps.modelcreator2.viewport.renderer.MoveToolRenderer;
import me.opl.apps.modelcreator2.viewport.renderer.ToolRenderer;

public class MoveTool extends SelectionTool implements EventListener {
	private BaseModel model;

	private Position actionCenter;
	private Position appliedChange = new Position();
	private Position totalAppliedChange = new Position();
	private Position dragVector;

	/**
	 * If set, indicates the axis a move will happen on. Also used to highlight
	 * hovered gizmo.
	 */
	private Axis moveAxis;
	private boolean shiftDown;

	private Face pressedFace;
	private FaceData pressedFaceData;
	private Rotation dragElementRotation;

	private Position tempMove;

	public MoveTool(BaseModel model) {
		this.model = model;

		model.getEventDispatcher().registerListeners(this);
	}

	@Override
	public void onPointerEvent(PointerToolEvent event) {
		// Hovering over the gizmos or moving the camera away from one
		if (event.getType() == PointerEventType.HOVER || (event.getType() == PointerEventType.DRAG && !event.isButtonDown(PointerToolEvent.BUTTON_LEFT))) {
			actionCenter = model.getSelectionAABBCenter();

			if (actionCenter != null) {
				Axis newAxis = null;

				// TODO: use closest line
				if (RayHelper.rayLineIntersection(event.getRay(), actionCenter, actionCenter.clone().add(2, 0, 0), 0.5f) != null) {
					newAxis = Axis.X;
				} else if (RayHelper.rayLineIntersection(event.getRay(), actionCenter, actionCenter.clone().add(0, 2, 0), 0.5f) != null) {
					newAxis = Axis.Y;
				} else if (RayHelper.rayLineIntersection(event.getRay(), actionCenter, actionCenter.clone().add(0, 0, 2), 0.5f) != null) {
					newAxis = Axis.Z;
				}

				if (newAxis != moveAxis) {
					moveAxis = newAxis;
					dragVector = moveAxis != null ? newAxis.getVector() : null;
					triggerUpdate();
				}
			}
		}

		if (moveAxis != null) { // Move
			// If the shift state changes, we need to set the lastIntersection to that of an intersection with the new plane
			boolean shiftStateChanged = shiftDown != event.isShiftDown() && event.getType() == PointerEventType.DRAG;

			if ((event.getType() == PointerEventType.DRAG_START && event.testDown(PointerToolEvent.BUTTON_LEFT, 0, PointerToolEvent.MODIFIER_SHIFT)) || shiftStateChanged) {
				Position planeNormal;
				if (!event.isShiftDown()) planeNormal = event.getFramebufferRenderer().getCameraDirection().cross(dragVector).cross(dragVector);
				else planeNormal = dragVector;

				shiftDown = event.isShiftDown();

				actionCenter = RayHelper.rayPlaneIntersection(shiftStateChanged ? event.getLastRay() : event.getRay(), model.getSelectionAABBCenter(), planeNormal);
				appliedChange.set(0, 0, 0);

				if (!shiftStateChanged) {
					totalAppliedChange.set(0, 0, 0);
					return;
				}
			} else if (event.getType() == PointerEventType.DRAG_END && !event.isLeftButtonDown()) {
				// Stop moving elements
				moveAxis = null;
				shiftDown = false;
				actionCenter.set(0, 0, 0);

				triggerUpdate();
				return;
			}

			// Not else if to allow for intersection changes caused by shift state changes to move elements on this event
			if (event.getType() == PointerEventType.DRAG && event.testHeld(PointerToolEvent.BUTTON_LEFT, 0, PointerToolEvent.MODIFIER_SHIFT)) {
				Position planeNormal;
				if (!event.isShiftDown()) planeNormal = event.getFramebufferRenderer().getCameraDirection().cross(dragVector).cross(dragVector);
				else planeNormal = dragVector;

				Position intersection = RayHelper.rayPlaneIntersection(event.getRay(), actionCenter, planeNormal);
				if (intersection == null) return;

				Position change;
				if (!event.isShiftDown()) change = RayHelper.closestLinePoint(actionCenter, actionCenter.clone().add(dragVector), intersection).subtract(actionCenter);
				else change = intersection.subtract(actionCenter);

				change = new Grid(1).snapToGrid(change).subtract(appliedChange);
				if (change.equals(0, 0, 0)) return;

				Element[] selectedElements = model.getSelectedElements();

				model.getEventDispatcher().cork();
				for (Element e : selectedElements) {
					e.setCorners(e.getFrom().add(change), e.getTo().add(change));

					if (e instanceof RotatableElement) {
						RotatableElement re = ((RotatableElement) e);
						re.setRotationOrigin(re.getRotationOrigin().add(change));
					}
				}
				model.getEventDispatcher().uncork();

				model.getEventDispatcher().fire(new ElementsResizedEvent(model, selectedElements));

				appliedChange.add(change);
				totalAppliedChange.add(change);

				triggerUpdate();
				return;
			}
		} else { // Resize
			if (event.getType() == PointerEventType.DOWN && event.testDown(PointerToolEvent.BUTTON_LEFT, 0)) {
				if (event.getRayIntersections().length > 0) {
					pressedFaceData = event.getRayIntersections()[0].getFaceData();
					actionCenter = event.getRayIntersections()[0].end();
					appliedChange.set(0, 0, 0);
					System.out.println("reset appliedChange 2");

					Element element = pressedFaceData.getFragment().getElement();
					dragElementRotation = element instanceof RotatableElement ? ((RotatableElement) element).getRotation().inverted() : new Rotation();

					return;
				}
			} else if (event.getType() == PointerEventType.DRAG_START && event.testHeld(PointerToolEvent.BUTTON_LEFT, 0)) {
				if (pressedFaceData != null) {
					if (!model.isFaceSelected(pressedFaceData)) {
						model.deselectAllFaces();
						model.selectFace(pressedFaceData);
					}

					pressedFace = pressedFaceData.getFragment().faceDataToFace(pressedFaceData);

					//dragFaceStartPosition = pressedFaceData.getFragment().getElement().getFaceCorners(face)[0];
					dragVector = pressedFaceData.getFragment().getElement().getFaceNormal(pressedFace);

					return;
				}
			//} else if (pressedFaceData == null) { // FIXME: dragVector is now reused, mark resizing in a different way
				// noop
			} else if (event.getType() == PointerEventType.DRAG_END && !event.isLeftButtonDown()) {
				pressedFaceData = null;
				dragVector = null;
				appliedChange.set(0, 0, 0);
				return;
			} else if (event.getType() == PointerEventType.DRAG && event.testHeld(PointerToolEvent.BUTTON_LEFT, 0)) {
				Position planeNormal = event.getFramebufferRenderer().getCameraDirection().cross(dragVector).cross(dragVector);
				Position intersection = RayHelper.rayPlaneIntersection(event.getRay(), actionCenter, planeNormal);

				if (intersection == null) return;

				Position change = RayHelper.closestLinePoint(actionCenter, actionCenter.clone().add(dragVector), intersection).subtract(actionCenter);
				RotationHelper.rotate(change, dragElementRotation);

				change = new Grid(1).snapToGrid(change).subtract(appliedChange);
				if (change.equals(0, 0, 0)) return;

				tempMove = change;

				boolean useTo = pressedFace == Face.SOUTH || pressedFace == Face.UP || pressedFace == Face.EAST;
				for (Element e : model.getSelectedElements()) {
					if (useTo) e.setCorners(e.getFrom(), e.getTo().add(change));
					else e.setCorners(e.getFrom().add(change), e.getTo());
				}

				appliedChange.add(change);

				// TODO: resize
				return;
			}
		}

		super.onPointerEvent(event);
	}

	public Position getActionCenter() {
		return actionCenter;
	}

	public Ray getDragVector() {
		if (actionCenter == null || dragVector == null) return null;
		return new Ray(actionCenter, actionCenter.clone().add(dragVector));
	}

	public Ray getMove() {
		if (actionCenter == null || tempMove == null) return null;
		return new Ray(actionCenter, actionCenter.clone().add(tempMove));
	}

	@Override
	public ToolRenderer createRenderer(RenderManager renderManager) {
		return new MoveToolRenderer(this);
	}

	public Axis getMoveAxis() {
		return moveAxis;
	}

	@EventHandler
	public void onElementsResized(ElementsResizedEvent event) {
		triggerUpdate();
	}

	@EventHandler
	public void onSelectionChanged(SelectionChangedEvent event) {
		triggerUpdate();
	}

	public Position getSelectionCenter() {
		return model.getSelectionAABBCenter();
	}
}

package me.opl.apps.modelcreator2.tool.tool;

import java.util.AbstractMap.SimpleEntry;

import me.opl.apps.modelcreator2.model.BaseModel;
import me.opl.apps.modelcreator2.model.Cuboid;
import me.opl.apps.modelcreator2.model.CuboidElement;
import me.opl.apps.modelcreator2.model.Face;
import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.model.RayFaceIntersection;
import me.opl.apps.modelcreator2.model.RotatableElement;
import me.opl.apps.modelcreator2.model.Rotation;
import me.opl.apps.modelcreator2.tool.PointerToolEvent;
import me.opl.apps.modelcreator2.tool.PointerToolEvent.PointerEventType;
import me.opl.apps.modelcreator2.util.MathHelper;
import me.opl.apps.modelcreator2.util.RayHelper;
import me.opl.apps.modelcreator2.util.RotationHelper;
import me.opl.apps.modelcreator2.viewport.RenderManager;
import me.opl.apps.modelcreator2.viewport.renderer.ElementCreateToolRenderer;
import me.opl.apps.modelcreator2.viewport.renderer.ToolRenderer;

public class ElementCreateTool extends CameraTool {
	private BaseModel model;

	private CreationStage stage = CreationStage.POINT;

	private Position cursor;
	private Position cursorPlaneU;
	private Position cursorPlaneV;
	private Position cursorPlaneOrigin;
	private Position cursorPlaneNormal;

	private Position point1PlaneOrigin;
	private Position point1PlaneNormal;

	private Position point1;
	private Position point2;
	private Rotation rotation;
	private Position rotationOrigin;

	public ElementCreateTool(BaseModel model) {
		this.model = model;
	}

	public Position getCursor() {
		return cursor == null ? null : cursor.clone();
	}

	public Position getPoint1() {
		return point1 == null ? null : point1.clone();
	}

	public Position getPoint2() {
		return point2 == null ? null : point2.clone();
	}

	@Override
	public void onPointerEvent(PointerToolEvent event) {
		if (event.getType() == PointerEventType.HOVER) {
			Position p = null;

			if (stage == CreationStage.POINT || (stage == CreationStage.PLANE && event.isShiftDown())) {
				if (event.getRayIntersections().length == 0) {
					@SuppressWarnings("unchecked")
					SimpleEntry<Position, Face>[] walls = new SimpleEntry[6];
					walls[0] = new SimpleEntry<Position, Face>(new Position(), Face.UP);
					walls[1] = new SimpleEntry<Position, Face>(new Position(0, 16, 0), Face.DOWN);
					walls[2] = new SimpleEntry<Position, Face>(new Position(), Face.WEST);
					walls[3] = new SimpleEntry<Position, Face>(new Position(16, 0, 0), Face.EAST);
					walls[4] = new SimpleEntry<Position, Face>(new Position(), Face.NORTH);
					walls[5] = new SimpleEntry<Position, Face>(new Position(0, 0, 16), Face.SOUTH);

					float closestDistSq = Float.MAX_VALUE;
					Position closest = null;
					SimpleEntry<Position, Face> closestWall = null;
					Position cameraPos = event.getFramebufferRenderer().getCameraPosition();

					for (SimpleEntry<Position, Face> wall : walls) {
						Position pos = RayHelper.rayPlaneIntersection(event.getRay(), wall.getKey(), wall.getValue().getNormal(), true);
						if (pos == null) continue;

						float dist = pos.distanceSq(cameraPos);

						if (dist < closestDistSq) {
							closestDistSq = dist;
							closest = pos;
							closestWall = wall;
						}
					}

					if (closest != null) {
						cursorPlaneOrigin = closestWall.getKey().clone();
						cursorPlaneNormal = closestWall.getValue().getNormal();
						cursorPlaneU = closestWall.getValue().getPlane1();
						cursorPlaneV = closestWall.getValue().getPlane2();

						rotation = new Rotation();
						rotationOrigin = new Position(8, 8, 8);

						float ul = 1f;
						float vl = 1f;

						p = cursorPlaneU.clone().multiply(Math.round(closest.dot(cursorPlaneU) * ul) / ul);
						p.add(cursorPlaneV.clone().multiply(Math.round(closest.dot(cursorPlaneV) * vl) / vl));
						p.add(cursorPlaneOrigin);
					} else {
						p = null;
					}
				} else {
					RayFaceIntersection ri = event.getRayIntersections()[0];
					p = ri.end().clone();

					if (stage == CreationStage.POINT && ri.getFaceData().getFragment().getElement() instanceof RotatableElement) {
						RotatableElement re = (RotatableElement) ri.getFaceData().getFragment().getElement();
						rotation = re.getRotation().clone();
						rotationOrigin = re.getRotationOrigin().clone();
					}

					if (!event.isControlDown()) {
						if (ri.getFaceData().getFragment() instanceof Cuboid) {
							Cuboid cuboid = (Cuboid) ri.getFaceData().getFragment();
							Position[] corners = cuboid.getFaceCorners(ri.getFaceData().getFragment().faceDataToFace(ri.getFaceData()));
							cursorPlaneOrigin = corners[0].clone();
							cursorPlaneU = corners[1].clone().subtract(corners[0]);
							cursorPlaneV = corners[2].clone().subtract(corners[0]);
							cursorPlaneNormal = cursorPlaneV.cross(cursorPlaneU).normalize();

							p.subtract(corners[0]);

							// TODO: multiply by grid size
							float ul = (float) Math.round(cursorPlaneU.length());
							float vl = (float) Math.round(cursorPlaneV.length());

							Position point = p.clone();
							p.set(cursorPlaneU.clone().multiply(Math.round(point.dot(cursorPlaneU) / cursorPlaneU.dot(cursorPlaneU) * ul) / ul));
							p.add(cursorPlaneV.clone().multiply(Math.round(point.dot(cursorPlaneV) / cursorPlaneV.dot(cursorPlaneV) * vl) / vl));
							p.add(corners[0]);
						}
					}
				}
			}

			if (stage == CreationStage.PLANE && !event.isShiftDown()) {
				p = RayHelper.rayPlaneIntersection(event.getRay(), cursorPlaneOrigin, cursorPlaneNormal);

				if (p != null && !event.isControlDown()) {
					p.subtract(cursorPlaneOrigin);

					float ul = (float) Math.round(cursorPlaneU.length());
					float vl = (float) Math.round(cursorPlaneV.length());
					Position point = p.clone();
					p.set(0, 0, 0);
					if (!MathHelper.isZero(ul)) p.add(cursorPlaneU.clone().multiply(Math.round(point.dot(cursorPlaneU) / cursorPlaneU.dot(cursorPlaneU) * ul) / ul));
					if (!MathHelper.isZero(vl)) p.add(cursorPlaneV.clone().multiply(Math.round(point.dot(cursorPlaneV) / cursorPlaneV.dot(cursorPlaneV) * vl) / vl));
					p.add(cursorPlaneOrigin);
				}
			}

			if (stage == CreationStage.CUBOID) {
				Position planePoint = RayHelper.rayPlaneIntersection(event.getRay(), cursorPlaneOrigin, event.getFramebufferRenderer().getCameraDirection().cross(cursorPlaneNormal).cross(cursorPlaneNormal));
				if (planePoint != null) {
					Position point = RayHelper.closestLinePoint(point2, point2.clone().add(cursorPlaneNormal), planePoint);

					if (!event.isShiftDown()) {
						float len = point.clone().subtract(cursorPlaneOrigin).length();
						if (!MathHelper.isZero(len)) point.subtract(cursorPlaneOrigin).multiply(Math.round(len) / len).add(cursorPlaneOrigin);
					}

					point2.set(point);
				}
			}

			cursor = p;
		} else if (event.getType() == PointerEventType.CLICK) {
			if (event.getButton() == PointerToolEvent.BUTTON_LEFT) {
				if (stage == CreationStage.POINT) {
					point1 = cursor.clone();
					point2 = point1.clone();
					point1PlaneOrigin = cursorPlaneOrigin.clone();
					point1PlaneNormal = cursorPlaneNormal.clone();
					stage = CreationStage.PLANE;
				} else if (stage == CreationStage.PLANE) {
					point2 = cursor.clone();

					// FIXME: check if the element isnt of depth 0 on more than 1 axis
					if (point1PlaneNormal.cross(cursorPlaneNormal).isZero() && MathHelper.isZero(cursorPlaneOrigin.clone().subtract(point1PlaneOrigin).dot(cursorPlaneNormal))) {
						cursorPlaneOrigin = point2.clone();
						stage = CreationStage.CUBOID;
					} else {
						createElement(event.getEditedModel());

						reset();
					}
				} else if (stage == CreationStage.CUBOID) {
					createElement(event.getEditedModel());

					reset();
				}
			} else if (event.getButton() == PointerToolEvent.BUTTON_RIGHT) {
				reset();
			}
		} else {
			super.onPointerEvent(event);
		}
	}

	private void createElement(BaseModel model) {
		CuboidElement element = new CuboidElement(model);

		Position point1 = RotationHelper.rotate(this.point1.clone(), rotation.inverted(), rotationOrigin);
		Position point2 = RotationHelper.rotate(this.point2.clone(), rotation.inverted(), rotationOrigin);

		element.setCorners(point1, point2);
		element.setRotationOrigin(rotationOrigin);
		element.setRotation(rotation);

		model.addElement(element);
	}

	private void reset() {
		point1 = null;
		point2 = null;
		rotation = null;
		cursorPlaneNormal = null;
		cursorPlaneOrigin = null;
		cursorPlaneU = null;
		cursorPlaneV = null;
		point1PlaneNormal = null;
		point1PlaneOrigin = null;
		stage = CreationStage.POINT;
	}

	@Override
	public void onDeactivated() {
		reset();

		super.onDeactivated();
	}

	@Override
	public ToolRenderer createRenderer(RenderManager renderManager) {
		return new ElementCreateToolRenderer(this);
	}

	@Override
	public long getLastUpdate() {
		return System.currentTimeMillis();
	}

	private static enum CreationStage {
		POINT,
		PLANE,
		CUBOID;
	};
}

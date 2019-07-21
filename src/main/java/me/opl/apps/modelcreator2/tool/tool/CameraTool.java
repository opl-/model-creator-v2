package me.opl.apps.modelcreator2.tool.tool;

import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.model.Ray;
import me.opl.apps.modelcreator2.tool.PointerToolEvent;
import me.opl.apps.modelcreator2.tool.PointerToolEvent.PointerEventType;
import me.opl.apps.modelcreator2.tool.Tool;
import me.opl.apps.modelcreator2.util.RayHelper;
import me.opl.apps.modelcreator2.util.RotationHelper;
import me.opl.apps.modelcreator2.viewport.ViewportFramebufferRenderer;
import me.opl.apps.modelcreator2.viewport.ViewportFramebufferRenderer.CameraMode.View;

public class CameraTool extends Tool {
	private static final float ALMOST_90 = (float) (Math.PI / 2f) - 0.0005f;

	@Override
	public void onPointerEvent(PointerToolEvent event) {
		ViewportFramebufferRenderer vfr = event.getFramebufferRenderer();

		if (event.getType() == PointerEventType.SCROLL) {
			boolean isOrtho = vfr.getCameraMode().getView() == View.ORTHO;

			float distanceFromTarget = vfr.getCameraPosition().distance(vfr.getCameraTarget()) - 0.05f;
			float zoomDistance = event.getScroll() * (event.isControlDown() ? 1 : 3);

			if (distanceFromTarget + zoomDistance < 0) zoomDistance = -distanceFromTarget;

			// Position positionChange = vfr.getCameraPosition().clone().subtract(vfr.getCameraTarget()).normalize().multiply(zoomDistance);
			Position positionChange = vfr.getCameraDirection().multiply(-zoomDistance);

			// FIXME: camera bounces in ortho when zooming in too far
			vfr.getCameraPosition().add(positionChange);
			if (event.isShiftDown()) vfr.getCameraTarget().add(positionChange);
		} else if (event.getType() == PointerEventType.DRAG && event.isRightButtonDown() && (event.getChangeX() != 0 || event.getChangeY() != 0)) {
			boolean isOrtho = vfr.getCameraMode().getView() == View.ORTHO;

			if (event.isShiftDown() || vfr.getCameraMode().hasDirection()) {
				Ray lastRay = RayHelper.rayFromClick(vfr, event.getLastX(), event.getLastY());

				Position positionChange = lastRay.end().clone().subtract(event.getRay().end());
				if (!isOrtho) positionChange.multiply(vfr.getCameraPosition().distance(vfr.getCameraTarget()));

				vfr.getCameraPosition().add(positionChange);
				vfr.getCameraTarget().add(positionChange);
			} else {
				Position cameraPosition = vfr.getCameraPosition().subtract(vfr.getCameraTarget());
	
				if (event.getChangeX() != 0) {
					RotationHelper.rotateY(cameraPosition, (float) (event.getChangeX() * RotationHelper.TO_RADIANS));
				}
	
				if (event.getChangeY() != 0) {
					float toKnownAxis = (float) Math.atan2(-cameraPosition.getZ(), cameraPosition.getX());
	
					RotationHelper.rotateY(cameraPosition, toKnownAxis);
	
					float currentRotation = (float) Math.atan2(cameraPosition.getY(), cameraPosition.getX());
					float rotateBy = (float) (-event.getChangeY() * RotationHelper.TO_RADIANS);
	
					if (currentRotation - rotateBy > ALMOST_90) rotateBy = -ALMOST_90 + currentRotation;
					else if (currentRotation - rotateBy < -ALMOST_90) rotateBy = ALMOST_90 + currentRotation;
	
					RotationHelper.rotateZ(cameraPosition, rotateBy);
					RotationHelper.rotateY(cameraPosition, -toKnownAxis);
				}
	
				cameraPosition.add(vfr.getCameraTarget());
			}
		} else {
			super.onPointerEvent(event);
		}
	}
}

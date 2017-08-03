package me.opl.apps.modelcreator2.tool.tool;

import me.opl.apps.modelcreator2.model.BaseModel;
import me.opl.apps.modelcreator2.model.Cuboid;
import me.opl.apps.modelcreator2.model.Fragment;
import me.opl.apps.modelcreator2.model.RayIntersection;
import me.opl.apps.modelcreator2.tool.PointerToolEvent;
import me.opl.apps.modelcreator2.tool.PointerToolEvent.PointerEventType;

public class SelectionTool extends CameraTool {
	@Override
	public void onPointerEvent(PointerToolEvent event) {
		if (event.getType() == PointerEventType.CLICK && event.getButton() == PointerToolEvent.BUTTON_LEFT) {
			RayIntersection closestIntersection = event.getRayIntersections()[0];
			BaseModel editedModel = event.getEditedModel();

			if (closestIntersection != null) {
				if (!event.isControlDown()) editedModel.deselectAllFaces();

				if (editedModel.isFaceSelected(closestIntersection.getFaceData())) editedModel.deselectFace(closestIntersection.getFaceData());
				else editedModel.selectFace(closestIntersection.getFaceData());

				Fragment fragment = closestIntersection.getFaceData().getFragment();
				if (fragment instanceof Cuboid) ((Cuboid) fragment).triggerUpdate();

				return;
			}
		}

		super.onPointerEvent(event);
	}
}

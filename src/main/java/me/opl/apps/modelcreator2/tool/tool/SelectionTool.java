package me.opl.apps.modelcreator2.tool.tool;

import me.opl.apps.modelcreator2.model.BaseModel;
import me.opl.apps.modelcreator2.model.FaceData;
import me.opl.apps.modelcreator2.tool.PointerToolEvent;
import me.opl.apps.modelcreator2.tool.PointerToolEvent.PointerEventType;

public class SelectionTool extends CameraTool {
	private static final byte SELECTING = 1;
	private static final byte DESELECTING = 2;

	private boolean dragSelecting;
	private byte selectType;

	@Override
	public void onPointerEvent(PointerToolEvent event) {
		if (dragSelecting && !event.testHeld(PointerToolEvent.BUTTON_LEFT, PointerToolEvent.MODIFIER_CONTROL)) dragSelecting = false;

		if (event.getType() == PointerEventType.CLICK && event.getButton() == PointerToolEvent.BUTTON_LEFT) {
			BaseModel editedModel = event.getEditedModel();

			if (event.getRayIntersections().length == 0) {
				if (!event.isControlDown()) editedModel.deselectAllFaces();
			} else {
				FaceData face = event.getRayIntersections()[0].getFaceData();

				if (!event.isControlDown()) editedModel.deselectAllFaces();

				if (editedModel.isFaceSelected(face)) {
					selectType = DESELECTING;
					editedModel.deselectFace(face);
				} else {
					selectType = SELECTING;
					editedModel.selectFace(face);
				}
			}

			return;
		} else if (event.getType() == PointerEventType.DRAG_START && event.getPressCount() == 2 && event.testHeld(PointerToolEvent.BUTTON_LEFT, PointerToolEvent.MODIFIER_CONTROL)) {
			dragSelecting = true;
			return;
		} else if (dragSelecting && event.getType() == PointerEventType.DRAG) {
			if (event.getRayIntersections().length > 0) {
				if (selectType == SELECTING) {
					event.getEditedModel().selectFace(event.getRayIntersections()[0].getFaceData());
					return;
				} else if (selectType == DESELECTING) {
					event.getEditedModel().deselectFace(event.getRayIntersections()[0].getFaceData());
					return;
				}
			}
		} else if (dragSelecting && event.getType() == PointerEventType.DRAG_END && !event.testHeld(PointerToolEvent.BUTTON_LEFT, PointerToolEvent.MODIFIER_CONTROL)) {
			dragSelecting = false;
			return;
		}

		super.onPointerEvent(event);
	}
}

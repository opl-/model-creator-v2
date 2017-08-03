package me.opl.apps.modelcreator2.tool.tool;

import me.opl.apps.modelcreator2.model.BaseModel;
import me.opl.apps.modelcreator2.model.Element;
import me.opl.apps.modelcreator2.model.Face;
import me.opl.apps.modelcreator2.model.FaceData;
import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.tool.PointerToolEvent;
import me.opl.apps.modelcreator2.tool.PointerToolEvent.PointerEventType;
import me.opl.apps.modelcreator2.viewport.RenderManager;
import me.opl.apps.modelcreator2.viewport.renderer.MoveToolRenderer;
import me.opl.apps.modelcreator2.viewport.renderer.ToolRenderer;

public class MoveTool extends SelectionTool {
	private Position selectionCenter = new Position(8, 8, 8);
	private Position moveVector;
	private Position faceDragVector;

	@Override
	public void onPointerEvent(PointerToolEvent event) {
		if (event.isButtonDown(PointerToolEvent.BUTTON_LEFT) && event.getType() == PointerEventType.DRAG_START) {
			if (event.getRayIntersections().length > 0) {
				BaseModel model = event.getEditedModel();
				FaceData faceData = event.getRayIntersections()[0].getFaceData();

				if (!model.isFaceSelected(faceData)) {
					model.deselectAllFaces();
					model.selectFace(faceData);
				}

				faceDragVector = faceData.getFragment().getElement().getFaceNormal(faceData);

				return;
			}
		} else if (event.isButtonDown(PointerToolEvent.BUTTON_LEFT) && event.getType() == PointerEventType.DRAG) {
			if (faceDragVector != null) {
				BaseModel model = event.getEditedModel();

				for (Element e : model.getSelectedElements()) {
					
				}
			}

			if (moveVector != null) {
				
			}
		} else if (!event.isButtonDown(PointerToolEvent.BUTTON_LEFT) && event.getType() == PointerEventType.DRAG_END) {
			faceDragVector = null;
			return;
		}

		super.onPointerEvent(event);
	}

	@Override
	public ToolRenderer createRenderer(RenderManager renderManager) {
		return new MoveToolRenderer(this);
	}

	public Position getSelectionCenter() {
		return selectionCenter;
	}
}

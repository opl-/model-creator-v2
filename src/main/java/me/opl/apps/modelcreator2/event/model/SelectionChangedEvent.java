package me.opl.apps.modelcreator2.event.model;

import me.opl.apps.modelcreator2.model.MinecraftModel;
import me.opl.apps.modelcreator2.model.Element;
import me.opl.apps.modelcreator2.model.FaceData;

public class SelectionChangedEvent extends ModelEvent {
	private ChangeType changeType;
	private FaceData[] faces;
	private Element[] elements;

	public SelectionChangedEvent(MinecraftModel model, ChangeType changeType, FaceData[] faces, Element[] elements) {
		super(model);
		this.changeType = changeType;
		this.faces = faces;
		this.elements = elements;
	}

	/**
	 * @return Type of selection change
	 */
	public ChangeType getChangeType() {
		return changeType;
	}

	/**
	 * @return Faces affected by this event
	 */
	public FaceData[] getFaces() {
		return faces;
	}

	/**
	 * Returns the elements affected by this selection change. May be empty if
	 * the elements the faces belong to have other faces selected.
	 *
	 * @return Elements affected by this event
	 */
	public Element[] getElements() {
		return elements;
	}

	public static enum ChangeType {
		ADDED,
		REMOVED;
	}
}

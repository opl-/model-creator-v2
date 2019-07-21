package me.opl.apps.modelcreator2.event;

import me.opl.apps.modelcreator2.model.BaseModel;
import me.opl.apps.modelcreator2.model.Element;

/**
 * Fired when the elements' rotation, rotation origins or rotation resize
 * properties have changed.
 */
public class ElementsRotatedEvent extends ModelEvent {
	private Element[] elements;

	public ElementsRotatedEvent(BaseModel model, Element[] elements) {
		super(model);
		this.elements = elements;
	}

	public Element[] getElements() {
		return elements;
	}
}

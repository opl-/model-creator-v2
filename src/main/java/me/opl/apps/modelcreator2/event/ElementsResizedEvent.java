package me.opl.apps.modelcreator2.event;

import me.opl.apps.modelcreator2.model.BaseModel;
import me.opl.apps.modelcreator2.model.Element;

/**
 * Fired when the elements' size or position has changed.
 */
public class ElementsResizedEvent extends ModelEvent {
	private Element[] elements;

	public ElementsResizedEvent(BaseModel model, Element[] elements) {
		super(model);
		this.elements = elements;
	}

	public Element[] getElements() {
		return elements;
	}
}

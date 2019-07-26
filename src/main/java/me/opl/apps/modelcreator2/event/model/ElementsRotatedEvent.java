package me.opl.apps.modelcreator2.event.model;

import me.opl.apps.modelcreator2.model.Element;
import me.opl.apps.modelcreator2.model.MinecraftModel;

/**
 * Fired when the elements' rotation, rotation origins or rotation resize
 * properties have changed.
 */
public class ElementsRotatedEvent extends ModelEvent {
	private Element[] elements;

	public ElementsRotatedEvent(MinecraftModel model, Element[] elements) {
		super(model);
		this.elements = elements;
	}

	public Element[] getElements() {
		return elements;
	}
}

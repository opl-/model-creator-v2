package me.opl.apps.modelcreator2.event.model;

import me.opl.apps.modelcreator2.model.Element;
import me.opl.apps.modelcreator2.model.MinecraftModel;

/**
 * Fired when the elements' size or position has changed.
 */
public class ElementsResizedEvent extends ModelEvent {
	private Element[] elements;

	public ElementsResizedEvent(MinecraftModel model, Element[] elements) {
		super(model);
		this.elements = elements;
	}

	public Element[] getElements() {
		return elements;
	}
}

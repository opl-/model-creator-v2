package me.opl.apps.modelcreator2.model;

import me.opl.apps.modelcreator2.viewport.renderer.Renderer;

public abstract class Fragment {
	private Element element;

	public Fragment(Element element) {
		this.element = element;
	}

	public Element getElement() {
		return element;
	}

	public abstract Renderer getRenderer();
}

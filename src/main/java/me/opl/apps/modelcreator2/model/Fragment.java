package me.opl.apps.modelcreator2.model;

import me.opl.apps.modelcreator2.viewport.RenderManager;
import me.opl.apps.modelcreator2.viewport.renderer.Renderer;

public abstract class Fragment {
	private Element element;

	private long lastUpdate;

	public Fragment(Element element) {
		this.element = element;
	}

	public Element getElement() {
		return element;
	}

	public abstract FaceData[] getFaces();

	public abstract Renderer createRenderer(RenderManager renderManager, BaseModel model);

	public abstract RayIntersection[] intersect(Ray ray);

	public long getLastUpdate() {
		return lastUpdate;
	}

	public void triggerUpdate() {
		lastUpdate = System.currentTimeMillis();
	}
}

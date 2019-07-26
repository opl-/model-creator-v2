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

	/**
	 * Returns the {@link Face} the passed {@link FaceData} is on.
	 *
	 * @param faceData One of this fragment's {@link FaceData} objects
	 * @return {@link Face} the {@link FaceData} is on or {@code null} if the
	 * passed face data doesn't belong to this fragment
	 */
	public abstract Face faceDataToFace(FaceData faceData);

	public abstract Renderer createRenderer(RenderManager renderManager, MinecraftModel model);

	public abstract RayFaceIntersection[] intersect(Ray ray);

	public long getLastUpdate() {
		return lastUpdate;
	}

	public void triggerUpdate() {
		lastUpdate = System.currentTimeMillis();
	}
}

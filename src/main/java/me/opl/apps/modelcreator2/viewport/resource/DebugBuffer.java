package me.opl.apps.modelcreator2.viewport.resource;

import java.util.HashMap;

import com.jogamp.opengl.GL3;

import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.model.Ray;

public class DebugBuffer extends LineBuffer {
	private HashMap<String, Integer> objects = new HashMap<>();

	public DebugBuffer(int segments) {
		super(segments);
	}

	public DebugBuffer drawPoint(String name, Position position) {
		return drawPoint(name, position, 1, 0, 1);
	}

	public DebugBuffer drawPoint(String name, Position position, float red, float green, float blue) {
		if (objects.containsKey(name)) {
			setPosition(objects.get(name).intValue());
		} else {
			setToLastPosition();
			objects.put(name, new Integer(getPosition()));
		}

		moveTo(position.getX() - 0.5f, position.getY(), position.getZ()).setWidth(2f).setColor(red, green, blue, 0.8f);
		lineTo(position.getX() + 0.5f, position.getY(), position.getZ()).setWidth(2f).setColor(red, green, blue, 0.8f);
		moveTo(position.getX(), position.getY() - 0.5f, position.getZ()).setWidth(2f).setColor(red, green, blue, 0.8f);
		lineTo(position.getX(), position.getY() + 0.5f, position.getZ()).setWidth(2f).setColor(red, green, blue, 0.8f);
		moveTo(position.getX(), position.getY(), position.getZ() - 0.5f).setWidth(2f).setColor(red, green, blue, 0.8f);
		lineTo(position.getX(), position.getY(), position.getZ() + 0.5f).setWidth(2f).setColor(red, green, blue, 0.8f);

		return this;
	}

	public DebugBuffer drawRay(String name, Ray ray) {
		return drawRay(name, ray, 1, 0, 1);
	}

	public DebugBuffer drawRay(String name, Ray ray, float red, float green, float blue) {
		if (objects.containsKey(name)) {
			setPosition(objects.get(name).intValue());
		} else {
			setToLastPosition();
			objects.put(name, new Integer(getPosition()));
		}

		moveTo(ray.start().getX(), ray.start().getY(), ray.start().getZ()).setWidth(2f).setColor(red, green, blue, 0.8f);
		lineTo(ray.end().getX(), ray.end().getY(), ray.end().getZ()).setWidth(2f).setColor(red, green, blue, 0.8f);
		drawPoint(name + "&start", ray.start(), red, green, blue);

		return this;
	}

	public void render(GL3 gl) {
		gl.glDisable(GL3.GL_DEPTH_TEST);
		gl.glEnable(GL3.GL_BLEND);

		bind(gl);
		gl.glDrawElements(GL3.GL_LINES, getIndiceCount(), GL3.GL_UNSIGNED_INT, 0);
		unbind(gl);

		gl.glEnable(GL3.GL_DEPTH_TEST);
		gl.glDisable(GL3.GL_BLEND);
	}
}

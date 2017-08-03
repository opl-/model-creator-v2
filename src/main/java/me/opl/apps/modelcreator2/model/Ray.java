package me.opl.apps.modelcreator2.model;

public class Ray {
	private Position rayStart;
	private Position rayPoint;

	public Ray(Position rayStart, Position rayPoint) {
		this.rayStart = rayStart;
		this.rayPoint = rayPoint;
	}

	public Position start() {
		return rayStart.clone();
	}

	public Position end() {
		return rayPoint.clone();
	}

	public float distance() {
		// TODO: cache this?
		return rayStart.distance(rayPoint);
	}

	@Override
	public Ray clone() {
		return new Ray(rayStart.clone(), rayPoint.clone());
	}
}

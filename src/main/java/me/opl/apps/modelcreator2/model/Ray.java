package me.opl.apps.modelcreator2.model;

public class Ray {
	private Position rayStart;
	private Position rayPoint;

	public Ray(Position rayStart, Position rayPoint) {
		this.rayStart = rayStart.clone();
		this.rayPoint = rayPoint.clone();
	}

	public Position start() {
		return rayStart.clone();
	}

	public Position end() {
		return rayPoint.clone();
	}

	public Position direction() {
		return rayPoint.clone().subtract(rayStart).normalize();
	}

	public float distance() {
		// TODO: cache this?
		return rayStart.distance(rayPoint);
	}

	@Override
	public Ray clone() {
		return new Ray(rayStart.clone(), rayPoint.clone());
	}

	@Override
	public String toString() {
		return "Ray[sx=" + rayStart.getX() + ",sy=" + rayStart.getY() + ",sz=" + rayStart.getZ() + ",px=" + rayPoint.getX() + ",py=" + rayPoint.getY() + ",pz=" + rayPoint.getZ() + "]";
	}
}

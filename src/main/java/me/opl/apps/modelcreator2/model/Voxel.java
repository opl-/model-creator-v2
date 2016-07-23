package me.opl.apps.modelcreator2.model;

import javax.media.opengl.GL2;

import me.opl.apps.modelcreator2.util.RotationHelper;

public class Voxel extends Element implements ClickTarget {
	private Position from;
	private Position to;

	private FaceData[] faceData;

	private Axis rotationAxis;
	private Position rotationOrigin;
	private double rotationAngle;

	private Position[] cornerCache;

	public Voxel(Position from, Position to) {
		this.from = from;
		this.to = to;

		this.faceData = new FaceData[6];

		this.rotationAxis = null;
		this.rotationOrigin = null;
		this.rotationAngle = 0d;

		cornerCache = new Position[8];
		for (int i = 0; i < cornerCache.length; i++) cornerCache[i] = new Position(0d, 0d, 0d);
		updateCache();
	}

	@Override
	public Position isHit(Position rayStart, Position rayPoint) {
		return null;
	}

	private void updateCache() {
		if (rotationAxis == null) {
//			cornerCache[0].set();
		} else {
			cornerCache[0].set(RotationHelper.rotate(cornerCache[0], rotationAxis, rotationAngle, rotationOrigin));
		}
	}

	@Override
	public void render(GL2 gl) {
		
	}
}

package me.opl.apps.modelcreator2.viewport.renderer;

import javax.media.opengl.GL4;

import me.opl.apps.modelcreator2.model.Cuboid;
import me.opl.apps.modelcreator2.model.Face;
import me.opl.apps.modelcreator2.model.FaceData;
import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.model.Texture;
import me.opl.apps.modelcreator2.util.ModelBuffer;

public class CuboidRenderer implements Renderer {
	private Cuboid cuboid;

	private ModelBuffer modelBuffer;

	public CuboidRenderer(Cuboid cuboid) {
		this.cuboid = cuboid;

		modelBuffer = new ModelBuffer(4, 6);
	}

	@Override
	public void prepare(GL4 gl) {
		update(gl);

		modelBuffer.prepare(gl);
	}

	@Override
	public void update(GL4 gl) {
		modelBuffer.setCurrentVertex(-1);

		Position[] cc = cuboid.getCornerCache();

		renderFace(cuboid.getFaceData(Face.DOWN), cc[0], cc[1], cc[3], cc[2]);
		renderFace(cuboid.getFaceData(Face.NORTH), cc[1], cc[0], cc[7], cc[4]);
		renderFace(cuboid.getFaceData(Face.EAST), cc[2], cc[1], cc[6], cc[7]);
		renderFace(cuboid.getFaceData(Face.SOUTH), cc[3], cc[2], cc[5], cc[6]);
		renderFace(cuboid.getFaceData(Face.WEST), cc[0], cc[3], cc[4], cc[5]);
		renderFace(cuboid.getFaceData(Face.UP), cc[4], cc[5], cc[7], cc[6]);
	}

	private void renderFace(FaceData faceData, Position c1, Position c2, Position c3, Position c4) {
		Texture texture = faceData.getTexture();

		modelBuffer.addVertex(c1);

		float r = ((float) Math.random() + 1f) / 2f;
		float g = ((float) Math.random() + 1f) / 2f;
		float b = ((float) Math.random() + 1f) / 2f;

		if (texture != null) {
			//modelBuffer.setTexture(texture, false);
			//modelBuffer.setUVCoord(, y)
			modelBuffer.setColor(1f, 0f, 1f);
		} else {
//			modelBuffer.setColor(0.3f, 0.7f, 0.45f);
			modelBuffer.setColor(r, g, b);
		}

		modelBuffer.addVertex(c2);

		if (texture != null) {
			//modelBuffer.setTexture(texture, false);
			//modelBuffer.setUVCoord(, y)
			modelBuffer.setColor(1f, 0f, 1f);
		} else {
//			modelBuffer.setColor(0.3f, 0.7f, 0.45f);
			modelBuffer.setColor(r, g, b);
		}

		modelBuffer.addVertex(c3);

		if (texture != null) {
			//modelBuffer.setTexture(texture, false);
			//modelBuffer.setUVCoord(, y)
			modelBuffer.setColor(1f, 0f, 1f);
		} else {
//			modelBuffer.setColor(0.3f, 0.7f, 0.45f);
			modelBuffer.setColor(r, g, b);
		}

		modelBuffer.addVertex(c4);

		if (texture != null) {
			//modelBuffer.setTexture(texture, false);
			//modelBuffer.setUVCoord(, y)
			modelBuffer.setColor(1f, 0f, 1f);
		} else {
//			modelBuffer.setColor(0.3f, 0.7f, 0.45f);
			modelBuffer.setColor(r, g, b);
		}
	}

	@Override
	public void render(GL4 gl) {
		modelBuffer.bind(gl);
		gl.glDrawElements(GL4.GL_TRIANGLES, modelBuffer.getIndexCount(), GL4.GL_UNSIGNED_INT, 0);
		modelBuffer.unbind(gl);
	}

	@Override
	public void destroy(GL4 gl) {
		modelBuffer.destroy(gl);
	}
}

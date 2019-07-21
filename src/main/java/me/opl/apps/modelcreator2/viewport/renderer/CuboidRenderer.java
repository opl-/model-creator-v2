package me.opl.apps.modelcreator2.viewport.renderer;

import com.jogamp.opengl.GL3;

import me.opl.apps.modelcreator2.model.BaseModel;
import me.opl.apps.modelcreator2.model.Cuboid;
import me.opl.apps.modelcreator2.model.Face;
import me.opl.apps.modelcreator2.model.FaceData;
import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.model.ResourceLocation;
import me.opl.apps.modelcreator2.model.Texture;
import me.opl.apps.modelcreator2.model.UV;
import me.opl.apps.modelcreator2.util.GLHelper;
import me.opl.apps.modelcreator2.viewport.RenderManager;
import me.opl.apps.modelcreator2.viewport.resource.ModelBuffer;

public class CuboidRenderer implements Renderer {
	private BaseModel model;
	private Cuboid cuboid;
	private long lastUpdate = -1;

	private ModelBuffer modelBuffer;

	public CuboidRenderer(RenderManager renderManager, BaseModel model, Cuboid cuboid) {
		this.model = model;
		this.cuboid = cuboid;

		modelBuffer = new ModelBuffer(renderManager, 4, 6);
	}

	@Override
	public boolean isInitialized() {
		return modelBuffer.isInitialized();
	}

	@Override
	public void prepare(GL3 gl) {
		modelBuffer.prepare(gl);

		update(gl);
	}

	@Override
	public boolean isReady() {
		return modelBuffer.isReady() && cuboid.getLastUpdate() == lastUpdate;
	}

	@Override
	public void update(GL3 gl) {
		if (cuboid.getLastUpdate() == lastUpdate) return;

		renderFace(Face.DOWN);
		renderFace(Face.NORTH);
		renderFace(Face.EAST);
		renderFace(Face.SOUTH);
		renderFace(Face.WEST);
		renderFace(Face.UP);

		modelBuffer.update(gl);

		lastUpdate = cuboid.getLastUpdate();
	}

	private void renderFace(Face face) {
		FaceData faceData = cuboid.getFaceData(face);

		// FIXME: make this actually stop rendering
		if (!faceData.isVisible()) return;

		Position[] corners = cuboid.getFaceCorners(face);

		Texture texture = null;

		if (faceData.getTexture() != null) {
			texture = model.resolveTexture(faceData.getTexture());

			if (texture == null) texture = new Texture("missingno", new ResourceLocation("missingno"));
		}

		UV uv = faceData.getUV();

		if (uv == null && texture != null) {
			Position[] cornersNoRotation = cuboid.getFaceCornersNoRotation(face);

			switch (face) {
			case NORTH:
				uv = new UV(16 - cornersNoRotation[0].getX(), 16 - cornersNoRotation[0].getY(), 16 - cornersNoRotation[3].getX(), 16 - cornersNoRotation[3].getY());
				break;
			case EAST:
				uv = new UV(16 - cornersNoRotation[0].getZ(), 16 - cornersNoRotation[0].getY(), 16 - cornersNoRotation[3].getZ(), 16 - cornersNoRotation[3].getY());
				break;
			case SOUTH:
				uv = new UV(cornersNoRotation[0].getX(), 16 - cornersNoRotation[0].getY(), cornersNoRotation[3].getX(), 16 - cornersNoRotation[3].getY());
				break;
			case WEST:
				uv = new UV(cornersNoRotation[0].getZ(), 16 - cornersNoRotation[0].getY(), cornersNoRotation[3].getZ(), 16 - cornersNoRotation[3].getY());
				break;
			case UP:
				uv = new UV(cornersNoRotation[0].getX(), cornersNoRotation[0].getZ(), cornersNoRotation[3].getX(), cornersNoRotation[3].getZ());
				break;
			case DOWN:
				uv = new UV(cornersNoRotation[0].getX(), 16 - cornersNoRotation[0].getZ(), cornersNoRotation[3].getX(), 16 - cornersNoRotation[3].getZ());
				break;
			}
		}

		modelBuffer.setCurrentVertex(face.ordinal() * 4 - 1);

		int hashcode = faceData.getFragment().hashCode();

		float r = (hashcode & 0xff) / 255f;
		float g = ((hashcode >> 8) & 0xff) / 255f;
		float b = ((hashcode >> 16) & 0xff) / 255f;

		boolean selected = model.getModelWithElements().isFaceSelected(faceData);

		modelBuffer.addVertex(corners[0]);
		modelBuffer.setSelected(selected);

		if (texture != null) {
			modelBuffer.setTexture(texture);
			modelBuffer.setUV(uv.getX1(), uv.getY1(), 16);
			modelBuffer.unsetColor();
		} else {
			modelBuffer.setColor(r, g, b);
		}

		modelBuffer.addVertex(corners[1]);
		modelBuffer.setSelected(selected);

		if (texture != null) {
			modelBuffer.setTexture(texture);
			modelBuffer.setUV(uv.getX1(), uv.getY2(), 16);
			modelBuffer.unsetColor();
		} else {
			modelBuffer.setColor(r, g, b);
		}

		modelBuffer.addVertex(corners[2]);
		modelBuffer.setSelected(selected);

		if (texture != null) {
			modelBuffer.setTexture(texture);
			modelBuffer.setUV(uv.getX2(), uv.getY1(), 16);
			modelBuffer.unsetColor();
		} else {
			modelBuffer.setColor(r, g, b);
		}

		modelBuffer.addVertex(corners[3]);
		modelBuffer.setSelected(selected);

		if (texture != null) {
			modelBuffer.setTexture(texture);
			modelBuffer.setUV(uv.getX2(), uv.getY2(), 16);
			modelBuffer.unsetColor();
		} else {
			modelBuffer.setColor(r, g, b);
		}
	}

	@Override
	public void render(GL3 gl) {
		modelBuffer.bind(gl);

		final int INDICES_PER_FACE = 6;

		int index = 0;
		for (Face f : Face.values()) {
			if (cuboid.getFaceData(f).isVisible()) {
				gl.glDrawElements(GL3.GL_TRIANGLES, INDICES_PER_FACE, GL3.GL_UNSIGNED_INT, GLHelper.INTEGER_SIZE * index * INDICES_PER_FACE);
			}

			index++;
		}

		// TODO: shouldnt this be in the ModelBuffer?
		// gl.glDrawElements(GL3.GL_TRIANGLES, modelBuffer.getIndexCount(), GL3.GL_UNSIGNED_INT, 0);

		modelBuffer.unbind(gl);
	}

	@Override
	public void destroy(GL3 gl) {
		modelBuffer.destroy(gl);
	}
}

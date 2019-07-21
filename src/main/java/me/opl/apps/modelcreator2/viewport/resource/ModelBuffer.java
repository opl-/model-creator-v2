package me.opl.apps.modelcreator2.viewport.resource;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.jogamp.opengl.GL3;

import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.model.Texture;
import me.opl.apps.modelcreator2.util.GLHelper;
import me.opl.apps.modelcreator2.viewport.RenderManager;

//TODO: update to be spec compliant
public class ModelBuffer implements Resource {
	private static final int OFFSET_POSITION = 0;
	private static final int SIZE_POSITION = GLHelper.FLOAT_SIZE * 3;
	private static final int OFFSET_TEXTURE = OFFSET_POSITION + SIZE_POSITION;
	private static final int SIZE_TEXTURE = 1;
	private static final int OFFSET_UV = OFFSET_TEXTURE + SIZE_TEXTURE;
	private static final int SIZE_UV = GLHelper.FLOAT_SIZE * 2;
	private static final int OFFSET_COLOR = OFFSET_UV + SIZE_UV;
	private static final int SIZE_COLOR = GLHelper.FLOAT_SIZE * 3;

	private static final int SIZE_PER_VERTEX = SIZE_POSITION + SIZE_TEXTURE + SIZE_UV + SIZE_COLOR;

	private static final int TEXTURE_BITS = 0x1F;
	private static final int USE_TEXTURE_BIT = 0x20;
	private static final int USE_COLOR_BIT = 0x40;
	private static final int SELECTED_BIT = 0x80;

	private RenderManager renderManager;

	private byte[] bytes;
	private ByteBuffer buffer;
	private ByteBuffer indicesBuffer;

	private int vertices;
	private int size;

	private int currentVertex = -1;

	private Texture[] textures = new Texture[16];
	private int currentTexture = 0;

	private int vao = -1;
	private int vbo = -1;
	private int ebo = -1;

	private boolean dirty = false;

	public ModelBuffer(RenderManager renderManager, int vertices, int size) {
		if (vertices < 3 || vertices > 4) throw new IllegalArgumentException("vertices has to be either 3 or 4");

		this.renderManager = renderManager;
		this.vertices = vertices;
		this.size = size;

		bytes = new byte[SIZE_PER_VERTEX * vertices * size];
		buffer = ByteBuffer.wrap(bytes);

		indicesBuffer = GLHelper.createByteBuffer((vertices - 2) * 3 * size * GLHelper.INTEGER_SIZE);

		prepareIndicesBuffer();
	}

	private void prepareIndicesBuffer() {
		indicesBuffer.clear();

		for (int f = 0; f < size; f++) {
			int fOffset = f * vertices;

			for (int v = 0; v < vertices - 2; v++) {
				if (v % 2 == 0) {
					indicesBuffer.putInt(fOffset + v);
					indicesBuffer.putInt(fOffset + v + 1);
					indicesBuffer.putInt(fOffset + v + 2);
				} else {
					indicesBuffer.putInt(fOffset + v + 1);
					indicesBuffer.putInt(fOffset + v);
					indicesBuffer.putInt(fOffset + v + 2);
				}
			}
		}
	}

	public int getIndexCount() {
		return (vertices - 2) * 3 * size;
	}

	public ModelBuffer setCurrentVertex(int vertex) {
		currentVertex = vertex;

		return this;
	}

	public ModelBuffer addVertex(Position pos) {
		return addVertex(pos.getX(), pos.getY(), pos.getZ());
	}

	public ModelBuffer addVertex(float x, float y, float z) {
		currentVertex++;

		if (currentVertex >= vertices * size) throw new IndexOutOfBoundsException("Too many vertices (" + currentVertex + "/" + (vertices * size) + ")");

		int offset = SIZE_PER_VERTEX * currentVertex + OFFSET_POSITION;

		putFloat(offset, x);
		putFloat(offset + GLHelper.FLOAT_SIZE, y);
		putFloat(offset + GLHelper.FLOAT_SIZE * 2, z);

		dirty = true;

		return this;
	}

	public ModelBuffer setTexture(Texture texture) {
		int offset = SIZE_PER_VERTEX * currentVertex + OFFSET_TEXTURE;

		int texturePosition = -1;

		for (int i = 0; i < textures.length; i++) if (textures[i] == texture) {
			texturePosition = i;
			break;
		}

		if (texturePosition == -1) {
			textures[currentTexture] = texture;

			texturePosition = currentTexture;

			currentTexture++;

			if (texturePosition > 15) throw new IllegalStateException("Texture position overflow");
		}

		bytes[offset] = (byte) ((bytes[offset] & (USE_COLOR_BIT | SELECTED_BIT)) | (texturePosition & TEXTURE_BITS) | USE_TEXTURE_BIT);

		dirty = true;

		return this;
	}

	public ModelBuffer setSelected(boolean selected) {
		int offset = SIZE_PER_VERTEX * currentVertex + OFFSET_TEXTURE;

		bytes[offset] = (byte) (bytes[offset] & (~SELECTED_BIT) | (selected ? SELECTED_BIT : 0));

		dirty = true;

		return this;
	}

	public ModelBuffer setUV(float x, float y) {
		int offset = SIZE_PER_VERTEX * currentVertex + OFFSET_UV;

		putFloat(offset, x);
		putFloat(offset + GLHelper.FLOAT_SIZE, 1 - y);

		dirty = true;

		return this;
	}

	public ModelBuffer setUV(float x, float y, int textureSize) {
		int offset = SIZE_PER_VERTEX * currentVertex + OFFSET_UV;

		putFloat(offset, x / (float) textureSize);
		putFloat(offset + GLHelper.FLOAT_SIZE, 1 - (y / (float) textureSize));

		dirty = true;

		return this;
	}

	public ModelBuffer unsetColor() {
		int offset = SIZE_PER_VERTEX * currentVertex;

		bytes[offset + OFFSET_TEXTURE] &= ~USE_COLOR_BIT;

		dirty = true;

		return this;
	}

	public ModelBuffer setColor(float red, float green, float blue) {
		int offset = SIZE_PER_VERTEX * currentVertex + OFFSET_COLOR;

		bytes[offset - OFFSET_COLOR + OFFSET_TEXTURE] |= USE_COLOR_BIT;
		putFloat(offset, red);
		putFloat(offset + GLHelper.FLOAT_SIZE, green);
		putFloat(offset + GLHelper.FLOAT_SIZE * 2, blue);

		dirty = true;

		return this;
	}

	private void putFloat(int offset, float value) {
		int a = Float.floatToRawIntBits(value);

		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
			bytes[offset] = (byte) (a & 0xFF);
			bytes[offset + 1] = (byte) (a >> 8 & 0xFF);
			bytes[offset + 2] = (byte) (a >> 16 & 0xFF);
			bytes[offset + 3] = (byte) (a >> 24 & 0xFF);
		} else {
			bytes[offset] = (byte) (a >> 24 & 0xFF);
			bytes[offset + 1] = (byte) (a >> 16 & 0xFF);
			bytes[offset + 2] = (byte) (a >> 8 & 0xFF);
			bytes[offset + 3] = (byte) (a & 0xFF);
		}
	}

	@Override
	public boolean isInitialized() {
		return vao != -1 && vbo != -1 && ebo != -1;
	}

	@Override
	public void prepare(GL3 gl) {
		if (isInitialized()) return;

		int[] ib = new int[2];

		gl.glGenVertexArrays(1, ib, 0);
		vao = ib[0];

		gl.glGenBuffers(2, ib, 0);
		vbo = ib[0];
		ebo = ib[1];

		gl.glBindVertexArray(vao);

		indicesBuffer.clear();
		gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, ebo);
		gl.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity(), indicesBuffer, GL3.GL_STATIC_DRAW);

		buffer.clear();
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo);
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, buffer.capacity(), buffer, GL3.GL_STATIC_DRAW);

		gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, SIZE_PER_VERTEX, OFFSET_POSITION);
		gl.glEnableVertexAttribArray(0);
		gl.glVertexAttribIPointer(1, 1, GL3.GL_BYTE, SIZE_PER_VERTEX, OFFSET_TEXTURE);
		gl.glEnableVertexAttribArray(1);
		gl.glVertexAttribPointer(2, 2, GL3.GL_FLOAT, false, SIZE_PER_VERTEX, OFFSET_UV);
		gl.glEnableVertexAttribArray(2);
		gl.glVertexAttribPointer(3, 3, GL3.GL_FLOAT, false, SIZE_PER_VERTEX, OFFSET_COLOR);
		gl.glEnableVertexAttribArray(3);

		gl.glBindVertexArray(0);
	}

	@Override
	public boolean isReady() {
		return isInitialized() && !dirty;
	}

	@Override
	public void update(GL3 gl) {
		if (isReady()) return;

		gl.glBindVertexArray(vao);

		buffer.clear();
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo);
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, buffer.capacity(), buffer, GL3.GL_STATIC_DRAW);

		gl.glBindVertexArray(0);

		dirty = false;
	}

	@Override
	public void bind(GL3 gl) {
		gl.glBindVertexArray(vao);

		for (int i = 0; i < currentTexture; i++) {
			gl.glActiveTexture(GL3.GL_TEXTURE0 + i);
			renderManager.getResourceManager().getTextureOrMissing(textures[i].getResourceLocation()).bind(gl);
		}
	}

	@Override
	public void unbind(GL3 gl) {
		gl.glActiveTexture(GL3.GL_TEXTURE0);
		gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);

		// XXX: removed texture unbinding to save a few cycles. shouldnt break anything

		gl.glBindVertexArray(0);
	}

	@Override
	public void destroy(GL3 gl) {
		gl.glDeleteVertexArrays(1, new int[] {vao}, 0);
		gl.glDeleteBuffers(2, new int[] {vbo, ebo}, 0);

		vao = vbo = ebo = -1;
	}
}

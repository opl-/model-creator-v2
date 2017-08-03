package me.opl.apps.modelcreator2.viewport.resource;

import com.jogamp.opengl.GL3;

public class ShaderProgramResource implements Resource {
	private String shaderName;

	private ShaderResource[] shaders;

	private int shaderProgramID;

	// TODO: improve readiness checks
	public ShaderProgramResource(String shaderName, int[] shaderTypes) {
		this.shaderName = shaderName;

		shaders = new ShaderResource[shaderTypes.length];

		for (int i = 0; i < shaderTypes.length; i++) {
			ShaderResource shader = new ShaderResource(shaderTypes[i], shaderName);
			shaders[i] = shader;
		}

		/*vertShader = ShaderCode.create(gl, GL3.GL_VERTEX_SHADER, this.getClass(), "shader", null, "lines", true);
		ShaderCode geomShader = ShaderCode.create(gl, GL3.GL_GEOMETRY_SHADER, this.getClass(), "shader", null, "lines", true);
		fragShader = ShaderCode.create(gl, GL3.GL_FRAGMENT_SHADER, this.getClass(), "shader", null, "lines", true);

		shaderProgram = new ShaderProgram();
		shaderProgram.add(vertShader);
		shaderProgram.add(geomShader);
		shaderProgram.add(fragShader);

		shaderProgram.init(gl);

		linesShader = shaderProgram.program();

		shaderProgram.link(gl, System.out);

		vertShader.destroy(gl);
		geomShader.destroy(gl);
		fragShader.destroy(gl);*/
	}

	public String getShaderName() {
		return shaderName;
	}

	public int glID() {
		return shaderProgramID;
	}

	@Override
	public boolean isInitialized() {
		return shaderProgramID != 0;
	}

	@Override
	public void prepare(GL3 gl) {
		shaderProgramID = gl.glCreateProgram();

		for (int i = 0; i < shaders.length; i++) {
			shaders[i].prepare(gl);
			shaders[i].attach(gl, shaderProgramID);
		}

		gl.glLinkProgram(shaderProgramID);

		for (int i = 0; i < shaders.length; i++) {
			shaders[i].destroy(gl);
			shaders[i] = null;
		}

		int[] ib = new int[1];
		gl.glGetProgramiv(shaderProgramID, GL3.GL_LINK_STATUS, ib, 0);

		if (ib[0] != 1) {
			gl.glGetProgramiv(shaderProgramID, GL3.GL_INFO_LOG_LENGTH, ib, 0);

			byte[] log = new byte[ib[0]];
			gl.glGetProgramInfoLog(shaderProgramID, ib[0], ib, 0, log, 0);

			throw new IllegalStateException(new String(log, 0, ib[0]));
		}
	}

	@Override
	public boolean isReady() {
		return isInitialized();
	}

	@Override
	public void bind(GL3 gl) {
		gl.glUseProgram(shaderProgramID);
	}

	@Override
	public void unbind(GL3 gl) {
		gl.glUseProgram(0);
	}

	@Override
	public void destroy(GL3 gl) {
		gl.glDeleteProgram(shaderProgramID);

		shaderProgramID = 0;
	}

	@Override public void update(GL3 gl) {}
}

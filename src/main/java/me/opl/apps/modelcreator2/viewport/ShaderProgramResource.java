package me.opl.apps.modelcreator2.viewport;

import javax.media.opengl.GL4;

public class ShaderProgramResource implements Resource {
	private ShaderResource[] shaders;

	private int shaderProgramID;

	public ShaderProgramResource(String shaderName, int[] shaderTypes) {
		shaders = new ShaderResource[shaderTypes.length];

		for (int i = 0; i < shaderTypes.length; i++) {
			ShaderResource shader = new ShaderResource(shaderTypes[i], shaderName);
			shaders[i] = shader;
		}

		/*vertShader = ShaderCode.create(gl, GL4.GL_VERTEX_SHADER, this.getClass(), "shader", null, "lines", true);
		ShaderCode geomShader = ShaderCode.create(gl, GL4.GL_GEOMETRY_SHADER, this.getClass(), "shader", null, "lines", true);
		fragShader = ShaderCode.create(gl, GL4.GL_FRAGMENT_SHADER, this.getClass(), "shader", null, "lines", true);

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

	@Override
	public int glID() {
		return shaderProgramID;
	}

	@Override
	public void prepare(GL4 gl) {
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
		gl.glGetProgramiv(shaderProgramID, GL4.GL_LINK_STATUS, ib, 0);

		if (ib[0] != 1) {
			gl.glGetProgramiv(shaderProgramID, GL4.GL_INFO_LOG_LENGTH, ib, 0);

			byte[] log = new byte[ib[0]];
			gl.glGetProgramInfoLog(shaderProgramID, ib[0], ib, 0, log, 0);

			throw new IllegalStateException(new String(log, 0, ib[0]));
		}
	}

	@Override
	public void bind(GL4 gl) {
		gl.glUseProgram(shaderProgramID);
	}

	@Override
	public void unbind(GL4 gl) {
		gl.glUseProgram(0);
	}

	@Override
	public void destroy(GL4 gl) {
		gl.glDeleteProgram(shaderProgramID);
	}
}

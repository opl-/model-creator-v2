package me.opl.apps.modelcreator2.viewport.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Scanner;

import com.jogamp.opengl.GL3;

public class ShaderResource implements Resource {
	private int shaderType;
	private String shaderName;
	private String extension;

	private int shaderID;

	// TODO: improve readiness checks
	public ShaderResource(int shaderType, String shaderName) {
		extension = extensionByShaderType(shaderType);
		if (this.extension == null) throw new IllegalArgumentException(shaderType + " is not a valid shader type");

		this.shaderType = shaderType;
		this.shaderName = shaderName;
	}

	@Override
	public boolean isInitialized() {
		return shaderID != 0;
	}

	@Override
	public void prepare(GL3 gl) {
		Scanner s;
		try {
			s = new Scanner(new File(getClass().getResource("/shader/" + this.shaderName + "." + this.extension).toURI()));
			String shaderCode = s.useDelimiter("\\A").next();
			s.close();

			shaderID = gl.glCreateShader(shaderType);

			gl.glShaderSource(shaderID, 1, new String[] {shaderCode}, new int[] {shaderCode.length()}, 0);

			gl.glCompileShader(shaderID);

			int[] ib = new int[1];
			gl.glGetShaderiv(shaderID, GL3.GL_COMPILE_STATUS, ib, 0);

			if (ib[0] != 1) {
				gl.glGetShaderiv(shaderID, GL3.GL_INFO_LOG_LENGTH, ib, 0);

				byte[] log = new byte[ib[0]];
				gl.glGetShaderInfoLog(shaderID, ib[0], ib, 0, log, 0);

				throw new IllegalStateException(shaderName + "." + extension + ": " + new String(log, 0, ib[0]));
			}
		} catch (FileNotFoundException | URISyntaxException e) {
			throw new IllegalArgumentException("Shader " + this.shaderName + "." + this.extension + " not found", e);
		}
	}

	@Override
	public boolean isReady() {
		return isInitialized();
	}

	public void attach(GL3 gl, int shaderProgramID) {
		// TODO: possibly move this to bind() (same for detach), making each ShaderResource store an instance of a program since shaders cant exist without a program anyway and since they map 1:1 to a program
		gl.glAttachShader(shaderProgramID, shaderID);
	}

	public void detach(GL3 gl, int shaderProgramID) {
		gl.glDetachShader(shaderProgramID, shaderID);
	}

	@Override
	public void bind(GL3 gl) {
		throw new IllegalAccessError("Impossible operation");
	}

	@Override
	public void unbind(GL3 gl) {
		throw new IllegalAccessError("Impossible operation");
	}

	@Override
	public void destroy(GL3 gl) {
		gl.glDeleteShader(shaderID);

		shaderID = 0;
	}

	private static String extensionByShaderType(int shaderType) {
		switch (shaderType) {
		case GL3.GL_VERTEX_SHADER: return "vs";
		case GL3.GL_TESS_CONTROL_SHADER: return "tcs";
		case GL3.GL_TESS_EVALUATION_SHADER: return "tes";
		case GL3.GL_GEOMETRY_SHADER: return "gs";
		case GL3.GL_FRAGMENT_SHADER: return "fs";
		default: return null;
		}
	}

	@Override public void update(GL3 gl) {}
}

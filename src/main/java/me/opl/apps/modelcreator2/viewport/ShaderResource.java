package me.opl.apps.modelcreator2.viewport;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Scanner;

import javax.media.opengl.GL4;

public class ShaderResource implements Resource {
	private int shaderType;
	private String shaderName;
	private String extension;

	private int shaderID;

	public ShaderResource(int shaderType, String shaderName) {
		extension = extensionByShaderType(shaderType);
		if (this.extension == null) throw new IllegalArgumentException(shaderType + " is not a valid shader type");

		this.shaderType = shaderType;
		this.shaderName = shaderName;
	}

	@Override
	public int glID() {
		return shaderID;
	}

	@Override
	public void prepare(GL4 gl) {
		Scanner s;
		try {
			s = new Scanner(new File(getClass().getResource("/shader/" + this.shaderName + "." + this.extension).toURI()));
			String shaderCode = s.useDelimiter("\\A").next();
			s.close();

			shaderID = gl.glCreateShader(shaderType);

			gl.glShaderSource(shaderID, 1, new String[] {shaderCode}, new int[] {shaderCode.length()}, 0);

			gl.glCompileShader(shaderID);

			int[] ib = new int[1];
			gl.glGetShaderiv(shaderID, GL4.GL_COMPILE_STATUS, ib, 0);

			if (ib[0] != 1) {
				gl.glGetShaderiv(shaderID, GL4.GL_INFO_LOG_LENGTH, ib, 0);

				byte[] log = new byte[ib[0]];
				gl.glGetShaderInfoLog(shaderID, ib[0], ib, 0, log, 0);

				throw new IllegalStateException(shaderName + "." + extension + ": " + new String(log, 0, ib[0]));
			}
		} catch (FileNotFoundException | URISyntaxException e) {
			throw new IllegalArgumentException("Shader " + this.shaderName + "." + this.extension + " not found", e);
		}
	}

	public void attach(GL4 gl, int shaderProgramID) {
		// TODO: possibly move this to bind() (same for detach), making each ShaderResource store an instance of a program since shaders cant exist without a program anyway and since they map 1:1 to a program
		gl.glAttachShader(shaderProgramID, shaderID);
	}

	public void detach(GL4 gl, int shaderProgramID) {
		gl.glDetachShader(shaderProgramID, shaderID);
	}

	@Override
	public void bind(GL4 gl) {
		throw new IllegalAccessError("Impossible operation");
	}

	@Override
	public void unbind(GL4 gl) {
		throw new IllegalAccessError("Impossible operation");
	}

	@Override
	public void destroy(GL4 gl) {
		gl.glDeleteShader(shaderID);
	}

	private static String extensionByShaderType(int shaderType) {
		switch (shaderType) {
		case GL4.GL_VERTEX_SHADER: return "vs";
		case GL4.GL_TESS_CONTROL_SHADER: return "tcs";
		case GL4.GL_TESS_EVALUATION_SHADER: return "tes";
		case GL4.GL_GEOMETRY_SHADER: return "gs";
		case GL4.GL_FRAGMENT_SHADER: return "fs";
		default: return null;
		}
	}
}

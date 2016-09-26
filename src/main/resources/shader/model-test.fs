#version 330 core

flat in int vTexture;
in vec3 vUVColor;

out vec4 color;

void main() {
	//color = vec4(1f);
	//color = vec4(vUVColor.x, vTexture / 1000000000f, 1f, 1f);
	color = vec4(vUVColor, gl_FrontFacing ? 1f : 0.5f);
}

#version 330 core

#define PI 3.14159265

uniform sampler2D[] textures;
uniform float time;
uniform int frame;

in vec3 vPosition;
/*
bits name
1-5  textureID
6    use texture
7    use color
8    selected
*/
flat in int vTexture;
in vec2 vUV;
in vec3 vColor;

out vec4 color;

vec4 colorByTextureID(int id, vec2 uv) {
	if (id == 0) return texture(textures[0], uv);
	else if (id == 1) return texture(textures[1], uv);
	else if (id == 2) return texture(textures[2], uv);
	else if (id == 3) return texture(textures[3], uv);
	else if (id == 4) return texture(textures[4], uv);
	else if (id == 5) return texture(textures[5], uv);
	else if (id == 6) return texture(textures[6], uv);
	else if (id == 7) return texture(textures[7], uv);
	else if (id == 8) return texture(textures[8], uv);
	else if (id == 9) return texture(textures[9], uv);
	else if (id == 10) return texture(textures[10], uv);
	else if (id == 11) return texture(textures[11], uv);
	else if (id == 12) return texture(textures[12], uv);
	else if (id == 13) return texture(textures[13], uv);
	else if (id == 14) return texture(textures[14], uv);
	else if (id == 15) return texture(textures[15], uv);
}

void main() {
	color = vec4(1f, 1f, 1f, 0f);

	if ((vTexture & 0x20) != 0) {
		// has texture
		color = colorByTextureID(vTexture & 0x1f, vUV);

		if ((vTexture & 0x40) != 0) {
			// and color
			color = mix(color, vec4(vColor, 1f), 0.5f);
		}
	} else if ((vTexture & 0x40) != 0) {
		// has color
		color = vec4(vColor, 1f);
	}

	// is selected
	if ((vTexture & 0x80) != 0) {
		vec3 pos = vPosition + (time / 10f);
		pos -= floor(pos);

		float lines = pos.x + pos.y + pos.z;

		float mixRatio = mod(lines, 1f) < 0.5f ? 0.1f : 0.4f;
		mixRatio += 0.05f * sin(time * PI);

		color = mix(color, vec4(1f, 1f, 0f, 1f), mixRatio);
	}
}

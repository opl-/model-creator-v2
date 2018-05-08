#version 330 core

#define PI 3.14159265

uniform sampler2D[] textures;
uniform float time;

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

sampler2D textureByID(int id) {
	if (id == 0) return textures[0];
	else if (id == 1) return textures[1];
	else if (id == 2) return textures[2];
	else if (id == 3) return textures[3];
	else if (id == 4) return textures[4];
	else if (id == 5) return textures[5];
	else if (id == 6) return textures[6];
	else if (id == 7) return textures[7];
	else if (id == 8) return textures[8];
	else if (id == 9) return textures[9];
	else if (id == 10) return textures[10];
	else if (id == 11) return textures[11];
	else if (id == 12) return textures[12];
	else if (id == 13) return textures[13];
	else if (id == 14) return textures[14];
	else if (id == 15) return textures[15];
}

void main() {
	color = vec4(1f, 1f, 1f, 0f);

	if ((vTexture & 0x20) != 0) {
		// has texture
		color = texture(textureByID(vTexture & 0x1f), vUV);

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

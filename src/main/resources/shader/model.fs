#version 330 core

uniform sampler2D[] textures;

/*
 0-15  texture ID
16-31  (texture ID + 16) + selection
32     solid color
*/
flat in int vTexture;
in vec3 vUVColor;

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
	if (vTexture >= 0 && vTexture < 32) {
		color = texture(textureByID(vTexture % 16), vUVColor.st);

		if (vTexture >= 16) {
			color = mix(color, vec4(1f, 1f, 0f, 0f), 0.5f);
		}
	} else if (vTexture == 32) {
		color = vec4(vUVColor, 1f);
	} else {
		color = vec4(1f, 0f, 1f, 1f);
	}
}

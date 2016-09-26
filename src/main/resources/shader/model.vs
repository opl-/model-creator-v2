#version 330 core

uniform mat4 viewProjection;

layout (location = 0) in vec3 position;
layout (location = 1) in int texture;
layout (location = 2) in vec3 uvColor;

flat out int vTexture;
out vec3 vUVColor;

void main() {
    gl_Position = viewProjection * vec4(position, 1f);
    vTexture = texture;
    vUVColor = uvColor;
}

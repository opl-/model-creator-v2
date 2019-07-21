#version 330 core

uniform mat4 modelMatrix;
uniform mat4 viewProjectionMatrix;

layout (location = 0) in vec3 position;
layout (location = 1) in int texture;
layout (location = 2) in vec2 uv;
layout (location = 3) in vec3 color;

out vec3 vPosition;
flat out int vTexture;
out vec2 vUV;
out vec3 vColor;

void main() {
    gl_Position = modelMatrix * viewProjectionMatrix * vec4(position, 1f);

    vPosition = position;
    vTexture = texture;
    vUV = uv;
    vColor = color;
}

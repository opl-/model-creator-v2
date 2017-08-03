#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in float width;
layout (location = 2) in vec4 color;

out float vWidth;
out vec4 vColor;

void main() {
    gl_Position = vec4(position, 1f);
    vWidth = width;
    vColor = color;
}

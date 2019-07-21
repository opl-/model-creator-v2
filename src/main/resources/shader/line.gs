#version 330 core

uniform mat4 modelMatrix;
uniform mat4 viewProjectionMatrix;
uniform vec3 cameraDirection;
uniform vec3 cameraPosition;
uniform bool constantWidth;

layout(lines) in;
layout(triangle_strip, max_vertices = 4) out;

in vec4 vColor[];
in float vWidth[];

out vec4 gColor;

void main() {
	vec4 n = vec4(normalize(cross(normalize(gl_in[1].gl_Position.xyz - gl_in[0].gl_Position.xyz), cameraDirection)) * 0.1f, 0);
	vec4 n1 = n * vWidth[0];
	vec4 n2 = n * vWidth[1];

	if (constantWidth) {
		vec3 d = cameraPosition - gl_in[0].gl_Position.xyz;
		n1 *= sqrt(dot(d, d)) / 100f;

		d = cameraPosition - gl_in[1].gl_Position.xyz;
		n2 *= sqrt(dot(d, d)) / 100f;
	}
	
	gColor = vColor[0];

	gl_Position = modelMatrix * viewProjectionMatrix * (gl_in[0].gl_Position - n1);
	EmitVertex();

	gl_Position = modelMatrix * viewProjectionMatrix * (gl_in[0].gl_Position + n1);
	EmitVertex();

	gColor = vColor[1];

	gl_Position = modelMatrix * viewProjectionMatrix * (gl_in[1].gl_Position - n2);
	EmitVertex();

	gl_Position = modelMatrix * viewProjectionMatrix * (gl_in[1].gl_Position + n2);
	EmitVertex();

	EndPrimitive();
}

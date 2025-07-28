#version 330 core
layout (location = 0) in vec2 renderingOffest;
layout (location = 1) in vec3 aColor;

out vec3 fragColor;

uniform vec2 windowPos;
uniform float zoom;

void main() {
    gl_Position = vec4(zoom * (2 * renderingOffest.x / windowPos.x), zoom * (2 * renderingOffest.y / windowPos.y), 0.0, 1.0);
    fragColor = aColor;
}
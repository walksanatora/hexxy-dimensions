#version 150

#moj_import <projection.glsl>

in vec3 Position;
out vec4 texProj0;

uniform float GameTime;
uniform vec2 ScreenSize;

void main() {
    gl_Position = vec4(Position, 1.0);

    texProj0 = projection_from_position(gl_Position);
}

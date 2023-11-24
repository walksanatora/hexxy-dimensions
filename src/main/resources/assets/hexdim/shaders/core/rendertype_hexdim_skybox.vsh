#version 150

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in vec2 UV2;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float GameTime;
uniform vec2 ScreenSize;

out vec4 vertexColor;
out vec2 texCoord0;
out vec2 texCoord2;
out vec4 normal;
out float gameTime;
out vec2 screenSize;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    vertexColor = Color;
    texCoord0 = UV0;
    texCoord2 = UV2;
    gameTime = GameTime;
    normal = ProjMat * ModelViewMat * vec4(Normal, 0.0);
}

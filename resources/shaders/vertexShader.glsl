#version 330 core

layout(location = 0) in vec3 vertexPosition;
layout(location = 1) in vec2 vertexUV;
layout(location = 2) in vec3 vertexColourTemplate;

out vec3 colourTemplate;
out vec2 UV;

uniform mat4 MVP;

void main() {

    gl_Position =  MVP * vec4(vertexPosition, 1);

    colourTemplate = vertexColourTemplate;

    UV = vertexUV;
}

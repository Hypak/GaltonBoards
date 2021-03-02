#version 330 core

in vec2 UV;
in vec3 colourTemplate;

out vec4 color;

uniform sampler2D texSampler;

void main() {
    vec4 texVal = vec4(texture( texSampler, UV ));
    float alpha = 1.0f;

    texVal.x *= colourTemplate.x;
    texVal.y *= colourTemplate.y;
    texVal.z *= colourTemplate.z;

    color = texVal;

    //gl_FragColor = tonemap(C_diff);
}

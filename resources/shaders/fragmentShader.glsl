#version 330 core

in vec2 UV;

out vec4 color;


uniform sampler2D texSampler;

void main() {
    vec4 texVal = vec4(texture( texSampler, UV ));
    float alpha = 1.0f;

    color = vec4(texVal);
    //gl_FragColor = tonemap(C_diff);
}

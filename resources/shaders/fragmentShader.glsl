#version 330 core

in vec2 UV;

out vec4 color;


uniform sampler2D texSampler;

// Tone mapping and display encoding combined
vec3 tonemap(vec3 linearRGB)
{
    float L_white = 0.7; // Controls the brightness of the image

    float inverseGamma = 1./2.2;
    return pow(linearRGB/L_white, vec3(inverseGamma)); // Display encoding - a gamma
}

void main() {
    vec3 texVal = vec3(texture( texSampler, UV ));
    float alpha = 1.0f;
    texVal = vec3(1,0,0);

    color = vec4(tonemap(texVal), alpha);
    color = vec4(1);
    //gl_FragColor = tonemap(C_diff);
}

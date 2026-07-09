#version 150

in vec3 Position;
in vec2 UV0;
in ivec2 UV2;

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMatrix;
};

layout(std140) uniform Projection {
    mat4 ProjMat;
};

out vec2 fragPos;
out float hue;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    fragPos = UV0;
    hue = UV2[0] / 360.0f;
}

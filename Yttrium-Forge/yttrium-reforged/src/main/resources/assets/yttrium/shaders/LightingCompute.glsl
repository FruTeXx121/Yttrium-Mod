#version 430
layout(local_size_x = 16, local_size_y = 16) in;
layout(std430, binding = 0) buffer LightLevels { float light[]; };
layout(std430, binding = 1) buffer BlockData { int blocks[]; };
void main() {
    uint i = gl_GlobalInvocationID.y * 16 + gl_GlobalInvocationID.x;
    light[i] *= (blocks[i] == 0 ? 1.0 : 0.5);
}

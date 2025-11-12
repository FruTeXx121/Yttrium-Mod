#version 430
layout(local_size_x = 64) in;
layout(std430, binding = 0) buffer Matrices { mat4 transforms[]; };
layout(std430, binding = 1) buffer Vectors { vec4 positions[]; };
layout(std430, binding = 2) buffer Results { vec4 results[]; };
void main() {
    uint id = gl_GlobalInvocationID.x;
    results[id] = transforms[id] * positions[id];
}

#version 430

layout(local_size_x = 64) in;

layout(std430, binding = 0) buffer Positions {
    vec3 pos[];
};

layout(std430, binding = 1) buffer Velocities {
    vec3 vel[];
};

layout(std430, binding = 2) buffer Results {
    vec3 newPos[];
};

uniform float deltaTime;

void main() {
    uint id = gl_GlobalInvocationID.x;
    newPos[id] = pos[id] + vel[id] * deltaTime;
}

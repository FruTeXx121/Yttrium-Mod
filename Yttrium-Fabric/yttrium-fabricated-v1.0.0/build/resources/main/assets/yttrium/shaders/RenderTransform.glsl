#version 430
layout(local_size_x = 64) in;
layout(std430, binding = 0) buffer Vertices { vec4 verts[]; };
layout(std430, binding = 1) buffer MVP { mat4 mvp; };
layout(std430, binding = 2) buffer Results { vec4 outVerts[]; };
void main() {
    outVerts[gl_GlobalInvocationID.x] = mvp * verts[gl_GlobalInvocationID.x];
}

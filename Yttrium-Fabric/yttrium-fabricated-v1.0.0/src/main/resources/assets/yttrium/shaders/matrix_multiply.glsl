#version 330 core

uniform mat4 matrixA;
uniform mat4 matrixB;

void main() {
    mat4 result = matrixA * matrixB;
    // Use result in rendering or write to buffer
}

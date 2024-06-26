#version 330

in vec3  v2f_color;
out vec4 f_color;

void main()
{
    f_color = vec4(v2f_color, 1.0);
}
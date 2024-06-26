#version 330

layout (location=0) in vec4 v_position;
layout (location=1) in vec3 v_color;
uniform mat4 modelview_projection_matrix;
out vec3 v2f_color;

void main()
{
    v2f_color = v_color;
    gl_Position = modelview_projection_matrix * v_position;
}
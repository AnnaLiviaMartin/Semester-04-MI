#version 330

layout (location=0) in vec4 v_position;
layout (location=1) in vec3 v_normen;
uniform mat4 modelview_projection_matrix;
uniform mat4 modelview_matrix;
uniform mat4 normal_matrix;
uniform vec3 light_pos;
//out vec3 v2f_color;


out vec3 fn;
out vec3 vertPos;

void main()
{
    //v2f_color = v_color;
    fn = vec3(normal_matrix * vec4(v_normen, 0.0));
    vec4 vertPos4 = modelview_matrix * v_position;
    vertPos = vec3(vertPos4) / verPos4.w;
    gl_Position = modelview_projection_matrix *modelview_matrix * v_position;
}
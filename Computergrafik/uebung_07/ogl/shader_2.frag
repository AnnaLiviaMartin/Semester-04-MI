#version 330

in vec3  v2f_color;
out vec4 f_color;

void main()
{
    float ambientStrength = 0.1;
    vec3 ambient = ambientStrength * lightColor;

    vec3 result = ambient * objectColor;
    f_color = vec4(result, 1.0);
} 
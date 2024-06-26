#version 330

//in vec3  v2f_color;
out vec4 f_color;

in vec2 tc;
in vec3 fn;
in vec3 vertPos;

uniform int mode;
uniform vec3 lightDirection;

const vec4 ambientColor = vec4(0.01, 0.0, 0.0, 1.0);
const vec4 diffuseColor = vec4(0.25, 0.0, 0.0, 1.0);
const vec4 specularColor = vec4(1.0, 1.0, 1.0, 1.0);
const float shininess = 20.0;
const vec4 lightColor = vec4(1.0, 1.0, 1.0, 1.0);
const float irradiPerp = 1.0;

vec3 phongBRDF(vec3 lightDir, vec3 viewDir, vec3 normal, 
          vec3 phongDiffuseCol, vec3 phongSpecularCol, float phongShininess) {
  vec3 color = phongDiffuseCol;
  vec3 reflectDir = reflect(-lightDir, normal);
  float specDot = max(dot(reflectDir, viewDir), 0.0);
  color += pow(specDot, phongShininess) * phongSpecularCol;
  return color;
}


void main() {
  vec3 lightDir = normalize(-lightDirection);
  vec3 viewDir = normalize(-vertPos);
  vec3 n = normalize(fn);

  vec3 radiance = ambientColor.rgb;
  
  float irradiance = max(dot(lightDir, n), 0.0) * irradiPerp;
  if(irradiance > 0.0) {
    vec3 brdf = phongBRDF(lightDir, viewDir, n, 
                          diffuseColor.rgb, specularColor.rgb, shininess);
    radiance += brdf * irradiance * lightColor.rgb;
  }

  radiance = pow(radiance, vec3(1.0 / 2.2) ); // gamma correction
  f_color.rgb = radiance;
  f_color.a = 1.0;
}
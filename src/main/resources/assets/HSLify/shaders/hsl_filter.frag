#version 330 core

uniform sampler2D tex;
uniform float hue;
uniform float saturation;
uniform float lightness;

in vec2 uv;
out vec4 fragColor;

void main()
{
vec4 color = texture(tex, uv);

// Convert RGB to HSL
float cmax = max(max(color.r, color.g), color.b);
float cmin = min(min(color.r, color.g), color.b);
float delta = cmax - cmin;

float hueValue = 0.0;

if (delta != 0.0) {
    if (cmax == color.r)
    hueValue = mod((color.g - color.b) / delta, 6.0);
    else if (cmax == color.g)
    hueValue = (color.b - color.r) / delta + 2.0;
    else
    hueValue = (color.r - color.g) / delta + 4.0;
}

hueValue = mod(hueValue * 60.0, 360.0);

float lightnessValue = (cmax + cmin) / 2.0;
float saturationValue = (delta == 0.0) ? 0.0 : delta / (1.0 - abs(2.0 * lightnessValue - 1.0));

// Apply custom float values
hueValue = mod(hueValue + hue, 360.0);
saturationValue = clamp(saturationValue + saturation / 100.0, 0.0, 1.0);
lightnessValue = clamp(lightnessValue + lightness / 100.0, 0.0, 1.0);

// Convert back to RGB
float c = (1.0 - abs(2.0 * lightnessValue - 1.0)) * saturationValue;
float x = c * (1.0 - abs(mod(hueValue / 60.0, 2.0) - 1.0));
float m = lightnessValue - c / 2.0;

vec3 adjustedColor;
if (hueValue < 60.0)
adjustedColor = vec3(c, x, 0.0);
else if (hueValue < 120.0)
adjustedColor = vec3(x, c, 0.0);
else if (hueValue < 180.0)
adjustedColor = vec3(0.0, c, x);
else if (hueValue < 240.0)
adjustedColor = vec3(0.0, x, c);
else if (hueValue < 300.0)
adjustedColor = vec3(x, 0.0, c);
else
adjustedColor = vec3(c, 0.0, x);

fragColor = vec4((adjustedColor + m), color.a);
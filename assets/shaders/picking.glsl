#type vertex
layout (location=0) in vec3 aPos;
layout (location=1) in vec4 aColour;
layout (location=2) in vec2 aTexCoords;
layout (location=3) in float aTexID;
layout (location=4) in float aEntityID;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColour;
out vec2 fTexCoords;
out float fTexID;
out float fEntityID;

void main()
{
    fColour = aColour;
    fTexCoords = aTexCoords;
    fTexID = aTexID;
    fEntityID = aEntityID;

    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}

#type fragment

in vec4 fColour;
in vec2 fTexCoords;
in float fTexID;
in float fEntityID;

uniform sampler2D uTextures[8];

out vec3 colour;

void main()
{
    vec4 texColour = vec4(1, 1, 1, 1);
    if (fTexID > 0){
        int id = int(fTexID);
        texColour = fColour * texture(uTextures[id], fTexCoords);
    }

    if (texColour.a < 0.5){
        discard;
    }

    colour = vec3(fEntityID, fEntityID, fEntityID);
}
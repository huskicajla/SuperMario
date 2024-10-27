#type vertex
#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;
layout (location = 2) in vec2 aTextureCords;
layout (location = 3) in float aTextureId;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTextureCords;
out float fTextureId;

void main() {
    fColor = aColor;
    fTextureCords = aTextureCords;
    fTextureId = aTextureId;
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}

#type fragment
#version 330 core

in vec4 fColor;
in vec2 fTextureCords;
in float fTextureId;

uniform sampler2D uTextures[8];

out vec4 color;

void main()
{
    if(fTextureId > 0) {
        int id = int(fTextureId);
        switch (id) {
            case 0:
            color = fColor * texture(uTextures[0], fTextureCords);
            break;
            case 1:
            color = fColor * texture(uTextures[1], fTextureCords);
            break;
            case 2:
            color = fColor * texture(uTextures[2], fTextureCords);
            break;
            case 3:
            color = fColor * texture(uTextures[3], fTextureCords);
            break;
            case 4:
            color = fColor * texture(uTextures[4], fTextureCords);
            break;
            case 5:
            color = fColor * texture(uTextures[5], fTextureCords);
            break;
            case 6:
            color = fColor * texture(uTextures[6], fTextureCords);
            break;
            case 7:
            color = fColor * texture(uTextures[7], fTextureCords);
            break;
        }
    } else {
        color = fColor;
    }

    if (color.a < 0.1) {
        discard;
    }
}

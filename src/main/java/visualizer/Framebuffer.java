package visualizer;

import static org.lwjgl.opengl.GL30.*;

public class Framebuffer {

    private int fboID ;// = 0;
    private Texture fboTexture;// = null;
    public int width, height;

    public Framebuffer(int width, int height) {
        this.width = width;
        this.height = height;

        fboID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);

        this.fboTexture = new Texture(width, height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D,
                this.fboTexture.getTextureID(), 0);

        int rboID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboID);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            assert false : "ERROR: Framebuffer is not complete.";
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int getFboID() {
        return this.fboID;
    }
    public int getFboTextureId() {
        return this.fboTexture.getTextureID();
    }

}
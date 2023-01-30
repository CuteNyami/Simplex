package tk.simplexclient.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class GaussianBlurUtils {

    public static ShaderUtils shader = new ShaderUtils("simplex/shader/blur.frag");

    public static Framebuffer framebuffer = new Framebuffer(1, 1, false);

    private static Minecraft mc = Minecraft.getMinecraft();

    public static void setupUniforms(float dir1, float dir2, float radius) {
        shader.setUniformi("textureIn", 0);
        shader.setUniformf("texelSize", 1.0F / (float) mc.displayWidth, 1.0F / (float) mc.displayHeight);
        shader.setUniformf("direction", dir1, dir2);
        shader.setUniformf("radius", radius);

        final FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);
        for (int i = 0; i <= radius; i++) {
            weightBuffer.put(calculateGaussianValue(i, radius / 2));
        }

        weightBuffer.rewind();
        glUniform1(shader.getUniform("weights"), weightBuffer);
    }

    public static void renderBlur(float radius) {
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

        framebuffer = createFrameBuffer(framebuffer);

        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        shader.init();
        setupUniforms(1, 0, radius);

        glBindTexture(GL_TEXTURE_2D, mc.getFramebuffer().framebufferTexture);

        ShaderUtils.drawQuads();
        framebuffer.unbindFramebuffer();
        shader.unload();

        mc.getFramebuffer().bindFramebuffer(true);
        shader.init();
        setupUniforms(0, 1, radius);

        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
        ShaderUtils.drawQuads();
        shader.unload();

        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.bindTexture(0);
    }

    public static float calculateGaussianValue(float x, float sigma) {
        double PI = 3.141592653;
        double output = 1.0 / Math.sqrt(2.0 * PI * (sigma * sigma));
        return (float) (output * Math.exp(-(x * x) / (2.0 * (sigma * sigma))));
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        }
        return framebuffer;
    }
    
}

package tk.simplexclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.util.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import tk.simplexclient.api.elements.RectangleElement;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class BlurUtils {

    private final float blurRadius;

    public BlurUtils(float blurRadius) {
        this.blurRadius = blurRadius;
    }

    private double lastBgBlurFactor = -1;

    private Framebuffer blurOutputHorizontal = null;
    private Framebuffer blurOutputVertical = null;

    private Shader blurShaderHorizontal = null;
    private Shader blurShaderVertical = null;

    public void blurBackground() {
        if (Minecraft.getDebugFPS() <= 30) return;
        GL11.glPushMatrix();
        if (!OpenGlHelper.isFramebufferEnabled()) return;

        int width = Minecraft.getMinecraft().displayWidth;
        int height = Minecraft.getMinecraft().displayHeight;

        if (blurOutputHorizontal == null) {
            blurOutputHorizontal = new Framebuffer(width, height, false);
            blurOutputHorizontal.setFramebufferFilter(GL11.GL_NEAREST);
        }
        if (blurOutputVertical == null) {
            blurOutputVertical = new Framebuffer(width, height, false);
            blurOutputVertical.setFramebufferFilter(GL11.GL_NEAREST);
        }

        if (blurOutputHorizontal == null || blurOutputVertical == null) {
            return;
        }
        if (blurOutputHorizontal.framebufferWidth != width || blurOutputHorizontal.framebufferHeight != height) {
            blurOutputHorizontal.createBindFramebuffer(width, height);
            blurShaderHorizontal.setProjectionMatrix(createProjectionMatrix(width, height));
            Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
        }
        if (blurOutputVertical.framebufferWidth != width || blurOutputVertical.framebufferHeight != height) {
            blurOutputVertical.createBindFramebuffer(width, height);
            blurShaderVertical.setProjectionMatrix(createProjectionMatrix(width, height));
            Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
        }

        if (blurShaderHorizontal == null) {
            try {
                blurShaderHorizontal = new Shader(Minecraft.getMinecraft().getResourceManager(), "menu_blur",
                        blurOutputVertical, blurOutputHorizontal);
                blurShaderHorizontal.getShaderManager().getShaderUniform("BlurDir").set(1, 0);
                blurShaderHorizontal.setProjectionMatrix(createProjectionMatrix(width, height));
            } catch (Exception e) {
            }
        }
        if (blurShaderVertical == null) {
            try {
                blurShaderVertical = new Shader(Minecraft.getMinecraft().getResourceManager(), "menu_blur",
                        blurOutputHorizontal, blurOutputVertical);
                blurShaderVertical.getShaderManager().getShaderUniform("BlurDir").set(0, 1);
                blurShaderVertical.setProjectionMatrix(createProjectionMatrix(width, height));
            } catch (Exception e) {
            }
        }
        if (blurShaderHorizontal != null && blurShaderVertical != null) {
            if (blurShaderHorizontal.getShaderManager().getShaderUniform("Radius") == null) {
                return;
            }
            if (15 != lastBgBlurFactor) {
                blurShaderHorizontal.getShaderManager().getShaderUniform("Radius").set(blurRadius);
                blurShaderVertical.getShaderManager().getShaderUniform("Radius").set(blurRadius);
                lastBgBlurFactor = 15;
            }
            GL11.glPushMatrix();
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, Minecraft.getMinecraft().getFramebuffer().framebufferObject);
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, blurOutputVertical.framebufferObject);
            GL30.glBlitFramebuffer(0, 0, width, height,
                    0, 0, width, height,
                    GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);

            blurShaderHorizontal.loadShader(0);
            blurShaderVertical.loadShader(0);
            GlStateManager.enableDepth();
            GL11.glPopMatrix();

            Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
        }
        GL11.glPopMatrix();
    }

    public void renderBlurredBackground(int screenWidth, int screenHeight, int x, int y, int blurWidth, int blurHeight) {
        if (Minecraft.getDebugFPS() <= 30) {
            Simplex.getInstance().getRenderManager().renderElement(new RectangleElement(x, y, blurWidth, blurHeight, new Color(0, 0, 0, 50)));
            return;
        }
        GL11.glPushMatrix();
        if (!OpenGlHelper.isFramebufferEnabled()) return;

        blurBackground();

        float uMin = x / (float) screenWidth;
        float uMax = (x + blurWidth) / (float) screenWidth;
        float vMin = (screenHeight - y) / (float) screenHeight;
        float vMax = (screenHeight - y - blurHeight) / (float) screenHeight;

        GlStateManager.depthMask(false);
        //Simplex.getInstance().getRenderManager().renderElement(new RectangleElement(x, y, blurWidth, blurHeight, 5f, new Color(fogColour)));
        int fogColor = 0;
        Gui.drawRect(x, y, x + blurWidth, y + blurHeight, fogColor);
        blurOutputVertical.bindFramebufferTexture();
        GlStateManager.color(1f, 1f, 1f, 1f);
        drawTexturedRect(x, y, blurWidth, blurHeight, uMin, uMax, vMin, vMax);
        blurOutputVertical.unbindFramebufferTexture();
        GlStateManager.depthMask(true);
        GL11.glPopMatrix();
    }

    public void renderRoundedBlurredBackground(int screenWidth, int screenHeight, int x, int y, int blurWidth, int blurHeight, float radius) {
        if (Minecraft.getDebugFPS() <= 30) {
            Simplex.getInstance().getRenderManager().renderElement(new RectangleElement(x, y, blurWidth, blurHeight, new Color(0, 0, 0, 50)));
            return;
        }
        GL11.glPushMatrix();
        if (!OpenGlHelper.isFramebufferEnabled()) return;

        blurBackground();

        float uMin = x / (float) screenWidth;
        float uMax = (x + blurWidth) / (float) screenWidth;
        float vMin = (screenHeight - y) / (float) screenHeight;
        float vMax = (screenHeight - y - blurHeight) / (float) screenHeight;

        GlStateManager.depthMask(false);
        int fogColor = 0;
        //Gui.drawRect(x, y, x + blurWidth, y + blurHeight, fogColor);
        Simplex.getInstance().getRenderManager().renderElement(new RectangleElement(x + 1, y + 1, blurWidth - 2, blurHeight - 2, radius / 2, new Color(fogColor)));
        blurOutputVertical.bindFramebufferTexture();
        GlStateManager.color(1f, 1f, 1f, 1f);
        drawRoundedTexturedRect(x, y, x + blurWidth, y + blurHeight, uMin, uMax, vMin, vMax, radius, new Color(255, 255, 255).getRGB());
        blurOutputVertical.unbindFramebufferTexture();
        GlStateManager.depthMask(true);
        GL11.glPopMatrix();
    }

    public void drawTexturedRect(float x, float y, float width, float height, float uMin, float uMax, float vMin, float vMax) {
        drawTexturedRect(x, y, width, height, uMin, uMax, vMin, vMax, GL11.GL_NEAREST);
    }

    public void drawTexturedRect(float x, float y, float width, float height, float uMin, float uMax, float vMin, float vMax, int filter) {
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);

        drawTexturedRectNoBlend(x, y, width, height, uMin, uMax, vMin, vMax, filter);

        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }

    public void drawTexturedRectNoBlend(float x, float y, float width, float height, float uMin, float uMax, float vMin, float vMax, int filter) {
        GL11.glPushMatrix();
        GlStateManager.enableTexture2D();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer
                .pos(x, y + height, 0.0D)
                .tex(uMin, vMax).endVertex();
        worldrenderer
                .pos(x + width, y + height, 0.0D)
                .tex(uMax, vMax).endVertex();
        worldrenderer
                .pos(x + width, y, 0.0D)
                .tex(uMax, vMin).endVertex();
        worldrenderer
                .pos(x, y, 0.0D)
                .tex(uMin, vMin).endVertex();
        tessellator.draw();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GlStateManager.disableTexture2D();
        GL11.glPopMatrix();
    }

    public void drawRoundedTexturedRect(float x, float y, float x1, float y1, float uMin, float uMax, float vMin, float vMax, float radius, int color) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        x *= 2.0D;
        y *= 2.0D;
        x1 *= 2.0D;
        y1 *= 2.0D;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        setColor(color);
        GL11.glBegin(GL11.GL_POLYGON);
        int i;
        for (i = 0; i <= 90; i += 3) {

            double vx = x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D;
            double vy = y + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D;

            GL11.glTexCoord2d(uMin, vMin);
            GL11.glVertex2d(vx, vy);
        }
        for (i = 90; i <= 180; i += 3) {

            double vx = x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D;
            double vy = y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D;

            GL11.glTexCoord2d(uMax, vMin);
            GL11.glVertex2d(vx, vy);
        }
        for (i = 0; i <= 90; i += 3) {

            double vx = x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius;
            double vy = y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius;

            GL11.glTexCoord2d(uMax, vMax);
            GL11.glVertex2d(vx, vy);
        }
        for (i = 90; i <= 180; i += 3) {
            double vx = x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius;
            double vy = y + radius + Math.cos(i * Math.PI / 180.0D) * radius;

            GL11.glTexCoord2d(uMin, vMax);
            GL11.glVertex2d(vx, vy);
        }
        GL11.glEnd();
        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (i = 0; i <= 90; i += 3) {

            double vx = x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D;
            double vy = y + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D;

            GL11.glTexCoord2d(uMin, vMax);
            GL11.glVertex2d(vx, vy);
        }
        for (i = 90; i <= 180; i += 3) {

            double vx = x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D;
            double vy = y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D;

            GL11.glTexCoord2d(uMax, vMax);
            GL11.glVertex2d(vx, vy);
        }
        for (i = 0; i <= 90; i += 3) {

            double vx = x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius;
            double vy = y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius;

            GL11.glTexCoord2d(uMax, vMin);
            GL11.glVertex2d(vx, vy);
        }
        for (i = 90; i <= 180; i += 3) {
            double vx = x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius;
            double vy = y + radius + Math.cos(i * Math.PI / 180.0D) * radius;

            GL11.glTexCoord2d(vMin, vMin);
            GL11.glVertex2d(vx, vy);
        }
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glScaled(2.0D, 2.0D, 2.0D);
        GL11.glPopAttrib();
        GL11.glLineWidth(1);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    private Matrix4f createProjectionMatrix(int width, int height) {
        Matrix4f projMatrix = new Matrix4f();
        projMatrix.setIdentity();
        projMatrix.m00 = 2.0F / (float) width;
        projMatrix.m11 = 2.0F / (float) (-height);
        projMatrix.m22 = -0.0020001999F;
        projMatrix.m33 = 1.0F;
        projMatrix.m03 = -1.0F;
        projMatrix.m13 = 1.0F;
        projMatrix.m23 = -1.0001999F;
        return projMatrix;
    }

    private void setColor(int color) {
        float a = (color >> 24 & 0xFF) / 255.0F;
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        GL11.glColor4f(r, g, b, a);
    }

}
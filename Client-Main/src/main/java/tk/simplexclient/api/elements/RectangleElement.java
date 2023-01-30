package tk.simplexclient.api.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import tk.simplexclient.api.RenderObject;
import tk.simplexclient.api.shader.Shader;
import tk.simplexclient.api.shader.ShaderLoader;

import java.awt.*;

public class RectangleElement extends RenderObject {

    private Shader shader;

    private final ScaledResolution resolution;

    private final boolean fastRect;

    public RectangleElement(float x, float y, float width, float height, float radius, Color color) {
        super(x, y, width, height, color, radius);
        this.fastRect = false;
        this.resolution = new ScaledResolution(Minecraft.getMinecraft());
    }

    public RectangleElement(float x, float y, float width, float height, float radius, Color color, boolean fastRect) {
        super(x, y, width, height, color, radius);
        this.fastRect = fastRect;
        this.resolution = new ScaledResolution(Minecraft.getMinecraft());
    }

    public RectangleElement(float x, float y, float width, float height, Color color) {
        super(x, y, width, height, color, -0.1F);
        this.fastRect = false;
        this.resolution = new ScaledResolution(Minecraft.getMinecraft());
    }

    @Override
    public void render() {
        float radius = (float) args[0];

        if (radius != -0.1F) {
            if (!fastRect) {
                if (shader == null) shader = ShaderLoader.loadShader("rounded", "rounded", "vertex");
                GlStateManager.pushMatrix();
                GlStateManager.resetColor();
                GlStateManager.enableBlend();
                GlStateManager.disableCull();
                GlStateManager.enableColorMaterial();
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                shader.use();

                float scaledX = x * resolution.getScaleFactor();
                float scaledY = y * resolution.getScaleFactor();
                float scaledWidth = width * resolution.getScaleFactor();
                float scaledHeight = height * resolution.getScaleFactor();
                float scaledRadius = radius *  resolution.getScaleFactor();
                shader.getUniform("loc").set(scaledX, (Minecraft.getMinecraft().displayHeight - scaledHeight) - scaledY);
                shader.getUniform("size").set(scaledWidth, scaledHeight);
                shader.getUniform("radius").set(scaledRadius);
                shader.getUniform("color").set(color);


                GL11.glBegin(GL11.GL_QUADS);
                {
                    GL11.glTexCoord2f(0, 0);
                    GL11.glVertex2f(x - 1, y - 1);
                    GL11.glTexCoord2f(0, 1);
                    GL11.glVertex2f(x - 1, y + height + 1);
                    GL11.glTexCoord2f(1, 1);
                    GL11.glVertex2f(x + width + 1, y + height + 1);
                    GL11.glTexCoord2f(1, 0);
                    GL11.glVertex2f(x + width + 1, y - 1);
                }
                GL11.glEnd();

                shader.unload();
                GlStateManager.disableBlend();
                GlStateManager.enableCull();
                GlStateManager.disableColorMaterial();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.popMatrix();
            } else {
                GlStateManager.pushMatrix();
                fastRoundedRectangle(x, y, x + width, y + height, radius);
                GlStateManager.popMatrix();
            }
        } else {
            drawRect(x, y, x + width, y + height, color);
        }
    }

    private void drawRect(float left, float top, float right, float bottom, final Color color) {
        GlStateManager.pushMatrix();
        if (left < right) {
            final float i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            final float j = top;
            top = bottom;
            bottom = j;
        }
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        setColor(color.getRGB());
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos((double) left, (double) bottom, 0.0).endVertex();
        worldrenderer.pos((double) right, (double) bottom, 0.0).endVertex();
        worldrenderer.pos((double) right, (double) top, 0.0).endVertex();
        worldrenderer.pos((double) left, (double) top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void fastRoundedRectangle(float x, float y, float width, float height, float radius) {
        final int i = 18;
        final float f1 = 90.0f / i;

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.enableColorMaterial();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glEnable(2848);
        GL11.glBegin(5);
        GL11.glVertex2f(x + radius, y);
        GL11.glVertex2f(x + radius, height);
        GL11.glVertex2f(width - radius, y);
        GL11.glVertex2f(width - radius, height);
        GL11.glEnd();
        GL11.glBegin(5);
        GL11.glVertex2f(x, y + radius);
        GL11.glVertex2f(x + radius, y + radius);
        GL11.glVertex2f(x, height - radius);
        GL11.glVertex2f(x + radius, height - radius);
        GL11.glEnd();
        GL11.glBegin(5);
        GL11.glVertex2f(width, y + radius);
        GL11.glVertex2f(width - radius, y + radius);
        GL11.glVertex2f(width, height - radius);
        GL11.glVertex2f(width - radius, height - radius);
        GL11.glEnd();
        GL11.glBegin(6);
        float f2 = width - radius;
        float f3 = y + radius;
        GL11.glVertex2f(f2, f3);
        for (int j = 0; j <= i; ++j) {
            final float f4 = j * f1;
            GL11.glVertex2f((float) (f2 + radius * Math.cos(Math.toRadians(f4))), (float) (f3 - radius * Math.sin(Math.toRadians(f4))));
        }
        GL11.glEnd();
        GL11.glBegin(6);
        f2 = x + radius;
        f3 = y + radius;
        GL11.glVertex2f(f2, f3);
        for (int j = 0; j <= i; ++j) {
            final float f4 = j * f1;
            GL11.glVertex2f((float) (f2 - radius * Math.cos(Math.toRadians(f4))), (float) (f3 - radius * Math.sin(Math.toRadians(f4))));
        }
        GL11.glEnd();
        GL11.glBegin(6);
        f2 = x + radius;
        f3 = height - radius;
        GL11.glVertex2f(f2, f3);
        for (int j = 0; j <= i; ++j) {
            final float f4 = j * f1;
            GL11.glVertex2f((float) (f2 - radius * Math.cos(Math.toRadians(f4))), (float) (f3 + radius * Math.sin(Math.toRadians(f4))));
        }
        GL11.glEnd();
        GL11.glBegin(6);
        f2 = width - radius;
        f3 = height - radius;
        GL11.glVertex2f(f2, f3);
        for (int j = 0; j <= i; ++j) {
            final float f4 = j * f1;
            GL11.glVertex2f((float) (f2 + radius * Math.cos(Math.toRadians(f4))), (float) (f3 + radius * Math.sin(Math.toRadians(f4))));
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.disableColorMaterial();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private void setColor(int color) {
        float a = (color >> 24 & 0xFF) / 255.0F;
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        GL11.glColor4f(r, g, b, a);
    }

}

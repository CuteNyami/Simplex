package tk.simplexclient.api.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import tk.simplexclient.api.RenderObject;

import java.awt.*;

public class TextureElement extends RenderObject {

    public TextureElement(ResourceLocation texture, float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight) {
        super(x, y, width, height, Color.WHITE, u, v, textureWidth, textureHeight, texture);
    }

    @Override
    public void render() {
        float u = (float) args[0];
        float v = (float) args[1];
        float textureWidth = (float) args[2];
        float textureHeight = (float) args[3];
        
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Minecraft.getMinecraft().getTextureManager().bindTexture((ResourceLocation) args[4]);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0D).tex( (u * f),  ((v +  height) * f1)).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).tex( ((u +  width) * f),  ((v +  height) * f1)).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).tex( ((u +  width) * f),  (v * f1)).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex( (u * f),  (v * f1)).endVertex();
        tessellator.draw();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}

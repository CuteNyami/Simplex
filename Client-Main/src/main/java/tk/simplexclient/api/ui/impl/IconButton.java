package tk.simplexclient.api.ui.impl;

import net.minecraft.util.ResourceLocation;
import tk.simplexclient.api.elements.RectangleElement;
import tk.simplexclient.api.elements.TextureElement;

import java.awt.*;

public abstract class IconButton extends UIButton {

    protected final ResourceLocation iconTexture;

    protected final float size;

    public IconButton(float x, float y, float width, float height, ResourceLocation iconTexture) {
        super(null, x, y, width, height);
        this.iconTexture = iconTexture;
        this.size = 10.0F;
    }

    public IconButton(float x, float y, float width, float height, float size, ResourceLocation iconTexture) {
        super(null, x, y, width, height);
        this.iconTexture = iconTexture;
        this.size = size;
    }

    @Override
    public void renderElement(int mouseX, int mouseY, float partialTicks) {
        Color color = isHovered(mouseX, mouseY) ? new Color(51, 51, 51, 145) : new Color(38, 38, 38, 145);

        renderer.renderElement(new RectangleElement(x, y, width, height, 5.0F, color));
        renderer.renderElement(new TextureElement(iconTexture, x + (width / 2 - (size / 2)), y + (height / 2 - (size / 2)), 0, 0, size, size, size, size));
    }

    @Override
    public abstract void onClick();
}
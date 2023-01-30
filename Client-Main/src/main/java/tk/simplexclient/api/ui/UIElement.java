package tk.simplexclient.api.ui;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import tk.simplexclient.Simplex;
import tk.simplexclient.api.IRenderHandler;

public abstract class UIElement {

    @Getter
    protected final float x, y, width, height;

    protected final IRenderHandler renderer;

    protected Minecraft mc;

    public UIElement(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.renderer = Simplex.getInstance().getRenderManager();
        this.mc = Minecraft.getMinecraft();
    }

    public void renderElement(int mouseX, int mouseY, float partialTicks) {}

    public boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    public abstract void onClick();

    public void onMouseButton(int mouseX, int mouseY, int mouseButton) {
        if (isHovered(mouseX, mouseY) && mouseButton == 0) {
            playPressSound(this.mc.getSoundHandler());
            onClick();
        }
    }

    public void playPressSound(SoundHandler soundHandler) {
        soundHandler.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
    }
}

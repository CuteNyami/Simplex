package tk.simplexclient.mixin.impl;

import com.google.common.base.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import tk.simplexclient.Simplex;

import java.awt.*;
import java.util.List;

@Mixin(GuiOverlayDebug.class)
public abstract class MixinGuiOverlayDebug {

    @Shadow public abstract List<String> call();

    @Shadow @Final public FontRenderer fontRenderer;

    @Shadow public abstract List<String> getDebugInfoRight();

    /**
     * @author Mojang
     * @reason smooth font
     */
    @Overwrite
    public void renderDebugInfoLeft() {
        List<String> list = this.call();

        for(int i = 0; i < list.size(); ++i) {
            String s = list.get(i);
            if (!Strings.isNullOrEmpty(s)) {
                int fontHeight = Simplex.getInstance().getFontRenderer().FONT_HEIGHT;
                int fontWidth = (int) Simplex.getInstance().getFontRenderer().getWidth(s);
                int y = 2 + fontHeight * i;
                Gui.drawRect(1, y - 1, 2 + fontWidth + 1, y + fontHeight - 1, -1873784752);
                Simplex.getInstance().getFontRenderer().drawString(s, 2, y - 1, new Color(224, 224, 224));
                //this.fontRenderer.drawString(s, 2, i1, 14737632);
            }
        }

    }

    /**
     * @author Mojang
     * @reason smooth font
     */
    @Overwrite
    public void renderDebugInfoRight(ScaledResolution scaledRes) {
        List<String> list = this.getDebugInfoRight();

        for(int i = 0; i < list.size(); ++i) {
            String s = list.get(i);
            if (!Strings.isNullOrEmpty(s)) {
                int fontHeight = Simplex.getInstance().getFontRenderer().FONT_HEIGHT;
                int fontWidth = (int) Simplex.getInstance().getFontRenderer().getWidth(s);
                int x = scaledRes.getScaledWidth() - 2 - fontWidth;
                int y = 2 + fontHeight * i;
                Gui.drawRect(x - 1, y - 1, x + fontWidth + 1, y + fontHeight - 1, -1873784752);
                Simplex.getInstance().getFontRenderer().drawString(s, x, y - 1, new Color(224, 224, 224));
                //this.fontRenderer.drawString(s, x, y, 14737632);
            }
        }

    }

}

package tk.simplexclient.mixin.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.simplexclient.Simplex;
import tk.simplexclient.api.elements.RectangleElement;

import java.awt.*;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {

    @Shadow @Final public Minecraft mc;

    @Inject(method = "renderGameOverlay", at = @At("TAIL"))
    public void renderGameOverlay(float partialTicks, CallbackInfo ci) {
        if (!this.mc.gameSettings.showDebugInfo) {
            ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
            Simplex.getInstance().getBlurUtils().renderBlurredBackground(resolution.getScaledWidth(), resolution.getScaledHeight(), 1, 2, 80, 30);
            Simplex.getInstance().getRenderManager().renderElement(new RectangleElement(1, 2, 80, 30, new Color(0, 0, 0, 50)));
            Simplex.getInstance().getFontRenderer().drawString("Simplex Client", 4, 3, Color.WHITE);
            Simplex.getInstance().getFontRenderer().drawString("Dev Build 026122022", 4, 12, Color.WHITE);
            Simplex.getInstance().getFontRenderer().drawString("Mod Menu Test", 4, 21, Color.WHITE);
        }
    }
}

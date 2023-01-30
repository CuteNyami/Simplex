package tk.simplexclient.ui.impl;

import tk.simplexclient.Simplex;
import tk.simplexclient.api.IRenderHandler;
import tk.simplexclient.api.elements.RectangleElement;
import tk.simplexclient.ui.UIScreen;
import tk.simplexclient.utils.GaussianBlurUtils;

import java.awt.*;

public class ModMenu extends UIScreen {

    private IRenderHandler renderer;

    @Override
    public void initGui() {
        renderer = Simplex.getInstance().getRenderManager();
        /*
        if (Minecraft.getDebugFPS() > 20) {
            mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/menu_blur.json"));
        }
         */
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        /*
        if (Minecraft.getDebugFPS() < 20) {
            mc.entityRenderer.stopUseShader();
        }
         */

        //Simplex.getInstance().getBlurUtils().renderRoundedBlurredBackground(width, height, width / 2 - 110, height / 2 - 75, 50, 152, 5f);
        //renderer.renderElement(new RectangleElement(width / 2 - 110, height / 2 - 75, 50, 152, new Color(0, 0, 0, 60)));
        //renderer.renderElement(new RectangleElement(width / 2 - 60, height / 2 - 75, 180, 152, new Color(26, 28, 33, 255)));

        renderer.renderElement(new RectangleElement(0, 0, 100, 100, 30f, Color.WHITE));

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onGuiClosed() {
        //mc.entityRenderer.stopUseShader();
    }
}

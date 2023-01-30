package tk.simplexclient.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import tk.simplexclient.api.ui.UIElement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UIScreen extends GuiScreen {

    private final List<UIElement> elements = new ArrayList<>();

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        elements.forEach(element -> element.renderElement(mouseX, mouseY, partialTicks));
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        elements.forEach(element -> element.onMouseButton(mouseX, mouseY, mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void setWorldAndResolution(Minecraft p_setWorldAndResolution_1_, int p_setWorldAndResolution_2_, int p_setWorldAndResolution_3_) {
        elements.clear();
        super.setWorldAndResolution(p_setWorldAndResolution_1_, p_setWorldAndResolution_2_, p_setWorldAndResolution_3_);
    }
}

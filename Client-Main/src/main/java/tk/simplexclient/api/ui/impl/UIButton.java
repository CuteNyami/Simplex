package tk.simplexclient.api.ui.impl;

import tk.simplexclient.Simplex;
import tk.simplexclient.api.elements.RectangleElement;
import tk.simplexclient.api.ui.UIElement;

import java.awt.*;

public abstract class UIButton extends UIElement {

    private final String text;

    public UIButton(String text, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.text = text;
    }

    @Override
    public void renderElement(int mouseX, int mouseY, float partialTicks) {
        Color color = isHovered(mouseX, mouseY) ? new Color(51, 51, 51, 145) : new Color(38, 38, 38, 145);
        //Color color = isHovered(mouseX, mouseY) ? new Color(255, 255, 255, 145) : new Color(203, 203, 203, 145); // light mode

        renderer.renderElement(new RectangleElement(x, y, width, height, 5.0F, color, false));

        Simplex.getInstance().getFontRenderer().drawCenteredString(text, (int) x + width / 2, (int) y + (height - (8 + 15 - 12)) / 2, new Color(206, 206, 206));
    }
    @Override
    public abstract void onClick();

}

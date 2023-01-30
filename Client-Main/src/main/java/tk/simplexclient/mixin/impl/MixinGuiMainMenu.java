package tk.simplexclient.mixin.impl;

import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.optifine.CustomPanorama;
import net.optifine.CustomPanoramaProperties;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import tk.simplexclient.Simplex;
import tk.simplexclient.api.elements.RectangleElement;
import tk.simplexclient.api.elements.TextureElement;
import tk.simplexclient.api.ui.UIElement;
import tk.simplexclient.api.ui.impl.IconButton;
import tk.simplexclient.api.ui.impl.UIButton;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mixin(GuiMainMenu.class)
public abstract class MixinGuiMainMenu extends GuiScreen {

    @Shadow public abstract void renderSkybox(int p_73971_1_, int p_73971_2_, float p_73971_3_);

    @Shadow public DynamicTexture viewportTexture;
    @Shadow public ResourceLocation backgroundTexture;

    @Shadow public abstract void drawPanorama(int p_73970_1_, int p_73970_2_, float p_73970_3_);

    private final List<UIElement> elements = new ArrayList<>();

    /**
     * Overwrites the default Minecraft initGui method!
     * All UI Elements will be registered here.
     *
     * @author Nyami
     * @reason Custom GUI
     */
    @Overwrite
    public void initGui() {
        this.viewportTexture = new DynamicTexture(256, 256);
        this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);

        elements.clear();
        elements.add(new UIButton("Singleplayer", (float) width / 2 - 50, (float) height / 2 - 17, 100, 15) {
            @Override
            public void onClick() {
                mc.displayGuiScreen(new GuiSelectWorld(mc.currentScreen));
            }
        });
        elements.add(new UIButton("Multiplayer", (float) width / 2 - 50, (float) height / 2 + 3, 100, 15) {
            @Override
            public void onClick() {
                mc.displayGuiScreen(new GuiMultiplayer(mc.currentScreen));
            }
        });
        elements.add(new IconButton((float) width / 2 - 70, (float) height / 2 + 3, 15, 15, 10, new ResourceLocation("simplex/icons/options_dark_mode.png")) {
            @Override
            public void onClick() {
                mc.displayGuiScreen(new GuiOptions(mc.currentScreen, mc.gameSettings));
            }
        });
    }

    /**
     * Overwrites the default Minecraft Main Menu
     *
     * @param mouseX       The x coordinate that is inside the window
     * @param mouseY       The y coordinate that is inside the window
     * @param partialTicks The partialTicks of the game
     * @author Nyami
     * @reason Custom Main menu
     */
    @Overwrite
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        GL11.glPushMatrix();
        GlStateManager.disableAlpha();
        this.renderSkybox(mouseX, mouseY, partialTicks);
        GlStateManager.enableAlpha();
        int overlay1Top = -2130706433;
        int overlay1Bottom = 16777215;
        int overlay2Top = 0;
        int overlay2Bottom = Integer.MIN_VALUE;

        CustomPanoramaProperties cpp = CustomPanorama.getCustomPanoramaProperties();

        if (cpp != null) {
            overlay1Top = cpp.getOverlay1Top();
            overlay1Bottom = cpp.getOverlay1Bottom();
            overlay2Top = cpp.getOverlay2Top();
            overlay2Bottom = cpp.getOverlay2Bottom();
        }

        if (overlay1Top != 0 || overlay1Bottom != 0) {
            this.drawGradientRect(0, 0, this.width, this.height, overlay1Top, overlay1Bottom);
        }

        if (overlay2Top != 0 || overlay2Bottom != 0) {
            this.drawGradientRect(0, 0, this.width, this.height, overlay2Top, overlay2Bottom);
        }
        GL11.glPopMatrix();

        GL11.glPushMatrix();

        Simplex.getInstance()
                .getRenderManager()
                .renderElement(new RectangleElement(0, 0, width, height, new Color(0, 0, 0, 100))); // light mode: new Color(0, 0, 0, 150)

        GL11.glPopMatrix();

        setColor(Color.WHITE.getRGB());

        Simplex.getInstance()
                .getRenderManager()
                .renderElement(new TextureElement(new ResourceLocation("simplex/title.png"), width / 2 - 64, (float) height / 2 - 75, 0, 0, 128, 64, 128, 64));
        /*
        Simplex.getInstance()
                .getRenderManager()
                .renderElement(new RectangleElement((float) width / 2 - 0.5F, 0, 1, height, Color.WHITE));

        Simplex.getInstance()
                .getRenderManager()
                .renderElement(new RectangleElement(0, (float) height / 2 - 0.5F, width, 1, Color.WHITE));
         */

        elements.forEach(element -> element.renderElement(mouseX, mouseY, partialTicks));
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        elements.forEach(element -> element.onMouseButton(mouseX, mouseY, mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void setColor(int color) {
        float a = (color >> 24 & 0xFF) / 255.0F;
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        GL11.glColor4f(r, g, b, a);
    }
}

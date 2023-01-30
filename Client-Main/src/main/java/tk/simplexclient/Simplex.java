package tk.simplexclient;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;
import tk.simplexclient.api.IRenderHandler;
import tk.simplexclient.api.elements.font.FontRenderer;
import tk.simplexclient.api.elements.font.TrueTypeFonts;
import tk.simplexclient.event.EventManager;
import tk.simplexclient.event.impl.EventUpdate;
import tk.simplexclient.listener.TickListener;
import tk.simplexclient.render.RenderManager;

import java.util.Arrays;

public class Simplex {

    @Getter
    private static Simplex instance;

    @Getter
    private IRenderHandler renderManager;

    @Getter
    private FontRenderer fontRenderer;

    @Getter
    private BlurUtils blurUtils;

    public static final KeyBinding MOD_MENU = new KeyBinding("Opens the Mod Menu", Keyboard.KEY_RSHIFT, "SimplexClient");

    public void init() {
        instance = this;
        renderManager = new RenderManager();
    }

    public void start() {
        fontRenderer = new FontRenderer(TrueTypeFonts.PRODUCT_SANS.getPath(), 15.0F);
        blurUtils = new BlurUtils(15.0F);

        registerKeyBind(MOD_MENU);

        EventManager.register(new TickListener());
        EventManager.register(new EventUpdate());
    }

    public static void registerKeyBind(KeyBinding key) {
        Minecraft.getMinecraft().gameSettings.keyBindings = ArrayUtils.add(Minecraft.getMinecraft().gameSettings.keyBindings, key);
    }

    public static void unregisterKeyBind(KeyBinding key) {
        if (Arrays.asList(Minecraft.getMinecraft().gameSettings.keyBindings).contains(key)) {
            Minecraft.getMinecraft().gameSettings.keyBindings = ArrayUtils.remove(Minecraft.getMinecraft().gameSettings.keyBindings, Arrays.asList(Minecraft.getMinecraft().gameSettings.keyBindings).indexOf(key));
        }
    }

}

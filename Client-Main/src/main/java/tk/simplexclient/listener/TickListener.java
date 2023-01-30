package tk.simplexclient.listener;

import net.minecraft.client.Minecraft;
import tk.simplexclient.Simplex;
import tk.simplexclient.event.EventTarget;
import tk.simplexclient.event.impl.ClientTickEvent;
import tk.simplexclient.ui.impl.ModMenu;

public class TickListener {

    private final Minecraft mc = Minecraft.getMinecraft();

    @EventTarget
    public void onTick(ClientTickEvent event) {
        if (Simplex.MOD_MENU.isPressed()) {
            mc.displayGuiScreen(new ModMenu());
        }
    }
}

package tk.simplexclient.mixin.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.simplexclient.Simplex;
import tk.simplexclient.access.AccessMinecraft;
import tk.simplexclient.event.impl.ClientTickEvent;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements AccessMinecraft {

    @Shadow
    private Timer timer = new Timer(20.0F);
    private final ClientTickEvent event = new ClientTickEvent();

    @Inject(method = "startGame", at = @At("HEAD"))
    public void initClient(CallbackInfo ci) {
        new Simplex().init();
    }

    @Inject(method = "startGame", at = @At("RETURN"))
    public void startClient(CallbackInfo ci) {
        Simplex.getInstance().start();
    }

    @Inject(method = "runTick", at = @At("TAIL"))
    public void runTick(CallbackInfo ci) {
        event.call();
    }

    @Override
    public Timer getTimer() {
        return timer;
    }
}

package tk.simplexclient.mixin.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.optifine.Lagometer;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import tk.simplexclient.MotionBlur;

import java.util.LinkedList;
import java.util.List;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

    @Shadow
    public ShaderGroup theShaderGroup;

    @Shadow
    public Minecraft mc;

    @Shadow
    public boolean useShader;

    @Shadow
    public abstract void frameInit();

    @Shadow
    public long prevFrameTime;

    @Shadow
    public float smoothCamYaw;

    @Shadow
    public float smoothCamPitch;

    @Shadow
    public float smoothCamPartialTicks;

    @Shadow
    public float smoothCamFilterX;

    @Shadow
    public float smoothCamFilterY;

    @Shadow
    public static boolean anaglyphEnable;

    @Shadow
    public abstract void renderWorld(float partialTicks, long finishTimeNano);

    @Shadow
    public long renderEndNanoTime;

    @Shadow
    public abstract void setupOverlayRendering();

    @Shadow
    public abstract void frameFinish();

    @Shadow
    public abstract void waitForServerThread();

    /**
     * @author edited version of Mojangs thing
     * @reason Motion Blur
     */
    @Overwrite
    public void updateShaderGroupSize(int width, int height) {
        if (OpenGlHelper.shadersSupported) {
            if (this.theShaderGroup != null) {
                this.theShaderGroup.createBindFramebuffers(width, height);
            }

            if (MotionBlur.motionBlurEnabled) {
                ShaderGroup motionBlur = MotionBlur.getShader();
                if (motionBlur != null) {
                    motionBlur.createBindFramebuffers(width, height);
                }
            }

            this.mc.renderGlobal.createBindEntityOutlineFbs(width, height);
        }
    }

    /*
    @Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;renderEntityOutlineFramebuffer()V"))
    public void updateCameraAndRender(float partialTicks, long nanoTime, CallbackInfo ci) {
        List<ShaderGroup> shaders = new LinkedList<>();

        if (this.theShaderGroup != null && this.useShader) {
            shaders.add(theShaderGroup);
        }

        ShaderGroup motionBlur = MotionBlur.getShader();
        if (motionBlur != null)
            shaders.add(motionBlur);
    }

    @Redirect(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/shader/ShaderGroup;loadShaderGroup(F)V"))
    public void loadShaderGroup(ShaderGroup instance, float p_loadShaderGroup_1_) {
        ShaderGroup motionBlur = MotionBlur.getShader();
        if (motionBlur != null) {
            motionBlur.loadShaderGroup(p_loadShaderGroup_1_);
            System.out.println("test");
        }
    }

     */

    /**
     * @author edited version of Mojangs thing
     * @reason Motion Blur
     */
    @Overwrite
    public void updateCameraAndRender(float p_updateCameraAndRender_1_, long p_updateCameraAndRender_2_) {
        boolean lvt_4_1_ = Display.isActive();
        this.frameInit();
        if (!lvt_4_1_ && this.mc.gameSettings.pauseOnLostFocus && (!this.mc.gameSettings.touchscreen || !Mouse.isButtonDown(1))) {
            if (Minecraft.getSystemTime() - this.prevFrameTime > 500L) {
                this.mc.displayInGameMenu();
            }
        } else {
            this.prevFrameTime = Minecraft.getSystemTime();
        }

        this.mc.mcProfiler.startSection("mouse");
        if (lvt_4_1_ && Minecraft.isRunningOnMac && this.mc.inGameHasFocus && !Mouse.isInsideWindow()) {
            Mouse.setGrabbed(false);
            Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
            Mouse.setGrabbed(true);
        }

        int lvt_9_2_;
        if (this.mc.inGameHasFocus && lvt_4_1_) {
            this.mc.mouseHelper.mouseXYChange();
            float lvt_5_1_ = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float lvt_6_1_ = lvt_5_1_ * lvt_5_1_ * lvt_5_1_ * 8.0F;
            float lvt_7_1_ = (float) this.mc.mouseHelper.deltaX * lvt_6_1_;
            float lvt_8_1_ = (float) this.mc.mouseHelper.deltaY * lvt_6_1_;
            lvt_9_2_ = 1;
            if (this.mc.gameSettings.invertMouse) {
                lvt_9_2_ = -1;
            }

            if (this.mc.gameSettings.smoothCamera) {
                this.smoothCamYaw += lvt_7_1_;
                this.smoothCamPitch += lvt_8_1_;
                float lvt_10_1_ = p_updateCameraAndRender_1_ - this.smoothCamPartialTicks;
                this.smoothCamPartialTicks = p_updateCameraAndRender_1_;
                lvt_7_1_ = this.smoothCamFilterX * lvt_10_1_;
                lvt_8_1_ = this.smoothCamFilterY * lvt_10_1_;
                this.mc.thePlayer.setAngles(lvt_7_1_, lvt_8_1_ * (float) lvt_9_2_);
            } else {
                this.smoothCamYaw = 0.0F;
                this.smoothCamPitch = 0.0F;
                this.mc.thePlayer.setAngles(lvt_7_1_, lvt_8_1_ * (float) lvt_9_2_);
            }
        }

        this.mc.mcProfiler.endSection();
        if (!this.mc.skipRenderWorld) {
            anaglyphEnable = this.mc.gameSettings.anaglyph;
            final ScaledResolution lvt_5_2_ = new ScaledResolution(this.mc);
            int lvt_6_2_ = lvt_5_2_.getScaledWidth();
            int lvt_7_2_ = lvt_5_2_.getScaledHeight();
            final int lvt_8_2_ = Mouse.getX() * lvt_6_2_ / this.mc.displayWidth;
            lvt_9_2_ = lvt_7_2_ - Mouse.getY() * lvt_7_2_ / this.mc.displayHeight - 1;
            int lvt_10_2_ = this.mc.gameSettings.limitFramerate;
            if (this.mc.theWorld != null) {
                this.mc.mcProfiler.startSection("level");
                int lvt_11_1_ = Math.min(Minecraft.getDebugFPS(), lvt_10_2_);
                lvt_11_1_ = Math.max(lvt_11_1_, 60);
                long lvt_12_1_ = System.nanoTime() - p_updateCameraAndRender_2_;
                long lvt_14_1_ = Math.max((long) (1000000000 / lvt_11_1_ / 4) - lvt_12_1_, 0L);
                this.renderWorld(p_updateCameraAndRender_1_, System.nanoTime() + lvt_14_1_);
                if (OpenGlHelper.shadersSupported) {
                    this.mc.renderGlobal.renderEntityOutlineFramebuffer();

                    List<ShaderGroup> shaders = new LinkedList<>();

                    if (this.theShaderGroup != null && this.useShader) {
                        shaders.add(theShaderGroup);
                    }

                    if (MotionBlur.motionBlurEnabled) {
                        ShaderGroup motionBlur = MotionBlur.getShader();
                        if (motionBlur != null) {
                            shaders.add(motionBlur);
                        }
                    }

                    for (ShaderGroup shader : shaders) {
                        GlStateManager.matrixMode(5890);
                        GlStateManager.pushMatrix();
                        GlStateManager.loadIdentity();
                        shader.loadShaderGroup(p_updateCameraAndRender_1_);
                        GlStateManager.popMatrix();
                    }

                    this.mc.getFramebuffer().bindFramebuffer(true);
                }

                this.renderEndNanoTime = System.nanoTime();
                this.mc.mcProfiler.endStartSection("gui");
                if (!this.mc.gameSettings.hideGUI || this.mc.currentScreen != null) {
                    GlStateManager.alphaFunc(516, 0.1F);
                    this.mc.ingameGUI.renderGameOverlay(p_updateCameraAndRender_1_);
                    if (this.mc.gameSettings.ofShowFps && !this.mc.gameSettings.showDebugInfo) {
                        //Config.drawFps();
                    }

                    if (this.mc.gameSettings.showDebugInfo) {
                        Lagometer.showLagometer(lvt_5_2_);
                    }
                }

                this.mc.mcProfiler.endSection();
            } else {
                GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
                GlStateManager.matrixMode(5889);
                GlStateManager.loadIdentity();
                GlStateManager.matrixMode(5888);
                GlStateManager.loadIdentity();
                this.setupOverlayRendering();
                this.renderEndNanoTime = System.nanoTime();
                TileEntityRendererDispatcher.instance.renderEngine = this.mc.getTextureManager();
                TileEntityRendererDispatcher.instance.fontRenderer = this.mc.fontRendererObj;
            }

            if (this.mc.currentScreen != null) {
                GlStateManager.clear(256);

                try {
                    this.mc.currentScreen.drawScreen(lvt_8_2_, lvt_9_2_, p_updateCameraAndRender_1_);
                } catch (Throwable var16) {
                    CrashReport lvt_12_2_ = CrashReport.makeCrashReport(var16, "Rendering screen");
                    CrashReportCategory lvt_13_1_ = lvt_12_2_.makeCategory("Screen render details");
                    lvt_13_1_.addCrashSectionCallable("Screen name", () -> mc.currentScreen.getClass().getCanonicalName());
                    int finalLvt_9_2_ = lvt_9_2_;
                    lvt_13_1_.addCrashSectionCallable("Mouse location", () -> String.format("Scaled: (%d, %d). Absolute: (%d, %d)", lvt_8_2_, finalLvt_9_2_, Mouse.getX(), Mouse.getY()));
                    lvt_13_1_.addCrashSectionCallable("Screen size", () -> String.format("Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d", lvt_5_2_.getScaledWidth(), lvt_5_2_.getScaledHeight(), mc.displayWidth, mc.displayHeight, lvt_5_2_.getScaleFactor()));
                    throw new ReportedException(lvt_12_2_);
                }
            }

        }
    }

}

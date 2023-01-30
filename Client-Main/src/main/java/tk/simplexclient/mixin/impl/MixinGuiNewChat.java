package tk.simplexclient.mixin.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import tk.simplexclient.BlurUtils;
import tk.simplexclient.Simplex;
import tk.simplexclient.api.elements.RectangleElement;

import java.awt.*;
import java.util.List;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat {

    @Shadow
    @Final
    private Minecraft mc;

    @Shadow
    public abstract int getLineCount();

    @Shadow
    @Final
    private List<ChatLine> field_146253_i;

    @Shadow
    public abstract boolean getChatOpen();

    @Shadow
    public abstract float getChatScale();

    @Shadow
    public abstract int getChatWidth();

    @Shadow
    private int scrollPos;

    @Shadow
    private boolean isScrolled;

    /**
     * @author mojang
     * @reason smooth font
     */
    @Overwrite
    public void drawChat(int p_drawChat_1_) {
        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) { // I don't know why but intellij gave me an error here, but it compiles.
            int lvt_2_1_ = this.getLineCount();
            boolean lvt_3_1_ = false;
            int lvt_4_1_ = 0;
            int lvt_5_1_ = this.field_146253_i.size();
            float lvt_6_1_ = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;
            if (lvt_5_1_ > 0) {
                if (this.getChatOpen()) {
                    lvt_3_1_ = true;
                }

                float lvt_7_1_ = this.getChatScale();
                int lvt_8_1_ = MathHelper.ceiling_float_int((float) this.getChatWidth() / lvt_7_1_);
                GlStateManager.pushMatrix();
                GlStateManager.translate(2.0F, 20.0F, 0.0F);
                GlStateManager.scale(lvt_7_1_, lvt_7_1_, 1.0F);

                int lvt_9_1_;
                int lvt_11_1_;
                int lvt_14_1_;
                int x;
                for (lvt_9_1_ = 0; lvt_9_1_ + this.scrollPos < this.field_146253_i.size() && lvt_9_1_ < lvt_2_1_; ++lvt_9_1_) {
                    ChatLine lvt_10_1_ = this.field_146253_i.get(lvt_9_1_ + this.scrollPos);
                    if (lvt_10_1_ != null) {
                        lvt_11_1_ = p_drawChat_1_ - lvt_10_1_.getUpdatedCounter();
                        if (lvt_11_1_ < 200 || lvt_3_1_) {
                            double lvt_12_1_ = (double) lvt_11_1_ / 200.0;
                            lvt_12_1_ = 1.0 - lvt_12_1_;
                            lvt_12_1_ *= 10.0;
                            lvt_12_1_ = MathHelper.clamp_double(lvt_12_1_, 0.0, 1.0);
                            lvt_12_1_ *= lvt_12_1_;
                            lvt_14_1_ = (int) (255.0 * lvt_12_1_);
                            if (lvt_3_1_) {
                                lvt_14_1_ = 255;
                            }

                            lvt_14_1_ = (int) ((float) lvt_14_1_ * lvt_6_1_);
                            ++lvt_4_1_;
                            if (lvt_14_1_ > 3) {
                                x = 0;
                                int y = -lvt_9_1_ * 9;
                                //Gui.drawRect(x, y - 9, x + lvt_8_1_ + 4, y, lvt_14_1_ / 2 << 24);

                                Gui.drawRect(x, y - 9, x + lvt_8_1_ + 4, y, new Color(0, 0, 0, 50).getRGB());
                                String text = lvt_10_1_.getChatComponent().getFormattedText();
                                GlStateManager.enableBlend();
                                //this.mc.fontRendererObj.drawStringWithShadow(text, (float)x, (float)(y - 8), 16777215 + (lvt_14_1_ << 24));
                                Simplex.getInstance().getFontRenderer().drawString(text, (float) x + 1, (float) (y - 9), Color.WHITE);
                                GlStateManager.disableAlpha();
                                GlStateManager.disableBlend();
                            }
                        }
                    }
                }

                if (lvt_3_1_) {
                    lvt_9_1_ = this.mc.fontRendererObj.FONT_HEIGHT;
                    GlStateManager.translate(-3.0F, 0.0F, 0.0F);
                    int lvt_10_2_ = lvt_5_1_ * lvt_9_1_ + lvt_5_1_;
                    lvt_11_1_ = lvt_4_1_ * lvt_9_1_ + lvt_4_1_;
                    int lvt_12_2_ = this.scrollPos * lvt_11_1_ / lvt_5_1_;
                    int lvt_13_1_ = lvt_11_1_ * lvt_11_1_ / lvt_10_2_;
                    if (lvt_10_2_ != lvt_11_1_) {
                        lvt_14_1_ = lvt_12_2_ > 0 ? 170 : 96;
                        x = this.isScrolled ? 13382451 : 3355562;
                        Gui.drawRect(0, -lvt_12_2_, 2, -lvt_12_2_ - lvt_13_1_, x + (lvt_14_1_ << 24));
                        Gui.drawRect(2, -lvt_12_2_, 1, -lvt_12_2_ - lvt_13_1_, 13421772 + (lvt_14_1_ << 24));
                    }
                }

                GlStateManager.popMatrix();
            }
        }
    }

}

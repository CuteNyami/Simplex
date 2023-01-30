package tk.simplexclient;

import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

/**
 * yes yes another thing that TheKodeToad made!!!!
 * well I made some changes to it
 *
 * @author TheKodeToad
 */
public class MotionBlur {

    private static final ResourceLocation LOCATION = new ResourceLocation("minecraft:shaders/post/motion_blur.json");
    private static final Logger LOGGER = LogManager.getLogger();
    private static ShaderGroup shader;
    private static float shaderBlur;
    private static boolean error;

    public static boolean motionBlurEnabled = false;

    /**
     * Gets the current blur level.
     *
     * @return The blur level, ranging from <code>0F</code> to <code>1F</code>.
     */
    public static float getBlurFactor() {
        return 0.7F;
    }

    public static ShaderGroup getShader() {
        if (error)
            return null;

        if (shader == null) {
            Minecraft mc = Minecraft.getMinecraft();

            shaderBlur = Float.NaN;

            try {
                shader = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), LOCATION);
                shader.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
            } catch (JsonSyntaxException | IOException error) {
                LOGGER.error("Could not load motion blur shader", error);
                MotionBlur.error = true;
                return null;
            }
        }

        if (shaderBlur != getBlurFactor()) {
            try {
                Field listShadersField = ShaderGroup.class.getDeclaredField("listShaders");
                listShadersField.setAccessible(true);
                List<Shader> listShaders = (List<Shader>) listShadersField.get(shader);

                listShaders.forEach((shader) -> {
                    ShaderUniform blendFactorUniform = shader.getShaderManager().getShaderUniform("BlurFactor");

                    if (blendFactorUniform != null)
                        blendFactorUniform.set(getBlurFactor());
                });

            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            shaderBlur = getBlurFactor();
        }

        return shader;
    }
}

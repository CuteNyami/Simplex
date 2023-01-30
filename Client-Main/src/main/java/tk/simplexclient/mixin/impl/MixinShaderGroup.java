package tk.simplexclient.mixin.impl;

import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import tk.simplexclient.access.AccessShaderGroup;

import java.util.List;

@Mixin(ShaderGroup.class)
public abstract class MixinShaderGroup implements AccessShaderGroup {

    @Override
    @Accessor
    public abstract List<Shader> getListShaders();
}

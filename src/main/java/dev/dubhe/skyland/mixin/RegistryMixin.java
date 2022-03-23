package dev.dubhe.skyland.mixin;

import dev.dubhe.skyland.generator.SkyLandChunkGenerator;
import dev.dubhe.skyland.generator.SkyLandGeneratorType;
import net.minecraft.core.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Registry.class)
public class RegistryMixin {

    @Inject(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;checkRegistry(Lnet/minecraft/core/Registry;)V"))
    private static void register(CallbackInfo ci) {
        Registry.register(Registry.CHUNK_GENERATOR, SkyLandGeneratorType.ID, SkyLandChunkGenerator.CODEC);
    }
}

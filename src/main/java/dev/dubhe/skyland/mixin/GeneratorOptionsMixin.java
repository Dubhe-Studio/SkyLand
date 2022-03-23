package dev.dubhe.skyland.mixin;

import dev.dubhe.skyland.generator.SkyLandGeneratorSettings;
import dev.dubhe.skyland.generator.SkyLandGeneratorType;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WorldGenSettings.class)
public class GeneratorOptionsMixin {

    @Inject(method = "create", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void addSkyLandGeneratorOptions(RegistryAccess registryManager, DedicatedServerProperties.WorldGenProperties worldGenProperties, CallbackInfoReturnable<WorldGenSettings> cir, long seed, Registry<DimensionType> registry, Registry<Biome> registry2, Registry<StructureSet> registry3, Registry<LevelStem> registry4) {
        if (SkyLandGeneratorType.ID.equals(worldGenProperties.levelType())) {
            cir.setReturnValue(new WorldGenSettings(seed, worldGenProperties.generateStructures(), false, SkyLandGeneratorSettings.getSkyLandRegistry(registryManager, seed)));
        }
    }
}

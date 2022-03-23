package dev.dubhe.skyland.generator;

import dev.dubhe.skyland.Skyland;
import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@OnlyIn(Dist.CLIENT)
public class SkyLandGeneratorType extends WorldPreset {
    public static final String ID = "skyland";

    public SkyLandGeneratorType(String translationKeySuffix) {
        super(Component.nullToEmpty(translationKeySuffix));
    }

    @Override
    protected ChunkGenerator generator(RegistryAccess registryManager, long seed) {
        return new SkyLandChunkGenerator(
                registryManager.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY),
                registryManager.registryOrThrow(Registry.NOISE_REGISTRY),
                MultiNoiseBiomeSource.Preset.OVERWORLD.biomeSource(registryManager.registryOrThrow(Registry.BIOME_REGISTRY), true),
                seed,
                registryManager.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY).getOrCreateHolder(NoiseGeneratorSettings.OVERWORLD)
        );
    }

    @Override
    public WorldGenSettings create(RegistryAccess drm, long seed, boolean generateStructures, boolean bonusChest) {
        return new WorldGenSettings(seed, generateStructures, bonusChest, SkyLandGeneratorSettings.getSkyLandRegistry(drm, seed));
    }

    public static void register() {
        PRESETS.add(new SkyLandGeneratorType(ID));
    }

}

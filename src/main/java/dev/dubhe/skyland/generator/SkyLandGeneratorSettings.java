package dev.dubhe.skyland.generator;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource.Preset;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class SkyLandGeneratorSettings {
    public static MappedRegistry<LevelStem> getSkyLandRegistry(RegistryAccess drm, long seed) {
        MappedRegistry<LevelStem> dimensionOptionsRegistry = new MappedRegistry<>(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental(), null);

        Registry<DimensionType> dimensionTypeRegistry = drm.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
        Registry<StructureSet> structureSets = drm.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY);
        Registry<NormalNoise.NoiseParameters> noiseParameters = drm.registryOrThrow(Registry.NOISE_REGISTRY);
        Registry<Biome> biomes = drm.registryOrThrow(Registry.BIOME_REGISTRY);
        Registry<NoiseGeneratorSettings> chunkGeneratorSettings = drm.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);

        dimensionOptionsRegistry.register(LevelStem.OVERWORLD, new LevelStem(
                dimensionTypeRegistry.getOrCreateHolder(DimensionType.OVERWORLD_LOCATION),
                new SkyLandChunkGenerator(
                        structureSets,
                        noiseParameters,
                        Preset.OVERWORLD.biomeSource(biomes, true),
                        seed,
                        chunkGeneratorSettings.getOrCreateHolder(NoiseGeneratorSettings.OVERWORLD)
                )
        ), Lifecycle.stable());

        dimensionOptionsRegistry.register(LevelStem.NETHER, new LevelStem(
                dimensionTypeRegistry.getOrCreateHolder(DimensionType.NETHER_LOCATION),
                new SkyLandChunkGenerator(
                        structureSets,
                        noiseParameters,
                        Preset.NETHER.biomeSource(biomes, true),
                        seed,
                        chunkGeneratorSettings.getOrCreateHolder(NoiseGeneratorSettings.NETHER)
                )
        ), Lifecycle.stable());

        dimensionOptionsRegistry.register(LevelStem.END, new LevelStem(
                dimensionTypeRegistry.getOrCreateHolder(DimensionType.END_LOCATION),
                new SkyLandChunkGenerator(
                        structureSets,
                        noiseParameters,
                        new TheEndBiomeSource(biomes, seed),
                        seed,
                        chunkGeneratorSettings.getOrCreateHolder(NoiseGeneratorSettings.END)
                )
        ), Lifecycle.stable());
        return dimensionOptionsRegistry;

    }
}

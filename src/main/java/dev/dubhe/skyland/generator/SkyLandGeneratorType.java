package dev.dubhe.skyland.generator;

import net.minecraft.client.world.GeneratorType;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

public class SkyLandGeneratorType {
    public static final GeneratorType SKYLAND = new GeneratorType("skyland") {

        @Override
        protected ChunkGenerator getChunkGenerator(DynamicRegistryManager registryManager, long seed) {
            return new SkyLandChunkGenerator(
                    registryManager.get(Registry.STRUCTURE_SET_WORLDGEN),
                    registryManager.get(Registry.NOISE_WORLDGEN),
                    MultiNoiseBiomeSource.Preset.OVERWORLD.getBiomeSource(registryManager.get(Registry.BIOME_KEY), true),
                    seed,
                    registryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY).getOrCreateEntry(ChunkGeneratorSettings.OVERWORLD)
            );
        }
    };
}

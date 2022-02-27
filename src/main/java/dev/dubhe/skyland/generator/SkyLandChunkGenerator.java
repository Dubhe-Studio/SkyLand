package dev.dubhe.skyland.generator;

import net.minecraft.class_7059;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

public class SkyLandChunkGenerator extends NoiseChunkGenerator {
    public SkyLandChunkGenerator(Registry<class_7059> noiseRegistry, Registry<DoublePerlinNoiseSampler.NoiseParameters> structuresRegistry, BiomeSource biomeSource, long seed, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(noiseRegistry, structuresRegistry, biomeSource, seed, settings);
    }
}

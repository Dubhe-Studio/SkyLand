package dev.dubhe.skyland.generator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class SkyLandChunkGenerator extends NoiseBasedChunkGenerator {
    private final Registry<NormalNoise.NoiseParameters> structuresRegistry;
    private final long seed;

    public static final Codec<SkyLandChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryOps.retrieveRegistry(Registry.STRUCTURE_SET_REGISTRY).forGetter(generator -> generator.structureSets),
            RegistryOps.retrieveRegistry(Registry.NOISE_REGISTRY).forGetter(generator -> generator.structuresRegistry),
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.runtimeBiomeSource),
            Codec.LONG.fieldOf("seed").stable().forGetter(generator -> generator.seed),
            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(generator -> generator.settings)
    ).apply(instance, instance.stable(SkyLandChunkGenerator::new)));

    public SkyLandChunkGenerator(Registry<StructureSet> noiseRegistry, Registry<NormalNoise.NoiseParameters> structuresRegistry, BiomeSource biomeSource, long seed, Holder<NoiseGeneratorSettings> settings) {
        super(noiseRegistry, structuresRegistry, biomeSource, seed, settings);
        this.seed = seed;
        this.structuresRegistry = structuresRegistry;
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long seed) {
        return new SkyLandChunkGenerator(this.structureSets, this.structuresRegistry, this.runtimeBiomeSource.withSeed(seed), seed, this.settings);
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor world) {
        return new NoiseColumn(world.getMinBuildHeight(), new BlockState[0]);
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, StructureFeatureManager structureAccessor, ChunkAccess chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel world, ChunkAccess chunk, StructureFeatureManager structureAccessor) {
        ChunkPos chunkPos = chunk.getPos();
        BlockPos pos = new BlockPos(chunkPos.getMinBlockX(), chunk.getMinBuildHeight(), chunkPos.getMinBlockZ());

        structureAccessor.startsForFeature(
                SectionPos.of(pos),
                world.registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY).get(new ResourceLocation("minecraft:stronghold"))
        ).forEach(structureStart -> {
            for (StructurePiece piece : structureStart.getPieces()) {
                if (piece.getType() == StructurePieceType.STRONGHOLD_PORTAL_ROOM) {
                    if (piece.isCloseToChunk(chunkPos, 0)) {
                        WorldgenRandom random = new WorldgenRandom(new XoroshiroRandomSource(RandomSupport.seedUniquifier()));
                        random.setLargeFeatureSeed(seed, chunkPos.x, chunkPos.z);
                    }
                }
            }
        });
    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureFeatureManager structures, ChunkAccess chunk) {
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {
    }

    @Override
    public void applyCarvers(WorldGenRegion chunkRegion, long seed, BiomeManager biomeAccess, StructureFeatureManager structureAccessor, ChunkAccess chunk, GenerationStep.Carving generationStep) {
    }

}

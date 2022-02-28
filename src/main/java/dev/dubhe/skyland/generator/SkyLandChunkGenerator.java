package dev.dubhe.skyland.generator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.class_7059;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.RandomSeed;
import net.minecraft.world.gen.random.Xoroshiro128PlusPlusRandom;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SkyLandChunkGenerator extends NoiseChunkGenerator {
    private final long seed;
    private final Registry<DoublePerlinNoiseSampler.NoiseParameters> structuresRegistry;

    public static final Codec<SkyLandChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryOps.createRegistryCodec(Registry.STRUCTURE_SET_WORLDGEN).forGetter(generator -> generator.field_37053),
            RegistryOps.createRegistryCodec(Registry.NOISE_WORLDGEN).forGetter(generator -> generator.structuresRegistry),
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.biomeSource),
            Codec.LONG.fieldOf("seed").stable().forGetter(generator -> generator.seed),
            ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(generator -> generator.settings)
    ).apply(instance, instance.stable(SkyLandChunkGenerator::new)));

    public SkyLandChunkGenerator(Registry<class_7059> noiseRegistry, Registry<DoublePerlinNoiseSampler.NoiseParameters> structuresRegistry, BiomeSource biomeSource, long seed, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(noiseRegistry, structuresRegistry, biomeSource, seed, settings);
        this.seed = seed;
        this.structuresRegistry = structuresRegistry;
    }


    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long seed) {
        return new SkyLandChunkGenerator(this.field_37053, this.structuresRegistry, this.biomeSource.withSeed(seed), seed, this.settings);
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
        return new VerticalBlockSample(world.getBottomY(), new BlockState[0]);
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        ChunkPos chunkPos = chunk.getPos();
        BlockPos pos = new BlockPos(chunkPos.getStartX(), chunk.getBottomY(), chunkPos.getStartZ());
        int startX = chunkPos.getStartX();
        int startZ = chunkPos.getStartZ();
        BlockBox chunkBoundary = new BlockBox(startX, chunk.getBottomY(), startZ, startX + 15, chunk.getTopY(), startZ + 15);

        structureAccessor
                .getStructureStarts(
                        ChunkSectionPos.from(pos),
                        Registry.STRUCTURE_FEATURE.get(new Identifier("minecraft:stronghold")))
                .forEach(
                        (structureStart) -> {
                            for (StructurePiece piece : structureStart.getChildren()) {
                                if (piece.getType() == StructurePieceType.STRONGHOLD_PORTAL_ROOM) {
                                    BlockPos portalPos =
                                            new BlockPos(
                                                    piece.getBoundingBox().getMinX(),
                                                    piece.getBoundingBox().getMinY(),
                                                    piece.getBoundingBox().getMinZ());
                                    if (piece.intersectsChunk(chunkPos, 0)) {
                                        ChunkRandom random = new ChunkRandom(new Xoroshiro128PlusPlusRandom(RandomSeed.getSeed()));
                                        random.setCarverSeed(seed, chunkPos.x, chunkPos.z);
                                        generateStrongholdPortalInBox(
                                                world, portalPos, random, Objects.requireNonNull(piece.getFacing()), chunkBoundary);
                                    }
                                }
                            }
                        });
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, Chunk chunk) {
        if (region.getDimension().isNatural()) {
            BlockPos spawn =
                    new BlockPos(
                            region.getLevelProperties().getSpawnX(),
                            region.getLevelProperties().getSpawnY(),
                            region.getLevelProperties().getSpawnZ());
            if (chunk.getPos().getStartX() <= spawn.getX()
                    && spawn.getX() <= chunk.getPos().getEndX()
                    && chunk.getPos().getStartZ() <= spawn.getZ()
                    && spawn.getZ() <= chunk.getPos().getEndZ()) {
                generateSpawnPlatformInBox(
                        region,
                        spawn,
                        new BlockBox(
                                chunk.getPos().getStartX(),
                                chunk.getBottomY(),
                                chunk.getPos().getStartZ(),
                                chunk.getPos().getStartX() + 15,
                                chunk.getTopY(),
                                chunk.getPos().getStartZ() + 15));
            }
        }
    }

    @Override
    public void populateEntities(ChunkRegion region) {
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver generationStep) {
    }

    private static void generateStrongholdPortalInBox(
            ServerWorldAccess world,
            BlockPos northWestCornerPos,
            Random random,
            Direction facing,
            BlockBox box) {

        // TODO switch this to pattern matching when it is no longer a preview
        BlockPos portalCenterPosition;
        switch (facing) {
            case EAST:
                portalCenterPosition = northWestCornerPos.add(10, 3, 5);
                break;
            case SOUTH:
                portalCenterPosition = northWestCornerPos.add(5, 3, 10);
                break;
            case WEST:
            case NORTH:
            default:
                portalCenterPosition = northWestCornerPos.add(5, 3, 5);
        }

        boolean completePortal = true;

        for (Direction d : Direction.values()) {
            if (d.getAxis().isHorizontal()) {
                completePortal &=
                        generateStrongholdPortalFrameSideInBox(world, portalCenterPosition, random, d, box);
            }
        }

        if (completePortal) {
            BlockState endPortal = Blocks.END_PORTAL.getDefaultState();
            fillRelativeBlockInBox(world, endPortal, portalCenterPosition, -1, 0, 1, -1, 0, 1, box);
        }
        BlockPos spawnerPos = portalCenterPosition.subtract(facing.getVector().multiply(4));

        if (box.contains(spawnerPos)) {
            world.setBlockState(spawnerPos, Blocks.SPAWNER.getDefaultState(), 2);
            BlockEntity spawnerEntity = world.getBlockEntity(spawnerPos);
            if (spawnerEntity instanceof MobSpawnerBlockEntity) {
                ((MobSpawnerBlockEntity) spawnerEntity).getLogic().setEntityId(EntityType.SILVERFISH);
            }
        }
    }

    private static boolean generateStrongholdPortalFrameSideInBox(
            ServerWorldAccess world, BlockPos centerPos, Random random, Direction side, BlockBox box) {
        BlockState portalFrameBlock =
                Blocks.END_PORTAL_FRAME
                        .getDefaultState()
                        .with(EndPortalFrameBlock.FACING, side.getOpposite());

        boolean completePortal = true;

        for (int i = -1; i < 2; ++i) {
            boolean hasEye = random.nextFloat() > 0.9F;
            completePortal &= hasEye;

            portalFrameBlock = portalFrameBlock.with(EndPortalFrameBlock.EYE, hasEye);
            Vec3i offset =
                    side.getVector().multiply(2).add(side.rotateClockwise(Direction.Axis.Y).getVector().multiply(i));
            placeRelativeBlockInBox(
                    world, portalFrameBlock, centerPos, offset.getX(), 0, offset.getZ(), box);
        }

        return completePortal;
    }

    protected static void generateSpawnPlatformInBox(ServerWorldAccess world, BlockPos spawnpoint, BlockBox box) {
        BlockState leavesBlockState = Blocks.OAK_LEAVES.getDefaultState().with(LeavesBlock.DISTANCE, 1);
        fillRelativeBlockInBox(
                world, Blocks.GRASS_BLOCK.getDefaultState(), spawnpoint, -2, -1, -7, 2, -1, 2, box);
        placeRelativeBlockInBox(world, Blocks.MYCELIUM.getDefaultState(), spawnpoint, 0, -1, 0, box);
        placeRelativeBlockInBox(
                world, Blocks.CRIMSON_NYLIUM.getDefaultState(), spawnpoint, -1, -1, 1, box);
        placeRelativeBlockInBox(
                world, Blocks.WARPED_NYLIUM.getDefaultState(), spawnpoint, 1, -1, 1, box);
        placeRelativeBlockInBox(world, Blocks.DIRT.getDefaultState(), spawnpoint, 0, -1, -5, box);
        fillRelativeBlockInBox(world, leavesBlockState, spawnpoint, -2, 3, -7, 2, 4, -3, box);
        fillRelativeBlockInBox(world, leavesBlockState, spawnpoint, -1, 5, -5, 1, 6, -5, box);
        fillRelativeBlockInBox(world, leavesBlockState, spawnpoint, 0, 5, -6, 0, 6, -4, box);
        placeRelativeBlockInBox(world, leavesBlockState, spawnpoint, -1, 5, -4, box);
        placeRelativeBlockInBox(world, leavesBlockState, spawnpoint, -1, 5, -6, box);
        placeRelativeBlockInBox(world, Blocks.AIR.getDefaultState(), spawnpoint, -2, 4, -3, box);
        placeRelativeBlockInBox(world, Blocks.AIR.getDefaultState(), spawnpoint, -2, 3, -7, box);
        fillRelativeBlockInBox(
                world, Blocks.OAK_LOG.getDefaultState(), spawnpoint, 0, 0, -5, 0, 5, -5, box);
    }

    protected static void placeRelativeBlockInBox(
            WorldAccess world,
            BlockState block,
            BlockPos referencePos,
            int x,
            int y,
            int z,
            BlockBox box) {
        BlockPos blockPos =
                new BlockPos(referencePos.getX() + x, referencePos.getY() + y, referencePos.getZ() + z);
        if (box.contains(blockPos)) {
            world.setBlockState(blockPos, block, 2);
        }
    }

    protected static void fillRelativeBlockInBox(
            WorldAccess world,
            BlockState block,
            BlockPos referencePos,
            int startX,
            int startY,
            int startZ,
            int endX,
            int endY,
            int endZ,
            BlockBox box) {
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    placeRelativeBlockInBox(world, block, referencePos, x, y, z, box);
                }
            }
        }
    }

}

package dev.dubhe.skyland.mixin;

import dev.dubhe.skyland.Skyland;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NaturalSpawner.class)
public class SpawnHelperMixin {
    @Shadow
    public static void spawnEntitiesInChunk(MobCategory group, ServerLevel world, ChunkAccess chunk, BlockPos pos, NaturalSpawner.SpawnPredicate checker, NaturalSpawner.AfterSpawnCallback runner) {}

    @Inject(
            method = "spawnCategoryForChunk",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void spawnEntities(MobCategory group, ServerLevel world, LevelChunk chunk, NaturalSpawner.SpawnPredicate checker, NaturalSpawner.AfterSpawnCallback runner, CallbackInfo ci) {
        if (world.getGameRules().getBoolean(Skyland.LC)) {
            for (int i = chunk.getMinBuildHeight(); i < chunk.getMaxBuildHeight(); i += 16) {
                LevelChunkSection chunkSection = chunk.getSections()[chunk.getSectionIndex(i)];
                if (chunkSection != null && !chunkSection.hasOnlyAir()) {
                    BlockPos blockPos = getRandomPosInChunk(world, chunk).offset(0, i, 0);
                    spawnEntitiesInChunk(group, world, chunk, blockPos, checker, runner);
                }
            }
            ci.cancel();
        }
    }

    private static BlockPos getRandomPosInChunk(Level world, LevelChunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int x = chunkPos.getMinBlockX() + world.random.nextInt(16);
        int z = chunkPos.getMinBlockZ() + world.random.nextInt(16);
        int y = world.random.nextInt(16) + 1;
        return new BlockPos(x, y, z);
    }
}

package dev.dubhe.skyland.mixin;

import dev.dubhe.skyland.generator.SkyLandChunkGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndDragonFight.class)
public class EnderDragonFightMixin {

    @Final
    @Shadow
    private ServerLevel world;

    @Shadow
    private BlockPos exitPortalLocation;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setPortal(ServerLevel world, long gatewaysSeed, CompoundTag nbt, CallbackInfo ci) {
        if (this.world.getChunkSource().getGenerator() instanceof SkyLandChunkGenerator && this.exitPortalLocation == null)
            this.exitPortalLocation = new BlockPos(0, 60, 0);
    }

}

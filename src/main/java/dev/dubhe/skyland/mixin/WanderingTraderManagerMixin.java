package dev.dubhe.skyland.mixin;

import dev.dubhe.skyland.Skyland;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;

@Mixin(WanderingTraderSpawner.class)
public class WanderingTraderManagerMixin {
    @Redirect(method = "spawn", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
    private int spawnX10(Random random, int value, ServerLevel world) {
        return world.getGameRules().getBoolean(Skyland.CHIEFTAIN) ? 0 : random.nextInt(value);
    }
}

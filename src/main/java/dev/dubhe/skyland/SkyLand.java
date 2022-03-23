package dev.dubhe.skyland;

import dev.dubhe.skyland.generator.SkyLandGeneratorType;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;

@Mod(Skyland.ID)
public class Skyland {
    public static final String ID = "skyland";
    public static final GameRules.Key<GameRules.BooleanValue> LC = GameRules.register("qnmdLC", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
    public static final GameRules.Key<GameRules.BooleanValue> CHIEFTAIN = GameRules.register("chieftainMode", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));

    @OnlyIn(Dist.CLIENT)
    public Skyland() {
        SkyLandGeneratorType.register();
    }
}

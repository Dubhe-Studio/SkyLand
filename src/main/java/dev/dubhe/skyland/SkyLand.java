package dev.dubhe.skyland;

import net.fabricmc.api.ModInitializer;
import net.minecraft.world.GameRules;

public class SkyLand implements ModInitializer {
    public static final GameRules.Key<GameRules.BooleanRule> LC = GameRules.register("qnmdLC", GameRules.Category.MOBS, GameRules.BooleanRule.create(false));
    public static final GameRules.Key<GameRules.BooleanRule> CHIEFTAIN = GameRules.register("chieftainMode", GameRules.Category.MOBS, GameRules.BooleanRule.create(false));

    @Override
    public void onInitialize() {
    }
}

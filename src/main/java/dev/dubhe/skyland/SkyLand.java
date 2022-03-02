package dev.dubhe.skyland;

import dev.dubhe.skyland.generator.SkyLandChunkGenerator;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.registry.Registry;

public class SkyLand implements ModInitializer {
    public static final String ID = "skyland";

    @Override
    public void onInitialize() {
        Registry.register(Registry.CHUNK_GENERATOR, ID, SkyLandChunkGenerator.CODEC);
    }
}

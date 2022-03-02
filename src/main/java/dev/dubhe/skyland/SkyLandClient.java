package dev.dubhe.skyland;

import dev.dubhe.skyland.generator.SkyLandGeneratorType;
import net.fabricmc.api.ClientModInitializer;


public class SkyLandClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        SkyLandGeneratorType.register();
    }
}

package com.fishguy129.singleportal;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SinglePortalMod implements ModInitializer {
    public static final String MOD_ID = "singleportal";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModBlocks.register();
        ModBlockEntities.register(); // <-- must happen at init
        LOGGER.info("SinglePortal loaded.");
    }
}

package com.fishguy129.singleportal;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModBlockEntities {

    public static BlockEntityType<SinglePortalBlockEntity> SINGLE_PORTAL_BE;

    public static void register() {
        SINGLE_PORTAL_BE = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(SinglePortalMod.MOD_ID, "single_portal"),
                BlockEntityType.Builder.create(SinglePortalBlockEntity::new, ModBlocks.SINGLE_PORTAL).build()
        );
    }

    private ModBlockEntities() {}
}

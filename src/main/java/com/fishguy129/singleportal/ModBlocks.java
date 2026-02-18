package com.fishguy129.singleportal;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModBlocks {
    public static final Block SINGLE_PORTAL = new SinglePortalBlock();

    public static void register() {
        Registry.register(Registries.BLOCK, id("single_portal"), SINGLE_PORTAL);
        Registry.register(Registries.ITEM, id("single_portal"),
                new BlockItem(SINGLE_PORTAL, new Item.Settings()));

        // easy testing in creative
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
                .register(entries -> entries.add(SINGLE_PORTAL));
    }

    private static Identifier id(String path) {
        return Identifier.of(SinglePortalMod.MOD_ID, path);
    }

    private ModBlocks() {}
}

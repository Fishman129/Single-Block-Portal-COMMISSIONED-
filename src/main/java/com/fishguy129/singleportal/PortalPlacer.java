package com.fishguy129.singleportal;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class PortalPlacer {

    public static BlockPos placePortalOrPlatform(ServerWorld world, int x, int z) {
        // Choose a reasonable search band per dimension
        int minY;
        int maxY;

        RegistryKey<World> key = world.getRegistryKey();
        if (key == World.NETHER) {
            minY = clamp(32, world.getBottomY() + 4, world.getTopY() - 10);
            maxY = clamp(96, world.getBottomY() + 4, world.getTopY() - 10);
        } else {
            // Overworld + others
            minY = clamp(world.getBottomY() + 4, world.getBottomY() + 4, world.getTopY() - 10);
            maxY = clamp(world.getTopY() - 20, world.getBottomY() + 4, world.getTopY() - 10);
        }

        BlockPos found = findSafeSpot(world, x, z, minY, maxY);
        if (found != null) {
            world.setBlockState(found, ModBlocks.SINGLE_PORTAL.getDefaultState());
            return found;
        }

        // Fallback: End-style 3x3 obsidian platform at a stable Y (64-ish)
        int platformY = clamp(64, world.getBottomY() + 4, world.getTopY() - 10);
        BlockPos center = new BlockPos(x, platformY, z);

        buildPlatform(world, center.down());
        world.setBlockState(center, ModBlocks.SINGLE_PORTAL.getDefaultState());
        return center;
    }

    /**
     * Returns a portal position that is "1 block above air":
     * - portalPos is air
     * - portalPos.down() is air (the “air gap”)
     * - portalPos.down(2) is solid (ground)
     * - portalPos.up() is air (headroom)
     */
    private static BlockPos findSafeSpot(ServerWorld world, int x, int z, int minY, int maxY) {
        for (int y = maxY; y >= minY; y--) {
            BlockPos portalPos = new BlockPos(x, y, z);

            if (!isAir(world, portalPos)) continue;
            if (!isAir(world, portalPos.up())) continue;

            BlockPos airGap = portalPos.down();
            BlockPos ground = portalPos.down(2);

            if (!isAir(world, airGap)) continue;

            BlockState groundState = world.getBlockState(ground);
            if (groundState.isAir()) continue;
            if (groundState.getFluidState() != null && !groundState.getFluidState().isEmpty()) continue; // avoid placing over fluids
            if (groundState.getBlock() == Blocks.LAVA) continue;

            // Avoid placing inside dangerous fluid columns
            if (!world.getBlockState(portalPos).getFluidState().isEmpty()) continue;
            if (!world.getBlockState(airGap).getFluidState().isEmpty()) continue;

            return portalPos;
        }
        return null;
    }

    private static void buildPlatform(ServerWorld world, BlockPos center) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos p = center.add(dx, 0, dz);
                world.setBlockState(p, Blocks.OBSIDIAN.getDefaultState());
                world.setBlockState(p.up(), Blocks.AIR.getDefaultState());
                world.setBlockState(p.up(2), Blocks.AIR.getDefaultState());
            }
        }
    }

    private static boolean isAir(ServerWorld world, BlockPos pos) {
        return world.getBlockState(pos).isAir();
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private PortalPlacer() {}
}

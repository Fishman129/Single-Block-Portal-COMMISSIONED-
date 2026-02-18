package com.fishguy129.singleportal;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

public final class PortalLocator {

    /**
     * Find the nearest single-portal block within a radius around the target X/Z.
     * Uses a dimension-aware Y scan band and chooses the best portal by 3D distance.
     */
    public static BlockPos findNearest(ServerWorld world, int centerX, int centerZ, int radius) {
        BlockPos best = null;
        double bestDistSq = Double.MAX_VALUE;

        // Target Y used only for scoring (not scanning). This keeps “closest” meaningful in 3D.
        int targetY = clamp(64, world.getBottomY() + 4, world.getTopY() - 4);

        // Scan a disk in X/Z
        int r2 = radius * radius;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int d2 = dx * dx + dz * dz;
                if (d2 > r2) continue;

                int x = centerX + dx;
                int z = centerZ + dz;

                // Choose a vertical scan band:
                // - Overworld: near surface (topY - 48 .. topY + 16)
                // - Nether: broader band (32..120), clamped to world bounds
                int minY;
                int maxY;

                if (world.getRegistryKey() == World.NETHER) {
                    minY = Math.max(world.getBottomY() + 4, 32);
                    maxY = Math.min(world.getTopY() - 4, 120);
                } else {
                    int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
                    minY = Math.max(world.getBottomY() + 4, topY - 48);
                    maxY = Math.min(world.getTopY() - 4, topY + 16);
                }

                for (int y = minY; y <= maxY; y++) {
                    BlockPos p = new BlockPos(x, y, z);
                    BlockState state = world.getBlockState(p);

                    if (state.getBlock() != ModBlocks.SINGLE_PORTAL) continue;

                    // Score by true 3D distance to the intended target point
                    double dx3 = (x + 0.5) - (centerX + 0.5);
                    double dy3 = (y + 0.5) - (targetY + 0.5);
                    double dz3 = (z + 0.5) - (centerZ + 0.5);
                    double distSq = dx3 * dx3 + dy3 * dy3 + dz3 * dz3;

                    if (distSq < bestDistSq) {
                        bestDistSq = distSq;
                        best = p.toImmutable();
                    }
                }
            }
        }

        return best;
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private PortalLocator() {}
}

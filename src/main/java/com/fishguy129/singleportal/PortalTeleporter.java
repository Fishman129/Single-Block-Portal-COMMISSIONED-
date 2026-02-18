package com.fishguy129.singleportal;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

public final class PortalTeleporter {
    private static final int SEARCH_RADIUS = 96;

    public static TeleportTarget createTarget(ServerWorld fromWorld, Entity entity, BlockPos fromPortalPos) {
        ServerWorld toWorld = getDestination(fromWorld);
        if (toWorld == null) return null;

        // 1) Link-first: if this portal remembers where it goes, go there exactly.
        BlockEntity be = fromWorld.getBlockEntity(fromPortalPos);
        if (be instanceof SinglePortalBlockEntity portalBe && portalBe.hasLink()) {
            ServerWorld linkedWorld = fromWorld.getServer().getWorld(portalBe.getLinkedDim());
            BlockPos linkedPos = portalBe.getLinkedPos();

            if (linkedWorld != null && linkedWorld.getBlockState(linkedPos).getBlock() == ModBlocks.SINGLE_PORTAL) {
                return makeTarget(linkedWorld, entity, linkedPos);
            } else {
                // Link is stale (broken portal, missing dimension, etc.)
                portalBe.clearLink();
            }
        }

        // 2) Compute scaled target using the PORTAL BLOCK position (stable).
        double scale = (fromWorld.getRegistryKey() == World.OVERWORLD) ? (1.0 / 8.0) : 8.0;

        // Use center-of-block for stable mapping, like vanilla-ish behavior
        double srcX = fromPortalPos.getX() + 0.5;
        double srcZ = fromPortalPos.getZ() + 0.5;

        int targetX = (int) Math.floor(srcX * scale);
        int targetZ = (int) Math.floor(srcZ * scale);

        // Clamp to world border correctly (BlockPos clamp)
        var border = toWorld.getWorldBorder();
        BlockPos clamped = border.clamp(new BlockPos(targetX, 0, targetZ));
        targetX = clamped.getX();
        targetZ = clamped.getZ();

        // Ensure the destination chunk is loaded around the target
        toWorld.getChunk(targetX >> 4, targetZ >> 4);

        // 3) Find existing portal near target, else create one
        BlockPos found = PortalLocator.findNearest(toWorld, targetX, targetZ, SEARCH_RADIUS);
        if (found == null) {
            found = PortalPlacer.placePortalOrPlatform(toWorld, targetX, targetZ);
        }

        // 4) Write bidirectional links so future teleports are exact
        writeLink(fromWorld, fromPortalPos, toWorld, found);
        writeLink(toWorld, found, fromWorld, fromPortalPos);

        return makeTarget(toWorld, entity, found);
    }

    private static void writeLink(ServerWorld aWorld, BlockPos aPos, ServerWorld bWorld, BlockPos bPos) {
        BlockEntity be = aWorld.getBlockEntity(aPos);
        if (be instanceof SinglePortalBlockEntity portalBe) {
            portalBe.setLink(bWorld.getRegistryKey(), bPos);
        }
    }

    private static TeleportTarget makeTarget(ServerWorld toWorld, Entity entity, BlockPos portalPos) {
        Vec3d dest = Vec3d.ofCenter(portalPos).add(0.0, 0.1, 0.0);
        return new TeleportTarget(
                toWorld,
                dest,
                entity.getVelocity(),
                entity.getYaw(),
                entity.getPitch(),
                (e) -> {}
        );
    }

    private static ServerWorld getDestination(ServerWorld fromWorld) {
        if (fromWorld.getRegistryKey() == World.OVERWORLD) {
            return fromWorld.getServer().getWorld(World.NETHER);
        }
        if (fromWorld.getRegistryKey() == World.NETHER) {
            return fromWorld.getServer().getWorld(World.OVERWORLD);
        }
        return null;
    }

    private PortalTeleporter() {}
}

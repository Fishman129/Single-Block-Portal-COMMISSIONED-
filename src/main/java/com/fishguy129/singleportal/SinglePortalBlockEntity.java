package com.fishguy129.singleportal;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SinglePortalBlockEntity extends BlockEntity {
    private RegistryKey<World> linkedDim;
    private BlockPos linkedPos;

    public SinglePortalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SINGLE_PORTAL_BE, pos, state);
    }

    public boolean hasLink() {
        return linkedDim != null && linkedPos != null;
    }

    public RegistryKey<World> getLinkedDim() {
        return linkedDim;
    }

    public BlockPos getLinkedPos() {
        return linkedPos;
    }

    public void setLink(RegistryKey<World> dim, BlockPos pos) {
        this.linkedDim = dim;
        this.linkedPos = pos.toImmutable();
        markDirty();
    }

    public void clearLink() {
        this.linkedDim = null;
        this.linkedPos = null;
        markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        if (hasLink()) {
            nbt.putString("linked_dim", linkedDim.getValue().toString());
            nbt.putInt("linked_x", linkedPos.getX());
            nbt.putInt("linked_y", linkedPos.getY());
            nbt.putInt("linked_z", linkedPos.getZ());
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        if (nbt.contains("linked_dim")) {
            Identifier id = Identifier.of(nbt.getString("linked_dim"));
            this.linkedDim = RegistryKey.of(RegistryKeys.WORLD, id);
            this.linkedPos = new BlockPos(
                    nbt.getInt("linked_x"),
                    nbt.getInt("linked_y"),
                    nbt.getInt("linked_z")
            );
        } else {
            this.linkedDim = null;
            this.linkedPos = null;
        }
    }
}

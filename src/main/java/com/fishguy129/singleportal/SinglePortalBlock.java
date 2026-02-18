package com.fishguy129.singleportal;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Portal;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SinglePortalBlock extends BlockWithEntity implements Portal {

    public static final MapCodec<SinglePortalBlock> CODEC =
            createCodec(SinglePortalBlock::new);

    // codec constructor (accepts Settings)
    public SinglePortalBlock(Settings settings) {
        super(settings);
    }

    // convenience constructor for your registry
    public SinglePortalBlock() {
        this(Settings.create()
                .noCollision()
                .strength(-1.0F, 3600000.0F)
                .dropsNothing());
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SinglePortalBlockEntity(pos, state);
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient && world instanceof ServerWorld) {
            entity.tryUsePortal(this, pos);
        }
    }

    @Override
    public TeleportTarget createTeleportTarget(ServerWorld fromWorld, Entity entity, BlockPos portalPos) {
        return PortalTeleporter.createTarget(fromWorld, entity, portalPos);
    }

    @Override
    public Portal.Effect getPortalEffect() {
        return Portal.Effect.CONFUSION;
    }

    @Override
    public int getPortalDelay(ServerWorld world, Entity entity) {
        return 80;
    }
}

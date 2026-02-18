package com.fishguy129.singleportal.mixin.client;

import com.fishguy129.singleportal.ModBlocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    // You can add this texture later; for now I just created a placeholder PNG using minecraft's nether portal texture.
    private static final Identifier SINGLE_OVERLAY =
            Identifier.of("singleportal", "textures/misc/single_portal_overlay.png");

    @Inject(method = "renderMiscOverlays", at = @At("HEAD"), cancellable = true)
    private void singleportal$swapPortalOverlay(net.minecraft.client.gui.DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        // Vanilla portal overlay is shown when the player has portalNauseaAmount > 0.
        // We only want to override if they’re actually inside the portal block.
        BlockPos p = client.player.getBlockPos();
        boolean inOurPortal =
                client.world.getBlockState(p).getBlock() == ModBlocks.SINGLE_PORTAL
                        || client.world.getBlockState(p.up()).getBlock() == ModBlocks.SINGLE_PORTAL;

        if (!inOurPortal) return;

        // Cancel vanilla misc overlay rendering and draw our own overlay.
        ci.cancel();

        int screenW = client.getWindow().getScaledWidth();
        int screenH = client.getWindow().getScaledHeight();

        int frameSize = 16;
        int textureW = 16;
        int textureH = 512;
        int frames = textureH / frameSize; // 32
        int frameTime = 2;

        long t = client.world.getTime();
        int frame = (int) ((t / frameTime) % frames);

        float u = 0f;
        float v = frame * frameSize;

        // Scale a 16x16 draw up to full screen (prevents tiling)
        var matrices = context.getMatrices();
        matrices.push();
        matrices.scale((float) screenW / frameSize, (float) screenH / frameSize, 1.0f);

        // Draw exactly one frame (16x16) at 0,0, stretched by the matrix
        context.drawTexture(
                SINGLE_OVERLAY,
                0, 0,
                0,
                u, v,
                frameSize, frameSize,
                textureW, textureH
        );

        matrices.pop();
    }
}

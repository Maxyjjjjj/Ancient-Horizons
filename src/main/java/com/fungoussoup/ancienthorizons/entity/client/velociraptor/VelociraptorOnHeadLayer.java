package com.fungoussoup.ancienthorizons.entity.client.velociraptor;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.VelociraptorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
// Import AbstractClientPlayer
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

// Change T extends Player to T extends AbstractClientPlayer
@OnlyIn(Dist.CLIENT)
public class VelociraptorOnHeadLayer<T extends AbstractClientPlayer> extends RenderLayer<T, PlayerModel<T>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/velociraptor/velociraptor.png");

    private final VelociraptorModel<VelociraptorEntity> model;

    public VelociraptorOnHeadLayer(RenderLayerParent<T, PlayerModel<T>> renderer, EntityRendererProvider.Context context) {
        super(renderer);
        this.model = new VelociraptorModel<>(context.bakeLayer(VelociraptorModel.LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       T player, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        // Only render if the player currently has a raptor on their head.
        if (!hasRaptorOnHead(player)) return;

        poseStack.pushPose();

        // Attach the transformation to the player's head
        this.getParentModel().getHead().translateAndRotate(poseStack);

        // Move above the head
        poseStack.translate(0.0D, -0.5D, 0.0D); // tune Y offset
        poseStack.scale(1F, 1F, 1F);

        // Render the tiny raptor
        VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(TEXTURE));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }

    private boolean hasRaptorOnHead(Player player) {
        return player.getPersistentData().getBoolean("HasRaptorOnHead");
    }
}
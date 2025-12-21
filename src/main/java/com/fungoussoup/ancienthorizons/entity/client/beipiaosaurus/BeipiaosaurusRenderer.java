package com.fungoussoup.ancienthorizons.entity.client.beipiaosaurus;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.BeipiaosaurusEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class BeipiaosaurusRenderer extends MobRenderer<BeipiaosaurusEntity, BeipiaosaurusModel<BeipiaosaurusEntity>> {
    public BeipiaosaurusRenderer(EntityRendererProvider.Context context) {
        super(context, new BeipiaosaurusModel<>(context.bakeLayer(BeipiaosaurusModel.LAYER_LOCATION)), 0.5f);

        // Add item rendering layer
        this.addLayer(new BeipiaosaurusItemInHandLayer(this, context.getItemInHandRenderer()));
    }

    @Override
    public void render(BeipiaosaurusEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            poseStack.scale(1f, 1f, 1f);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(BeipiaosaurusEntity beipiaosaurusEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/beipiaosaurus/beipiaosaurus.png");
    }

    // Custom item rendering layer for Beipiaosaurus
    private static class BeipiaosaurusItemInHandLayer extends ItemInHandLayer<BeipiaosaurusEntity, BeipiaosaurusModel<BeipiaosaurusEntity>> {
        private final ItemInHandRenderer itemInHandRenderer;

        public BeipiaosaurusItemInHandLayer(MobRenderer<BeipiaosaurusEntity, BeipiaosaurusModel<BeipiaosaurusEntity>> renderer, ItemInHandRenderer itemRenderer) {
            super(renderer, itemRenderer);
            this.itemInHandRenderer = itemRenderer;
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, BeipiaosaurusEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            // Only render item if entity has stolen something
            if (entity.getEntityData().get(BeipiaosaurusEntity.HAS_STOLEN_ITEM)) {
                ItemStack stolenItem = entity.getStolenItem();

                if (!stolenItem.isEmpty()) {
                    poseStack.pushPose();

                    // Get the model and translate to the correct hand based on handedness
                    BeipiaosaurusModel<BeipiaosaurusEntity> model = this.getParentModel();
                    boolean isLeftHanded = entity.isLeftHanded();

                    if (isLeftHanded) {
                        model.translateToHand(HumanoidArm.LEFT, poseStack);
                    } else {
                        model.translateToHand(HumanoidArm.RIGHT, poseStack);
                    }

                    // Render the item
                    this.itemInHandRenderer.renderItem(
                            entity,
                            stolenItem,
                            ItemDisplayContext.GROUND,
                            false,
                            poseStack,
                            buffer,
                            packedLight
                    );

                    poseStack.popPose();
                }
            }
        }
    }
}
package com.fightclass3.entity.render;

import com.fightclass3.FightClass3Mod;
import com.fightclass3.entity.JiuJiTaeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class JiuJiTaeRenderer extends HumanoidMobRenderer<JiuJiTaeEntity, HumanoidModel<JiuJiTaeEntity>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(FightClass3Mod.MOD_ID, "textures/entity/jiu_ji_tae.png");

    public JiuJiTaeRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.5f);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE_INNER_ARMOR)),
                new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR)),
                ctx.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(JiuJiTaeEntity entity) { return TEXTURE; }

    @Override
    protected void scale(JiuJiTaeEntity entity, PoseStack pose, float partialTick) {
        pose.scale(1.0f, 1.0f, 1.0f); // Standard player size
    }
}

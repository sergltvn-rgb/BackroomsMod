/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.MobRenderer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Mob
 */
package fr.meulti.mbackrooms.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.meulti.mbackrooms.BackroomsMod;
import fr.meulti.mbackrooms.entity.client.DeathRatModel;
import fr.meulti.mbackrooms.entity.client.ModModelLayers;
import fr.meulti.mbackrooms.entity.custom.DeathRatEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

public class DeathRatRenderer
extends MobRenderer<DeathRatEntity, DeathRatModel<DeathRatEntity>> {
    public DeathRatRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new DeathRatModel(pContext.m_174023_(ModModelLayers.DEATH_RAT_LAYER)), 0.2f);
    }

    public ResourceLocation getTextureLocation(DeathRatEntity pEntity) {
        return BackroomsMod.getModResource("textures/entity/death_rat.png");
    }

    public void render(DeathRatEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.m_7392_((Mob)pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }
}

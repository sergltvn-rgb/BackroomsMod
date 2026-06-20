/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.MobRenderer
 *  net.minecraft.client.renderer.entity.RenderLayerParent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Mob
 */
package fr.meulti.mbackrooms.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.meulti.mbackrooms.BackroomsMod;
import fr.meulti.mbackrooms.entity.client.LevelTwoEntityGlowLayer;
import fr.meulti.mbackrooms.entity.client.LevelTwoEntityModel;
import fr.meulti.mbackrooms.entity.client.ModModelLayers;
import fr.meulti.mbackrooms.entity.custom.LevelTwoEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

public class LevelTwoEntityRenderer
extends MobRenderer<LevelTwoEntity, LevelTwoEntityModel<LevelTwoEntity>> {
    public LevelTwoEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new LevelTwoEntityModel(pContext.m_174023_(ModModelLayers.LEVEL_TWO_ENTITY)), 0.0f);
        this.m_115326_(new LevelTwoEntityGlowLayer((RenderLayerParent<LevelTwoEntity, LevelTwoEntityModel<LevelTwoEntity>>)this));
    }

    public ResourceLocation getTextureLocation(LevelTwoEntity pEntity) {
        return BackroomsMod.getModResource("textures/misc/empty.png");
    }

    public void render(LevelTwoEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        pMatrixStack.m_85836_();
        float scale = 2.5f;
        pMatrixStack.m_85841_(scale, scale, scale);
        super.m_7392_((Mob)pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
        pMatrixStack.m_85849_();
    }
}

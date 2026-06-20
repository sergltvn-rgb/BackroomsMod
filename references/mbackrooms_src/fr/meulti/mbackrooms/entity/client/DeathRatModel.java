/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.model.HierarchicalModel
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.model.geom.PartPose
 *  net.minecraft.client.model.geom.builders.CubeDeformation
 *  net.minecraft.client.model.geom.builders.CubeListBuilder
 *  net.minecraft.client.model.geom.builders.LayerDefinition
 *  net.minecraft.client.model.geom.builders.MeshDefinition
 *  net.minecraft.client.model.geom.builders.PartDefinition
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 */
package fr.meulti.mbackrooms.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fr.meulti.mbackrooms.entity.animations.ModAnimationsDefinitions;
import fr.meulti.mbackrooms.entity.custom.DeathRatEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class DeathRatModel<T extends Entity>
extends HierarchicalModel<T> {
    private final ModelPart death_rat;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart arm;
    private final ModelPart a_right;
    private final ModelPart a_left;
    private final ModelPart tale;
    private final ModelPart leg;
    private final ModelPart l_left;
    private final ModelPart l_right;

    public DeathRatModel(ModelPart root) {
        this.death_rat = root.m_171324_("death_rat");
        this.head = this.death_rat.m_171324_("head");
        this.body = this.death_rat.m_171324_("body");
        this.arm = this.death_rat.m_171324_("arm");
        this.a_right = this.arm.m_171324_("a_right");
        this.a_left = this.arm.m_171324_("a_left");
        this.tale = this.death_rat.m_171324_("tale");
        this.leg = this.death_rat.m_171324_("leg");
        this.l_left = this.leg.m_171324_("l_left");
        this.l_right = this.leg.m_171324_("l_right");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.m_171576_();
        PartDefinition death_rat = partdefinition.m_171599_("death_rat", CubeListBuilder.m_171558_(), PartPose.m_171419_((float)2.8007f, (float)22.9854f, (float)-7.8236f));
        PartDefinition head = death_rat.m_171599_("head", CubeListBuilder.m_171558_().m_171514_(16, 25).m_171488_(-2.0507f, -1.4854f, -2.9264f, 4.0f, 3.0f, 3.0f, new CubeDeformation(0.0f)).m_171514_(34, 36).m_171488_(1.1993f, -2.7354f, -0.4264f, 2.0f, 2.0f, 0.0f, new CubeDeformation(0.0f)).m_171514_(8, 37).m_171488_(-2.8007f, -2.7354f, -0.4264f, 2.0f, 2.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.m_171419_((float)-2.75f, (float)-2.5f, (float)4.75f));
        PartDefinition cube_r1 = head.m_171599_("cube_r1", CubeListBuilder.m_171558_().m_171514_(36, 15).m_171488_(0.0f, -3.0f, 1.0f, 2.0f, 3.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)1.75f, (float)2.5f, (float)-4.75f, (float)0.1372f, (float)-0.3027f, (float)-0.0411f));
        PartDefinition cube_r2 = head.m_171599_("cube_r2", CubeListBuilder.m_171558_().m_171514_(4, 36).m_171488_(0.0f, -3.0f, 1.0f, 2.0f, 3.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)-3.7588f, (float)2.1715f, (float)-4.1537f, (float)0.1372f, (float)0.3027f, (float)0.0411f));
        PartDefinition cube_r3 = head.m_171599_("cube_r3", CubeListBuilder.m_171558_().m_171514_(30, 40).m_171488_(0.0f, -2.0f, 1.8214f, 1.0f, 1.0f, 1.0f, new CubeDeformation(0.0f)).m_171514_(30, 38).m_171488_(-3.5f, -2.0f, 1.8214f, 1.0f, 1.0f, 1.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)1.1993f, (float)1.3951f, (float)-3.9179f, (float)0.1309f, (float)0.0f, (float)0.0f));
        PartDefinition cube_r4 = head.m_171599_("cube_r4", CubeListBuilder.m_171558_().m_171514_(30, 36).m_171488_(0.0f, -2.0f, 1.8214f, 1.0f, 1.0f, 1.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)-0.5507f, (float)1.8951f, (float)-7.4179f, (float)0.1309f, (float)0.0f, (float)0.0f));
        PartDefinition cube_r5 = head.m_171599_("cube_r5", CubeListBuilder.m_171558_().m_171514_(10, 30).m_171488_(-1.0f, 0.0f, 1.0f, 2.0f, 1.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)-0.0507f, (float)2.0146f, (float)-5.4264f, (float)0.3927f, (float)0.0f, (float)0.0f));
        PartDefinition cube_r6 = head.m_171599_("cube_r6", CubeListBuilder.m_171558_().m_171514_(20, 5).m_171488_(-1.0f, -2.0f, 1.0f, 3.0f, 2.0f, 2.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)-0.5507f, (float)2.0146f, (float)-5.6764f, (float)0.1309f, (float)0.0f, (float)0.0f));
        PartDefinition body = death_rat.m_171599_("body", CubeListBuilder.m_171558_(), PartPose.m_171419_((float)-2.8007f, (float)-2.7354f, (float)10.3236f));
        PartDefinition cube_r7 = body.m_171599_("cube_r7", CubeListBuilder.m_171558_().m_171514_(30, 31).m_171488_(0.0f, -3.0f, 0.0f, 3.0f, 3.0f, 2.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)-1.5f, (float)1.75f, (float)-5.5f, (float)0.1309f, (float)0.0f, (float)0.0f));
        PartDefinition cube_r8 = body.m_171599_("cube_r8", CubeListBuilder.m_171558_().m_171514_(0, 0).m_171488_(-1.0f, -4.0f, 0.0f, 5.0f, 4.0f, 5.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)-1.5f, (float)0.75f, (float)0.25f, (float)-0.0436f, (float)0.0f, (float)0.0f));
        PartDefinition cube_r9 = body.m_171599_("cube_r9", CubeListBuilder.m_171558_().m_171514_(0, 9).m_171488_(-1.0f, -4.0f, 0.0f, 5.0f, 4.0f, 4.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)-1.5f, (float)2.25f, (float)-3.5f, (float)0.48f, (float)0.0f, (float)0.0f));
        PartDefinition cube_r10 = body.m_171599_("cube_r10", CubeListBuilder.m_171558_().m_171514_(0, 17).m_171488_(-1.0f, -3.0f, 1.0f, 4.0f, 3.0f, 5.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)-1.0f, (float)0.75f, (float)-3.5f, (float)0.0873f, (float)0.0f, (float)0.0f));
        PartDefinition arm = death_rat.m_171599_("arm", CubeListBuilder.m_171558_(), PartPose.m_171419_((float)-2.8007f, (float)-3.4854f, (float)6.8236f));
        PartDefinition a_right = arm.m_171599_("a_right", CubeListBuilder.m_171558_(), PartPose.m_171419_((float)-3.0f, (float)0.0f, (float)0.0f));
        PartDefinition cube_r11 = a_right.m_171599_("cube_r11", CubeListBuilder.m_171558_().m_171514_(0, 36).m_171488_(2.0f, -4.0f, 0.0f, 1.0f, 3.0f, 1.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)-3.0f, (float)5.0f, (float)-1.0f, (float)1.6229f, (float)-0.1322f, (float)-0.1146f));
        PartDefinition cube_r12 = a_right.m_171599_("cube_r12", CubeListBuilder.m_171558_().m_171514_(0, 30).m_171488_(2.0f, -3.0f, 0.0f, 1.0f, 2.0f, 4.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)-2.5f, (float)3.75f, (float)1.5f, (float)1.3569f, (float)0.0924f, (float)0.0928f));
        PartDefinition cube_r13 = a_right.m_171599_("cube_r13", CubeListBuilder.m_171558_().m_171514_(32, 20).m_171488_(2.0f, -5.0f, 0.0f, 1.0f, 4.0f, 1.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)-3.5f, (float)2.0f, (float)1.0f, (float)2.321f, (float)-0.1322f, (float)-0.1146f));
        PartDefinition a_left = arm.m_171599_("a_left", CubeListBuilder.m_171558_(), PartPose.m_171419_((float)3.0f, (float)0.0f, (float)0.0f));
        PartDefinition cube_r14 = a_left.m_171599_("cube_r14", CubeListBuilder.m_171558_().m_171514_(36, 0).m_171488_(-3.0f, -4.0f, 0.0f, 1.0f, 3.0f, 1.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)3.0f, (float)5.0f, (float)-1.0f, (float)1.6229f, (float)0.1322f, (float)0.1146f));
        PartDefinition cube_r15 = a_left.m_171599_("cube_r15", CubeListBuilder.m_171558_().m_171514_(32, 15).m_171488_(-3.0f, -5.0f, 0.0f, 1.0f, 4.0f, 1.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)3.5f, (float)2.0f, (float)1.0f, (float)2.321f, (float)0.1322f, (float)0.1146f));
        PartDefinition cube_r16 = a_left.m_171599_("cube_r16", CubeListBuilder.m_171558_().m_171514_(30, 25).m_171488_(-3.0f, -3.0f, 0.0f, 1.0f, 2.0f, 4.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)2.5f, (float)3.75f, (float)1.5f, (float)1.3569f, (float)-0.0924f, (float)-0.0928f));
        PartDefinition tale = death_rat.m_171599_("tale", CubeListBuilder.m_171558_(), PartPose.m_171419_((float)-2.8007f, (float)-4.2354f, (float)15.5736f));
        PartDefinition cube_r17 = tale.m_171599_("cube_r17", CubeListBuilder.m_171558_().m_171514_(50, 0).m_171488_(1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 5.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)-2.0f, (float)0.0f, (float)-0.5f, (float)-0.6545f, (float)0.0f, (float)0.0f));
        PartDefinition cube_r18 = tale.m_171599_("cube_r18", CubeListBuilder.m_171558_().m_171514_(52, 7).m_171488_(2.0f, -1.0f, 0.0f, 1.0f, 1.0f, 5.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)-2.5f, (float)3.5439f, (float)2.9678f, (float)-0.0436f, (float)0.0f, (float)0.0f));
        PartDefinition leg = death_rat.m_171599_("leg", CubeListBuilder.m_171558_(), PartPose.m_171419_((float)-2.8007f, (float)-3.9854f, (float)13.5736f));
        PartDefinition l_left = leg.m_171599_("l_left", CubeListBuilder.m_171558_(), PartPose.m_171419_((float)2.5f, (float)0.0f, (float)0.0f));
        PartDefinition cube_r19 = l_left.m_171599_("cube_r19", CubeListBuilder.m_171558_().m_171514_(18, 9).m_171488_(-3.0f, -3.0f, 0.0f, 2.0f, 3.0f, 5.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)3.0f, (float)3.5f, (float)-1.25f, (float)0.7897f, (float)-0.0924f, (float)-0.0928f));
        PartDefinition cube_r20 = l_left.m_171599_("cube_r20", CubeListBuilder.m_171558_().m_171514_(10, 31).m_171488_(-2.0f, -2.0f, 0.0f, 1.0f, 2.0f, 4.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)2.75f, (float)3.0f, (float)-3.25f, (float)-0.432f, (float)-0.0924f, (float)-0.0928f));
        PartDefinition cube_r21 = l_left.m_171599_("cube_r21", CubeListBuilder.m_171558_().m_171514_(32, 10).m_171488_(-2.0f, -2.0f, 0.0f, 1.0f, 1.0f, 4.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)2.5f, (float)3.0f, (float)0.75f, (float)-3.1373f, (float)-0.0924f, (float)-0.0928f));
        PartDefinition l_right = leg.m_171599_("l_right", CubeListBuilder.m_171558_(), PartPose.m_171419_((float)-2.5f, (float)0.0f, (float)0.0f));
        PartDefinition cube_r22 = l_right.m_171599_("cube_r22", CubeListBuilder.m_171558_().m_171514_(32, 5).m_171488_(1.0f, -2.0f, 0.0f, 1.0f, 1.0f, 4.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)-2.5f, (float)3.0f, (float)0.75f, (float)-3.1373f, (float)0.0924f, (float)0.0928f));
        PartDefinition cube_r23 = l_right.m_171599_("cube_r23", CubeListBuilder.m_171558_().m_171514_(20, 31).m_171488_(1.0f, -2.0f, 0.0f, 1.0f, 2.0f, 4.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)-2.75f, (float)3.0f, (float)-3.25f, (float)-0.432f, (float)0.0924f, (float)0.0928f));
        PartDefinition cube_r24 = l_right.m_171599_("cube_r24", CubeListBuilder.m_171558_().m_171514_(18, 17).m_171488_(1.0f, -3.0f, 0.0f, 2.0f, 3.0f, 5.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)-3.0f, (float)3.5f, (float)-1.25f, (float)0.7897f, (float)0.0924f, (float)0.0928f));
        return LayerDefinition.m_171565_((MeshDefinition)meshdefinition, (int)64, (int)64);
    }

    public void m_6973_(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.m_142109_().m_171331_().forEach(ModelPart::m_233569_);
        this.applyHeadRotation(netHeadYaw, headPitch, ageInTicks);
        this.m_267799_(ModAnimationsDefinitions.RAT_WALK, limbSwing, limbSwingAmount, 2.0f, 2.5f);
        this.m_233385_(((DeathRatEntity)((Object)entity)).idleAnimationState, ModAnimationsDefinitions.RAT_IDLE, ageInTicks, 1.0f);
    }

    private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch, float pAgeInTicks) {
        pNetHeadYaw = Mth.m_14036_((float)pNetHeadYaw, (float)-30.0f, (float)30.0f);
        pHeadPitch = Mth.m_14036_((float)pHeadPitch, (float)-25.0f, (float)45.0f);
        this.head.f_104204_ = pNetHeadYaw * ((float)Math.PI / 180);
        this.head.f_104203_ = pHeadPitch * ((float)Math.PI / 180);
    }

    public void m_7695_(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.death_rat.m_104306_(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public ModelPart m_142109_() {
        return this.death_rat;
    }
}

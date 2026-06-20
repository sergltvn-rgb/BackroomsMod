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
package com.litvin.backrooms.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import com.litvin.backrooms.entity.custom.DeathRatEntity;
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
        this.death_rat = root.getChild("death_rat");
        this.head = this.death_rat.getChild("head");
        this.body = this.death_rat.getChild("body");
        this.arm = this.death_rat.getChild("arm");
        this.a_right = this.arm.getChild("a_right");
        this.a_left = this.arm.getChild("a_left");
        this.tale = this.death_rat.getChild("tale");
        this.leg = this.death_rat.getChild("leg");
        this.l_left = this.leg.getChild("l_left");
        this.l_right = this.leg.getChild("l_right");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition death_rat = partdefinition.addOrReplaceChild("death_rat", CubeListBuilder.create(), PartPose.offset((float)2.8007f, (float)22.9854f, (float)-7.8236f));
        PartDefinition head = death_rat.addOrReplaceChild("head", CubeListBuilder.create().texOffs(16, 25).addBox(-2.0507f, -1.4854f, -2.9264f, 4.0f, 3.0f, 3.0f, new CubeDeformation(0.0f)).texOffs(34, 36).addBox(1.1993f, -2.7354f, -0.4264f, 2.0f, 2.0f, 0.0f, new CubeDeformation(0.0f)).texOffs(8, 37).addBox(-2.8007f, -2.7354f, -0.4264f, 2.0f, 2.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.offset((float)-2.75f, (float)-2.5f, (float)4.75f));
        PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(36, 15).addBox(0.0f, -3.0f, 1.0f, 2.0f, 3.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)1.75f, (float)2.5f, (float)-4.75f, (float)0.1372f, (float)-0.3027f, (float)-0.0411f));
        PartDefinition cube_r2 = head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(4, 36).addBox(0.0f, -3.0f, 1.0f, 2.0f, 3.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)-3.7588f, (float)2.1715f, (float)-4.1537f, (float)0.1372f, (float)0.3027f, (float)0.0411f));
        PartDefinition cube_r3 = head.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(30, 40).addBox(0.0f, -2.0f, 1.8214f, 1.0f, 1.0f, 1.0f, new CubeDeformation(0.0f)).texOffs(30, 38).addBox(-3.5f, -2.0f, 1.8214f, 1.0f, 1.0f, 1.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)1.1993f, (float)1.3951f, (float)-3.9179f, (float)0.1309f, (float)0.0f, (float)0.0f));
        PartDefinition cube_r4 = head.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(30, 36).addBox(0.0f, -2.0f, 1.8214f, 1.0f, 1.0f, 1.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)-0.5507f, (float)1.8951f, (float)-7.4179f, (float)0.1309f, (float)0.0f, (float)0.0f));
        PartDefinition cube_r5 = head.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(10, 30).addBox(-1.0f, 0.0f, 1.0f, 2.0f, 1.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)-0.0507f, (float)2.0146f, (float)-5.4264f, (float)0.3927f, (float)0.0f, (float)0.0f));
        PartDefinition cube_r6 = head.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(20, 5).addBox(-1.0f, -2.0f, 1.0f, 3.0f, 2.0f, 2.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)-0.5507f, (float)2.0146f, (float)-5.6764f, (float)0.1309f, (float)0.0f, (float)0.0f));
        PartDefinition body = death_rat.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset((float)-2.8007f, (float)-2.7354f, (float)10.3236f));
        PartDefinition cube_r7 = body.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(30, 31).addBox(0.0f, -3.0f, 0.0f, 3.0f, 3.0f, 2.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)-1.5f, (float)1.75f, (float)-5.5f, (float)0.1309f, (float)0.0f, (float)0.0f));
        PartDefinition cube_r8 = body.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0f, -4.0f, 0.0f, 5.0f, 4.0f, 5.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)-1.5f, (float)0.75f, (float)0.25f, (float)-0.0436f, (float)0.0f, (float)0.0f));
        PartDefinition cube_r9 = body.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(0, 9).addBox(-1.0f, -4.0f, 0.0f, 5.0f, 4.0f, 4.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)-1.5f, (float)2.25f, (float)-3.5f, (float)0.48f, (float)0.0f, (float)0.0f));
        PartDefinition cube_r10 = body.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0f, -3.0f, 1.0f, 4.0f, 3.0f, 5.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)-1.0f, (float)0.75f, (float)-3.5f, (float)0.0873f, (float)0.0f, (float)0.0f));
        PartDefinition arm = death_rat.addOrReplaceChild("arm", CubeListBuilder.create(), PartPose.offset((float)-2.8007f, (float)-3.4854f, (float)6.8236f));
        PartDefinition a_right = arm.addOrReplaceChild("a_right", CubeListBuilder.create(), PartPose.offset((float)-3.0f, (float)0.0f, (float)0.0f));
        PartDefinition cube_r11 = a_right.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(0, 36).addBox(2.0f, -4.0f, 0.0f, 1.0f, 3.0f, 1.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)-3.0f, (float)5.0f, (float)-1.0f, (float)1.6229f, (float)-0.1322f, (float)-0.1146f));
        PartDefinition cube_r12 = a_right.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(0, 30).addBox(2.0f, -3.0f, 0.0f, 1.0f, 2.0f, 4.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)-2.5f, (float)3.75f, (float)1.5f, (float)1.3569f, (float)0.0924f, (float)0.0928f));
        PartDefinition cube_r13 = a_right.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(32, 20).addBox(2.0f, -5.0f, 0.0f, 1.0f, 4.0f, 1.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)-3.5f, (float)2.0f, (float)1.0f, (float)2.321f, (float)-0.1322f, (float)-0.1146f));
        PartDefinition a_left = arm.addOrReplaceChild("a_left", CubeListBuilder.create(), PartPose.offset((float)3.0f, (float)0.0f, (float)0.0f));
        PartDefinition cube_r14 = a_left.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(36, 0).addBox(-3.0f, -4.0f, 0.0f, 1.0f, 3.0f, 1.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)3.0f, (float)5.0f, (float)-1.0f, (float)1.6229f, (float)0.1322f, (float)0.1146f));
        PartDefinition cube_r15 = a_left.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(32, 15).addBox(-3.0f, -5.0f, 0.0f, 1.0f, 4.0f, 1.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)3.5f, (float)2.0f, (float)1.0f, (float)2.321f, (float)0.1322f, (float)0.1146f));
        PartDefinition cube_r16 = a_left.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(30, 25).addBox(-3.0f, -3.0f, 0.0f, 1.0f, 2.0f, 4.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)2.5f, (float)3.75f, (float)1.5f, (float)1.3569f, (float)-0.0924f, (float)-0.0928f));
        PartDefinition tale = death_rat.addOrReplaceChild("tale", CubeListBuilder.create(), PartPose.offset((float)-2.8007f, (float)-4.2354f, (float)15.5736f));
        PartDefinition cube_r17 = tale.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(50, 0).addBox(1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 5.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)-2.0f, (float)0.0f, (float)-0.5f, (float)-0.6545f, (float)0.0f, (float)0.0f));
        PartDefinition cube_r18 = tale.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(52, 7).addBox(2.0f, -1.0f, 0.0f, 1.0f, 1.0f, 5.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)-2.5f, (float)3.5439f, (float)2.9678f, (float)-0.0436f, (float)0.0f, (float)0.0f));
        PartDefinition leg = death_rat.addOrReplaceChild("leg", CubeListBuilder.create(), PartPose.offset((float)-2.8007f, (float)-3.9854f, (float)13.5736f));
        PartDefinition l_left = leg.addOrReplaceChild("l_left", CubeListBuilder.create(), PartPose.offset((float)2.5f, (float)0.0f, (float)0.0f));
        PartDefinition cube_r19 = l_left.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(18, 9).addBox(-3.0f, -3.0f, 0.0f, 2.0f, 3.0f, 5.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)3.0f, (float)3.5f, (float)-1.25f, (float)0.7897f, (float)-0.0924f, (float)-0.0928f));
        PartDefinition cube_r20 = l_left.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(10, 31).addBox(-2.0f, -2.0f, 0.0f, 1.0f, 2.0f, 4.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)2.75f, (float)3.0f, (float)-3.25f, (float)-0.432f, (float)-0.0924f, (float)-0.0928f));
        PartDefinition cube_r21 = l_left.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(32, 10).addBox(-2.0f, -2.0f, 0.0f, 1.0f, 1.0f, 4.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)2.5f, (float)3.0f, (float)0.75f, (float)-3.1373f, (float)-0.0924f, (float)-0.0928f));
        PartDefinition l_right = leg.addOrReplaceChild("l_right", CubeListBuilder.create(), PartPose.offset((float)-2.5f, (float)0.0f, (float)0.0f));
        PartDefinition cube_r22 = l_right.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(32, 5).addBox(1.0f, -2.0f, 0.0f, 1.0f, 1.0f, 4.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)-2.5f, (float)3.0f, (float)0.75f, (float)-3.1373f, (float)0.0924f, (float)0.0928f));
        PartDefinition cube_r23 = l_right.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(20, 31).addBox(1.0f, -2.0f, 0.0f, 1.0f, 2.0f, 4.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)-2.75f, (float)3.0f, (float)-3.25f, (float)-0.432f, (float)0.0924f, (float)0.0928f));
        PartDefinition cube_r24 = l_right.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(18, 17).addBox(1.0f, -3.0f, 0.0f, 2.0f, 3.0f, 5.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)-3.0f, (float)3.5f, (float)-1.25f, (float)0.7897f, (float)0.0924f, (float)0.0928f));
        return LayerDefinition.create((MeshDefinition)meshdefinition, (int)64, (int)64);
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch, ageInTicks);
        // walk animation
        // idle animation
    }

    private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch, float pAgeInTicks) {
        pNetHeadYaw = Mth.clamp((float)pNetHeadYaw, (float)-30.0f, (float)30.0f);
        pHeadPitch = Mth.clamp((float)pHeadPitch, (float)-25.0f, (float)45.0f);
        this.head.yRot = pNetHeadYaw * ((float)Math.PI / 180);
        this.head.xRot = pHeadPitch * ((float)Math.PI / 180);
    }

    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.death_rat.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public ModelPart root() {
        return this.death_rat;
    }
}

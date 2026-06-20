import os
import re

replacements = {
    'fr.meulti.mbackrooms': 'com.litvin.backrooms',
    'import com.litvin.backrooms.entity.ai.DRAttackGoal;': 'import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;',
    'new DRAttackGoal': 'new MeleeAttackGoal',
    'import com.litvin.backrooms.sound.ModSounds;': 'import net.minecraft.sounds.SoundEvents;',
    'import com.litvin.backrooms.entity.client.ModModelLayers;': '',
    'import com.litvin.backrooms.entity.animations.ModAnimationsDefinitions;': '',
    '((LevelTwoEntity)((Object)entity))': '((LevelTwoEntity)entity)',
    'ModModelLayers.LEVEL_TWO_ENTITY_LAYER': 'com.litvin.backrooms.init.EntityInit.CRAWLER_LAYER',
    'BackroomsMod.getModResource("textures/entity/crawler.png")': 'new ResourceLocation(BackroomsMod.MODID, "textures/entity/crawler.png")',
    'this.m_142109_().m_171331_().forEach(ModelPart::m_233569_);': 'this.root().getAllParts().forEach(ModelPart::resetPose);',
    'Mth.m_14036_': 'Mth.clamp',
    'f_104204_': 'yRot',
    'f_104203_': 'xRot',
    'm_104306_': 'render',
    'm_174023_': 'bakeLayer',
    'm_7392_': 'render',
    'm_135353_': 'defineId',
    'f_135035_': 'BOOLEAN',
    'm_8119_': 'tick',
    'm_9236_': 'level',
    'm_5776_': 'isClientSide',
    'f_19796_': 'random',
    'm_188503_': 'nextInt',
    'm_216977_': 'start',
    'f_19797_': 'tickCount',
    'm_20089_': 'getPose',
    'f_267362_': 'walkAnimation',
    'm_267566_': 'setSpeed',
    'm_8097_': 'customServerAiStep',
    'f_19804_': 'entityData',
    'm_135372_': 'set',
    'f_21345_': 'goalSelector',
    'm_25352_': 'addGoal',
    'f_21346_': 'targetSelector',
    'm_21183_': 'createMobAttributes',
    'm_22268_': 'add',
    'f_22276_': 'MAX_HEALTH',
    'f_22277_': 'FOLLOW_RANGE',
    'f_22279_': 'MOVEMENT_SPEED',
    'f_22285_': 'FLYING_SPEED',
    'f_22282_': 'KNOCKBACK_RESISTANCE',
    'f_22281_': 'ATTACK_DAMAGE',
    'm_7515_': 'getAmbientSound',
    'm_7975_': 'getHurtSound',
    'm_5592_': 'getDeathSound',
    'm_21532_': 'isBaby',
    'm_171599_': 'addOrReplaceChild',
    'm_171558_': 'create',
    'm_171514_': 'texOffs',
    'm_171488_': 'addBox',
    'm_171423_': 'offsetAndRotation',
    'm_171419_': 'offset',
    'm_171565_': 'create',
    'm_6973_': 'setupAnim',
    'm_267799_': 'animateWalk',
    'm_233385_': 'animate',
    'm_7695_': 'renderToBuffer',
    'm_142109_': 'root',
    'm_171324_': 'getChild',
    'm_171576_': 'getRoot',
    'this.walkAnimation.setSpeed(f, 0.2f);': 'this.walkAnimation.setSpeed(f);',
    'this.entityData.set(ATTACKING, (Object)false);': 'this.entityData.set(ATTACKING, false);',
    'super.render((Mob)pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);': 'super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);'
}

for root, _, files in os.walk('src/main/java/com/litvin/backrooms/entity'):
    for f in files:
        if 'LevelTwo' in f:
            path = os.path.join(root, f)
            with open(path, 'r', encoding='utf-8') as file:
                content = file.read()
            for k, v in replacements.items():
                content = content.replace(k, v)
            
            # also regex to replace Sounds
            content = re.sub(r'private static final List<SoundEvent> SOUNDS = Arrays\.asList[^;]+;', 'private static final List<SoundEvent> SOUNDS = Arrays.asList(SoundEvents.ZOMBIE_AMBIENT);', content)
            # fix animations
            content = re.sub(r'this\.animateWalk\(ModAnimationsDefinitions[^;]+;', '// walk animation', content)
            content = re.sub(r'this\.animate\([^;]+;', '// idle animation', content)
            
            with open(path, 'w', encoding='utf-8') as file:
                file.write(content)

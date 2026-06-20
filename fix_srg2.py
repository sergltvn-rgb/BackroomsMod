import os
import re

replacements = {
    'm_171324_': 'getChild',
    'm_171576_': 'getRoot',
    'this.walkAnimation.setSpeed(f, 0.2f);': 'this.walkAnimation.setSpeed(f);',
    'this.entityData.set(ATTACKING, (Object)false);': 'this.entityData.set(ATTACKING, false);',
    'super.render((Mob)pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);': 'super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);'
}

for root, _, files in os.walk('src/main/java/com/litvin/backrooms/entity'):
    for f in files:
        if f.endswith('.java'):
            path = os.path.join(root, f)
            with open(path, 'r', encoding='utf-8') as file:
                content = file.read()
            for k, v in replacements.items():
                content = content.replace(k, v)
            with open(path, 'w', encoding='utf-8') as file:
                file.write(content)

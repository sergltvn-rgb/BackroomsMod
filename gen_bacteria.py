import json
import random

random.seed(42)

def gen_cubes(count, size_min, size_max, offset_range, base_uv=[0,0]):
    cubes = []
    for _ in range(count):
        sx = random.uniform(size_min[0], size_max[0])
        sy = random.uniform(size_min[1], size_max[1])
        sz = random.uniform(size_min[2], size_max[2])
        
        ox = random.uniform(-offset_range[0], offset_range[0])
        oy = random.uniform(-offset_range[1], offset_range[1])
        oz = random.uniform(-offset_range[2], offset_range[2])
        
        cubes.append({
            'origin': [ox - sx/2, oy - sy/2, oz - sz/2],
            'size': [sx, sy, sz],
            'uv': base_uv
        })
    return cubes

geo = {
  'format_version': '1.12.0',
  'minecraft:geometry': [
    {
      'description': {
        'identifier': 'geometry.bacteria',
        'texture_width': 64,
        'texture_height': 64,
        'visible_bounds_width': 4,
        'visible_bounds_height': 4,
        'visible_bounds_offset': [0, 1.5, 0]
      },
      'bones': [
        {
          'name': 'root',
          'pivot': [0, 0, 0]
        },
        {
          'name': 'body',
          'parent': 'root',
          'pivot': [0, 24, 0],
          'cubes': gen_cubes(15, [0.5, 5, 0.5], [1.5, 20, 1.5], [3, 10, 3])
        },
        {
          'name': 'head',
          'parent': 'body',
          'pivot': [0, 42, 0],
          'cubes': gen_cubes(10, [0.5, 0.5, 0.5], [1.5, 8, 1.5], [4, 4, 4])
        },
        {
          'name': 'rightArm',
          'parent': 'body',
          'pivot': [-4, 40, 0],
          'cubes': gen_cubes(8, [0.5, 5, 0.5], [1.2, 25, 1.2], [1, 10, 1], base_uv=[8, 0])
        },
        {
          'name': 'leftArm',
          'parent': 'body',
          'pivot': [4, 40, 0],
          'cubes': gen_cubes(8, [0.5, 5, 0.5], [1.2, 25, 1.2], [1, 10, 1], base_uv=[8, 0])
        },
        {
          'name': 'rightLeg',
          'parent': 'root',
          'pivot': [-2, 24, 0],
          'cubes': gen_cubes(8, [0.5, 5, 0.5], [1.5, 20, 1.5], [1, 10, 1], base_uv=[16, 0])
        },
        {
          'name': 'leftLeg',
          'parent': 'root',
          'pivot': [2, 24, 0],
          'cubes': gen_cubes(8, [0.5, 5, 0.5], [1.5, 20, 1.5], [1, 10, 1], base_uv=[16, 0])
        }
      ]
    }
  ]
}

def fix_origins(bone, pivot):
    if 'cubes' in bone:
        for c in bone['cubes']:
            c['origin'][0] += pivot[0]
            c['origin'][1] += pivot[1]
            c['origin'][2] += pivot[2]

for b in geo['minecraft:geometry'][0]['bones']:
    fix_origins(b, b.get('pivot', [0,0,0]))

with open('F:/BackroomsMod/src/main/resources/assets/backroomsmod/geo/bacteria.geo.json', 'w') as f:
    json.dump(geo, f, indent=2)

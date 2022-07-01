import {
  ASMAPI,
  CoreMods,
  InsnList,
  InsnNode,
  JumpInsnNode,
  LabelNode,
  MethodNode,
  Opcodes,
  VarInsnNode
} from "../coremods";

// level parameter must be the first, method may not be static
function patchRandomTick(method: MethodNode): MethodNode {
  const label = new LabelNode();
  const target = new InsnList();
  target.add(new VarInsnNode(Opcodes.ALOAD, 1));
  target.add(ASMAPI.buildMethodCall(
    'io/github/noeppi_noeppi/mods/bingolobby/core/CoreRandomTick',
    'randomTick', '(Lnet/minecraft/world/level/Level;)Z',
    ASMAPI.MethodType.STATIC
  ));
  target.add(new JumpInsnNode(Opcodes.IFEQ, label));
  target.add(new InsnNode(Opcodes.RETURN));
  target.add(label);

  method.instructions.insert(target);

  return method;
}

function initializeCoreMod(): CoreMods {
  return {
    'random_tick_block': {
      'target': {
        'type': 'METHOD',
        'class': 'net.minecraft.world.level.block.state.BlockBehaviour$BlockStateBase',
        'methodName': 'm_222972_',
        'methodDesc': '(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V'
      },
      'transformer': patchRandomTick
    },
    'random_tick_fluid': {
      'target': {
        'type': 'METHOD',
        'class': 'net.minecraft.world.level.material.FluidState',
        'methodName': 'm_230562_',
        'methodDesc': '(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V'
      },
      'transformer': patchRandomTick
    }
  }
}

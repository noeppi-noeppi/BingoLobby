"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var coremods_1 = require("../coremods");
// level parameter must be the first, method may not be static
function patchRandomTick(method) {
    var label = new coremods_1.LabelNode();
    var target = new coremods_1.InsnList();
    target.add(new coremods_1.VarInsnNode(coremods_1.Opcodes.ALOAD, 1));
    target.add(coremods_1.ASMAPI.buildMethodCall('io/github/noeppi_noeppi/mods/bingolobby/core/CoreRandomTick', 'randomTick', '(Lnet/minecraft/world/level/Level;)Z', coremods_1.ASMAPI.MethodType.STATIC));
    target.add(new coremods_1.JumpInsnNode(coremods_1.Opcodes.IFEQ, label));
    target.add(new coremods_1.InsnNode(coremods_1.Opcodes.RETURN));
    target.add(label);
    method.instructions.insert(target);
    return method;
}
function initializeCoreMod() {
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
    };
}

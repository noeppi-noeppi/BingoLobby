package io.github.noeppi_noeppi.mods.bingolobby.mixin;

import io.github.noeppi_noeppi.mods.bingolobby.pregen.ServerPreTickQueue;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Queue;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
    
    @Inject(
            method = "Lnet/minecraft/server/MinecraftServer;tick(Ljava/util/function/BooleanSupplier;)V",
            at = @At("HEAD"),
            cancellable = true
    )
     public void tick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        Queue<Supplier<Boolean>> queue = ServerPreTickQueue.getQueue((MinecraftServer) (Object) this);
        if (queue != null) {
            Supplier<Boolean> task;
            while ((task = queue.poll()) != null) {
                if (task.get()) {
                    ci.cancel();
                    return;
                }
            }
        }
     }
}

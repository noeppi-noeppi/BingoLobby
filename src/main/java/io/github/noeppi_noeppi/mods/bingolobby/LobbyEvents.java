package io.github.noeppi_noeppi.mods.bingolobby;

import io.github.noeppi_noeppi.libx.event.RandomTickEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class LobbyEvents {

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().level.dimension().equals(ModDimensions.LOBBY_DIMENSION) && !event.getPlayer().hasPermissions(2)) {
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
        }
    }
    
    @SubscribeEvent
    public void mobGrief(EntityMobGriefingEvent event) {
        try {
            //noinspection ConstantConditions
            if (event.getEntity() != null && event.getEntity().level != null && event.getEntity().level.dimension() != null) {
                if (event.getEntity().level.dimension().equals(ModDimensions.LOBBY_DIMENSION)) {
                    event.setResult(Event.Result.DENY);
                }
            }
        } catch(NullPointerException e) {
            //
        }
    }
    
    @SubscribeEvent
    public void explode(ExplosionEvent.Start event) {
        if (event.getWorld().dimension().equals(ModDimensions.LOBBY_DIMENSION)) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void blockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer().level.dimension().equals(ModDimensions.LOBBY_DIMENSION) && !event.getPlayer().hasPermissions(2)) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void blockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getWorld() instanceof Level && ((Level) event.getWorld()).dimension().equals(ModDimensions.LOBBY_DIMENSION)) {
            if (!(event.getEntity() instanceof Player) || !event.getEntity().hasPermissions(2)) {
                event.setCanceled(true);
            }
        }
    }
    
    @SubscribeEvent
    public void blockMultiPlace(BlockEvent.EntityMultiPlaceEvent event) {
        if (event.getWorld() instanceof Level && ((Level) event.getWorld()).dimension().equals(ModDimensions.LOBBY_DIMENSION)) {
            if (!(event.getEntity() instanceof Player) || !event.getEntity().hasPermissions(2)) {
                event.setCanceled(true);
            }
        }
    }
    
    @SubscribeEvent
    public void farmlandTrample(BlockEvent.FarmlandTrampleEvent event) {
        if (event.getEntity().level.dimension().equals(ModDimensions.LOBBY_DIMENSION)) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void cropGrow(BlockEvent.CropGrowEvent.Pre event) {
        if (event.getWorld() instanceof Level && ((Level) event.getWorld()).dimension().equals(ModDimensions.LOBBY_DIMENSION)) {
            event.setResult(Event.Result.DENY);
        }
    }
    
    @SubscribeEvent
    public void cropGrow(BlockEvent.BlockToolInteractEvent event) {
        if (event.getPlayer().level.dimension().equals(ModDimensions.LOBBY_DIMENSION) && !event.getPlayer().hasPermissions(2)) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void mobSpawnAttempt(LivingSpawnEvent.CheckSpawn event) {
        Level level;
        if (event.getWorld() instanceof Level) level = (Level) event.getWorld();
        else level = event.getEntity().level;
        if (level != null && level.dimension().equals(ModDimensions.LOBBY_DIMENSION) && !(event.getEntity() instanceof Player)) {
            event.setResult(Event.Result.DENY);
        }
    }
    
    @SubscribeEvent
    public void mobSpawn(LivingSpawnEvent.SpecialSpawn event) {
        Level level;
        if (event.getWorld() instanceof Level) level = (Level) event.getWorld();
        else level = event.getEntity().level;
        if (level != null && level.dimension().equals(ModDimensions.LOBBY_DIMENSION) && !(event.getEntity() instanceof Player)) {
            if (event.getSpawnReason() != MobSpawnType.SPAWN_EGG && event.getSpawnReason() != MobSpawnType.BUCKET
                    && event.getSpawnReason() != MobSpawnType.MOB_SUMMONED && event.getSpawnReason() != MobSpawnType.COMMAND) {
                if (event.isCancelable()) {
                    event.setCanceled(true);
                }
            }
        }
    }
    
    @SubscribeEvent
    public void livingAttack(LivingAttackEvent event) {
        if (!event.getSource().isBypassInvul() && event.getEntity().level.dimension().equals(ModDimensions.LOBBY_DIMENSION) && (!(event.getSource().getEntity() instanceof Player) || !event.getSource().getEntity().hasPermissions(2))) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void livingHurt(LivingHurtEvent event) {
        if (!event.getSource().isBypassInvul() && event.getEntity().level.dimension().equals(ModDimensions.LOBBY_DIMENSION) && (event.getEntity() instanceof Player || !(event.getSource().getEntity() instanceof Player) || !event.getSource().getEntity().hasPermissions(2))) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.level.isClientSide && !event.player.isDeadOrDying() && event.player.tickCount % 20 == 0 && event.player.level.dimension().equals(ModDimensions.LOBBY_DIMENSION)) {
            event.player.setHealth(20);
            event.player.getFoodData().setFoodLevel(20);
            event.player.setAirSupply(event.player.getMaxAirSupply());
            event.player.setRemainingFireTicks(0);
            if (event.player.getY() < -3 && event.player instanceof ServerPlayer) {
                ModDimensions.teleportToLobby((ServerPlayer) event.player, false);
            }
        }
    }
    
    @SubscribeEvent
    public void levelTick(TickEvent.WorldTickEvent event) {
        if (!event.world.dimension().equals(ModDimensions.LOBBY_DIMENSION) && event.world instanceof ServerLevel level) {
            level.setWeatherParameters(6000, 0, false, false);
        }
    }
    
    @SubscribeEvent
    public void randomTickBlock(RandomTickEvent.Block event) {
        if (event.getLevel().dimension().equals(ModDimensions.LOBBY_DIMENSION)) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void randomTickFluid(RandomTickEvent.Fluid event) {
        if (event.getLevel().dimension().equals(ModDimensions.LOBBY_DIMENSION)) {
            event.setCanceled(true);
        }
    }
}

package io.github.noeppi_noeppi.mods.bingolobby;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class LobbyEvents {

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        if (event.getEntity().level.dimension().equals(ModDimensions.LOBBY_DIMENSION) && !event.getEntity().hasPermissions(2)) {
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
        if (event.getLevel().dimension().equals(ModDimensions.LOBBY_DIMENSION)) {
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
        if (event.getLevel() instanceof Level && ((Level) event.getLevel()).dimension().equals(ModDimensions.LOBBY_DIMENSION)) {
            if (!(event.getEntity() instanceof Player) || !event.getEntity().hasPermissions(2)) {
                event.setCanceled(true);
            }
        }
    }
    
    @SubscribeEvent
    public void blockMultiPlace(BlockEvent.EntityMultiPlaceEvent event) {
        if (event.getLevel() instanceof Level && ((Level) event.getLevel()).dimension().equals(ModDimensions.LOBBY_DIMENSION)) {
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
        if (event.getLevel() instanceof Level && ((Level) event.getLevel()).dimension().equals(ModDimensions.LOBBY_DIMENSION)) {
            event.setResult(Event.Result.DENY);
        }
    }
    
    @SubscribeEvent
    public void cropGrow(BlockEvent.BlockToolModificationEvent event) {
        if (event.getLevel() instanceof ServerLevel level && level.dimension().equals(ModDimensions.LOBBY_DIMENSION) && (event.getPlayer() == null || !event.getPlayer().hasPermissions(2))) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void mobSpawnAttempt(MobSpawnEvent.FinalizeSpawn event) {
        Level level;
        if (event.getLevel() instanceof Level) level = (Level) event.getLevel();
        else level = event.getEntity().level;
        if (level != null && level.dimension().equals(ModDimensions.LOBBY_DIMENSION)) {
            event.setCanceled(true);
            event.setSpawnCancelled(true);
        }
    }
    
    @SubscribeEvent
    public void livingAttack(LivingAttackEvent event) {
        if (!event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY) && event.getEntity().level.dimension().equals(ModDimensions.LOBBY_DIMENSION) && (!(event.getSource().getEntity() instanceof Player) || !event.getSource().getEntity().hasPermissions(2))) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void livingHurt(LivingHurtEvent event) {
        if (!event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY) && event.getEntity().level.dimension().equals(ModDimensions.LOBBY_DIMENSION) && (event.getEntity() instanceof Player || !(event.getSource().getEntity() instanceof Player) || !event.getSource().getEntity().hasPermissions(2))) {
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
    public void levelTick(TickEvent.LevelTickEvent event) {
        if (!event.level.dimension().equals(ModDimensions.LOBBY_DIMENSION) && event.level instanceof ServerLevel level) {
            level.setWeatherParameters(6000, 0, false, false);
        }
    }
}

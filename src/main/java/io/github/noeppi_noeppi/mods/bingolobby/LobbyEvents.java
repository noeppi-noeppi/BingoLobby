package io.github.noeppi_noeppi.mods.bingolobby;

import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
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
        if (event.getPlayer().world.getDimensionKey().equals(ModDimensions.LOBBY_DIMENSION) && !event.getPlayer().hasPermissionLevel(2)) {
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
        }
    }
    
    @SubscribeEvent
    public void mobGrief(EntityMobGriefingEvent event) {
        try {
            //noinspection ConstantConditions
            if (event.getEntity() != null && event.getEntity().world != null && event.getEntity().world.getDimensionKey() != null) {
                if (event.getEntity().world.getDimensionKey().equals(ModDimensions.LOBBY_DIMENSION)) {
                    event.setResult(Event.Result.DENY);
                }
            }
        } catch(NullPointerException e) {
            //
        }
    }
    
    @SubscribeEvent
    public void explode(ExplosionEvent.Start event) {
        if (event.getWorld().getDimensionKey().equals(ModDimensions.LOBBY_DIMENSION)) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void blockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer().world.getDimensionKey().equals(ModDimensions.LOBBY_DIMENSION) && !event.getPlayer().hasPermissionLevel(2)) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void blockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getWorld() instanceof World && ((World) event.getWorld()).getDimensionKey().equals(ModDimensions.LOBBY_DIMENSION)) {
            if (!(event.getEntity() instanceof PlayerEntity) || !event.getEntity().hasPermissionLevel(2)) {
                event.setCanceled(true);
            }
        }
    }
    
    @SubscribeEvent
    public void blockMultiPlace(BlockEvent.EntityMultiPlaceEvent event) {
        if (event.getWorld() instanceof World && ((World) event.getWorld()).getDimensionKey().equals(ModDimensions.LOBBY_DIMENSION)) {
            if (!(event.getEntity() instanceof PlayerEntity) || !event.getEntity().hasPermissionLevel(2)) {
                event.setCanceled(true);
            }
        }
    }
    
    @SubscribeEvent
    public void farmlandTrample(BlockEvent.FarmlandTrampleEvent event) {
        if (event.getEntity().world.getDimensionKey().equals(ModDimensions.LOBBY_DIMENSION)) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void cropGrow(BlockEvent.CropGrowEvent.Pre event) {
        if (event.getWorld() instanceof World && ((World) event.getWorld()).getDimensionKey().equals(ModDimensions.LOBBY_DIMENSION)) {
            event.setResult(Event.Result.DENY);
        }
    }
    
    @SubscribeEvent
    public void cropGrow(BlockEvent.BlockToolInteractEvent event) {
        if (event.getPlayer().world.getDimensionKey().equals(ModDimensions.LOBBY_DIMENSION) && !event.getPlayer().hasPermissionLevel(2)) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void mobSpawnAttempt(LivingSpawnEvent.CheckSpawn event) {
        World world;
        if (event.getWorld() instanceof World) world = (World) event.getWorld();
        else world = event.getEntity().world;
        if (world != null && world.getDimensionKey().equals(ModDimensions.LOBBY_DIMENSION) && !(event.getEntity() instanceof PlayerEntity)) {
            event.setResult(Event.Result.DENY);
        }
    }
    
    @SubscribeEvent
    public void mobSpawn(LivingSpawnEvent.SpecialSpawn event) {
        World world;
        if (event.getWorld() instanceof World) world = (World) event.getWorld();
        else world = event.getEntity().world;
        if (world != null && world.getDimensionKey().equals(ModDimensions.LOBBY_DIMENSION) && !(event.getEntity() instanceof PlayerEntity)) {
            if (event.getSpawnReason() != SpawnReason.SPAWN_EGG && event.getSpawnReason() != SpawnReason.BUCKET
                    && event.getSpawnReason() != SpawnReason.MOB_SUMMONED && event.getSpawnReason() != SpawnReason.COMMAND) {
                if (event.isCancelable()) {
                    event.setCanceled(true);
                }
            }
        }
    }
    
    @SubscribeEvent
    public void livingAttack(LivingAttackEvent event) {
        if (!event.getSource().canHarmInCreative() && event.getEntity().world.getDimensionKey().equals(ModDimensions.LOBBY_DIMENSION) && (!(event.getSource().getTrueSource() instanceof PlayerEntity) || !event.getSource().getTrueSource().hasPermissionLevel(2))) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void livingHurt(LivingHurtEvent event) {
        if (!event.getSource().canHarmInCreative() && event.getEntity().world.getDimensionKey().equals(ModDimensions.LOBBY_DIMENSION) && (event.getEntity() instanceof PlayerEntity || !(event.getSource().getTrueSource() instanceof PlayerEntity) || !event.getSource().getTrueSource().hasPermissionLevel(2))) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.getEntityWorld().isRemote && !event.player.getShouldBeDead() && event.player.ticksExisted % 20 == 0 && event.player.world.getDimensionKey().equals(ModDimensions.LOBBY_DIMENSION)) {
            event.player.setHealth(20);
            event.player.getFoodStats().setFoodLevel(20);
            event.player.setAir(event.player.getMaxAir());
            event.player.forceFireTicks(0);
        }
    }
}

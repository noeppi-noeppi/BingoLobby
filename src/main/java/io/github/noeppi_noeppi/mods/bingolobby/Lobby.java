package io.github.noeppi_noeppi.mods.bingolobby;

import net.minecraft.network.chat.Component;
import org.moddingx.libx.util.game.ServerMessages;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.network.BongoMessageType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Lobby extends SavedData {
    
    public static final String ID = BingoLobby.getInstance().modid;

    private static Lobby clientInstance;
    private static Minecraft mc = null;
    
    public static Lobby get(Level level) {
        if (!level.isClientSide) {
            DimensionDataStorage storage = ((ServerLevel) level).getServer().overworld().getDataStorage();
            Lobby lobby = storage.computeIfAbsent(Lobby::new, Lobby::new, ID);
            lobby.level = (ServerLevel) level;
            return lobby;
        } else {
            return clientInstance == null ? new Lobby() : clientInstance;
        }
    }

    public static void updateClient(Lobby lobby, @SuppressWarnings("unused") BongoMessageType bongoMessageType) {
        clientInstance = lobby;
        if (mc == null) {
            mc = Minecraft.getInstance();
        }
    }

    private ServerLevel level;
    private final Set<UUID> vips;
    private final Set<DyeColor> vipTeams;
    private int maxPlayers;
    private int countdown;

    public Lobby() {
        this.vips = new HashSet<>();
        this.vipTeams = new HashSet<>();
        this.vipTeams.add(DyeColor.YELLOW);
        this.maxPlayers = -1;
        this.countdown = -1;
    }
    
    public Lobby(CompoundTag nbt) {
        this();
        this.load(nbt);
    }

    public boolean vip(Player player) {
        return this.vip(player.getGameProfile().getId());
    }
    
    public boolean vip(UUID uid) {
        return this.vips.contains(uid);
    }
    
    public void setVip(Player player, boolean setVip) {
        this.setVip(player.getGameProfile().getId(), setVip);
    }
    
    public void setVip(UUID uid, boolean setVip) {
        if (setVip) {
            this.vips.add(uid);
        } else {
            this.vips.remove(uid);
        }
        this.setDirty();
    }
    
    public boolean vipTeam(DyeColor team) {
        return this.vipTeams.contains(team);
    }

    public void setVipTeam(DyeColor team, boolean setVip) {
        if (setVip) {
            this.vipTeams.add(team);
        } else {
            this.vipTeams.remove(team);
        }
        this.setDirty();
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        this.setDirty();
    }

    @Nullable
    public MutableComponent canAccess(Player player, @Nullable Team team) {
        if (team == null) {
            return null;
        } else if (this.vipTeam(team.color) && !this.vip(player)) {
            return Component.translatable("bingolobby.nojoin.novip");
        } else if (this.maxPlayers >= 0 && team.getPlayers().size() >= this.maxPlayers) {
            return Component.translatable("bingolobby.nojoin.full");
        }
        return null;
    }

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag compound) {
        ListTag vipList = new ListTag();
        for (UUID uid : this.vips) {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("player", uid);
            vipList.add(entry);
        }
        compound.put("vips", vipList);
        
        compound.putIntArray("vipTeams", this.vipTeams.stream().mapToInt(DyeColor::getId).toArray());
        
        compound.putInt("maxPlayers", this.maxPlayers);
        compound.putInt("countdown", this.countdown);
        
        return compound;
    }

    public void load(@Nonnull CompoundTag nbt) {
        this.vips.clear();
        if (nbt.contains("vips", Tag.TAG_LIST)) {
            ListTag vipList = nbt.getList("vips", Tag.TAG_COMPOUND);
            for (int i = 0; i < vipList.size(); i++) {
                CompoundTag entry = vipList.getCompound(i);
                this.vips.add(entry.getUUID("player"));
            }
        }
        
        this.vipTeams.clear();
        if (nbt.contains("vipTeams", Tag.TAG_INT_ARRAY)) {
            int[] vipTeamsList = nbt.getIntArray("vipTeams");
            this.vipTeams.addAll(Arrays.stream(vipTeamsList).mapToObj(DyeColor::byId).collect(Collectors.toSet()));
        }
        
        this.maxPlayers = nbt.getInt("maxPlayers");
        this.countdown = nbt.getInt("countdown");
    }

    public void setCountdown(int seconds) {
        this.countdown = seconds < 0 ? -1 : seconds;
    }
    
    public void tickCountdown() {
        if (this.level != null) {
            Bongo bongo = Bongo.get(this.level);
            boolean dirty = false;
            if (!bongo.active() || bongo.running() || bongo.won()) {
                this.countdown = -1;
                dirty = true;
            }
            if (this.countdown > 0) {
                this.countdown -= 1;
                if ((this.countdown <= 10 && this.countdown >= 1) || this.countdown < 60 && this.countdown > 10 && this.countdown % 10 == 0) {
                    ServerMessages.broadcast(this.level, Component.translatable("bingolobby.countdown.seconds",
                            Component.literal(Integer.toString(this.countdown)).withStyle(Style.EMPTY.applyFormats(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD))
                    ).withStyle(ChatFormatting.DARK_AQUA));
                } else if (this.countdown >= 60 && this.countdown % 60 == 0) {
                    ServerMessages.broadcast(this.level, Component.translatable("bingolobby.countdown.minutes",
                            Component.literal(Integer.toString(this.countdown / 60)).withStyle(Style.EMPTY.applyFormats(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD))
                    ).withStyle(ChatFormatting.DARK_AQUA));
                } else if (this.countdown == 0) {
                    this.countdown = -1;
                    bongo.start();
                }
                dirty = true;
            }
            if (dirty) {
                this.setDirty();
            }
        }
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public int getCountdown() {
        return this.countdown;
    }
    
    @Override
    public void setDirty() {
        this.setChanged(false);
    }
    
    public void setChanged(boolean suppressLobbySync) {
        super.setDirty();
        if (this.level != null && !suppressLobbySync) {
            BingoLobby.getNetwork().updateLobby(this.level);
        }
    }
}

package io.github.noeppi_noeppi.mods.bingolobby;

import io.github.noeppi_noeppi.libx.util.ServerMessages;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.network.BongoMessageType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Lobby extends WorldSavedData {
    
    public static final String ID = BingoLobby.getInstance().modid;

    private static Lobby clientInstance;
    private static Minecraft mc = null;
    
    public static Lobby get(World world) {
        if (!world.isRemote) {
            DimensionSavedDataManager storage = ((ServerWorld) world).getServer().getOverworld().getSavedData();
            Lobby lobby = storage.getOrCreate(Lobby::new, ID);
            lobby.world = (ServerWorld) world;
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

    private ServerWorld world;
    private final Set<UUID> vips;
    private final Set<DyeColor> vipTeams;
    private int maxPlayers;
    private int countdown;
    
    public Lobby() {
        this(ID);
    }

    public Lobby(String name) {
        super(name);
        this.vips = new HashSet<>();
        this.vips.add(UUID.fromString("3358ddae-3a41-4ba0-bdfa-ee54b6c55cf5"));
        this.vipTeams = new HashSet<>();
        this.vipTeams.add(DyeColor.YELLOW);
        this.maxPlayers = -1;
        this.countdown = -1;
    }

    public boolean vip(PlayerEntity player) {
        return this.vip(player.getGameProfile().getId());
    }
    
    public boolean vip(UUID uid) {
        return this.vips.contains(uid);
    }
    
    public void setVip(PlayerEntity player, boolean setVip) {
        this.setVip(player.getGameProfile().getId(), setVip);
    }
    
    public void setVip(UUID uid, boolean setVip) {
        if (setVip) {
            this.vips.add(uid);
        } else {
            this.vips.remove(uid);
        }
        this.markDirty();
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
        this.markDirty();
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        this.markDirty();
    }

    @Nullable
    public IFormattableTextComponent canAccess(PlayerEntity player, @Nullable Team team) {
        if (team == null) {
            return null;
        } else if (this.vipTeam(team.color) && !this.vip(player)) {
            return new TranslationTextComponent("bingolobby.nojoin.novip");
        } else if (this.maxPlayers >= 0 && team.getPlayers().size() >= this.maxPlayers) {
            return new TranslationTextComponent("bingolobby.nojoin.full");
        }
        return null;
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT nbt) {
        ListNBT vipList = new ListNBT();
        for (UUID uid : this.vips) {
            CompoundNBT entry = new CompoundNBT();
            entry.putUniqueId("player", uid);
            vipList.add(entry);
        }
        nbt.put("vips", vipList);
        
        nbt.putIntArray("vipTeams", this.vipTeams.stream().mapToInt(DyeColor::getId).toArray());
        
        nbt.putInt("maxPlayers", this.maxPlayers);
        nbt.putInt("countdown", this.countdown);
        
        return nbt;
    }

    @Override
    public void read(@Nonnull CompoundNBT nbt) {
        this.vips.clear();
        if (nbt.contains("vips", Constants.NBT.TAG_LIST)) {
            ListNBT vipList = nbt.getList("vips", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < vipList.size(); i++) {
                CompoundNBT entry = vipList.getCompound(i);
                this.vips.add(entry.getUniqueId("player"));
            }
        }
        
        this.vipTeams.clear();
        if (nbt.contains("vipTeams", Constants.NBT.TAG_INT_ARRAY)) {
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
        if (this.world != null) {
            Bongo bongo = Bongo.get(this.world);
            boolean dirty = false;
            if (!bongo.active() || bongo.running() || bongo.won()) {
                this.countdown = -1;
                dirty = true;
            }
            if (this.countdown > 0) {
                this.countdown -= 1;
                if ((this.countdown <= 10 && this.countdown >= 1) || this.countdown < 60 && this.countdown > 10 && this.countdown % 10 == 0) {
                    ServerMessages.broadcast(this.world,
                            new TranslationTextComponent("bingolobby.countdown.seconds",
                                    new StringTextComponent(Integer.toString(this.countdown)).mergeStyle(Style.EMPTY.mergeWithFormatting(TextFormatting.DARK_PURPLE, TextFormatting.BOLD))
                            ).mergeStyle(TextFormatting.DARK_AQUA));
                } else if (this.countdown >= 60 && this.countdown % 60 == 0) {
                    ServerMessages.broadcast(this.world,
                            new TranslationTextComponent("bingolobby.countdown.minutes",
                                    new StringTextComponent(Integer.toString(this.countdown / 60)).mergeStyle(Style.EMPTY.mergeWithFormatting(TextFormatting.DARK_PURPLE, TextFormatting.BOLD))
                            ).mergeStyle(TextFormatting.DARK_AQUA)
                    );
                } else if (this.countdown == 0) {
                    this.countdown = -1;
                    bongo.start();
                }
                dirty = true;
            }
            if (dirty) {
                this.markDirty();
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
    public void markDirty() {
        this.markDirty(false);
    }
    
    public void markDirty(boolean suppressLobbySync) {
        super.markDirty();
        if (this.world != null && !suppressLobbySync) {
            BingoLobby.getNetwork().updateLobby(this.world);
        }
    }
}

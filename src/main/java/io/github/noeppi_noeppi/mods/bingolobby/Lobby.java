package io.github.noeppi_noeppi.mods.bingolobby;

import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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
    
    public static Lobby get(World world) {
        if (!world.isRemote) {
            DimensionSavedDataManager storage = ((ServerWorld) world).getServer().func_241755_D_().getSavedData();
            Lobby lobby = storage.getOrCreate(Lobby::new, ID);
            lobby.world = (ServerWorld) world;
            return lobby;
        } else {
            throw new IllegalStateException("Lobby can not be accessed client side.");
        }
    }

    @SuppressWarnings("unused")
    private ServerWorld world;
    private final Set<UUID> vips;
    private final Set<DyeColor> vipTeams;
    private int maxPlayers;
    
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
    }
}

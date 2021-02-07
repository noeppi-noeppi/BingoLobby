package io.github.noeppi_noeppi.mods.bingolobby.pregen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.command.Commands;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.datafix.codec.DatapackCodec;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.*;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.storage.SaveFormat;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PregenOptions {
    
    private final Object lock = new Object();
    private final Object wait = new Object();
    
    private final long seed;
    private final Path targetPath;
    private final Map<RegistryKey<World>, List<AnvilCoordinates>> anvilFiles;
    
    private boolean done = false;
    private boolean success = false;
    private Map<RegistryKey<World>, Path> worldPathMap = null;

    public PregenOptions(long seed, Path targetPath, Map<RegistryKey<World>, List<AnvilCoordinates>> anvilFiles) {
        this.seed = seed;
        this.targetPath = targetPath;
        //noinspection UnstableApiUsage
        this.anvilFiles = anvilFiles.entrySet().stream()
                .map(entry -> Pair.of(entry.getKey(), ImmutableList.copyOf(entry.getValue())))
                .collect(ImmutableMap.toImmutableMap(Pair::getKey, Pair::getValue));
    }

    public long getSeed() {
        return this.seed;
    }

    public Path getTargetPath() {
        return this.targetPath;
    }

    public Set<RegistryKey<World>> getWorlds() {
        return this.anvilFiles.keySet();
    }
    
    public Map<RegistryKey<World>, List<AnvilCoordinates>> getAnvilFiles() {
        return this.anvilFiles;
    }
    
    public List<AnvilCoordinates> getAnvilFiles(RegistryKey<World> world) {
        return this.anvilFiles.get(world);
    }

    public SaveFormat.LevelSave getLevelSave(String world) {
        try {
            SaveFormat saveFormat = SaveFormat.create(this.targetPath);
            return saveFormat.getLevelSave(world);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public WorldSettings getWorldSettings(String world, DatapackCodec datapacks) {
        return new WorldSettings(world, GameType.SURVIVAL, false, Difficulty.NORMAL, true, new GameRules(), datapacks);
    }
    
    public DimensionGeneratorSettings getGeneratorSettings(DynamicRegistries registries) {
        /*Registry<DimensionType> dimensionRegistry = registries.getRegistry(Registry.DIMENSION_TYPE_KEY);
        Registry<Biome> biomeRegistry = registries.getRegistry(Registry.BIOME_KEY);
        Registry<DimensionSettings> settingsRegistry = registries.getRegistry(Registry.NOISE_SETTINGS_KEY);
        SimpleRegistry<Dimension> registry = DimensionType.getDefaultSimpleRegistry(dimensionRegistry, biomeRegistry, settingsRegistry, this.seed);
        return new DimensionGeneratorSettings(this.seed, true, false, registry);*/
        Properties properties = new Properties();
        properties.put("level-seed", Long.toString(this.seed));
        return DimensionGeneratorSettings.func_242753_a(registries, properties);
    }
    
    public DataPackRegistries getDataRegistries() {
        return new DataPackRegistries(Commands.EnvironmentType.DEDICATED, 4);
    }
    
    public void setWorldPathMap(Map<RegistryKey<World>, Path> map) {
        synchronized (this.lock) {
            if (this.worldPathMap != null) {
                throw new IllegalStateException("World path data has already been set. Was he same options object used on two pregen servers?");
            }
            this.worldPathMap = ImmutableMap.copyOf(map);
        }
    }
    
    public Map<RegistryKey<World>, Path> getWorldPathMap() {
        synchronized (this.lock) {
            if (this.worldPathMap == null) {
                throw new IllegalStateException("World path data is not available. Is the pregen server done?");
            }
            return this.worldPathMap;
        }
    }
    
    public void notifyServerDone(boolean success) {
        synchronized (this.wait) {
            if (!this.done) {
                this.done = true;
                this.success = success;
                this.wait.notifyAll();
            }
        }
    }
    
    public boolean waitForServer() {
        synchronized (this.wait) {
            if (this.done) {
                return this.success;
            } else {
                try {
                    this.wait.wait();
                    return this.success;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
    }
    
    public boolean isRegionFile(RegistryKey<World> world, Path path) {
        for (AnvilCoordinates coords : this.anvilFiles.get(world)) {
            if (coords.isRegionFile(path)) {
                return true;
            }
        }
        return false;
    }
}

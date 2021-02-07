package io.github.noeppi_noeppi.mods.bingolobby.pregen;

import net.minecraft.util.math.ChunkPos;

import java.io.File;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnvilCoordinates {

    private static final Pattern chunkFilePattern = Pattern.compile("^c.(-?\\d+).(-?\\d+).mcc$");
    
    public final int x;
    public final int z;

    public AnvilCoordinates(int x, int z) {
        this.x = x;
        this.z = z;
    }
    
    public ChunkPos[] getChunks() {
        ChunkPos[] chunks = new ChunkPos[32 * 32];
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
                chunks[(i * 32) + j] = new ChunkPos((this.x * 32) + i, (this.z * 32) + j);
            }
        }
        return chunks;
    }
    
    public boolean isRegionFile(Path path) {
        String fileName = path.getFileName().toString();
        if (fileName.startsWith("/") || fileName.startsWith(File.separator)) {
            fileName = fileName.substring(1);
        }
        if (fileName.endsWith("/") || fileName.endsWith(File.separator)) {
            fileName = fileName.substring(0, fileName.length() - 1);
        }
        if (fileName.equals("r." + this.x + "." + this.z + ".mca")) {
            return true;
        } else if (fileName.endsWith(".mcc")) {
            // A chunk file. Used for chunks larger than 1MB
            Matcher match = chunkFilePattern.matcher(fileName);
            if (match.matches()) {
                try {
                    int chunkX = Integer.parseInt(match.group(1));
                    int chunkZ = Integer.parseInt(match.group(2));
                    return chunkX >= 32 * this.x && chunkX < 32 * (this.x + 1) && chunkZ >= 32 * this.z && chunkZ < 32 * (this.z + 1);
                } catch (NumberFormatException e) {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}

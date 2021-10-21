package io.github.noeppi_noeppi.mods.bingolobby;

import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;

public class WorldPresetManager {

    public static void copyWorld(Path target) {
        Path sourceZip = FMLPaths.CONFIGDIR.get().resolve("bingolobby-preset.zip");
        if (Files.isRegularFile(sourceZip) && !Files.exists(target)) {
            try (FileSystem fs = FileSystems.newFileSystem(URI.create("jar:" + sourceZip.toUri()), Map.of())) {
                Files.createDirectories(target.getParent());
                Files.walkFileTree(fs.getPath("/"), new FileVisitor<>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attributes) throws IOException {
                        Files.createDirectory(target.resolve(fs.getPath("/").relativize(path).toString()));
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) throws IOException {
                        Files.copy(path, target.resolve(fs.getPath("/").relativize(path).toString()));
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path path, IOException e) throws IOException {
                        throw e;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path path, IOException e) throws IOException {
                        if (e != null) throw e;
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException("Failed to copy BingoLobby preset", e);
            }
        }
    }
}

package learn.jfx;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public class Analyzer {
    private HashMap<String, Long> sizes;

    public Map<String, Long> dirrSize(Path path) {
        try {

            sizes = new HashMap<>();
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    long size = Files.size(file);
                    updDirSize(file, size);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
            });
            return sizes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updDirSize(Path path, long size){
        String key = path.toString();
        sizes.put(key, size + sizes.getOrDefault(key, 0L));

        Path parent = path.getParent();

        if (parent != null) {
            updDirSize(parent, size);
        }
    }
}

package fr.esgi.archi.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public abstract class FileUtils {

    /**
     * Move the file in the given folder
     *
     * @param from
     * @param to
     * @return
     */
    public static File moveTo(File from, String to) {
        try {
            to += from.getName();
            Files.move(from.toPath(), Paths.get(to), StandardCopyOption.REPLACE_EXISTING);
            return new File(to);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

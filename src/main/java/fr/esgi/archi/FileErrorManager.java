package fr.esgi.archi;

import io.vertx.core.AbstractVerticle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;


public class FileErrorManager extends AbstractVerticle {
    private final File errorDir = new File("error/");

    @Override
    public void start() {
        vertx.setPeriodic(2000,
                aLong -> {
                    List<File> files = Arrays.asList(this.getFiles());
                        for (File f : files) {
                            if (f != null) {
                                vertx.eventBus().request(
                                        "my-channel-error", f, reply -> {
                                            if (reply.succeeded()) {
                                                System.out.println(f.delete());
                                            } else {
                                                System.out.println("Error failure");
                                            }
                                        });
                            }
                        }
                }
        );
    }


    /**
     * Get every files in the "error/" folder
     *
     * @return
     */
    private File[] getFiles() {
        File[] files = this.errorDir.listFiles();
        if (files == null) return files;
        return files;
    }

}

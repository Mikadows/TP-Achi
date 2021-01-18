package fr.esgi.archi;

import io.vertx.core.AbstractVerticle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;


/**
 * - Distribution des fichiers sur workers génériques
 * - Une fois le fichier traiter l'envoie dans un autre répertoire
 * - Gestion du ACK (du retour du worker)
 * extraire dans le directory courant
 */
public class FileManager extends AbstractVerticle {
    private final File inputDir = new File("input/");
    private final String pendingDir = "pending/";
    private final String outputDir = "output/";


    @Override
    public void start() {
        vertx.setPeriodic(2000,
                aLong -> {
                    this.getFiles();
                    File[] files = this.getFiles();
                    for (File f : files) {
                        if (f.isDirectory()) {
                            try {
                                this.emptyDirectoryInput(f);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            f = this.moveTo(f, this.pendingDir);
                            if (f != null) {
                                vertx.eventBus().request(
                                        "my-channel", f, reply -> {
                                            if (reply.succeeded()) {
                                                File replyFile = (File) reply.result().body();
                                                System.out.println("Moved TO");
                                                replyFile = this.moveTo(replyFile, this.outputDir);
                                                if (replyFile != null) {
                                                    System.out.println(replyFile.toString());
                                                }
                                            }
                                        });
                            }
                        }
                    }
                }
        );
    }

    /***
     * Empty a directory receive into the /input directory and delete it afterwards
     * @param f
     */
    private void emptyDirectoryInput(File f) throws IOException {
        for (File file : Objects.requireNonNull(f.listFiles())) {
            System.out.println(file.getName());
            if (file.isDirectory()) {
                Path sourcePath = file.toPath();
                System.out.println(sourcePath);
                System.out.println(f.toPath());
                Files.move(new File(file.getPath()).toPath(), new File(this.inputDir + "/" + file.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else {
                System.out.println("Test");
                file.renameTo(new File(this.inputDir + "/" + file.getName()));
            }
        }
        f.delete();
    }

    /**
     * Move the file in the given folder
     *
     * @param from
     * @param to
     * @return
     */
    private File moveTo(File from, String to) {
        try {
            to += from.getName();
            Files.move(from.toPath(), Paths.get(to), StandardCopyOption.REPLACE_EXISTING);
            return new File(to);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Get every files in the "input/" folder
     *
     * @return
     */
    private File[] getFiles() {
        File[] files = this.inputDir.listFiles();
        if (files == null) return files;
        return files;
    }

}

package fr.esgi.archi;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import com.google.common.collect.Lists;

import java.util.stream.Collectors;


/**
 * - Distribution des fichiers sur workers génériques
 * - Une fois le fichier traiter l'envoie dans un autre répertoire
 * - Gestion du ACK (du retour du worker)
 * Traitement par lot des fichiers et si erreur mettre les fichiers dans "error/"
 * extraire dans le directory courant
 */
public class FileManager extends AbstractVerticle {
    private final File inputDir = new File("input/");
    private final String pendingDir = "pending/";
    private final String outputDir = "output/";
    private final String errorDir = "error/";

    @Override
    public void start() {
        vertx.setPeriodic(2000,
                aLong -> {
                    //this.getFiles();

                    try {
                        List<File> files = Arrays.asList(this.getFiles());
                        if (files.size() > 15) {
                            int partitioningSize = 2;
                            List<List<File>> lists
                                    = Lists.partition(files, partitioningSize);
                            for (List<File> sublist : lists) {
                                for (File f : files) {
                                    if (f.isDirectory()) {
                                        this.emptyDirectoryInput(f);
                                    } else {
                                        f = this.moveTo(f, this.pendingDir);
                                        if (f != null) {
                                            vertx.eventBus().request(
                                                    "my-channel", f, reply -> {
                                                        if (reply.succeeded()) {
                                                            succesManagement(reply.result());
                                                        } else {
                                                            errorManagement(reply.cause());
                                                        }
                                                    });
                                        }
                                    }
                                }
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    private ArrayList<Comparable> subArray(List<File> A, int start, int end) {
        ArrayList toReturn = new ArrayList();
        for (int i = start; i <= end; i++) {
            toReturn.add(A.get(i));
        }
        return toReturn;
    }

    private void succesManagement(Message<Object> result) {
        File replyFile = (File) result.body();
        replyFile = this.moveTo(replyFile, this.outputDir);
        assert replyFile != null;
        System.out.println("Moved TO " + replyFile.toString());
    }

    private void errorManagement(Throwable cause) {
        File replyFile = new File(cause.getMessage());
        System.out.println(cause);
        replyFile = this.moveTo(replyFile, this.errorDir);
        assert replyFile != null;
        System.out.println("Moved TO " + replyFile.toString());
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

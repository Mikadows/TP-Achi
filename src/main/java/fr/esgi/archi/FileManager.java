package fr.esgi.archi;

import fr.esgi.archi.util.FileUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

import com.google.common.collect.Lists;


/**
 * - Distribution des fichiers sur workers génériques
 * - Une fois le fichier traiter l'envoie dans un autre répertoire
 * - Gestion du ACK (du retour du worker)
 * Traitement par lot des fichiers et si erreur mettre les fichiers dans "error/"
 * extraire dans le directory courant
 */
public class FileManager extends AbstractVerticle {


    @Override
    public void start() {
        vertx.setPeriodic(2000,
                aLong -> {
                    try {
                        List<File> files = Arrays.asList(this.getFiles());
                        //partitioning if number of files sup than 15
                        if (files.size() > 15) {
                            int partitioningSize = 2;
                            List<List<File>> lists
                                    = Lists.partition(files, partitioningSize);
                            for (List<File> sublist : lists) {
                                for (File f : sublist) {
                                    fileTraitement(f);
                                }
                            }
                         //if number of file inf than 15
                        }else{
                            for (File f : files) {
                                fileTraitement(f);
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    /***
     * traitement of the file depending on its type.
     * @param f
     * @throws IOException
     */
    private void fileTraitement(File f) throws IOException {
        if (f.isDirectory()) {
            this.emptyDirectoryInput(f);
        } else {
            f = FileUtils.moveTo(f, FileManagerConfig.PENDING_DIR);
            if (f != null) {
                senfFileToWorker(f);
            }
        }
    }

    /***
     * Send the file to the worker
     * @param f
     */
    private void senfFileToWorker(File f) {
        vertx.eventBus().request(
                "my-channel", f, reply -> {
                    if (reply.succeeded()) {
                        succesManagement(reply.result());
                    } else {
                        errorManagement(reply.cause());
                    }
                });
    }

    /***
     * Traitement after a response in succes from a worker
     * @param result
     */
    private void succesManagement(Message<Object> result) {
        File replyFile = (File) result.body();
        replyFile = FileUtils.moveTo(replyFile, FileManagerConfig.OUTPUT_DIR);
        assert replyFile != null;
        System.out.println("Moved TO " + replyFile.toString());
    }

    /***
     * Traitement after a failing response from a worker
     * @param cause
     */
    private void errorManagement(Throwable cause) {
        File replyFile = new File(cause.getMessage());
        System.out.println(cause);
        replyFile = FileUtils.moveTo(replyFile, FileManagerConfig.ERROR_DIR);
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
                Files.move(new File(file.getPath()).toPath(), new File(FileManagerConfig.INPUT_DIR_FILE + "/" + file.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else {
                System.out.println("Test");
                file.renameTo(new File(FileManagerConfig.INPUT_DIR_FILE + "/" + file.getName()));
            }
        }
        f.delete();
    }



    /**
     * Get every files in the "input/" folder
     *
     * @return
     */
    private File[] getFiles() {
        File[] files = FileManagerConfig.INPUT_DIR_FILE.listFiles();
        return files;
    }

}

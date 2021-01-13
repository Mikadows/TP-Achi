package fr.esgi.archi;

import io.vertx.core.AbstractVerticle;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


/**
 * - Distribution des fichiers sur workers génériques
 * - Une fois le fichier traiter l'envoie dans un autre répertoire
 * - Gestion du ACK (du retour du worker)
 * - (option) Vérifie les extensions des fichiers
 * pour les distribuer sur des workers specialisés (.txt, .md) sinon worker gen
 * - (option) Si dossier reprendre le contenue de celui-ci et (File.isDirectory())
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
                        f = this.moveTo(f, this.pendingDir);
                        if ( f != null) {
                            vertx.eventBus().request(
                                    "my-channel", f, reply -> {
                                        if (reply.succeeded()) {
                                            File replyFile = (File) reply.result().body();
                                            System.out.println("Moved TO");
                                            replyFile = this.moveTo(replyFile, this.outputDir);
                                            if (replyFile != null){
                                                System.out.println(replyFile.toString());
                                            }
                                        }
                                    });
                        }
                    }
                }
        );
    }

    /**
     * Move the file in the given folder
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
     * @return
     */
    private File[] getFiles() {
        File[] files = this.inputDir.listFiles();
        if (files == null) return files;
        return files;
    }

}

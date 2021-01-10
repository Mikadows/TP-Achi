import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;

import java.io.File;

/**
 * TODO :
 *  - Distribution des fichiers sur workers génériques
 *  - Une fois le fichier traiter l'envoie dans un autre répertoire
 *  - Gestion du ACK (du retour du worker)
 *  - (option) Vérifie les extensions des fichiers
 *    pour les distribuer sur des workers specialisés (.txt, .md) sinon worker gen
 *  - (option) Si dossier reprendre le contenue de celui-ci et (File.isDirectory())
 *    extraire dans le directory courant
 */
public class FileManager extends AbstractVerticle {


    public void start(){
        vertx.setPeriodic(100, new Handler<Long>() {
            @Override
            public void handle(Long aLong) {
                //TODO: send file
            }
        });
    }

}

package fr.esgi.archi;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;

import java.io.File;

/**
 * TODO :
 * - Distribution des fichiers sur workers génériques
 * - Une fois le fichier traiter l'envoie dans un autre répertoire
 * - Gestion du ACK (du retour du worker)
 * - (option) Vérifie les extensions des fichiers
 * pour les distribuer sur des workers specialisés (.txt, .md) sinon worker gen
 * - (option) Si dossier reprendre le contenue de celui-ci et (File.isDirectory())
 * extraire dans le directory courant
 */
public class FileManager extends AbstractVerticle {

    @Override
    public void start() {
        vertx.setPeriodic(2000,
                aLong -> vertx.eventBus().request(
                        "my-channel", "PING", reply -> {
                            if (reply.succeeded()) {
                                System.out.println(reply.result().body());
                            }
                        }));
    }

}

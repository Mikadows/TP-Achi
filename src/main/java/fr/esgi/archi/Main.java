package fr.esgi.archi;

import fr.esgi.archi.codec.FileCodec;
import fr.esgi.archi.workers.Worker1;
import fr.esgi.archi.workers.Worker2;
import io.vertx.core.Vertx;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();
        vertx.eventBus().registerDefaultCodec(File.class, new FileCodec());
        vertx.deployVerticle(new FileManager());
        vertx.deployVerticle(new Worker1());
        vertx.deployVerticle(new Worker2());
    }
}

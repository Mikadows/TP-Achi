package fr.esgi.archi.workers;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

import java.io.File;

/**
 * TODO :
 * - Thread.sleep
 * - renvoyer son ACK
 */
public class Worker1 extends AbstractVerticle {

    @Override
    public void start() {
        EventBus eventBus = vertx.eventBus();
        eventBus.consumer("my-channel",
                message -> {
                    File f = (File) message.body();
                    System.out.println("Worker 1 - " + f.toString());
//                    System.out.println("Worker 1 - " + message.body());

                    message.reply(f);
                });
    }
}

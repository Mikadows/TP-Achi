package fr.esgi.archi.workers;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;

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
                    System.out.println("Worker 1 - " + message.body());
                    message.reply("PONG from worker 1");
                });
    }
}

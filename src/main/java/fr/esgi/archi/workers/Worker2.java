package fr.esgi.archi.workers;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

import java.io.File;

public class Worker2 extends AbstractVerticle {

    @Override
    public void start() {
        EventBus eventBus = vertx.eventBus();
        eventBus.consumer("my-channel",
                message -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    File f = (File) message.body();
                    System.out.println("Worker 2 - " + f.toString());

                    message.reply(f);
                });
    }
}

package fr.esgi.archi.workers;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class WorkerError extends AbstractVerticle{

    @Override
    public void start() {
        EventBus eventBus = vertx.eventBus();
        eventBus.consumer("my-channel-error",
                message -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    File f = (File) message.body();
                    System.out.println("Worker error 1- " + f.toString());
                    String body = "";
                    try {
                        body = readFile(f);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if(body.equals("")){
                        message.fail(500, f.getPath());
                    }else{
                        message.reply(f);
                    }
                });
    }

    public String readFile(File f) throws FileNotFoundException {
        Scanner myReader = new Scanner(f);
        String res = "";
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            res = res + data;
        }
        myReader.close();
        return res;
    }
}

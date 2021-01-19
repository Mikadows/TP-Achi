package fr.esgi.archi;

import fr.esgi.archi.util.FileUtils;
import io.vertx.core.AbstractVerticle;
import java.io.File;


public class FileErrorManager extends AbstractVerticle {
    private final File errorDir = new File("error/");
    private static final String DEADQUEUE = "deadqueue/";
    private final String pendingDir = "pending/";
    private static final String OUTPUT = "output/";

    @Override
    public void start() {
        vertx.setPeriodic(2000,
                aLong -> {
                    File[] files = this.getFiles();
                        for (File f : files) {
                            if (f != null) {
                                f = FileUtils.moveTo(f, this.pendingDir);
                                File finalF = f;
                                vertx.eventBus().request(
                                        "my-channel-error", f, reply -> {
                                            if (reply.succeeded()) {
                                                FileUtils.moveTo(finalF, OUTPUT);
                                            } else {
                                                FileUtils.moveTo(finalF, DEADQUEUE);
                                                System.out.println("Failure... move to deadqueue");
                                            }
                                        });
                            }
                        }
                }
        );
    }


    /**
     * Get every files in the "error/" folder
     *
     * @return
     */
    private File[] getFiles() {
        return this.errorDir.listFiles();
    }

}

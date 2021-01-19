package fr.esgi.archi;

import fr.esgi.archi.util.FileUtils;
import io.vertx.core.AbstractVerticle;
import java.io.File;


public class FileErrorManager extends AbstractVerticle {


    @Override
    public void start() {
        vertx.setPeriodic(2000,
                aLong -> {
                    File[] files = this.getFiles();
                        for (File f : files) {
                            if (f != null) {
                                f = FileUtils.moveTo(f, FileManagerConfig.PENDING_DIR);
                                File finalF = f;
                                vertx.eventBus().request(
                                        "my-channel-error", f, reply -> {
                                            if (reply.succeeded()) {
                                                FileUtils.moveTo(finalF, FileManagerConfig.OUTPUT_DIR);
                                            } else {
                                                FileUtils.moveTo(finalF, FileManagerConfig.DEADQUEUE_DIR);
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
        return FileManagerConfig.ERROR_DIR_FILE.listFiles();
    }

}

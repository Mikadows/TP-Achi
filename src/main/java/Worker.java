import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;

/**
 * TODO :
 * - Thread.sleep
 * - renvoyer son ACK
 */
public class Worker extends AbstractVerticle {

    public void start(){
        EventBus eventBus = vertx.eventBus();

    }
}

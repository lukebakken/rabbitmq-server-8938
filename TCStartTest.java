///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.testcontainers:rabbitmq:1.17.6
//DEPS org.slf4j:slf4j-jdk14:1.7.36

import java.lang.Runtime;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

import static java.lang.System.*;

public class TCStartTest {
    public static void main(String... args) {
        
        var imageName = DockerImageName.parse("rabbitmq:3.9-management").asCompatibleSubstituteFor("rabbitmq");
        
        while (true) {
            try {
                System.out.println("Starting RabbitMQ Container");
                var container = new ConfiguredRabbitMQContainer(imageName, 0, 0, "rabbitmq");
                container.start();
                System.out.println("Stopping RabbitMQ Container");
                container.stop();
            }
            catch(Throwable x) {
                System.out.println("Container startup failed, halting to leave container in docker!\nException Message:" + x.getMessage());
                Runtime.getRuntime().halt(1);
            }
        }
    }
}

// Copied from Quarkus RabbitMQ devservices implementation 
final class ConfiguredRabbitMQContainer extends RabbitMQContainer {
    
    private static final String DEV_SERVICE_LABEL = "quarkus-dev-service-rabbitmq";

    private static final int RABBITMQ_PORT = 5672;
    private static final int RABBITMQ_HTTP_PORT = 15672;
    
    private final int port;
    private final int httpPort;

    ConfiguredRabbitMQContainer(DockerImageName dockerImageName, int fixedExposedPort, int fixedExposedHttpPort, String serviceName) {
        super(dockerImageName);
        this.port = fixedExposedPort;
        this.httpPort = fixedExposedHttpPort;
        withNetwork(Network.SHARED);
        withExposedPorts(RABBITMQ_PORT, RABBITMQ_HTTP_PORT);
        if (serviceName != null) { // Only adds the label in dev mode.
            withLabel(DEV_SERVICE_LABEL, serviceName);
        }
        if (!dockerImageName.getRepository().endsWith("rabbitmq")) {
            throw new IllegalArgumentException("Only official rabbitmq images are supported");
        }
    }

    @Override
    protected void configure() {
        super.configure();
        if (port > 0) {
            addFixedExposedPort(port, RABBITMQ_PORT);
        }
        if (httpPort > 0) {
            addFixedExposedPort(httpPort, RABBITMQ_HTTP_PORT);
        }
    }

    public int getPort() {
        return getMappedPort(RABBITMQ_PORT);
    }
    
    public void specialStop() {
        super.stop();
    }
}

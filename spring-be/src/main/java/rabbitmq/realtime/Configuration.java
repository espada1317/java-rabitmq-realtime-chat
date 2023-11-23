package rabbitmq.realtime;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Configuration {

    public static final String EXCHANGE_NAME = "hello-exchange";
    private static final String HOST_NAME = "localhost";

    public static final String[] ROUTING_KEY_DIRECT = {"error", "debug", "info"};
    public static final String[] ROUTING_KEY_TOPIC = {"error.app1", "error.app2", "debug.app1", "debug.app2", "info.app1", "info.app2"};

    public static Connection getConnection() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST_NAME);

        Connection connection = null;
        try {
            connection = factory.newConnection();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        return connection;
    }

    private Configuration() {
    }
}

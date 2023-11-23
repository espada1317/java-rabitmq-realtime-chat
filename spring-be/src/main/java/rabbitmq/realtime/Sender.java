package rabbitmq.realtime;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static java.lang.System.out;
import static rabbitmq.realtime.Configuration.EXCHANGE_NAME;
import static rabbitmq.realtime.Configuration.ROUTING_KEY_DIRECT;
import static rabbitmq.realtime.Configuration.ROUTING_KEY_TOPIC;

@Slf4j
public class Sender {

    public static Thread send(final String type, final String message, final int count) {
        Thread t = new Thread(new Runnable() {

            private Connection connection = null;
            private Channel channel = null;

            @Override
            public void run() {
                try {
                    connection = Configuration.getConnection();
                    channel = connection.createChannel();
                    channel.exchangeDeclare(EXCHANGE_NAME, type);

                    int i = 0;
                    String[] routingKey = type.equals("direct") ? ROUTING_KEY_DIRECT : ROUTING_KEY_TOPIC;

                    while (!Thread.currentThread().isInterrupted()) {
                        if (i++ >= count) break;

                        String newMessage = message + ": " + i;

                        channel.basicPublish(EXCHANGE_NAME, routingKey[i % routingKey.length], MessageProperties.PERSISTENT_TEXT_PLAIN, newMessage.getBytes());
                        out.println("Sent " + routingKey[i % routingKey.length] + ": '" + newMessage + "'");
                        Thread.sleep(1000);
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    try {
                        channel.close();
                        connection.close();
                    } catch (IOException | TimeoutException e) {
                        throw new RuntimeException(e);
                    }
                    out.println("Publisher thread exists!");
                }
            }
        });

        t.start();
        return t;
    }

}

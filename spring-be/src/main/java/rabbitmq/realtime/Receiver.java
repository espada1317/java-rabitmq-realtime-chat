package rabbitmq.realtime;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import static java.lang.System.out;
import static rabbitmq.realtime.Configuration.EXCHANGE_NAME;

public class Receiver {

    public static Thread receive(final String type, final String name, final String bindingKey) {
        Thread t = new Thread(new Runnable() {

            private Connection connection = null;
            private Channel channel = null;
            private String queueName = null;

            @Override
            public void run() {
                try {
                    connection = Configuration.getConnection();
                    channel = connection.createChannel();
                    channel.exchangeDeclare(EXCHANGE_NAME, type);

                    boolean durable = true;
                    queueName = channel.queueDeclare("", durable, false, false, null).getQueue();
                    channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);

                    Consumer consumer = new DefaultConsumer(channel) {
                        @Override
                        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                            String message = new String(body, StandardCharsets.UTF_8);
                            out.println(name + " Received " + envelope.getRoutingKey() + ": '" + message + "'");
                        }
                    };

                    channel.basicConsume(queueName, true, consumer);

                    while (!Thread.currentThread().isInterrupted()) {
                        Thread.sleep(500);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    try {
                        channel.queueDelete(queueName);
                        channel.close();
                        connection.close();
                    } catch (IOException | TimeoutException e) {
                        throw new RuntimeException(e);
                    }
                }
                out.println(name + " thread exists!");
            }
        });

        t.start();
        return t;
    }

    private Receiver() {
    }
}

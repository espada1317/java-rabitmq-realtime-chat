package rabbitmq.realtime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBackRest {
    public static void main(String[] args) {
        SpringApplication.run(SpringBackRest.class, args);

        try {
            Thread publisher = Sender.send("topic", "test message", 100);
            Thread subscriber1 = Receiver.receive("topic", "subs1", "error.*");
            Thread subscriber2 = Receiver.receive("topic", "subs2", "*.app1");
            Thread subscriber3 = Receiver.receive("topic", "subs3", "info.app2");

            System.in.read();

            publisher.interrupt();
            subscriber1.interrupt();
            subscriber2.interrupt();
            subscriber3.interrupt();

            publisher.join();
            subscriber1.join();
            subscriber2.join();
            subscriber3.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
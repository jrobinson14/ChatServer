package ChatServer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * testing code, out of date
 * @author Jonathan Robinson
 */
public class ServerTest {

    public static void main(String args[]) throws IOException, InterruptedException {
        Server tester = new Server(12345);
        Thread serverTest = new Thread(tester);
        serverTest.start();

        Message message = new Message("Tom", "hello");
        Message message2 = new Message("Jim", "hello");
        Message message3 = new Message("Test", "test");
        Message message4 = new Message("Test", "test2");

        Socket socket = new Socket("localhost", 12345);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(message);
        oos.flush();

        Thread.sleep(1000);


        Socket socket2 = new Socket("localhost", 12345);
        ObjectOutputStream oos2 = new ObjectOutputStream(socket2.getOutputStream());
        System.out.println("Sending second message");
        oos2.writeObject(message2);

        Thread.sleep(1000);

        oos.writeObject(message3);
        oos2.writeObject(message4);



    }
}

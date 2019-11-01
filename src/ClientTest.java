import ChatClient.*;
import ChatServer.*;


/**
 * Testing code, out of date.
 * @author Jonathan Robinson
 */
import java.io.IOException;

public class ClientTest {

    public static void main(String args[]) throws IOException, ClassNotFoundException, InterruptedException {

        //start new server
        Server tester = new Server(12345);
        Thread serverTest = new Thread(tester);
        serverTest.start();

        //start new client
        Client testClient = new Client("Tom", 6789, "localhost", 12345, false);
        Thread clientThread = new Thread(testClient);
        clientThread.start();

        Client testClient2 = new Client("Jim", 6789, "localhost", 12345, false);
        Thread clientThread2 = new Thread(testClient2);
        clientThread2.start();

        Thread.sleep(10);
        testClient.requestRecipient("Jim");
        testClient.sendMessage("hello");
        testClient.disconnect();
        testClient2.sendMessage("hi");
    }
}

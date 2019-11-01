import ChatClient.Client;

import java.io.IOException;

/**
 * Main method for client, creates a new client to connect to desired server.
 * Also launches client GUI
 * Needs command line args: server IP address, server port number, desired username
 * @author Jonathan Robinson
 */
public class client {

    public static void main(String args[]) throws IOException, ClassNotFoundException, InterruptedException{

        String serverIP = args[0];
        int serverPort = Integer.parseInt(args[1]);
        String username = args[2];



        Client testClient = new Client(username, 6789, serverIP, serverPort, true);
        Thread clientThread = new Thread(testClient);
        clientThread.start();

        /*Client testClient2 = new Client("Jim", 6790, serverIP, 12345);
        Thread clientThread2 = new Thread(testClient2);
        clientThread2.start();*/

        //Thread.sleep(10);
        //testClient.requestRecipient("Jim");
        //testClient2.requestRecipient("Tom");
        //testClient.sendMessage("hello");
        //testClient.disconnect();
    }
}

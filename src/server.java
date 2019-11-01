import ChatServer.Server;

import java.io.IOException;

/**
 * Main method for launching server, creates a new server on desired port
 * Needs port number specified in command line
 * @author Jonathan Robinson
 */
public class server {

    public static void main(String args[]) throws IOException, ClassNotFoundException, InterruptedException {


        int portNum = Integer.parseInt(args[0]);
        Server newServer = new Server(portNum);
        Thread serverThread = new Thread(newServer);
        serverThread.start();
    }
}

import ChatClient.Client;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Main testing class for lab1, create
 * desired number of clients to test network traffic
 * Needs the following command line args: server IP address, server port number, number of clients to test
 * @author Jonathan Robinson
 */
public class NetworkTest {


    public static void main(String args[]) throws IOException, ClassNotFoundException, InterruptedException {

        String serverIP = args[0];
        int serverPort = Integer.parseInt(args[1]);
        int testNum = Integer.parseInt(args[2]); //number of clients to test with

        //store all clients here
        ArrayList<Client> clientList = new ArrayList<>();


        //create a bunch of clients
        /*
           each loop creates two clients, so divide number of desired clients by 2 to get i
           each of the two clients is paired with each other
         */
        for(int i = 0; i < testNum/2; i++){
            Client newClient = new Client(Integer.toString(i), 6789, serverIP, serverPort, false);
            Thread clientThread = new Thread(newClient);
            clientThread.start();

            Client newClient2 = new Client(Integer.toString(i) + "b", 6789, serverIP, serverPort, false);
            Thread clientThread2 = new Thread(newClient2);
            clientThread2.start();

            newClient.requestRecipient(Integer.toString(i) + "b");
            newClient2.requestRecipient(Integer.toString(i));

            clientList.add(newClient);
            clientList.add(newClient2);


        }

        //send messages back and forth
        for(int i = 0; i < clientList.size() -1; i++){
            clientList.get(i).sendMessage("testing");
            clientList.get(i+1).sendMessage("testing2");
            clientList.get(i).sendMessage("testing3");
            clientList.get(i+1).sendMessage("testing4");


        }

        //disconnect TODO: this is causing problems and needs to be fixed
       /* for(int i = 0; i < clientList.size(); i++){
            clientList.get(i).disconnect();
        }*/

    }
}

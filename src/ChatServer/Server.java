
package ChatServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


/**
 * Primary server class. Waits for message from a client then
 * creates a new thread to handle client requests
 * @author Jonathan Robinson
 */
public class Server implements Runnable{

    private int portNum;
    private InetAddress ipNum;
    ServerSocket serverSocket;
    //ObjectInputStream input;
    //Socket socket;
    private ArrayList<ClientProxy> activeClients; //store list of active clients here
    public ArrayList<Message> offlineMessages;
    public ArrayList<String> allMessages;


    /**
     * Constructor for Server, creates a new server
     * @param port port number for server to use
     * @throws IOException
     */
    public Server(int port) throws IOException {
        System.out.println("Server is starting");
        this.ipNum = InetAddress.getLocalHost();
        this.portNum = port;
        serverSocket = new ServerSocket(portNum);
        activeClients = new ArrayList<ClientProxy>();
        offlineMessages = new ArrayList<Message>();
        allMessages = new ArrayList<String>();

        System.out.println("IP address and port number: " + ipNum + ", " + portNum);
    }

    /**
     * Run method for server, accepts an incoming Message object
     * and creates a new client proxy
     */
    public void run(){
        System.out.println("Server Message: Server is now ready for requests");
        while(true){

            System.out.println("Server Message: Waiting For Requests");
            try {
                Socket socket = serverSocket.accept();
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                Message newClient = (Message) input.readObject(); //new message for client connection recieved
                if(newClient != null) {
                    createNewClient(this, socket, input, newClient);
                    //System.out.println("Server Message: Message received: " + newClient.getMessage());
                } else System.out.println("Server ERROR: Error Processing New Client Message");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Server ERROR: Cannot read incoming request");
            }

            System.out.println("Server Message: Successfull loop");
        }
    }

    /**
     * Create a new client and add that client to the active clients list
     * @param input
     * @param message
     */
    //TODO: client gets offline messages if there are any
    private void createNewClient(Server server, Socket socket, ObjectInputStream input, Message message) throws IOException {
        ClientProxy newClient = new ClientProxy(server, socket, input, message.getIpNum(), message.getSender());
        activeClients.add(newClient);
        Thread newClientThread = new Thread(newClient);
        newClientThread.start();
    }

    //TODO: if client is not found, store messages for client when it connects

    /**
     * Search list of activeClients for the desired client
     * @param name
     * @return
     */
    synchronized protected ClientProxy getRecipient(String name){
        for(ClientProxy client: activeClients){
            if(client.getName().equals(name))
                return client;
        }
        return null;
    }


}

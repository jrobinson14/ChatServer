
package ChatServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Proxy for client, represents client on server side
 * Contains information about the client, handles all requests and messages for client
 * @author Jonathan Robinson
 */
public class ClientProxy implements Runnable {

    private ClientProxy recipient; //the client being talked to
    private InetAddress ipNum; //ip info of client
    private String name; //username of the client
    private Server parentServer; //the server that created this client
    Socket socket;
    ObjectInputStream input;
    ObjectOutputStream output;
    boolean run = true;

    /**
     * Constructor
     * @param server top-level server that created this client proxy
     * @param newSocket socket to client
     * @param newInput input stream to client for writting object
     * @param ip ip address of server
     * @param newName name of client
     * @throws IOException
     */
    public ClientProxy(Server server, Socket newSocket, ObjectInputStream newInput, InetAddress ip, String newName) throws IOException {
        this.parentServer = server;
        this.socket = newSocket;
        this.input = newInput;
        this.ipNum = ip;
        this.name = newName;
        output = new ObjectOutputStream(socket.getOutputStream());
        //System.out.println("Client Proxy Message: New Proxy created for: " + name);
    }

    private void addRecipient(ClientProxy newRecipient){
        this.recipient = newRecipient;
        System.out.println(name + " has new reciepient: " + recipient.getName());
    }

    public String getName() {
        return name;
    }

    public InetAddress getIpNum(){
        return ipNum;
    }

    /**
     * main run method, waits for message from client and uses switch statement to
     * interpret what to do with the message
     */
    public void run(){
        System.out.println("Client Proxy Message: New Proxy Started for : " + name);
        try {
            //send notification to client that connection was successful
            Message requestReturn = new Message("server", "System: request successful");
            output.writeObject(requestReturn);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("Getting offline messages");
            getOfflineMsg();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Server Error: Failed to get offline messages");
        }

        while(run) {
            try {
                //TODO: ClientProxy needs to decide what to do based on the message recieved
                System.out.println("Client Proxy Message: Waiting for input for: " + name);

                //all this does right now is recieves a message and prints it (makes sure message is coming through
                Message newMessage = (Message) input.readObject();
                System.out.println("New message is " + newMessage.getMessage());

                //case statement to process message. If message contains a command, do that command
                //else send message to recipient. See readme for proposed commands and syntax
                String command = newMessage.getMessage();
                switch (command){
                    case("System: request new recipient"): //clientproxy told to find a recipient
                        Message recipeintName = (Message) input.readObject();
                        System.out.println("ClientProxy Message: looking for: " + recipeintName.getMessage());
                        getRecipient(recipeintName.getMessage());
                        break;
                    case("System: Disconnect"): //client proxy told to disconnect, client is done
                        run = false;
                        System.out.println("run is now false");
                        System.out.println("ClientProxy Message: Disconnecting Client: " + name);
                        break;
                    case("System: Partner Left"):
                        recipient = null;
                        Message update = new Message("Server", "Partner disconnected");
                    default: //assume message is for client's current recipient
                        sendMessage(newMessage);
                }

                parentServer.allMessages.add(newMessage.toString());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Client Proxy Message: Error getting message");
            }
        }

    }

    /**
     * Send a message to the recipient if client currently has one, else output
     * error message
     * @param message
     */
    private void sendMessage(Message message) throws IOException {
        if(recipient != null)
          recipient.toClient(message);
        else {
            parentServer.offlineMessages.add(message);
            System.out.println("ClientProxy Message: Client " + name + "has no current recipient, storing offline message");
        }
    }

    /**
     * Send a recieved message to the client
     * @param message
     */
    public void toClient(Message message) throws IOException {
        output.writeObject(message);
    }

    /**
     * Update current recipeint, not sure if this will be used
     * just use getRecipient?
     * @param newRecipient
     */
    private void newRecipient(ClientProxy newRecipient){
        this.recipient = newRecipient;
    }

    /**
     * Search for active clients with desired username, if found the
     * recipient is updated, if not outputs error message
     * @param searchName
     */
    private void getRecipient(String searchName) throws IOException {
        ClientProxy newRecipient = parentServer.getRecipient(searchName);
        if(newRecipient != null) {
            this.recipient = newRecipient;
            System.out.println("Client Message: Recipient found: " + recipient.name);
        }
        else {
            System.out.println("Server Message: Unable to locate recipeint for: " + name);
            Message warning = new Message(name, "System: User Not Online");
            output.writeObject(warning);
        }
    }

    /**
     * Get any stored messages for the client when they connect to the server
     * @throws IOException
     */
    private void getOfflineMsg() throws IOException {
        for(Message message: parentServer.offlineMessages){
            if(message.getRecipient().equals(name))
                output.writeObject(message);
        }
    }
}

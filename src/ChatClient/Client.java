package ChatClient;

import ChatServer.Message;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import java.awt.BorderLayout;
import javax.swing.*;

/**
 * Class for client, this is what the user will be interacting with.
 * Sends commands to server (handled by client proxy)
 * @author Jonathan Robinson, Faycel Beji
 */
public class Client implements Runnable{

    private String name;
    private InetAddress ipNum;
    private int portNum;
    InetAddress serverIP;
    int serverPort;
    Socket socket;
    ObjectInputStream input;
    ObjectOutputStream output;
    String cuurentRecipient;
    public ArrayList<String> messageList;
    JFrame frame = new JFrame("Chatter");
    //Scanner in;
    //PrintWriter out;
    JTextField textField = new JTextField(60);
    JTextArea messageArea = new JTextArea(20, 60);
    JButton enter = new JButton();
    //FileWriter fileWriter;
    boolean isGUI;


    /**
     * Creates new client
     * @param newName username of client
     * @param newPortNum port number client is using
     * @param newServer IP address of server
     * @param newServPort port number of server
     * @throws IOException
     */
    public Client(String newName, int newPortNum, String newServer, int newServPort, boolean gui) throws IOException, ClassNotFoundException {
        this.name = newName;
        this.ipNum = InetAddress.getLocalHost();
        this.portNum = newPortNum;
        this.serverIP = InetAddress.getByName(newServer);
        this.serverPort = newServPort;
        socket = new Socket(serverIP, serverPort);
        messageList = new ArrayList<String>();
        this.isGUI = gui;

        //if client needs gui, create gui
        if(isGUI) {
            initComponent();
        }
    }

    /**
     * Main run method, sends connection request then waits for incoming message
     */
    public void run() {


        //send a connection request to the server, error message if unsuccessful
        System.out.println("Client Message: Starting client for: " + name);
        try {
            //create output stream, send initial connection request to server
            output = new ObjectOutputStream(socket.getOutputStream());
            connectionRequest();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.printf("Client Message: Client %s could not connect to server\n", name);
        }

        //wait for new message to client, to be displayed in chat window
        while(true){
            try {
                Message newMessage = (Message) input.readObject();
                System.out.println("Client Message: Message Recieved: " + newMessage.getMessage());
                messageList.add(newMessage.toString());
                messageArea.append(newMessage.toString());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Request a new connection to the server,
     * @throws IOException
     */
    public void connectionRequest() throws IOException, ClassNotFoundException {
        System.out.println("Client Message: Requesting connection for: " + name);
        Message message = new Message(name, "System: Request Connection");
        output.writeObject(message);
        output.flush();

        //wait for response from server indicating if request was successful
        input = new ObjectInputStream(socket.getInputStream());
        Message response = (Message) input.readObject();
        System.out.println("Client Message: Message from server " + response.getMessage());
    }

    /**
     * Request a new recipeint
     * @param recipient the recipient client the current client wishes to chat with
     * @throws IOException
     * @throws InterruptedException
     */
    public void requestRecipient(String recipient) throws IOException, InterruptedException {
        cuurentRecipient = recipient;
        System.out.println("Client Message: Requesting New recipient");
        Message request = new Message(name, "System: request new recipient");
        Message newRecipient = new Message(name, recipient);
        Thread.sleep(100); //TODO without wait, messages sent too fast, figure out how to fix without sleep
        output.writeObject(request);
        output.flush();
        output.writeObject(newRecipient);
    }

    /**
     * Send a disconnect request to the server
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void disconnect() throws IOException, ClassNotFoundException {
        Message discMessage = new Message(name, "System: Disconnect");
        output.writeObject(discMessage);
        output.flush();
        frame.setVisible(false);
        //socket.close();
        System.exit(0); //TODO: TEST!!!!!
    }

    /**
     * Send a message to current recipient, will display error message in GUI
     * if no recipient has been specified
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        Message toSend = new Message(name, message);
        if(cuurentRecipient == null){
            String notify = "No recipient selected, no message sent\n";
            messageList.add(notify);
            messageArea.append(notify);
        }
        else {
            toSend.setRecipient(cuurentRecipient);
            output.writeObject(toSend);
            output.flush();
            messageList.add(toSend.toString());
        }
            messageArea.append(toSend.toString());

    }

    /**
     * Create all swing GUI components and action listeners
     */
    private void initComponent(){

        enter.setText("Send");
        enter.setSize(5,5);
        JButton choose = new JButton("Choose A Recipient");
        JButton disc = new JButton("Disconnect");

        textField.setEditable(true);

        frame.getContentPane().add(textField, BorderLayout.CENTER);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.NORTH);
        frame.getContentPane().add(choose, BorderLayout.SOUTH);
        frame.getContentPane().add(disc, BorderLayout.LINE_START);
        frame.getContentPane().add(enter, BorderLayout.AFTER_LINE_ENDS);
        frame.pack();


        //textField.setHorizontalAlignment();
        //GroupLayout layout = new GroupLayout(frame.getContentPane());
        //frame.getContentPane().setLayout(layout);


        enter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String message = textField.getText();
                try {
                    sendMessage(message);
                    textField.setText("");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        disc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    disconnect();
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });

        choose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String recip = textField.getText();
                try {
                    requestRecipient(recip);
                    String inform = "New Chat Partner: " + recip + "\n";
                    messageList.add(inform);
                    messageArea.append(inform);
                    textField.setText("");
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });

        frame.setTitle(name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    private void updateMessages(){
        for(String text: messageList) {
            messageArea.append(text);
        }
    }

}


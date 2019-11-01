
package ChatServer;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

/**
 * Message is used to contain and send a message to/from server/client.
 * It Contains a timestamp, info on sender, and the message itself
 * @author Jonathan Robinson
 */
public class Message implements Serializable {

    private long time;
    private InetAddress ipNum;
    private String sender;
    private String recipient;
    private String message;

    /**
     * Constructor
     * @param newSender client sending the message
     * @param newMessage message content
     * @throws UnknownHostException
     */
    public Message(String newSender, String newMessage) throws UnknownHostException {
        this.time = System.currentTimeMillis();
        this.sender = newSender;
        this.message = newMessage;
        this.ipNum = InetAddress.getLocalHost();
    }

    public Long getTime(){ return time;}
    public String getSender(){return sender;}
    public String getMessage(){return message;}
    public InetAddress getIpNum(){return ipNum;}

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String name){
        recipient = name;
    }

    @Override
    public String toString(){
        String messageString = Long.toString(time) + " " + sender + " : " + message + "\n";
        return messageString;
    }

    public void toList(){
        String list = sender + ";" + recipient + ";" + Long.toString(time) + "\n";
    }


}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/*
 * The Server class listens for incoming client connections via port 9806.
 * It handles each client in a seperate thread using the ConversationHandler class.
 */
public class Server {
    static ArrayList<String> userNames = new ArrayList<String>(); // stores usernames of all connected clients
    static ArrayList<PrintWriter> printWriters = new ArrayList<PrintWriter>(); // stores PrintWriter objects to showcase messages to all of the connected clients
    
    /*
     * Entry point of the server.
     * Listens for new client connections and starts a thread for each one.
     */
    public static void main(String[] args) throws Exception{
        System.out.println("Waiting for clients...");
        ServerSocket ss  = new ServerSocket(9806);
        while (true) {
            Socket soc = ss.accept();
            System.out.println("Connection established");
            ConversationHandler handler = new ConversationHandler(soc);
            handler.start();
        }
    }
}

/*
 * The ConversationHandler class handles communication with a single connected client.
 * Manages username, message reception, and broadcasting.
 */
class ConversationHandler extends Thread {
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    String name;

    /**
     * constructor that recieves the client's socket.
     */
    public ConversationHandler(Socket socket) throws IOException{
        this.socket = socket;
    }

    /*
     * runs the conversation handler thread
     */
    public void run(){
        try{
            // set up the input and out streams for communication
            in = new BufferedReader((new InputStreamReader((socket.getInputStream())))); 
            out = new PrintWriter(socket.getOutputStream(), true); 

            int count = 0; // keeping track if the name already exists
            while(true){
                if(count > 0){
                    out.println("NAMEALREADYEXISTS");
                }else{
                    out.println("NAMEREQUIRED");
                }
                name = in.readLine();
                if(name == null){
                    return;
                }
                if(!Server.userNames.contains(name)){
                    Server.userNames.add(name);
                    break;
                }
                count++;
            }
            out.println("NAMEACCEPTED");
            Server.printWriters.add(out);

            while (true){
                String message = in.readLine();
                if(message == null){
                    return;
                }
                for(PrintWriter writer : Server.printWriters){
                    writer.println(name + ": "+message);
                }
            }

        }catch (Exception e){
            System.out.println(e);
        }
    }
}
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

/**
 * The Client class creates a GUI-based chat app. that connects to the server.
 * It handles sending and recieving messages in real-time via sockets.
 */
public class Client {
    static JFrame chatWindow = new JFrame("Chat");
    static JTextArea chatArea = new JTextArea(22,40);
    static JTextField textField = new JTextField(40);
    static JLabel blankLabel = new JLabel("     ");
    static JButton sendButton = new JButton("Send");
    
    
    static BufferedReader in;
    static PrintWriter out;

    /*
     * constructs the GUI window, sets up the layout, and attaches the listeners
     */
    Client() {
        chatWindow.setLayout(new FlowLayout());
        chatWindow.add(new JScrollPane(chatArea));
        chatWindow.add(blankLabel);
        chatWindow.add(textField);
        chatWindow.add(sendButton);

        chatWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatWindow.setSize(475, 500);
        chatWindow.setVisible(true);

        textField.setEditable(false);
        chatArea.setEditable(false);

        sendButton.addActionListener(new Listener());
        textField.addActionListener(new Listener());
    }
    
    /*
     * starts the chat client, connects to the server, handles name negotiation, and cincoming messages from the server.
     */
    void startChat() throws Exception{
        String ipAddress = JOptionPane.showInputDialog(
                chatWindow,
                "Enter IP Address",
                "IP Address required",
                JOptionPane.PLAIN_MESSAGE
        );
        Socket soc = new Socket(ipAddress, 9806);
        in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
        out = new PrintWriter(soc.getOutputStream(), true);

        while (true){
            String str = in.readLine();
            if(str.equals("NAMEREQUIRED")){
                String name = JOptionPane.showInputDialog(
                        chatWindow,
                        "Enter name",
                        "Name required",
                        JOptionPane.PLAIN_MESSAGE
                );
                out.println(name);
            } else if(str.equals("NAMEALREADYEXISTS")){
                String name = JOptionPane.showInputDialog(
                        chatWindow,
                        "Enter another name",
                        "Name already exists",
                        JOptionPane.WARNING_MESSAGE
                );
                out.println(name);
            } else if(str.equals("NAMEACCEPTED")){
                textField.setEditable(true);
            }else{
                chatArea.append(str + '\n');
            }

        }
    }

    // main method to launch
    public static void main(String[] args) throws Exception{
        Client client = new Client();
        client.startChat();
    }
}

/*
 * The Listener class handles sending messages from the text field or send button.
 */
class Listener implements ActionListener{
    @Override
    public void actionPerformed(ActionEvent e){
        Client.out.println(Client.textField.getText());
        Client.textField.setText("");
    }
}


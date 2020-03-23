package client.chat;

import java.net.Socket;
import java.io.IOException;
import java.util.Scanner;

import client.chat.EnvoiMessage;
import client.chat.LireMessage;


class ChatClient {

    private Socket s = null;
    private Thread tem = null;
    private Thread tlm = null;
    private String username = null;
    
    public ChatClient() {}

    public void init(String username) {
        try {
            this.s = new Socket("localhost", 16523);
            EnvoiMessage em = new EnvoiMessage(this.s);
            LireMessage lm = new LireMessage(this.s);
           
            this.username = username;
            em.setUsername(username);
 
            this.tem = new Thread(em);
            this.tlm = new Thread(lm);
        }
        catch (IOException e) {
            System.out.println("[ERROR] - An error occured while starting the chat");
            System.out.println("[ERROR] - " + e.getMessage());
        }
    }

    public void start() {
        this.tlm.start();
        this.tem.start();
    }

    public static void main(String[] args) {
        ChatClient cc = new ChatClient();
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter username : ");
        cc.init(sc.nextLine());
        cc.start();
    }    

}

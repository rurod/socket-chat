package client.chat;

import java.net.Socket;
import java.io.IOException;
import java.io.DataOutputStream;
import java.util.Scanner;

class EnvoiMessage implements Runnable {

    private DataOutputStream dos = null;
    private Scanner sc = null;
    private String username = null;
    private boolean dialog = false;    

    public EnvoiMessage(Socket s) {
        try {
            this.dos = new DataOutputStream(s.getOutputStream());
            this.sc = new Scanner(System.in);
            this.dialog = true;
        }
        catch (IOException e) {
            System.out.println("[ERROR] - An error occured while getting the output stream");
            System.out.println("[ERROR] - " + e.getMessage());
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void sendMessage(String msg) throws IOException {
        this.dos.writeUTF(msg);
        this.dos.flush();
    }

    public void filterMessage(String msg) throws IOException {
        if (msg.equals("exit")) {
            this.sendMessage("order#exit");
            this.dialog = false;
            this.closeConnection();
        }
        else {
            this.sendMessage(msg);
        }
    }

    public void closeConnection() {
        try {
            if (this.dos != null) {
                this.dos.close();
            }
        }
        catch (IOException e) {
            System.out.println("[ERROR] - An error occured while closing connection");
            System.out.println("[ERROR] - " + e.getMessage());
        }
    }

    public void run() {
        String  msg = null;
        try {
            if (this.dialog)
                this.sendMessage("order#username#" + this.username);
            while (this.dialog) {
                msg = this.sc.nextLine();
                System.out.print("# ");
                this.filterMessage(msg); 
            }
        } 
        catch (IOException e) {
            System.out.println("[ERROR] - An error occured while sending the message");
            System.out.println("[ERROR] - " + e.getMessage());
        }
    }

}

package server.chat;

import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

class ClientHandler implements Runnable {
    
    private Socket s = null;
    private DataInputStream dis = null;
    private DataOutputStream dos = null;
    private String username = null;
    private Vector<ClientHandler> clients;
    private boolean dialog = true;

    public ClientHandler(Socket s, Vector<ClientHandler> clients) {
        this.s = s;
        this.clients = clients;
    }
    
    public boolean getStreams() {
        try {
            this.dis = new DataInputStream(this.s.getInputStream());
            this.dos = new DataOutputStream(this.s.getOutputStream());
            return true;
        }
        catch (IOException e) {
            System.out.println("[ERROR] - An error occured while getting the socket streams");
            System.out.println("[ERROR] - " + e.getMessage());
            return false;
        }
    }
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String u) {
        this.username = u;
    }

    public void pushMessage(String msg) {
        try {
            this.dos.writeUTF(msg);
            this.dos.flush();
        }
        catch (IOException e) {
            System.out.println("[ERROR] - An error occured while sending the message");
            System.out.println("[ERROR] - " + e.getMessage());
        }
    }
    
    public void stopDialog() {
        this.dialog = false;
        this.closeConnection();
    }

    public void sendMessageTo(String msg, String dest) {
        for (ClientHandler ch: this.clients) {
            if (ch.getUsername().equals(dest)) {
                ch.pushMessage(msg + "#" + this.username + "#private");
                break;
            }
        }
    }

    public void broadcast(String msg) {
        for (ClientHandler ch: this.clients) {
            if (!ch.getUsername().equals(this.username))
                ch.pushMessage(msg + "#" + this.username + "#broadcast");
        }
    }

    public void filterMessage(String msg) {
        StringTokenizer st = new StringTokenizer(msg, "#");
        String token = null, dest = null; 
        System.out.println("[INFO] - Received from " + this.username + " : " + msg);
        if (st.hasMoreTokens()) {
            token = st.nextToken();
            if (token.equals("order")) {
                if (st.hasMoreTokens()) {
                    token = st.nextToken();
                    if (token.equals("stop")) {
                        this.stopDialog();
                    }
                    else if (token.equals("username")) {
                        if (st.hasMoreTokens()) {
                            token = st.nextToken();
                            this.setUsername(token);
                            this.broadcast("notify#" + token + " joined the chat");
                        }
                    }
                }
            }
            else {
                if (st.hasMoreTokens()) {
                    dest = st.nextToken();
                    this.sendMessageTo(token, dest);
                }
                else {
                    this.broadcast(token);
                }
            }
        }
    }

    public void welcomeMessage() {
        try {
            this.dos.writeUTF("notify# Successfully connected to the server !");
            this.dos.writeUTF("notify#*** Welcome to the chat ***");
            this.dos.writeUTF("notify#*** " + this.clients.size() + " users are connected ***");
            this.dos.flush();
        }
        catch (IOException e) {
            System.out.println("[ERROR] - An error occure while sending the welcome message");
            System.out.println("[ERROR] - " + e.getMessage());
        }
    }   
 
    public void closeConnection() {
        System.out.println("[INFO] - Closing connection of " + username);
        try {
            if (this.dos != null) {
                    this.dos.close();
            }
        }
        catch (IOException e) {
            System.out.println("[ERROR] - An error occured while closing the OutputStream");
            System.out.println("[ERROR] - " + e.getMessage());
        } 
        try {
            if (this.dis != null) {
                    this.dis.close();
            }
        }
        catch (IOException e) {
            System.out.println("[ERROR] - An error occured while closing the InputStream");
            System.out.println("[ERROR] - " + e.getMessage());
        } 
        try {
            if (this.s != null) {
                    this.s.close();
            }
        }
        catch (IOException e) {
            System.out.println("[ERROR] - An error occured while closing the Socket");
            System.out.println("[ERROR] - " + e.getMessage());
        }
        this.clients.remove(this);
    }

    public void run() {
        if (this.getStreams()) {
            this.welcomeMessage();
            try {
                while (this.dialog) {   
                    this.filterMessage(this.dis.readUTF());       
                }
            }
            catch (IOException e) {
                    System.out.println("[ERROR] - An error occured while receiving a message");
                    System.out.println("[ERROR] - " + e.getMessage());
            }
            finally {
                this.closeConnection();
            }
        }
        else {
            System.out.println("[ERROR] - ClientHandler stopped - Error occured while getting the streams");
        }
    }

}

package client.chat;

import java.net.Socket;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.StringTokenizer;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;


class LireMessage implements Runnable {

    private DataInputStream dis = null;
    private boolean dialog = false;

    public LireMessage(Socket s) {
        try {
            this.dis = new DataInputStream(s.getInputStream());
            this.dialog = true;
        }
        catch (IOException e) {
            System.out.println("[ERROR] - An error occured while getting the input stream");
            System.out.println("[ERROR] - " + e.getMessage());
        }
    }

    public String buildLine(String msg) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        StringTokenizer st = new StringTokenizer(msg, "#");
        String line = null, token = null;
        if (st.hasMoreTokens()) {
            token = st.nextToken();
            if (token.equals("notify")) {
                if (st.hasMoreTokens()) {
                    line = "[Notification] - " + st.nextToken();
                }
            }
            else {
                line = token;
                if (st.hasMoreTokens()) {
                    line = st.nextToken() + " : " + line;
                    if (st.hasMoreTokens()) {
                        token = st.nextToken();
                        if (token.equals("private"))
                            line = "[Private] " + line;
                    }
                }
            }
        }
        return dtf.format(now) + " " + line;
    }

    public void closeConnection() {
        if (this.dis != null) {
            try {
                this.dis.close();
            }
            catch (IOException e) {
                System.out.println("[ERROR] - An error occured while closing the socket");
                System.out.println("[ERROR] - " + e.getMessage());
            }
        }
    }

    public void run() {
        System.out.print("# ");
        try {
            while (true) {
                System.out.println(this.buildLine(this.dis.readUTF()));
                System.out.print("# ");
            }
        }
        catch (IOException e) {
            System.out.println("Chat exited.");
        }
        finally {
            this.closeConnection();
        }
    }

}
